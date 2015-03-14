package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.GroundItem;
import com.runemate.game.api.hybrid.entities.definitions.ItemDefinition;
import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.GroundItemQueryBuilder;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.hybrid.region.GroundItems;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.osrs.net.Zybez;
import com.runemate.game.api.rs3.net.GrandExchange;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Framework.Movement;
import scripts.MassFighter.MassFighter;

import java.util.Arrays;
import java.util.HashMap;

import static scripts.MassFighter.MassFighter.settings;
import static scripts.MassFighter.MassFighter.userProfile;

public class Loot extends Task {

    public static HashMap<String, Integer> itemPrices = new HashMap<>();

    public static GroundItemQueryBuilder validLoot = GroundItems.newQuery().within(userProfile.getFightArea()).filter(new Filter<GroundItem>() {
        @Override
        public boolean accepts(GroundItem groundItem) {
            String itemName = groundItem.getDefinition().getName().toLowerCase();
            return hasRoomForItem(groundItem) && settings.lootByValue ? isWorthLooting(groundItem) : Arrays.asList(userProfile.getLootNames()).contains(itemName) || (settings.buryBones && (itemName.contains("bones") || itemName.contains("ashes")));
        }
    }).reachable();

    @Override
    public boolean validate() {
        return !validLoot.results().isEmpty() && (settings.lootInCombat || !MassFighter.methods.isInCombat());
    }

    @Override
    public void execute() {
        MassFighter.status = "Looting";
        LocatableEntityQueryResults<GroundItem> lootResults = validLoot.results();
        GroundItem targetItem = lootResults.sortByDistance().limit(2).random();
        if (targetItem != null) {
            String targetItemName = targetItem.getDefinition().getName().toLowerCase();
            if (takeGroundItem(targetItem)) {
                if (targetItemName.contains("arrow") || targetItemName.contains("bolt")) {
                    SpriteItemQueryResults equipmentQuery = Equipment.newQuery().filter(new Filter<SpriteItem>() {
                        @Override
                        public boolean accepts(SpriteItem spriteItem) {
                            return spriteItem.getDefinition().getName().toLowerCase().equals(targetItemName);
                        }
                    }).results();
                    if (!equipmentQuery.isEmpty()) {
                        SpriteItemQueryBuilder equipItems = Inventory.newQuery().filter(new Filter<SpriteItem>() {
                            @Override
                            public boolean accepts(SpriteItem spriteItem) {
                                return spriteItem.getDefinition().getName().toLowerCase().equals(targetItemName);
                            }
                        });
                        if (!equipItems.results().isEmpty()) {
                            SpriteItem equipItem = equipItems.results().random();
                            if (equipItem != null) {
                                if (equipItem.interact("Wield")) {
                                    Execution.delayUntil(() -> equipItems.results().isEmpty(), 1500, 2000);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static Boolean hasRoomForItem(GroundItem groundItem) {
        if (groundItem != null) {
            ItemDefinition itemDefinition = groundItem.getDefinition();
            int itemId = itemDefinition.getId();
            int notedId = itemDefinition.getNotedId();
            return !Inventory.isFull() || (itemId == notedId && Inventory.contains(notedId)) || (notedId == -1 && Inventory.contains(itemId));
        }
        return false;
    }

    /**
     * Returns true if the item is above the specified loot value
    @param gItem The GroundItem to check the value of
     */
    public static Boolean isWorthLooting(GroundItem gItem) {
        int itemValue = 0;
        String itemName = gItem.getDefinition().getName();
        int itemId = gItem.getId();
        if (itemPrices.containsKey(itemName)) {
            itemValue = itemPrices.get(itemName);
        } else {
            if (Environment.isRS3()) {
                GrandExchange.Item item = GrandExchange.lookup(itemId);
                if (item != null) {
                    itemValue = item.getPrice();
                }
            } else {
                itemValue = Zybez.getAveragePrice(itemName);
            }
            itemPrices.put(itemName, itemValue);
        }
        return itemValue >= settings.lootValue;
    }

    /**
     * Returns true if a ground item is successfully looted, otherwise will attempt to relocate to the item
     * and return false.
     * @param item The GroundItem to loot
     * @return Whether the item was successfully looted
     */
    private Boolean takeGroundItem(GroundItem item) {
        int invCount = Inventory.getQuantity();
        String itemName = item.getDefinition().getName();
        if (item.isVisible()) {
            if (item.interact("Take", itemName)) {
                Execution.delayUntil(() -> Inventory.getQuantity() > invCount, 1500,2000);
                return true;
            }
        } else {
            Movement.moveToLocatable(item);
        }
        return false;
    }
}
