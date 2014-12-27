package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.MassFighter;

public class FoodHandler extends Task {

    public boolean validate() {
        return MassFighter.useFood && Health.getCurrent() < MassFighter.eatValue && Inventory.contains(MassFighter.food.getName());
    }

    @Override
    public void execute() {
        MassFighter.status = "Eating";
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
            MassFighter.methods.logout();
        }
    }
}
