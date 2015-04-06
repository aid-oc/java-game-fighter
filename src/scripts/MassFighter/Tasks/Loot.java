package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.entities.GroundItem;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.queries.GroundItemQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.GroundItems;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import helpers.Movement;
import scripts.MassFighter.MassFighter;


import java.util.Arrays;

import static scripts.MassFighter.MassFighter.settings;
import static scripts.MassFighter.Framework.Methods.*;
import static scripts.MassFighter.MassFighter.userProfile;

public class Loot extends Task {

    private GroundItemQueryBuilder validLoot = GroundItems.newQuery().within(userProfile.getFightArea()).filter(new Filter<GroundItem>() {
        @Override
        public boolean accepts(GroundItem groundItem) {
            if (MassFighter.methods.hasRoomForItem(groundItem)) {
                String itemName = groundItem.getDefinition().getName().toLowerCase();
                return ((settings.lootByValue && MassFighter.methods.isWorthLooting(groundItem)) || Arrays.asList(userProfile.getLootNames()).contains(itemName) || (settings.buryBones && (itemName.contains("bones") || itemName.contains("ashes"))));
            }
            return false;
        }
    });

    @Override
    public boolean validate() {
        return !validLoot.results().isEmpty() && (settings.lootInCombat || !MassFighter.methods.isInCombat());
    }

    @Override
    public void execute() {
        MassFighter.status = "Looting";
        out("Loot: We need to loot something");
        LocatableEntityQueryResults<GroundItem> lootResults = validLoot.results();
        GroundItem targetItem = lootResults.sortByDistance().limit(2).random();
        if (targetItem != null) {
            out("Loot: Found a valid item");
            takeGroundItem(targetItem);
        }
    }

    private Boolean takeGroundItem(GroundItem item) {
        int invCount = Inventory.getQuantity();
        String itemName = item.getDefinition().getName();
        if (item.isVisible()) {
            if (item.interact("Take", itemName)) {
                out("Loot: Successful");
                Execution.delayUntil(() -> Inventory.getQuantity() > invCount, 1500, 2000);
                return true;
            }
        } else {
            out("Loot: We need to move to the item");
            Movement.moveToInteractable(item);
        }
        out("Loot: Unsuccessful");
        return false;
    }
}
