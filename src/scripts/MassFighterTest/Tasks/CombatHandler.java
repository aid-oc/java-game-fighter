package scripts.MassFighterTest.Tasks;

import com.runemate.game.api.hybrid.entities.GroundItem;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.Menu;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;
import com.runemate.game.api.hybrid.queries.NpcQueryBuilder;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighterTest.Framework.BankingProfile;
import scripts.MassFighterTest.Framework.CombatProfile;
import scripts.MassFighterTest.MassFighterTest;

public class CombatHandler extends Task {

    private final CombatProfile combatProfile = MassFighterTest.combatProfile;
    private final Area[] fightAreas =  MassFighterTest.methods.fightAreasAsArray();
    final NpcQueryBuilder validTargetQuery = Npcs.newQuery().within(fightAreas).names(combatProfile.getNpcNames()).filter(new Filter<Npc>() {
        @Override
        public boolean accepts(Npc npc) {
            return npc.getHealthGauge() == null && npc.isValid() && npc.getAnimationId() == -1;
        }
    }).reachable();

    public boolean validate() {
        if (combatProfile instanceof BankingProfile) {
            return MassFighterTest.methods.readyToFight() && !Inventory.isFull();
        }
        return MassFighterTest.methods.readyToFight();
    }

    @Override
    public void execute() {
        Npc targetNpc = null;
        /* LOOTING */
        if (MassFighterTest.lootInCombat || !MassFighterTest.methods.isInCombat()) {
            // Bury bones
            final SpriteItemQueryBuilder buryItems = Inventory.newQuery().actions("Bury", "Scatter");
            if (MassFighterTest.buryBones && !buryItems.results().isEmpty()) {
                MassFighterTest.status = "Burying Bones";
                SpriteItemQueryResults bones = buryItems.results();
                bones.stream().filter(bone -> bone != null).forEach(bone -> {
                    String name = bone.getDefinition().getName();
                    if (name.toLowerCase().contains("bones") && bone.interact("Bury") || name.toLowerCase().contains("ashes") && bone.interact("Scatter")) {
                        Execution.delayUntil(() -> !bone.isValid(), 1000,1400);
                    }
                });
            }
            // Pick up loot
            if (MassFighterTest.looting && !MassFighterTest.methods.validLoot.results().isEmpty() && !Inventory.isFull()) {
                GroundItem targetLoot = MassFighterTest.methods.validLoot.results().nearest();
                if (targetLoot != null) {
                    MassFighterTest.status = "Picking up " + targetLoot.getDefinition().getName();
                    if (targetLoot.isVisible()) {
                        if (targetLoot.interact("Take", targetLoot.getDefinition().getName())) {
                            Execution.delayUntil(() -> !targetLoot.isValid(), 2500,3000);
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
        }
        /* COMBAT */
        if (Players.getLocal().getTarget() == null) {
            if (MassFighterTest.methods.inFightAreas(Players.getLocal())) {
                // Get NPC Attacking US
                if (MassFighterTest.methods.isInCombat()) {
                    MassFighterTest.status = "We're under attack - finding target";
                    System.out.println("Combat: We're in combat, but not attacking");
                    NpcQueryBuilder opponents = Npcs.newQuery().within(fightAreas).reachable().filter(new Filter<Npc>() {
                        @Override
                        public boolean accepts(Npc npc) {
                            return npc.getTarget() != null && npc.getTarget().equals(Players.getLocal());
                        }
                    });
                    if (!opponents.results().isEmpty()) {
                        System.out.println("Combat: Found who's attacking us");
                        targetNpc = opponents.results().nearest();
                    }
                } else {
                    if (!validTargetQuery.results().isEmpty()) {
                        System.out.println("Combat: Not in combat, getting new target");
                        targetNpc = validTargetQuery.results().sortByDistance().limit(MassFighterTest.targetSelection).random();
                    } else {
                        if (fightAreas.length > 1 && !Players.getLocal().isMoving()) {
                            MassFighterTest.status = "Changing fight area";
                            System.out.println("Combat: Changing Area");
                            int currentIndex = MassFighterTest.methods.getFightAreaIndex();
                            int targetIndex = currentIndex == fightAreas.length - 1 ? 0 : currentIndex + 1;
                            WebPath toNextArea = Traversal.getDefaultWeb().getPathBuilder().buildTo(fightAreas[targetIndex]);
                            if (toNextArea != null) {
                                System.out.println("Travering WEB path to next area");
                                Execution.delayUntil(() -> {
                                    toNextArea.step(true);
                                    return fightAreas[targetIndex].contains(Players.getLocal());
                                }, (int) Distance.between(Players.getLocal(), fightAreas[targetIndex]) * 1000);
                            } else {
                                System.out.println("Traversing BACKUP path to next area");
                                Execution.delayUntil(() -> {
                                    BresenhamPath.buildTo(fightAreas[targetIndex]).step(true);
                                    return fightAreas[targetIndex].contains(Players.getLocal());
                                }, (int) Distance.between(Players.getLocal(), fightAreas[targetIndex]) * 1000);
                            }
                        } else {
                            MassFighterTest.status = "No monsters in set tile range, waiting";
                        }
                    }
                }
            } else {
                MassFighterTest.status = "Moving back into fight area";
                System.out.println("Returning to fight area");
                BresenhamPath.buildTo(fightAreas[0]).step(true);
            }
        }
        // Attack
        if (targetNpc != null) {
            if (targetNpc.getName().equals("Aviansie")) {
                // Model to exclude the rapidly moving wings
                targetNpc.setForcedModel(new int[]{-118, -341, -73}, new int[]{95, -188, 25});
            }
            attackTarget(targetNpc);
        }
    }

    private void attackTarget(final Npc targetNpc) {
        MassFighterTest.status = "Locating target";
        MassFighterTest.targetNpc = targetNpc;
        if (targetNpc.isVisible()) {
            System.out.println("Combat: Target is visible");
            if (targetNpc.interact("Attack", targetNpc.getName())) {
                MassFighterTest.status = "Attacking Target";
                System.out.println("Combat: Attacked target");
                Execution.delayUntil(MassFighterTest.methods::isInCombat, 800, 1000);
                if (MassFighterTest.waitForLoot) {
                    Coordinate npcTile = targetNpc.getPosition();
                    Execution.delayUntil(() -> {
                        System.out.println("Waiting for loot");
                        return !targetNpc.isValid() && Execution.delay(1000,1400) || targetNpc.getPosition() != npcTile;
                }, 10000);
                }
            }
        } else if (Distance.to(targetNpc) > 2) {
            System.out.println("Combat: Moving to target");
            BresenhamPath.buildTo(targetNpc).step(true);
        } else {
            System.out.println("Combat: Turning Camera");
            Camera.turnTo(targetNpc);
        }
    }
}

