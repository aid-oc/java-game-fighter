package scripts.MassFighter.Framework;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.RuneScape;
import com.runemate.game.api.hybrid.entities.GroundItem;
import com.runemate.game.api.hybrid.entities.Item;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.entities.definitions.ItemDefinition;
import com.runemate.game.api.hybrid.local.hud.interfaces.*;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.hybrid.util.Filters;
import com.runemate.game.api.hybrid.util.calculations.CommonMath;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.osrs.net.Zybez;
import com.runemate.game.api.rs3.local.hud.Powers;
import com.runemate.game.api.rs3.net.GrandExchange;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.AbstractScript;
import scripts.MassFighter.GUI.Settings;
import scripts.MassFighter.MassFighter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public final class Methods {

    public static HashMap<String, Integer> itemPrices = new HashMap<>();

    public static SpriteItemQueryBuilder getFood() {
        SpriteItemQueryBuilder foodQuery = Inventory.newQuery().filter(Filters.DECLINE_ALL);
        String[] foodNames = Settings.foodNames;
        if (foodNames != null && foodNames.length > 0) {
            foodQuery = Inventory.newQuery().filter(new Filter<SpriteItem>() {
                @Override
                public boolean accepts(SpriteItem spriteItem) {
                    return spriteItem != null && spriteItem.getDefinition() != null & Arrays.asList(foodNames).contains(spriteItem.getDefinition().getName().toLowerCase());
                }
            });
        }
        return foodQuery;
    }

    public static boolean arrayIsValid(String[] array) {
        return array != null && array.length > 0;
    }

    public static Boolean isInCombat() {
        Player player = Players.getLocal();
        return RuneScape.isLoggedIn() && player != null && (player.getTarget() != null
                || !Npcs.newQuery().actions("Attack").targeting(player).reachable().results().isEmpty());
    }

    public static void logout() {
        if (!isInCombat()) {
            if (RuneScape.isLoggedIn()) {
                if (RuneScape.logout()) {
                    Execution.delayUntil(() -> !RuneScape.isLoggedIn(), 3000);
                }
            }
        }
        AbstractScript script = Environment.getScript();
        if (script != null) {
            script.stop();
        }

    }

    public static int getPrayPoints() {
        int prayerPoints = -1;
        if (Environment.isRS3()) {
            prayerPoints = Powers.Prayer.getPoints();
        } else {
            InterfaceComponent prayerOrb = Interfaces.getAt(160, 14);
            if (prayerOrb != null && prayerOrb.getText() != null) {
                Integer intValue = Integer.valueOf(prayerOrb.getText());
                if (intValue != null) {
                    prayerPoints = intValue;
                }
            }
        }
        return prayerPoints;
    }

    public static int changeHealthValue(int setValue) {
        int maxHealth = Health.getMaximum();
        float increase = (float)maxHealth/100*Random.nextInt(0, 10);
        int eatValue = setValue +  Math.round(increase);
        return eatValue > maxHealth ? setValue : eatValue;
    }

    public static void out(String s) {
        if (MassFighter.debug) {
            System.out.println("MassFighter - " + s);
        }
    }

    public static Boolean hasRoomForItem(Item item) {
        if (item != null) {
            ItemDefinition itemDefinition = item.getDefinition();
            if (itemDefinition != null) {
                int itemId = itemDefinition.getId();
                int notedId = itemDefinition.getNotedId();
                return !Inventory.isFull() || (itemId == notedId && Inventory.contains(notedId)) || (notedId == -1 && Inventory.contains(itemId));
            }
        }
        out("Loot: We don't have room for that item");
        return false;
    }

    public static boolean itemIsNoted(Item item) {
        if (item != null) {
            ItemDefinition itemDefinition = item.getDefinition();
            if (itemDefinition != null) {
                int itemId = itemDefinition.getId();
                int notedId = itemDefinition.getNotedId();
                return itemId == notedId  || notedId == -1;
            }
        }
        return false;
    }


    public static Boolean isWorthLooting(GroundItem gItem) {
        int itemValue = 0;
        String itemName = gItem.getDefinition().getName();
        int itemId = gItem.getId();
        if (itemName.equals("Coins")) {
            System.out.println("Loot Value: We have found coins, getting quantity..");
            itemValue = gItem.getQuantity();
            System.out.println("Loot Value: Coin worth = " + itemValue);
        } else {
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
        }
        return itemValue >= Settings.lootValue;
    }

}
