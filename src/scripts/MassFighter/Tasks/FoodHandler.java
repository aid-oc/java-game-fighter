package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.RuneScape;
import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.MassFighter;

public class FoodHandler extends Task {

    public boolean validate() {
        return MassFighter.useFood && Health.getCurrent() < MassFighter.eatValue;
    }

    @Override
    public void execute() {
        MassFighter.status = "Food Handler is Active";
        // Interacts with a SpriteItem with the name set by the user (chosenFood)
        // This activates if the players health falls below their set threshold
        if (MassFighter.useFood && Inventory.contains(MassFighter.food.getName())) {
            final int startHealth = Health.getCurrent();
            SpriteItem i = Inventory.getItems(MassFighter.food.getName()).random();
            if (i != null) {
                if (i.interact("Eat")) {
                    Execution.delayUntil(() -> Health.getCurrent() != startHealth, 1600,2000);
                }
            }
        } else {
            MassFighter.status = "Paused: out of food";
            RuneScape.logout();
            Environment.getScript().pause();
        }
    }
}
