package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.ActionBar;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.SlotAction;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Data.Settings;
import util.Functions;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Ozzy on 07/11/2014.
 */
public class AbilityHandler extends Task {

    @Override
    public boolean validate() {
         return (Players.getLocal().getTarget() != null) && Functions.isBusy();
    }

    @Override
    public void execute() {

        Settings.status = "Ability Handler Active";

        // Enables the action bar if it is minimised
        if (!ActionBar.isExpanded()) {
            ActionBar.toggleExpansion();
        }

        // Loops through available abilities on the action bar, activates each if they are ready
        // with a delay of 800-1800ms between activations
        // Loop exits if the players target is null
        List<SlotAction> abilities = ActionBar.getActions();
        for (SlotAction a : abilities) {
            if (Players.getLocal().getTarget() == null)
                break;
            if (a.getName() != null) {
                if (a.isReady()) {
                    if (a.activate()) {
                        Execution.delayUntil(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                return Players.getLocal().getTarget() == null;
                            }
                        }, Random.nextInt(800,1800));
                    }
                }
            }
        }
    }
}
