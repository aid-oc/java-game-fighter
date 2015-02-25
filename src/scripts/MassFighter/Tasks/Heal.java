package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import com.runemate.game.api.script.framework.task.TaskScript;
import scripts.MassFighter.MassFighter;

import static scripts.MassFighter.MassFighter.settings;

public class Heal extends Task {

    public boolean validate() {
        return settings.useFood && Health.getCurrent() < settings.eatValue;
    }

    @Override
    public void execute() {
        MassFighter.status = "Eating";
        if (Inventory.contains(settings.foodName)) {
            final int startHealth = Health.getCurrent();
            SpriteItem i = Inventory.getItems(settings.foodName).random();
            if (i != null) {
                if (i.isValid() && i.interact("Eat")) {
                    Execution.delayUntil(() -> Health.getCurrent() != startHealth, 1600,2000);
                    if (Random.nextInt(100) > 90) {
                        execute();
                    }
                }
            }
        } else if (settings.exitOutFood) {
            System.out.println("Food: We're out - exiting");
            MassFighter.methods.logout();
        } else if (MassFighter.userProfile.getBankArea() == null) {
            settings.useFood = false;
            System.out.println("Trying to remove Food Handler");
            TaskScript rootScript = (TaskScript) Environment.getScript();
            rootScript.getTasks().stream().filter(task -> task != null && task instanceof Heal).forEach(task -> {
                System.out.println("Removed Food Handler");
                rootScript.remove(task);
            });
        }
    }
}
