package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.local.hud.Menu;
import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.hybrid.util.Filters;
import com.runemate.game.api.osrs.local.hud.interfaces.Magic;
import com.runemate.game.api.rs3.local.hud.Powers;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.ActionBar;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.SlotAction;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Framework.Methods;
import scripts.MassFighter.GUI.Settings;
import scripts.MassFighter.MassFighter;

import java.util.Arrays;

import static scripts.MassFighter.Framework.Methods.out;

public class Alchemy extends Task {

    private SpriteItemQueryBuilder getAlchableItems() {
        SpriteItemQueryBuilder alchableItemQuery = Inventory.newQuery().filter(Filters.DECLINE_ALL);
        String[] alchLootNames = Settings.alchLoot;
        if (Methods.arrayIsValid(alchLootNames)) {
            alchableItemQuery = Inventory.newQuery().filter(new Filter<SpriteItem>() {
                @Override
                public boolean accepts(SpriteItem spriteItem) {
                    return Arrays.asList(alchLootNames).contains(spriteItem.getDefinition().getName().toLowerCase());
                }
            });
        }
        return alchableItemQuery;
    }

    private final SpriteItemQueryBuilder validStaff = Equipment.newQuery().filter(new Filter<SpriteItem>() {
        @Override
        public boolean accepts(SpriteItem spriteItem) {
            if (spriteItem.getDefinition().getEquipmentSlot().equals(Equipment.Slot.WEAPON)) {
                String itemName = spriteItem.getDefinition().getName().toLowerCase();
                return itemName.contains("fire") || itemName.contains("lava") || itemName.contains("mystic");
            }
            return false;
        }
    });

    private boolean hasAlchReqs() {
        return Inventory.containsAnyOf("Nature rune") && (((Inventory.containsAnyOf("Fire rune") && Inventory.getQuantity("Fire rune") >= 5)) || !validStaff.results().isEmpty());
    }


    public boolean validate() {
        return !getAlchableItems().results().isEmpty() && hasAlchReqs();
    }

    @Override
    public void execute() {

        MassFighter.status = "Alching";
        if (Skill.MAGIC.getCurrentLevel() >= 55) {
            if ((Environment.isRS3() && Powers.Magic.Book.getCurrent().equals(Powers.Magic.Book.STANDARD) || (Environment.isOSRS() && Magic.Book.getCurrent().equals(Magic.Book.STANDARD)))) {
                SpriteItem alchItem = getAlchableItems().results().limit(3).random();
                if (alchItem != null) {
                    String targetItemName = alchItem.getDefinition().getName();
                    SlotAction highAlch = ActionBar.getFirstAction("High Level Alchemy");
                    if (highAlch != null && highAlch.isValid() && highAlch.isActivatable()) {
                        if (highAlch.activate()) {
                            out("Alchemy: Spell Activated");
                            int itemCount = Inventory.getQuantity(targetItemName);
                            if (alchItem.click()) {
                                out("Alchemy: Successful cast on " + targetItemName);
                                Execution.delayUntil(() -> Inventory.getQuantity(targetItemName) < itemCount, 2000, 2200);
                            }
                        }
                    } else {
                        if ((Environment.isRS3() && Powers.Magic.HIGH_LEVEL_ALCHEMY.activate()) || (Environment.isOSRS() && Magic.HIGH_LEVEL_ALCHEMY.activate())) {
                            Execution.delay(600, 800);
                            out("Alchemy: Spell Activated");
                            int itemCount = Inventory.getQuantity(targetItemName);
                            if (alchItem.click()) {
                                out("Alchemy: Successful cast on " + targetItemName);
                                Execution.delayUntil(() -> Inventory.getQuantity(targetItemName) < itemCount, 2000, 2200);
                            }
                        } else {
                            if (Menu.isOpen()) Menu.close();
                        }
                    }
                } else {
                    out("Alchemy: Target item is invalid");
                }
            }
        } else {
            out("Alchemy: Tried to alch, but your level is too low");
        }
    }
}
