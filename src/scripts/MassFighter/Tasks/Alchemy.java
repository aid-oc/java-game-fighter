package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.local.hud.Menu;
import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.osrs.local.hud.interfaces.Magic;
import com.runemate.game.api.rs3.local.hud.Powers;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.ActionBar;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.SlotAction;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.MassFighter;

import java.util.Arrays;

public class Alchemy extends Task {

    private SpriteItemQueryBuilder alchItems = Inventory.newQuery().filter(new Filter<SpriteItem>() {
        @Override
        public boolean accepts(SpriteItem spriteItem) {
            return Arrays.asList(MassFighter.userProfile.getAlchLoot()).contains(spriteItem.getDefinition().getName().toLowerCase());
        }
    });
    private SpriteItemQueryBuilder validStaff = Equipment.newQuery().filter(new Filter<SpriteItem>() {
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
        return MassFighter.userProfile.getAlchLoot() != null && !alchItems.results().isEmpty() && hasAlchReqs();
    }

    @Override
    public void execute() {

        MassFighter.status = "Alching";
        if (Skill.MAGIC.getCurrentLevel() >= 55) {
            if ((Environment.isRS3() && Powers.Magic.Book.getCurrent().equals(Powers.Magic.Book.STANDARD) || (Environment.isOSRS() && Magic.Book.getCurrent().equals(Magic.Book.STANDARD)))) {
                SpriteItem targetItem = alchItems.results().limit(3).random();
                SlotAction highAlch = ActionBar.getFirstAction("High Level Alchemy");
                if (highAlch != null && highAlch.isValid() && highAlch.isActivatable()) {
                    if (highAlch.activate()) {
                        if (targetItem != null) {
                            String itemName = targetItem.getDefinition().getName();
                            int itemCount = Inventory.getQuantity(itemName);
                            if (targetItem.click()) {
                                System.out.println("Alched: " + targetItem.getDefinition().getName());
                                Execution.delayUntil(() -> Inventory.getQuantity(itemName) < itemCount, 2000, 2200);
                            }
                        }
                    }
                } else {
                    if (Powers.Magic.HIGH_LEVEL_ALCHEMY.activate()) {
                        Execution.delay(600, 800);
                        System.out.println("Activated Alchemy");
                        if (targetItem != null) {
                            String itemName = targetItem.getDefinition().getName();
                            int itemCount = Inventory.getQuantity(itemName);
                            if (targetItem.click()) {
                                System.out.println("Alched: " + targetItem.getDefinition().getName());
                                Execution.delayUntil(() -> Inventory.getQuantity(itemName) < itemCount, 2000, 2200);
                            }
                        }
                    } else {
                        if (Menu.isOpen()) Menu.close();
                        System.out.println("Failed to activate high alchemy");
                    }
                }
            }
        }
    }
}
