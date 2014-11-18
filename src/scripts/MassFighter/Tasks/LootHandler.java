package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.entities.GroundItem;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.Menu;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.queries.GroundItemQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.hybrid.region.GroundItems;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Data.Settings;

import java.util.concurrent.Callable;


public class LootHandler extends Task {

    // Accessed by the CombatHandler
    public static final GroundItemQueryBuilder suitableGroundItemQuery = GroundItems.newQuery().within(CombatHandler.fightArea)
            .names(Settings.lootChoices).reachable();

    @Override
    public boolean validate() {
        return Settings.lootCharms && !suitableGroundItemQuery.results().isEmpty() && !Inventory.isFull();
    }

    @Override
    public void execute() {
        Settings.status = "Loot Handler is Active";
        GroundItem targetLoot = suitableGroundItemQuery.results().nearest();
        if (targetLoot != null) {
            targetLoot.setBackupModel(new int[]{-11, -11, -12}, new int[]{13, 2, 6});
            final SpriteItemQueryResults initialInventory = Inventory.getItems();
            if (targetLoot.isVisible()) {
                if (targetLoot.interact("Take", targetLoot.getDefinition().getName())) {
                    Execution.delayUntil(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return Inventory.getItems() != initialInventory;
                        }
                    }, 1600, 2000);
                } else if (Menu.isOpen()) {
                    Menu.close();
                } else {
                    Camera.turnTo(targetLoot);
                }
            } else if (Distance.to(targetLoot) > 2) {
                BresenhamPath.buildTo(targetLoot).step(true);
            } else {
                Camera.turnTo(targetLoot);
            }
        }
    }
}
