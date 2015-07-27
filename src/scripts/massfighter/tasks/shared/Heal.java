package scripts.massfighter.tasks.shared;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import com.runemate.game.api.script.framework.task.TaskScript;
import scripts.massfighter.framework.Methods;
import scripts.massfighter.gui.Settings;
import scripts.massfighter.MassFighter;

import static scripts.massfighter.framework.Methods.out;

public class Heal extends Task {

    public boolean validate() {
        return Methods.arrayIsValid(Settings.foodNames) && Methods.varyValue(Health.getCurrentPercent(), 5) < Settings.eatPercentage;
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
            }
        } else {
            out("Heal: Attempting to remove heal task");
            TaskScript rootScript = (TaskScript) Environment.getScript();
            rootScript.getTasks().stream().filter(task -> task != null && task instanceof Heal).forEach(task -> {
                out("Heal: Successfully removed heal task");
                rootScript.remove(task);
            });
        }
    }
}
