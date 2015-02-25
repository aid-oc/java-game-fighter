package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.GroundItem;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.queries.GroundItemQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.GroundItems;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.osrs.net.Zybez;
import com.runemate.game.api.rs3.net.GrandExchange;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Framework.Spice;
import scripts.MassFighter.MassFighter;

import java.util.Arrays;
import java.util.HashMap;

import static scripts.MassFighter.MassFighter.settings;
import static scripts.MassFighter.MassFighter.userProfile;

/**
 * Created by Aidan on 12/02/2015.
 */
public class Loot extends Task {

    public static HashMap<String, Integer> itemPrices = new HashMap<>();
    private String itemName;
    public static GroundItemQueryBuilder validLoot = GroundItems.newQuery().within(userProfile.getFightArea()).filter(new Filter<GroundItem>() {
        @Override
        public boolean accepts(GroundItem groundItem) {
            String itemName = groundItem.getDefinition().getName().toLowerCase();
            if (settings.lootByValue) {
                int itemValue = 0;
                if (itemPrices.containsKey(itemName)) {
                    itemValue = itemPrices.get(itemName);
                } else {
                    if (Environment.isRS3()) {
                        GrandExchange.Item item = GrandExchange.lookup(groundItem.getId());
                        if (item != null) {
                            itemValue = item.getPrice();
                        }
                    } else {
                        itemValue = Zybez.getAveragePrice(itemName);
                    }
                    itemPrices.put(itemName, itemValue);
                }
                if (itemValue >= settings.lootValue) {
                    return true;
                }
            }
            return Arrays.asList(userProfile.getLootNames()).contains(itemName) || (settings.buryBones && (itemName.contains("bones") || itemName.contains("ashes")));
        }
    }).reachable();

    @Override
    public boolean validate() {
        return !Inventory.isFull() && !validLoot.results().isEmpty() && (settings.lootInCombat || !MassFighter.methods.isInCombat());
    }

    @Override
    public void execute() {
        MassFighter.status = "Looting";
        LocatableEntityQueryResults<GroundItem> lootResults = validLoot.results();
        GroundItem targetItem = lootResults.sortByDistance().limit(2).random();
        if (takeGroundItem(targetItem)) {
            System.out.println("Looted: " + itemName);
        }
    }

    private Boolean takeGroundItem(GroundItem item) {
        if (item != null) {
            int invCount = Inventory.getQuantity();
            itemName = item.getDefinition().getName();
            if (item.getVisibility() == 100) {
                if (item.interact("Take", itemName)) {
                    Execution.delayUntil(() -> Inventory.getQuantity() > invCount, 1500,2000);
                    return true;
                }
            } else {
                Spice.moveToLocatable(item);
            }
        }
        return false;
    }
}
