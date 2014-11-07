package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Data.Settings;

import java.util.concurrent.Callable;

/**
 * Created by Aidan on 06/11/2014.
 */
public class Heal extends Task {
    @Override
    public boolean validate() {
        return Health.getCurrent() < Settings.eatValue;
    }

    @Override
    public void execute() {
        Settings.status = "Healing";
        if (Settings.usingFood && Inventory.contains(Settings.chosenFood.getName())) {
            final int startHealth = Health.getCurrent();
            SpriteItem i = Inventory.getRandomItem(Settings.chosenFood.getName());
                if (i.interact("Eat")) {
                    Execution.delayUntil(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return Health.getCurrent() != startHealth;
                        }
                    }, 2000);
                }
            } else {
            System.out.println("No food, low health - exiting");
            Environment.getScript().stop();
        }

    }
}
