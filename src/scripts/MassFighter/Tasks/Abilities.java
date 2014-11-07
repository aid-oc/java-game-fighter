package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.ActionBar;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.SlotAction;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Data.Settings;
import util.Functions;

import java.util.List;

/**
 * Created by Aidan on 07/11/2014.
 */
public class Abilities extends Task {
    @Override
    public boolean validate() {
        return Functions.isBusy() && Players.getLocal().getHealthGauge() != null && Players.getLocal().getInteractingEntity() != null
                && Health.getCurrent() > Settings.eatValue;
    }

    @Override
    public void execute() {


        if (!ActionBar.isExpanded()) {
            ActionBar.toggleExpansion();
        }

        List<SlotAction> abilities = ActionBar.getActions();
            for (SlotAction a : abilities) {
                if (a.getName() != null) {
                    if (a.isReady()) {
                        System.out.println("Attempting to activate ability as it is valid");
                        if (a.activate()) {
                            System.out.println("Successfully used ability " + a.getName());
                            Execution.delay(Random.nextInt(1000,1400));
                        } else {
                            System.out.println("Failed to use ability");
                        }
                    }
                }
            }
    }
}
