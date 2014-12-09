package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.entities.GroundItem;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.Menu;
import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.queries.GroundItemQueryBuilder;
import com.runemate.game.api.hybrid.queries.NpcQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.QueryResults;
import com.runemate.game.api.hybrid.region.GroundItems;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.rs3.local.hud.Powers;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Data.Settings;
import scripts.MassFighter.MassFighter;
import util.Functions;


public class CombatHandler extends Task {

    private final NpcQueryBuilder validTargetQuery = Npcs.newQuery().within(MassFighter.fightArea)
            .names(Settings.chosenNpcName).filter(new Filter<Npc>() {
        @Override
        public boolean accepts(Npc npc) {
            return npc.getHealthGauge() == null && npc.isValid() && npc.getAnimationId() == -1;
        }}).reachable();

    public boolean validate() {
        return  !isInCombat() && readyToFight();
    }


    @Override
    public void execute() {

        final GroundItemQueryBuilder validLoot = GroundItems.newQuery().within(MassFighter.fightArea)
                .names(Settings.lootChoices);
        if (Settings.lootCharms && !validLoot.results().isEmpty() && !Inventory.isFull()) {
            Settings.status = "LootHandler is Active";
            GroundItem targetLoot = validLoot.results().nearest();
            if (targetLoot != null) {
                // Temporary model for an RS3 charm
                targetLoot.setBackupModel(new int[]{-10, 7, -20}, new int[]{14, 6, 16});
                if (targetLoot.isVisible()) {
                    if (targetLoot.interact("Take", targetLoot.getDefinition().getName())) {
                        Execution.delayUntil(() -> !targetLoot.isValid(), 1500,1600);
                    } else if (Menu.isOpen()) {
                        Menu.close();
                    }
                } else if (Distance.to(targetLoot) > 2) {
                    BresenhamPath.buildTo(targetLoot).step(true);
                } else {
                    Camera.turnTo(targetLoot);
                }
            }
        } else {
            Settings.status = "Combat Handler is Active";
            QueryResults suitableTargets = validTargetQuery.results();
            if (!suitableTargets.isEmpty()) {
                final Npc targetNpc = validTargetQuery.results().limit(2).random();
                if (targetNpc != null) {
                    MassFighter.targetNpc = targetNpc;
                    if (targetNpc.isVisible()) {
                        if (targetNpc.interact("Attack", targetNpc.getName())) {
                            Execution.delayUntil(this::isInCombat, 2000, 3000);
                        }
                    } else if (Distance.to(targetNpc) < 4) {
                        Camera.turnTo(targetNpc);
                    } else {
                        BresenhamPath.buildTo(targetNpc).step(true);
                        Camera.turnTo(targetNpc);
                    }
                }
            }
        }
    }


    private Boolean isInCombat() {
        return Players.getLocal().getTarget() != null || !Npcs.newQuery().within(MassFighter.fightArea).filter(new Filter<Npc>() {
            @Override
            public boolean accepts(Npc npc) {
                return npc.getTarget() != null && npc.getTarget().equals(Players.getLocal());
            }
        }).results().isEmpty();
    }

    private Boolean readyToFight() {
        if (Settings.useSoulsplit) {
            return Powers.Prayer.getPoints() > Powers.Prayer.getMaximumPoints() / 2 && Functions.isSoulsplitActive();
        } else return Health.getCurrent() >= Settings.eatValue;
    }

}

