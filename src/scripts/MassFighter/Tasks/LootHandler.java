package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.entities.GroundItem;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.region.GroundItems;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Data.Settings;
import util.Functions;

import java.util.concurrent.Callable;


public class LootHandler extends Task {

    public boolean validate() {
        return Settings.lootCharms && !GroundItems.newQuery().within(CombatHandler.fightArea).names(Settings.lootChoices).reachable().results().isEmpty()
                && !Inventory.isFull() && !Functions.isBusy();
    }

    @Override
    public void execute() {
        Settings.status = "Loot Handler is Active";

        // This resolved some interaction issues
        if (Camera.getPitch() < .6) {
            Camera.setPitch(.65);
        }

        final GroundItem targetLoot = GroundItems.newQuery().within(CombatHandler.fightArea).names(Settings.lootChoices).reachable().results().nearest();
        if (targetLoot != null) {
            targetLoot.setBackupModel(new int[]{-15,-10,-14}, new int[]{14,-2,14});
            if (Distance.to(targetLoot) > 3) {
                BresenhamPath.buildTo(targetLoot).step(true);
            }
            if (targetLoot.isVisible()) {
                if (targetLoot.interact("Take", targetLoot.getDefinition().getName())) {
                    // Interaction successful, delay until that grounditem is no longer valid (we've picked it up)
                    Execution.delayUntil(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return !GroundItems.getLoaded().contains(targetLoot);
                        }
                    }, 2000,3000);
                }
            } else {
                Camera.turnTo(targetLoot);
            }
        }
    }
}
