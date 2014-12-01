package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.ActionBar;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.SlotAction;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Data.Settings;

import java.util.List;
import java.util.concurrent.Callable;

public class AbilityHandler extends Task implements Runnable {

    @Override
    public boolean validate() {
         return Players.getLocal().getTarget() != null;
    }

    @Override
    public void execute() {

        Settings.abilityStatus = "Ability Handler Running";

        List<SlotAction> abilities = ActionBar.getActions();
        for (SlotAction a : abilities) {
            if (Players.getLocal().getTarget() == null)
                break;
            if (a.getName() != null) {
                if (a.getType().equals(SlotAction.Type.ABILITY) && a.isReady()) {
                    if (a.activate()) {
                        Execution.delayUntil(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                return Players.getLocal().getTarget() == null;
                            }
                        }, 1600,2500);
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        if (this.validate())
            this.execute();
    }
}
