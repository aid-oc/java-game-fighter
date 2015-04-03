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
import static scripts.MassFighter.Framework.Methods.*;

public class Heal extends Task {

    public boolean validate() {
        return settings.useFood && Health.getCurrent() < settings.eatValue;
    }

    @Override
    public void execute() {
        MassFighter.status = "Eating";
        out("Heal: We need to use food");
        if (Inventory.contains(settings.foodName)) {
            final int startHealth = Health.getCurrent();
            SpriteItem i = Inventory.getItems(settings.foodName).random();
            if (i != null) {
                if (i.isValid() && i.interact("Eat")) {
                    out("Heal: Ate something nice");
                    Execution.delayUntil(() -> Health.getCurrent() != startHealth, 1600, 2000);
                    if (Random.nextInt(100) > 90) {
                        out("Heal: Eating extra");
                        execute();
                    }
                }
            } else {
                out("Heal: Food item is invalid");
            }
        } else if (settings.exitOutFood) {
            out("Heal: We're out of food, exiting");
            MassFighter.methods.logout();
        } else if (MassFighter.userProfile.getBankArea() == null) {
            settings.useFood = false;
            out("Heal: Attempting to remove heal task");
            TaskScript rootScript = (TaskScript) Environment.getScript();
            rootScript.getTasks().stream().filter(task -> task != null && task instanceof Heal).forEach(task -> {
                out("Heal: Successfully removed heal task");
                rootScript.remove(task);
                MassFighter.getSimpleTasks(rootScript.getTasks());
            });
        }
    }
}
