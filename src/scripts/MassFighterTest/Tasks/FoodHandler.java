package scripts.MassFighterTest.Tasks;

import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighterTest.MassFighterTest;

public class FoodHandler extends Task {

    public boolean validate() {
        return MassFighterTest.useFood && Health.getCurrent() < MassFighterTest.eatValue;
    }

    @Override
    public void execute() {
        MassFighterTest.status = "Eating";
        // Interacts with a SpriteItem with the name set by the user (chosenFood)
        // This activates if the players health falls below their set threshold
        if (Inventory.contains(MassFighterTest.food.getName())) {
            final int startHealth = Health.getCurrent();
            SpriteItem i = Inventory.getItems(MassFighterTest.food.getName()).random();
            if (i != null) {
                if (i.interact("Eat")) {
                    Execution.delayUntil(() -> Health.getCurrent() != startHealth, 1600,2000);
                }
            }
        } else if (MassFighterTest.exitOutFood) {
            System.out.println("Food: We're out - exiting");
            MassFighterTest.methods.logout();
        } else if (Health.getCurrent() > MassFighterTest.criticalHitpoints) {
            System.out.println("Food: We're out - no longer using food");
            MassFighterTest.useFood = false;
        }
    }
}
