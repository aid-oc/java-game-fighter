package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.local.hud.interfaces.ChatDialog;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;


/**
 * ozzy
 */
public class DismissDialog extends Task {

    public boolean validate() {
        return ChatDialog.getContinue() != null;
    }

    @Override
    public void execute() {
        if (ChatDialog.getContinue().select()) {
            Execution.delayUntil(() -> ChatDialog.getContinue() == null, 3000);
        }
    }
}
