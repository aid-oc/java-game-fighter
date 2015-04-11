package scripts.MassFighter.Framework;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.RuneScape;
import com.runemate.game.api.hybrid.entities.GroundItem;
import com.runemate.game.api.hybrid.entities.definitions.ItemDefinition;
import com.runemate.game.api.hybrid.local.hud.interfaces.*;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.osrs.net.Zybez;
import com.runemate.game.api.rs3.local.hud.Powers;
import com.runemate.game.api.rs3.net.GrandExchange;
import com.runemate.game.api.script.framework.AbstractScript;
import scripts.MassFighter.MassFighter;

import java.util.Arrays;
import java.util.HashMap;

import static scripts.MassFighter.MassFighter.settings;

public class Methods {

    public HashMap<String, Integer> itemPrices = new HashMap<>();

    public SpriteItemQueryBuilder foodItems = Inventory.newQuery().filter(new Filter<SpriteItem>() {
        @Override
        public boolean accepts(SpriteItem spriteItem) {
            return Arrays.asList(settings.foodNames).contains(spriteItem.getDefinition().getName().toLowerCase());
        }
    });

    public Boolean isInCombat() {
        return RuneScape.isLoggedIn() && !Npcs.newQuery().actions("Attack").targeting(Players.getLocal()).results().isEmpty();
    }

    public void logout() {
        if (!MassFighter.methods.isInCombat()) {
            if (RuneScape.isLoggedIn()) {
                if (RuneScape.logout()) {
                    if (!RuneScape.isLoggedIn()) {
                        MassFighter.status = "Logged you out";
                        AbstractScript runningScript = Environment.getScript();
                        if (runningScript != null) {
                            runningScript.pause();
                        }
                    }
                }
            }
        }
    }

    public int getPrayPoints() {
        if (Environment.isRS3()) {
            return Powers.Prayer.getPoints();
        } else {
            InterfaceComponent prayerValue = Interfaces.getAt(548, 87);
            return prayerValue != null ? Integer.valueOf(prayerValue.getText()) : -1;
        }
    }

    public Boolean readyToFight() {
        if (RuneScape.isLoggedIn()) {
            if (settings.useSoulsplit || settings.quickPray) {
                return getPrayPoints() >= settings.prayValue;
            } else if (settings.useFood) {
                return !foodItems.results().isEmpty() && Health.getCurrent() >= settings.eatValue;
            } else if (Health.getCurrent() < settings.criticalHitpoints) {
                logout();
                return false;
            }
            return true;
        }
        return false;
    }

    public static void out(String s) {
        System.out.println("MassFighter - " + s);
    }

    public Boolean hasRoomForItem(GroundItem groundItem) {
        if (groundItem != null) {
            ItemDefinition itemDefinition = groundItem.getDefinition();
            if (itemDefinition != null) {
                int itemId = itemDefinition.getId();
                int notedId = itemDefinition.getNotedId();
                return !Inventory.isFull() || (itemId == notedId && Inventory.contains(notedId)) || (notedId == -1 && Inventory.contains(itemId));
            }
        }
        out("Loot: We don't have room for that item");
        return false;
    }

    public Boolean isWorthLooting(GroundItem gItem) {
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

}
