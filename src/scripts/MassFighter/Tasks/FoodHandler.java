package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Data.Settings;

import java.util.concurrent.Callable;

public class FoodHandler extends Task {
    @Override
    public boolean validate() {
        return Settings.usingFood && Health.getCurrent() < Settings.eatValue;
    }

    @Override
    public void execute() {
        Settings.status = "Food Handler is Active";

        // Interacts with a SpriteItem with the name set by the user (chosenFood)
        // This activates if the players health falls below their set threshold
        if (Settings.usingFood && Inventory.contains(Settings.chosenFood.getName())) {
            final int startHealth = Health.getCurrent();
            SpriteItem i = Inventory.getRandomItem(Settings.chosenFood.getName());
            if (i != null) {
                if (i.interact("Eat")) {
                    Execution.delayUntil(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return Health.getCurrent() != startHealth;
                        }
                    }, 1600,2000);
                }
            }
        } else {
            Environment.getScript().stop();
        }
    }
}
