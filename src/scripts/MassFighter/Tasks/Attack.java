package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.entities.GroundItem;
import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.entities.definitions.NpcDefinition;
import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.queries.GroundItemQueryBuilder;
import com.runemate.game.api.hybrid.queries.NpcQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.GroundItems;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.hybrid.util.Filters;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import helpers.Movement;
import scripts.MassFighter.Framework.Methods;
import scripts.MassFighter.GUI.Settings;
import scripts.MassFighter.MassFighter;

import java.util.Arrays;

import static scripts.MassFighter.Framework.Methods.out;

public class Attack extends Task {

    private GroundItemQueryBuilder getValidLoot()
    {
        GroundItemQueryBuilder lootQuery = GroundItems.newQuery().filter(Filters.DECLINE_ALL);
        Area fightArea = Settings.fightArea;
        String[] lootNames = Settings.lootNames;
        if (fightArea != null && Methods.arrayIsValid(lootNames)) {
            lootQuery = GroundItems.newQuery().within(fightArea).filter(new Filter<GroundItem>() {
                @Override
                public boolean accepts(GroundItem groundItem) {
                    if (Methods.hasRoomForItem(groundItem)) {
                        String itemName = groundItem.getDefinition().getName().toLowerCase();
                        return ((Settings.lootByValue && Methods.isWorthLooting(groundItem)) || Arrays.asList(lootNames).contains(itemName));
                    }
                    return false;
                }
            }).reachable();
        }
        return lootQuery;
    }

    private NpcQueryBuilder getAttackingNpcs() {
        NpcQueryBuilder attackingNpcQuery = Npcs.newQuery().filter(Filters.DECLINE_ALL);
        Player player = Players.getLocal();
        if (player != null) {
            attackingNpcQuery = Npcs.newQuery().actions("Attack").targeting(player).reachable();
        }
        return attackingNpcQuery;
    }

    private NpcQueryBuilder getSuitableNpcs() {
        NpcQueryBuilder suitableNpcQuery = Npcs.newQuery().filter(Filters.DECLINE_ALL);
        Area fightArea = Settings.fightArea;
        String[] npcNames = Settings.npcNames;
        if (fightArea != null && Methods.arrayIsValid(npcNames)) {
            suitableNpcQuery = Npcs.newQuery().within(fightArea).names(npcNames)
                    .filter(new Filter<Npc>() {
                        @Override
                        public boolean accepts(Npc npc) {
                            return npc != null && npc.getPosition() != null && (Settings.attackCombatMonsters ? npc.isValid()
                                    : npc.isValid() && npc.getId() != 1273 && npc.getTarget() == null && npc.getHealthGauge() == null)
                                    && (Settings.bypassReachable || npc.getPosition().isReachable());
                        }
                    });
        }
        return suitableNpcQuery;
    }


    public boolean validate() {
        return Health.getCurrent() > Settings.criticalHitpoints && getValidLoot().results().isEmpty();
    }

    @Override
    public void execute() {

        final Player player = Players.getLocal();
        LocatableEntityQueryResults<Npc> npcsTargettingUs = getAttackingNpcs().results();

        if (!isBusy() || npcsTargettingUs.isEmpty() && player.getTarget() == null || (Settings.tagMode && npcsTargettingUs.size() < Settings.tagSelection)) {
            final LocatableEntityQueryResults<Npc> validTargetResults = getSuitableNpcs().results();
            if (!validTargetResults.isEmpty()) {
                out("Combat: We need a new target");
                MassFighter.status = "Finding Target";
                Npc targetNpc;
                if (npcsTargettingUs.isEmpty() || Settings.tagMode && npcsTargettingUs.size() < Settings.tagSelection) {
                    targetNpc = validTargetResults.sortByDistance().limit(Settings.targetSelection).random();
                } else {
                    targetNpc = npcsTargettingUs.random();
                }
                if (targetNpc != null) {
                    final NpcDefinition targetNpcDefinition = targetNpc.getDefinition();
                    if (targetNpcDefinition != null) {
                        if (targetNpc.isVisible()) {
                            MassFighter.targetEntity = targetNpc;
                            if (targetNpc.interact("Attack", targetNpcDefinition.getName())) {
                                out("Combat: Attacked a target");
                                final Npc target = targetNpc;
                                Execution.delayUntil(() -> target.getTarget() != null, 1000, 2000);
                            } else {
                                out("Combat: NPC interaction failed");
                            }
                        } else {
                            Movement.moveToInteractable(targetNpc);
                        }
                    } else {
                        out("Combat: Failed to get info on our target");
                    }
                } else {

                    out("Combat: Invalid Target");
                }
            } else {
                out("Combat: Waiting for new targets");
                MassFighter.status = "No Targets";
            }
        } else {
            MassFighter.status = "In Combat";
            LocatableEntity target = player.getTarget();
            if (target != null) MassFighter.targetEntity = target;
        }

    }

    private boolean isBusy() {
        Player player = Players.getLocal();
        return player != null && (player.getTarget() != null || player.getAnimationId() != -1);
    }

}

