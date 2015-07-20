package scripts.massfighter.framework;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.RuneScape;
import com.runemate.game.api.hybrid.entities.*;
import com.runemate.game.api.hybrid.entities.definitions.ItemDefinition;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.local.hud.interfaces.*;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.hybrid.util.Filters;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.osrs.net.Zybez;
import com.runemate.game.api.rs3.local.hud.Powers;
import com.runemate.game.api.rs3.net.GrandExchange;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.AbstractScript;
import scripts.massfighter.gui.Settings;
import scripts.massfighter.MassFighter;

import java.util.Arrays;
import java.util.HashMap;

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

    public static boolean isNotInCombat() {
        Player player = Players.getLocal();
        Actor target = player.getTarget();
        LocatableEntityQueryResults<Npc> attackingNpcs = Npcs.newQuery().actions("Attack").targeting(player).reachable().results();
        if (target == null || (attackingNpcs.isEmpty() || (attackingNpcs.nearest().getAnimationId() == -1 && attackingNpcs.nearest().getHealthGauge() == null))) {
            return true;
        } else {
            MassFighter.status = "Fighting";
            return false;
        }
    }

    public static void logout() {
        if (isNotInCombat()) {
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

    public static int varyValue(int original, int variation) {
        return original - Random.nextInt(variation);
    }

    public static void out(String s) {
        if (MassFighter.debug) {
            System.out.println("MassFighter - " + s);
        }
    }

    public static boolean hasRoomForItem(Item item) {
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

    public static int prayerPercentage() {
        if (Environment.isOSRS()) {
            float percentage = (float) getPrayPoints() / Skill.PRAYER.getBaseLevel() * 100;
            return Math.round(percentage);
        } else {
            float percentage = (float) getPrayPoints() / Powers.Prayer.getMaximumPoints() * 100;
            return Math.round(percentage);
        }
    }


    public static boolean isWorthLooting(GroundItem gItem) {
        int itemValue = 0;
        String itemName = "";
        if (gItem != null) {
            itemName = gItem.getDefinition().getName();
            int itemId = gItem.getId();
            if (itemName.equals("Coins")) {
                itemValue = gItem.getQuantity();
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
                if (itemIsNoted(gItem))
                {
                    itemValue = itemValue*gItem.getQuantity();
                }
            }
        }
        return (itemName.equals("Coins") && !Settings.restrictCoinValue) || itemValue >= Settings.lootValue;
    }

}
