package scripts.MassFighter.Methods;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.RuneScape;
import com.runemate.game.api.hybrid.entities.GroundItem;
import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.queries.GroundItemQueryBuilder;
import com.runemate.game.api.hybrid.region.GroundItems;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.osrs.net.Zybez;
import com.runemate.game.api.rs3.local.hud.Powers;
import com.runemate.game.api.rs3.net.GrandExchange;
import scripts.MassFighter.MassFighter;


import java.util.Arrays;
import java.util.HashMap;

import static scripts.MassFighter.MassFighter.settings;
import static scripts.MassFighter.MassFighter.userProfile;

public class Methods  {

    private HashMap<String, Integer> itemPrices = new HashMap<>();

    public Boolean isInCombat() {
        return !Npcs.newQuery().within(userProfile.getFightArea()).actions("Attack").reachable().targeting(Players.getLocal()).results().isEmpty();
    }

    public void logout() {
        if (!MassFighter.methods.isInCombat()) {
            if (RuneScape.logout()) {
                MassFighter.status = "Paused: Supplies";
                Environment.getScript().pause();
            }
        }
    }

    public Boolean readyToFight() {
        if (RuneScape.isLoggedIn()) {
            if (settings.useSoulsplit || settings.quickPray) {
                return Powers.Prayer.getPoints() >= settings.prayValue;
            } else if (settings.useFood) {
                return Inventory.contains(settings.food.getName()) && Health.getCurrent() >= settings.eatValue;
            } else if (Health.getCurrent() < settings.criticalHitpoints) {
                logout();
                return false;
            }
            return true;
        }
        return false;
    }


    public GroundItemQueryBuilder validLoot = GroundItems.newQuery().within(userProfile.getFightArea()).filter(new Filter<GroundItem>() {
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
                    System.out.println("Looting: " + itemName + "-" + itemName);
                    return true;
                }
            }
            return Arrays.asList(userProfile.getLootNames()).contains(itemName) || (settings.buryBones && (itemName.contains("bones") || itemName.contains("ashes")));
        }
    }).reachable();
}
