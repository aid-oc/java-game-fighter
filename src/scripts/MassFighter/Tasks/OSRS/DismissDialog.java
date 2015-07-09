package scripts.MassFighter.Tasks.OSRS;

import com.runemate.game.api.hybrid.local.hud.interfaces.ChatDialog;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;


/**
 * ozzy
 */
public class DismissDialog extends Task {

    ChatDialog.Continue continueOption;

    public boolean validate() {
        return (continueOption = ChatDialog.getContinue()) != null;
    }

    @Override
    public void execute() {
        if (continueOption.select()) {
            Execution.delayUntil(() -> continueOption == null, 1000, 3000);
        }
    }
}
