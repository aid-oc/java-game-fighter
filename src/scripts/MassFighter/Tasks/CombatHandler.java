package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.entities.GroundItem;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.Menu;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
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
import scripts.MassFighter.Framework.UserProfile;
import scripts.MassFighter.MassFighter;


import static scripts.MassFighter.MassFighter.settings;

public class CombatHandler extends Task {

    private final UserProfile userProfile = MassFighter.userProfile;
    private final Area fightArea = userProfile.getFightArea();
    private final NpcQueryBuilder validTargetQuery = Npcs.newQuery().within(fightArea).names(userProfile.getNpcNames()).filter(new Filter<Npc>() {
        @Override
        public boolean accepts(Npc npc) {
            return npc != null && npc.getHealthGauge() == null && npc.isValid() && npc.getAnimationId() == -1;
        }
    }).reachable();
    private final SpriteItemQueryBuilder buryItems = Inventory.newQuery().actions("Bury", "Scatter");

    public boolean validate() {
        if (userProfile.getBankArea() != null) {
            return MassFighter.methods.readyToFight() && !Inventory.isFull() && (!settings.useFood || Inventory.contains(settings.food.getName()));
        }
        return MassFighter.methods.readyToFight();
    }

    @Override
    public void execute() {
        Npc targetNpc = null;

        if ((settings.lootInCombat || !MassFighter.methods.isInCombat()) && (settings.looting || settings.buryBones)
                && !Inventory.isFull() && !MassFighter.methods.validLoot.results().isEmpty()) {
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
                    BresenhamPath path = BresenhamPath.buildTo(targetLoot);
                    if (path != null) {
                        path.step(true);
                    }
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
                LocatableEntityQueryResults<Npc> tagTargets = validTargetQuery.results();
                if (currentTargetCount < settings.tagSelection && !tagTargets.isEmpty()) {
                    Npc target = tagTargets.sortByDistance().limit(settings.tagSelection).first();
                    if (target != null) {
                        attackTarget(target);
                    }
                }
            } else {
                // Assign a new target if possible
                if (Players.getLocal().getTarget() == null) {
                    if (userProfile.getFightArea().contains(Players.getLocal())) {
                        // Get NPC Attacking US
                        if (MassFighter.methods.isInCombat()) {
                            MassFighter.status = "Under Attack";
                            NpcQueryBuilder opponents = Npcs.newQuery().within(fightArea).actions("Attack").targeting(Players.getLocal()).reachable();
                            if (!opponents.results().isEmpty()) {
                                targetNpc = opponents.results().nearest();
                            }
                        } else {
                            if (!validTargetQuery.results().isEmpty()) {
                                targetNpc = validTargetQuery.results().sortByDistance().limit(settings.targetSelection).first();
                            } else {
                                MassFighter.status = "Waiting";
                                Execution.delayUntil(() -> !validTargetQuery.results().isEmpty(), 700, 6000);
                                Camera.turnTo(fightArea.getRandomCoordinate());
                            }
                        }
                    } else {
                        MassFighter.status = "Returning";
                        BresenhamPath path = BresenhamPath.buildTo(fightArea);
                        if (path != null) {
                            path.step(true);
                        }
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
            if (targetNpc.interact("Attack", targetNpc.getName())) {
                MassFighter.status = "Attacking Target";
                if (settings.tagMode) {
                    Execution.delayUntil(() -> {
                        if (targetNpc.getTarget() != null) {
                            if (targetNpc.getTarget().equals(Players.getLocal())) return true;
                        }
                        return false;
                    }, 1500,2000);
                } else {
                    Execution.delayUntil(MassFighter.methods::isInCombat, 1000, 1300);
                }
                return true;
            }
        } else if (Distance.to(targetNpc) > Random.nextInt(5, 7)) {
            BresenhamPath toNpc = BresenhamPath.buildTo(targetNpc);
            if (toNpc != null) {
                toNpc.step(true);
            }
        } else {
            Camera.turnTo(targetNpc);
            Execution.delayUntil(targetNpc::isVisible, 1200, 1500);
        }
        return false;
    }
}

