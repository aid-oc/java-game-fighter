package scripts.massfighter.tasks.shared;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.definitions.ItemDefinition;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.local.hud.Menu;
import com.runemate.game.api.hybrid.local.hud.MenuItem;
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
import scripts.massfighter.framework.Methods;
import scripts.massfighter.gui.Settings;
import scripts.massfighter.MassFighter;

import java.util.Arrays;
import java.util.List;

import static scripts.massfighter.framework.Methods.out;

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

    private boolean hasValidStaff() {
        SpriteItem weapon = Equipment.getItemIn(Equipment.Slot.WEAPON);
        ItemDefinition itemDefinition;
        if (weapon != null && (itemDefinition = weapon.getDefinition()) != null) {
            String itemName = itemDefinition.getName();
            if (itemName != null) {
                itemName = itemName.toLowerCase();
                return itemName.contains("fire") || itemName.contains("lava") || itemName.contains("mystic");
            }
        }
        return false;
    }


    private boolean hasAlchReqs() {
        return Inventory.containsAnyOf("Nature rune") && (((Inventory.getQuantity("Fire rune") >= 5)) || hasValidStaff());
    }


    public boolean validate() {
        return Skill.MAGIC.getCurrentLevel() >= 55 && Methods.arrayIsValid(Settings.alchLoot) && !getAlchableItems().results().isEmpty() && hasAlchReqs();
    }

    private boolean alchemyIsActivated() {
        List<MenuItem> items = Menu.getItems();
        if (items != null) {
            for (MenuItem i : items) {
                if (i != null && i.getTarget() != null && i.getTarget().toLowerCase().contains("alchemy")) return true;
            }
        }
        return false;
    }

    @Override
    public void execute() {

        MassFighter.status = "Alching";
        SpriteItem alchItem = getAlchableItems().results().limit(3).random();
        out("Alchemy: Not Activated -> Activating");
        if (Environment.isRS3() && Powers.Magic.Book.getCurrent().equals(Powers.Magic.Book.STANDARD)) {
            out("Alchemy: Not Activated -> RS3");
            SlotAction highAlch = ActionBar.getFirstAction("High Level Alchemy");
            if ((highAlch != null && highAlch.isValid() && highAlch.isActivatable() && highAlch.activate()) || Powers.Magic.HIGH_LEVEL_ALCHEMY.activate()) {
                Execution.delayUntil(this::alchemyIsActivated, 800, 1500);
            }
        } else if (Environment.isOSRS() && Magic.Book.getCurrent().equals(Magic.Book.STANDARD)) {
            out("Alchemy: Not Activated -> OSRS");
            if (Magic.HIGH_LEVEL_ALCHEMY.activate()) Execution.delayUntil(this::alchemyIsActivated, 800, 1500);
        }
        out("Alchemy: Activated -> Alching");
        if (alchItem != null) {
            if (alchItem.hover() && alchemyIsActivated() && alchItem.click()) {
                out("Alchemy: Activated -> Clicked item");
                Execution.delayUntil(() -> !alchemyIsActivated(), 500, 1500);
            } else {
                out("Alchemy: Activated -> Failed to click item");
            }
        }
    }
}
