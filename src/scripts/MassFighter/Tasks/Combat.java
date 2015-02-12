package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.queries.NpcQueryBuilder;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.hybrid.util.StopWatch;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Framework.UserProfile;
import scripts.MassFighter.MassFighter;

import java.util.concurrent.TimeUnit;

import static scripts.MassFighter.MassFighter.settings;

public class Combat extends Task {

    private final UserProfile userProfile = MassFighter.userProfile;
    private final Area fightArea = userProfile.getFightArea();
    private final NpcQueryBuilder validTargetQuery = Npcs.newQuery().within(fightArea).names(userProfile.getNpcNames()).filter(new Filter<Npc>() {
        @Override
        public boolean accepts(Npc npc) {
            return npc != null && npc.getHealthGauge() == null && npc.isValid() && npc.getAnimationId() == -1;
        }
    }).reachable();
    private final SpriteItemQueryBuilder buryItems = Inventory.newQuery().actions("Bury", "Scatter");
    private StopWatch idleTime = new StopWatch();

    public boolean validate() {
        if (userProfile.getBankArea() != null) {
            return MassFighter.methods.readyToFight() && !Inventory.isFull();
        }
        return MassFighter.methods.readyToFight() && (Loot.validLoot.results().isEmpty() || Inventory.isFull());
    }

    @Override
    public void execute() {
        Npc targetNpc = null;
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
                    LocatableEntityQueryResults<Npc> aggro = Npcs.newQuery().within(fightArea).actions("Attack").targeting(Players.getLocal()).reachable().results();
                    if (aggro.isEmpty()) {
                        // We're not in combat, get a new target
                        LocatableEntityQueryResults<Npc> targets = validTargetQuery.results();
                        if (!targets.isEmpty()) {
                            System.out.println("Getting new target");
                            targetNpc = targets.sortByDistance().limit(settings.targetSelection).random();
                        } else if (!userProfile.getFightArea().contains(Players.getLocal())) {
                            MassFighter.status = "Returning";
                            Coordinate random = fightArea.getRandomCoordinate();
                            if (random != null && random.isReachable()) {
                                if (random.isVisible()) {
                                    random.click();
                                } else {
                                    Camera.turnTo(random);
                                }
                            }
                        } else {
                            MassFighter.status = "No Targets";
                            Execution.delayUntil(() -> !validTargetQuery.results().isEmpty(), 700, 6000);
                            Camera.turnTo(fightArea.getRandomCoordinate());
                        }
                    } else {
                        // Assign a target which is attacking us
                        if (!idleTime.isRunning()) idleTime.start();
                        if (idleTime.getRuntime(TimeUnit.SECONDS) > Random.nextInt(5, 10)) {
                            System.out.println("Getting AGGRO Target: idled for " + idleTime.getRuntime(TimeUnit.SECONDS));
                            targetNpc = aggro.sortByDistance().limit(settings.targetSelection).random();
                            idleTime.stop();
                            idleTime.reset();
                        }
                    }
                }
            } else {
                idleTime.reset();
                MassFighter.targetEntity = Players.getLocal().getTarget();
                MassFighter.status = "In Combat";
            }
            // Attack our target if it is valid
            if (targetNpc != null && targetNpc.isValid() && targetNpc.getName() != null) {
                if (targetNpc.getName().equals("Aviansie")) {
                    // Model to exclude the rapidly moving wings
                    targetNpc.setForcedModel(new int[]{-118, -341, -73}, new int[]{95, -188, 25});
                }
                attackTarget(targetNpc);
            }
        }
    }


    private void buryBones() {
        if (settings.buryBones && !buryItems.results().isEmpty()) {
            final SpriteItemQueryBuilder buryItems = Inventory.newQuery().actions("Bury", "Scatter");
            if (!buryItems.results().isEmpty()) {
                MassFighter.status = "Burying";
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
        MassFighter.status = "Finding Target";
        MassFighter.targetEntity = targetNpc;
        if (targetNpc.getVisibility() == 100) {
            if (targetNpc.interact("Attack", targetNpc.getName())) {
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

