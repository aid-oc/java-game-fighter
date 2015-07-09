package scripts.MassFighter.Tasks.Shared;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import com.runemate.game.api.script.framework.task.TaskScript;
import scripts.MassFighter.Framework.Methods;
import scripts.MassFighter.GUI.Settings;
import scripts.MassFighter.MassFighter;

import static scripts.MassFighter.Framework.Methods.out;

public class Heal extends Task {

    public boolean validate() {
        return Methods.arrayIsValid(Settings.foodNames) && Health.getCurrent() < Methods.changeHealthValue(Settings.eatValue);
    }

    @Override
    public void execute() {
        SpriteItemQueryResults validFoodItems = Methods.getFood().results();
        MassFighter.status = "Eating";
        out("Heal: We need to use food");
        if (!validFoodItems.isEmpty()) {
            final int startHealth = Health.getCurrent();
            SpriteItem i = validFoodItems.random();
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
        } else if (Settings.exitOutFood && Settings.safetyLogout) {
            out("Heal: We're out of food, exiting");
            Methods.logout();
        } else {
            Settings.foodNames = null;
            out("Heal: Attempting to remove heal task");
            TaskScript rootScript = (TaskScript) Environment.getScript();
            rootScript.getTasks().stream().filter(task -> task != null && task instanceof Heal).forEach(task -> {
                out("Heal: Successfully removed heal task");
                rootScript.remove(task);
            });
        }
    }
}
