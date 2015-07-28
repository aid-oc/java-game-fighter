package scripts.massfighter.tasks.shared;

import com.runemate.game.api.hybrid.entities.definitions.ItemDefinition;
import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.massfighter.MassFighter;
import scripts.massfighter.gui.Settings;

import java.util.Arrays;
import java.util.List;

public class Throwing extends Task {

    private List<String> throwableWeapons = Arrays.asList("dart", "knife", "thrownaxe");

    private final SpriteItemQueryBuilder availableThrowingWeapons = Inventory.newQuery().filter(new Filter<SpriteItem>() {
        @Override
        public boolean accepts(SpriteItem spriteItem) {
            String[] itemName = null;
            ItemDefinition itemDefinition = null;
            if (spriteItem != null) {
                itemDefinition = spriteItem.getDefinition();
                if (itemDefinition != null) {
                    itemName = itemDefinition.getName().toLowerCase().split(" ");
                }
            }
            return itemName != null && itemName.length > 1 && throwableWeapons.contains(itemName[1]);
        }
    });

    private boolean throwingIsValid() {
        SpriteItem currentThrowing = Equipment.getItemIn(Equipment.Slot.WEAPON);
        if (currentThrowing != null) {
            ItemDefinition itemDefinition = currentThrowing.getDefinition();
            if (itemDefinition != null) {
                String itemName = itemDefinition.getName();
                if (itemName != null) {
                    return !availableThrowingWeapons.filter(item -> item.getDefinition().getName().equals(itemName) && (Inventory.getQuantity(item.getId()) >= Settings.ammoAmount)).results().isEmpty();
                }
            }
        }
        return false;
    }

    public boolean validate() {
        return Settings.equipAmmunition && !availableThrowingWeapons.results().isEmpty() && (throwingIsValid() || Equipment.getItemIn(Equipment.Slot.WEAPON) == null);
    }

    @Override
    public void execute() {
        MassFighter.status = "Getting thrown ammo";
        SpriteItem targetWeapon;
        if (Equipment.getItemIn(Equipment.Slot.WEAPON) == null) {
            targetWeapon = availableThrowingWeapons.results().random();
        } else {
            targetWeapon = availableThrowingWeapons.filter(item -> item.getDefinition().getName().equals(Equipment.getItemIn(Equipment.Slot.WEAPON).getDefinition().getName())).results().first();
        }
        if (targetWeapon != null && Inventory.equip(targetWeapon)) {
            Execution.delayUntil(() -> !targetWeapon.isValid(), 2000);
        }
    }

}
