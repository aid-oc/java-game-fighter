package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.entities.GroundItem;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.region.GroundItems;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.ActionBar;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Data.Settings;
import util.Functions;

import java.util.concurrent.Callable;

/**
 * Created by Aidan on 06/11/2014.
 */
public class Fight extends Task {

    private Area fightArea;
    private Player player = Players.getLocal();

    @Override
    public boolean validate() {
        fightArea = new Area.Circular(Settings.startLocation, Settings.chosenFightRegion);
        return fightArea.contains(player) && !Functions.isBusy() && Health.getCurrent() > Settings.eatValue;
}

    @Override
    public void execute() {
        Settings.status = "Fighting";
        Npc target = Npcs.newQuery().within(fightArea).names(Settings.chosenNpcName).actions("Attack").results().sortByDistance().limit(3).random();
        Settings.targetNpc = target;

        if (Settings.isLooting) {
            for (final GroundItem g : GroundItems.newQuery().within(fightArea).names(Settings.lootChoices).results().sortByDistance()) {
                 if (!Inventory.isFull() && g.isValid()) {
                     Settings.status = "Looting";
                     if (g.interact("Take", g.getDefinition().getName())) {
                         System.out.println("Picked up loot");
                         Functions.waitFor(!g.isValid(), 2000);
                     }
                 }
            }
        }

        if (!ActionBar.isAutoRetaliating())
            ActionBar.toggleAutoRetaliation();

        if (player.getInteractingEntity() != null) {
            System.out.println("Player is underattack, setting that guy as my target");
            target = (Npc)player.getInteractingEntity();
        }



        if (target != null) {
                if (target.isVisible()) {
                    if (target.interact("Attack")) {
                        Functions.waitFor(!Functions.isBusy(), 6000);
                                System.out.println("Player no longer fighting, finding a new mob");
                    } else {
                        // temporary fix for a bug I came across at waterfiends
                        target = Npcs.newQuery().within(fightArea).names(Settings.chosenNpcName).actions("Attack").results().sortByDistance().limit(3).random();
                        if (target.interact("Attack")) {
                            Functions.waitFor(!Functions.isBusy(), 6000);
                                    System.out.println("Player no longer fighting, finding a new mob - 2");
                        }
                    }
                } else if (Distance.between(player, target) < 3) {
                    Camera.turnTo(target);
                } else {
                    BresenhamPath routeToTarget = BresenhamPath.buildTo(target);
                    if (routeToTarget != null) {
                        routeToTarget.step();
                        Execution.delayUntil(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                return !player.isMoving();
                            }
                        }, 4000);
                    }
                }
        } else {
            System.out.println("NPC was Null, finding another");
        }
    }

}
