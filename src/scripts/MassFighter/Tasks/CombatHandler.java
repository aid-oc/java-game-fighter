package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.entities.GroundItem;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.Menu;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;
import com.runemate.game.api.hybrid.queries.NpcQueryBuilder;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Framework.BankingProfile;
import scripts.MassFighter.Framework.CombatProfile;
import scripts.MassFighter.MassFighter;

import static scripts.MassFighter.MassFighter.settings;

public class CombatHandler extends Task {

    private final CombatProfile combatProfile = MassFighter.combatProfile;
    private final Area[] fightAreas =  MassFighter.methods.fightAreasAsArray();
    private final NpcQueryBuilder validTargetQuery = Npcs.newQuery().within(fightAreas).names(combatProfile.getNpcNames()).filter(new Filter<Npc>() {
        @Override
        public boolean accepts(Npc npc) {
            return npc.getHealthGauge() == null && npc.isValid() && npc.getAnimationId() == -1;
        }
    }).reachable();
    private final SpriteItemQueryBuilder buryItems = Inventory.newQuery().actions("Bury", "Scatter");

    public boolean validate() {
        if (combatProfile instanceof BankingProfile) {
            return MassFighter.methods.readyToFight() && !Inventory.isFull() && (!settings.useFood || Inventory.contains(settings.food.getName()));
        }
        return MassFighter.methods.readyToFight();
    }

    @Override
    public void execute() {
        Npc targetNpc = null;

        if ((settings.lootInCombat || !MassFighter.methods.isInCombat()) && (settings.looting || settings.buryBones)
                && !MassFighter.methods.validLoot.results().isEmpty() && !Inventory.isFull()) {
                GroundItem targetLoot = MassFighter.methods.validLoot.results().nearest();
            if (targetLoot != null) {
                String targetLootName = targetLoot.getDefinition().getName();
                MassFighter.status = "Picking up " + targetLootName;
                MassFighter.targetEntity = targetLoot;
                if (targetLoot.isVisible()) {
                    if (targetLoot.interact("Take", targetLootName)) {
                        Execution.delayUntil(() -> !targetLoot.isValid(), 2500, 3000);
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
            if (settings.buryBones) {
                buryBones();
            }
            if (settings.tagMode) {
                int currentTargetCount = Npcs.newQuery().targeting(Players.getLocal()).results().size();
                MassFighter.currentTargetCount =  currentTargetCount;
                LocatableEntityQueryResults<Npc> tagTargets = Npcs.newQuery().within(fightAreas).names(combatProfile.getNpcNames()).filter(new Filter<Npc>() {
                    @Override
                    public boolean accepts(Npc npc) {
                        return npc.getTarget() == null && npc.isValid();
                    }
                }).reachable().results();

                if (currentTargetCount < settings.tagSelection && !tagTargets.isEmpty()) {
                    Npc target = tagTargets.sortByDistance().limit(2).random();
                    if (target != null) {
                        if (attackTarget(target)) {
                            System.out.println("Tagged an NPC");
                        }
                    }
                }
            } else {
                // Assign a new target if possible
                if (Players.getLocal().getTarget() == null) {
                    if (MassFighter.methods.inFightAreas(Players.getLocal())) {
                        // Get NPC Attacking US
                        if (MassFighter.methods.isInCombat()) {
                            MassFighter.status = "Under Attack";
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
                                targetNpc = validTargetQuery.results().sortByDistance().limit(settings.targetSelection).random();
                            } else {
                                if (fightAreas.length > 1 && !Players.getLocal().isMoving()) {
                                    MassFighter.status = "Moving";
                                    System.out.println("Combat: Changing Area");
                                    int currentIndex = MassFighter.methods.getFightAreaIndex();
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
                                    MassFighter.status = "Waiting";
                                    Execution.delayUntil(() -> !validTargetQuery.results().isEmpty(), 700, 6000);
                                    Camera.turnTo(fightAreas[0].getRandomCoordinate());
                                    System.out.println("Facing towards a random coordinate in the fight area");
                                }
                            }
                        }
                    } else {
                        MassFighter.status = "Returning";
                        System.out.println("Returning to fight area");
                        BresenhamPath.buildTo(fightAreas[0]).step(true);
                    }
                }
                // Attack our target if it is valid
                if (targetNpc != null && targetNpc.getName() != null) {
                    if (targetNpc.getName().equals("Aviansie")) {
                        // Model to exclude the rapidly moving wings
                        targetNpc.setForcedModel(new int[]{-118, -341, -73}, new int[]{95, -188, 25});
                    }
                    attackTarget(targetNpc);
                }
            }
        }
    }

    private void buryBones() {
        if (settings.buryBones && !buryItems.results().isEmpty()) {
            final SpriteItemQueryBuilder buryItems = Inventory.newQuery().actions("Bury", "Scatter");
            if (!buryItems.results().isEmpty()) {
                MassFighter.status = "Burying Bones";
                SpriteItemQueryResults bones = buryItems.results();
                bones.stream().filter(bone -> bone != null).forEach(bone -> {
                    String name = bone.getDefinition().getName();
                    if (name.toLowerCase().contains("bones") && bone.interact("Bury") || name.toLowerCase().contains("ashes") && bone.interact("Scatter")) {
                        Execution.delayUntil(() -> !bone.isValid(), 1000,1400);
                    }
                });
            }
        }
    }

    private boolean attackTarget(final Npc targetNpc) {
        MassFighter.status = "Locating target";
        MassFighter.targetEntity = targetNpc;
        if (targetNpc.getVisibility() == 100) {
            System.out.println("Combat: Target is visible");
            if (targetNpc.interact("Attack", targetNpc.getName())) {
                MassFighter.status = "Attacking Target";
                System.out.println("Combat: Attacked target");
                if (settings.tagMode) {
                    Execution.delayUntil(() -> {
                        if (targetNpc.getTarget() != null) {
                            if (targetNpc.getTarget().equals(Players.getLocal())) return true;
                        }
                        return false;
                    }, 1500,2000);
                } else {
                    Execution.delayUntil(MassFighter.methods::isInCombat, 800, 1000);
                }
                return true;
            }
        } else if (Distance.to(targetNpc) > Random.nextInt(2, 4)) {
            System.out.println("Combat: Moving to target");
            BresenhamPath.buildTo(targetNpc).step(true);
        } else {
            System.out.println("Combat: Turning Camera");
            Camera.turnTo(targetNpc);
        }
        return false;
    }
}

