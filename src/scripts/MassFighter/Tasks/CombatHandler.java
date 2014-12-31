package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;
import com.runemate.game.api.hybrid.queries.NpcQueryBuilder;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Framework.BankingProfile;
import scripts.MassFighter.Framework.CombatProfile;
import scripts.MassFighter.MassFighter;

public class CombatHandler extends Task {

    private final CombatProfile combatProfile = MassFighter.combatProfile;
    private final Area[] fightAreas =  MassFighter.methods.fightAreasAsArray();
    final NpcQueryBuilder validTargetQuery = Npcs.newQuery().within(fightAreas).names(combatProfile.getNpcNames()).filter(new Filter<Npc>() {
        @Override
        public boolean accepts(Npc npc) {
            return npc.getHealthGauge() == null && npc.isValid() && npc.getAnimationId() == -1;
        }
    }).reachable();

    public boolean validate() {
        if (!MassFighter.methods.lootAvailable()) {
            if (combatProfile instanceof BankingProfile) {
                return MassFighter.methods.inFightAreas(Players.getLocal()) && Players.getLocal().getTarget() == null && MassFighter.methods.readyToFight() && !Inventory.isFull();
            } else {
                System.out.println("In fight areas");
                return MassFighter.methods.inFightAreas(Players.getLocal()) && Players.getLocal().getTarget() == null && MassFighter.methods.readyToFight();
            }
        }
        return false;
    }

    @Override
    public void execute() {
        MassFighter.status = "Fighting";
        Npc targetNpc = null;
        if (MassFighter.methods.isInCombat()) {
            NpcQueryBuilder opponents = Npcs.newQuery().within(fightAreas).reachable().filter(new Filter<Npc>() {
                @Override
                public boolean accepts(Npc npc) {
                    return npc.getTarget() != null && npc.getTarget().equals(Players.getLocal());
                }
            });
            if (!opponents.results().isEmpty()) {
                targetNpc = opponents.results().nearest();
            }
        } else {
            if (!validTargetQuery.results().isEmpty()) {
                System.out.println("Limiting query to the nearest: " + MassFighter.targetSelection);
                targetNpc = validTargetQuery.results().sortByDistance().limit(MassFighter.targetSelection).random();
            } else {
                // This was a ball ache, using delayUntil() it now should not reroute unless it times out
                if (fightAreas.length > 1 && !Players.getLocal().isMoving()) {
                    int currentIndex = MassFighter.methods.getFightAreaIndex();
                    int targetIndex = currentIndex == fightAreas.length-1 ? 0 : currentIndex+1;
                    WebPath toNextArea = Traversal.getDefaultWeb().getPathBuilder().buildTo(fightAreas[targetIndex]);
                    if (toNextArea != null) {
                        System.out.println("Travering WEB path to next area");
                        Execution.delayUntil(() -> {
                            toNextArea.step(true);
                            return fightAreas[targetIndex].contains(Players.getLocal());
                        }, (int)Distance.between(Players.getLocal(), fightAreas[targetIndex])*1000);
                    } else {
                        System.out.println("Traversing BACKUP path to next area");
                        Execution.delayUntil(() -> {
                            BresenhamPath.buildTo(fightAreas[targetIndex]).step(true);
                            return fightAreas[targetIndex].contains(Players.getLocal());
                        }, (int)Distance.between(Players.getLocal(), fightAreas[targetIndex])*1000);
                    }
                } else if (!Players.getLocal().isMoving() && !MassFighter.methods.isInCombat()) {
                    System.out.println("Moving to a different spot in the fight area");
                    BresenhamPath.buildTo(fightAreas[0].getRandomCoordinate().getPosition()).step(true);
                }
            }
        }
        // Attack
        if (targetNpc != null) {
            if (targetNpc.getName().equals("Aviansie")) {
                // Model to deal with the rapidly moving wings
                targetNpc.setForcedModel(new int[]{-118, -341, -73}, new int[]{95, -188, 25});
            }
            attackTarget(targetNpc);
        }
    }

    private void attackTarget(final Npc targetNpc) {
        MassFighter.targetNpc = targetNpc;
        if (targetNpc.isVisible()) {
            if (targetNpc.interact("Attack", targetNpc.getName())) {
                Execution.delayUntil(MassFighter.methods::isInCombat, 2000, 3000);
            }
        } else if (Distance.to(targetNpc) > 4) {
            BresenhamPath.buildTo(targetNpc).step(true);
        } else {
            Camera.turnTo(targetNpc);
        }
    }
}

