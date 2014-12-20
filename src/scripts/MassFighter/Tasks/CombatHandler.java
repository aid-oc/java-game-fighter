package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.entities.GroundItem;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.Menu;
import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.queries.GroundItemQueryBuilder;
import com.runemate.game.api.hybrid.queries.NpcQueryBuilder;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.QueryResults;
import com.runemate.game.api.hybrid.region.GroundItems;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.rs3.local.hud.Powers;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Framework.BankingProfile;
import scripts.MassFighter.Framework.CombatProfile;
import scripts.MassFighter.MassFighter;

public class CombatHandler extends Task {

    private final CombatProfile combatProfile = MassFighter.combatProfile;
    private final Area[] fightAreas = combatProfile.getFightAreas().toArray(new Area[combatProfile.getFightAreas().size()]);
    private final NpcQueryBuilder validTargetQuery = Npcs.newQuery().within(fightAreas).names(combatProfile.getNpcNames()).filter(new Filter<Npc>() {
        @Override
        public boolean accepts(Npc npc) {
            return npc.getHealthGauge() == null && npc.isValid() && npc.getAnimationId() == -1;
        }
    }).reachable();

    public boolean validate() {
        if (combatProfile instanceof BankingProfile) {
            return Players.getLocal().getTarget() == null && readyToFight() && !Inventory.isFull();
        } else {
            return Players.getLocal().getTarget() == null && readyToFight();
        }
    }

    @Override
    public void execute() {

        final GroundItemQueryBuilder validLoot = GroundItems.newQuery().within(fightAreas)
                .names(combatProfile.getLootNames());
        final GroundItemQueryBuilder validBones = GroundItems.newQuery().within(fightAreas).filter(new Filter<GroundItem>() {
            @Override
            public boolean accepts(GroundItem groundItem) {
                String name = groundItem.getDefinition().getName();
                return name.contains("Bones") || name.contains("bones") || name.contains("ashes");
            }
        });
        final SpriteItemQueryBuilder buryItems = Inventory.newQuery().actions("Bury", "Scatter");

        if (MassFighter.buryBones) {
            System.out.println("Bury Bones Activated");
            if (!buryItems.results().isEmpty()) {
                SpriteItem buryItem = buryItems.results().first();
                if (buryItem != null) {
                    if (buryItem.getDefinition().getName().contains("ones")) {
                        if (buryItem.interact("Bury")) {
                            Execution.delayUntil(() -> !buryItem.isValid(), 600, 1000);
                        }
                    } else if (buryItem.getDefinition().getName().contains("ashes")) {
                        if (buryItem.interact("Scatter")) {
                            Execution.delayUntil(() -> !buryItem.isValid(), 600,1000);
                        }
                    }
                }
            } else if (!validBones.results().isEmpty() && !Inventory.isFull()) {
                pickupLoot(validBones.results().nearest());
            }
        }
        if (MassFighter.looting && !validLoot.results().isEmpty() && !Inventory.isFull()) {
            MassFighter.status = "LootHandler is Active";
             pickupLoot(validLoot.results().nearest());
        } else {
            MassFighter.status = "Combat Handler is Active";
            if (isInCombat()) {
                NpcQueryBuilder opponents = Npcs.newQuery().within(fightAreas).reachable().filter(new Filter<Npc>() {
                    @Override
                    public boolean accepts(Npc npc) {
                        return npc.getTarget() != null && npc.getTarget().equals(Players.getLocal());
                    }
                });
                if (!opponents.results().isEmpty()) {
                    Npc targetNpc = opponents.results().limit(2).random();
                    if (targetNpc != null) {
                        attackTarget(targetNpc);
                    }
                }
            } else {
                QueryResults suitableTargets = validTargetQuery.results();
                if (!suitableTargets.isEmpty()) {
                    final Npc targetNpc = validTargetQuery.results().limit(2).random();
                    if (targetNpc != null) {
                        attackTarget(targetNpc);
                    }
                }
            }
        }
    }

    private void attackTarget(final Npc targetNpc) {
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

    private void pickupLoot(final GroundItem targetLoot) {
        if (targetLoot != null) {
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
    }

    private Boolean isInCombat() {
        return Players.getLocal().getTarget() != null || !Npcs.newQuery().within(fightAreas).reachable().filter(new Filter<Npc>() {
            @Override
            public boolean accepts(Npc npc) {
                return npc.getTarget() != null && npc.getTarget().equals(Players.getLocal());
            }
        }).results().isEmpty();
    }

    public static Boolean readyToFight() {
        if (MassFighter.useSoulsplit) {
            return Powers.Prayer.getPoints() > Powers.Prayer.getMaximumPoints() / 2
                    && Powers.Prayer.Curse.SOUL_SPLIT.isActivated();
        } else return Health.getCurrent() >= MassFighter.eatValue;
    }
}

