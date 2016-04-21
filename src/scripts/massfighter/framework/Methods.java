package scripts.massfighter.framework;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.RuneScape;
import com.runemate.game.api.hybrid.entities.*;
import com.runemate.game.api.hybrid.entities.definitions.ItemDefinition;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.local.hud.InteractableRectangle;
import com.runemate.game.api.hybrid.local.hud.interfaces.*;
import com.runemate.game.api.hybrid.queries.NpcQueryBuilder;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.osrs.net.Zybez;
import com.runemate.game.api.rs3.local.hud.Powers;
import com.runemate.game.api.hybrid.net.GrandExchange;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.AbstractScript;
import scripts.massfighter.gui.Settings;
import scripts.massfighter.MassFighter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Predicate;

public final class Methods {

    public static HashMap<String, Integer> itemPrices = new HashMap<>();
    public static NpcQueryBuilder attackingNpcs = Npcs.newQuery().actions("Attack").targeting(Players.getLocal()).filter(new Predicate<Npc>() {
        @Override
        public boolean test(Npc npc) {
            LocatableEntityQueryResults attackers = Players.newQuery().targeting(npc).results();
            Player player = Players.getLocal();
            return attackers.isEmpty() || (player != null && attackers.contains(player) && attackers.size() == 1);
        }
    });
    public static NpcQueryBuilder attackingReachableNpcs = attackingNpcs.reachable();


    public static SpriteItemQueryBuilder getFood() {
        SpriteItemQueryBuilder foodQuery = Inventory.newQuery().filter(o -> false);
        String[] foodNames = Settings.foodNames;
        if (foodNames != null && foodNames.length > 0) {
            foodQuery = Inventory.newQuery().filter(new Predicate<SpriteItem>() {
                @Override
                public boolean test(SpriteItem spriteItem) {
                    ItemDefinition itemDefinition;
                    return spriteItem != null && (itemDefinition = spriteItem.getDefinition()) != null && Arrays.asList(foodNames).contains(itemDefinition.getName().toLowerCase());
                }
            });
        }
        return foodQuery;
    }

    public static boolean arrayIsValid(String[] array) {
        return array != null && array.length > 0;
    }

    public static boolean isNotInCombat() {
        LocatableEntityQueryResults<Npc> targets = Settings.bypassReachable ? attackingNpcs.results() : attackingReachableNpcs.results();
        Player player = Players.getLocal();
        if (player != null) {
            Actor target = player.getTarget();
            if (target == null && player.getAnimationId() == -1) {
                return true;
            } else if (target != null && player.getAnimationId() == -1) {
                Npc nearest = targets.nearest();
                if (nearest != null && (nearest.getTarget() == null || nearest.getHealthGauge() == null)) {
                    return true;
                }
            }
        }
        MassFighter.status = "Fighting";
        return false;
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
            InterfaceComponent prayerOrb = Interfaces.newQuery().filter(new Predicate<InterfaceComponent>() {
                @Override
                public boolean test(InterfaceComponent interfaceComponent) {
                    InteractableRectangle interactableRectangle;
                    return interfaceComponent != null && (interactableRectangle = interfaceComponent.getBounds()) != null &&
                            ((interactableRectangle.getX() == 521 && interactableRectangle.getY() == 101) ||
                            (interactableRectangle.getX() == 5 &&  interactableRectangle.getY() == 97));
                }
            }).results().first();
            if (prayerOrb != null && prayerOrb.getText() != null) {
                String text = prayerOrb.getText();
                if (text.matches("\\d+")) {
                    Integer intValue = Integer.valueOf(prayerOrb.getText());
                    if (intValue != null) {
                        prayerPoints = intValue;
                    }
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
                        itemValue = Zybez.getAveragePrice(itemId);
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
