package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import com.runemate.game.api.script.framework.task.TaskScript;
import scripts.MassFighter.Framework.BankingProfile;
import scripts.MassFighter.MassFighter;

import static scripts.MassFighter.MassFighter.settings;

public class FoodHandler extends Task {

    public boolean validate() {
        return settings.useFood && Health.getCurrent() <settings.eatValue;
    }

    @Override
    public void execute() {
        MassFighter.status = "Eating";
        // Interacts with a SpriteItem with the name set by the user (chosenFood)
        // This activates if the players health falls below their set threshold
        if (Inventory.contains(settings.food.getName())) {
            final int startHealth = Health.getCurrent();
            SpriteItem i = Inventory.getItems(settings.food.getName()).random();
            if (i != null) {
                if (i.interact("Eat")) {
                    Execution.delayUntil(() -> Health.getCurrent() != startHealth, 1600,2000);
                }
            }
        } else if (settings.exitOutFood) {
            System.out.println("Food: We're out - exiting");
            MassFighter.methods.logout();
        } else if (!(MassFighter.combatProfile instanceof BankingProfile)) {
            settings.useFood = false;
            System.out.println("Trying to remove Food Handler");
            TaskScript rootScript = (TaskScript) Environment.getScript();
            rootScript.getTasks().stream().filter(task -> task != null && task instanceof FoodHandler).forEach(task -> {
                System.out.println("Removed Food Handler");
                rootScript.remove(task);
            });
        }
    }
}
