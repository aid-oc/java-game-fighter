package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.ActionBar;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.SlotAction;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;

import java.util.List;

public class AbilityHandler extends Task implements Runnable {

    @Override
    public boolean validate() {
         return Players.getLocal().getTarget() != null;
    }

    @Override
    public void execute() {
        List<SlotAction> abilities = ActionBar.getActions();
        for (SlotAction a : abilities) {
            if (Players.getLocal().getTarget() == null || a.getType() == null)
                break;
            if (a.getName() != null && a.getType().equals(SlotAction.Type.ABILITY) && a.isReady()) {
                if (a.activate()) {
                    Execution.delayUntil(() -> Players.getLocal().getTarget() == null, 1000,1600);
                }
            }
        }
    }

    @Override
    public void run() {
        if (this.validate()) {
            this.execute();
        }
    }
}
