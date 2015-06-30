package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.entities.definitions.ItemDefinition;
import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.hybrid.util.Filters;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;


public class Ammunition extends Task {

    private final SpriteItemQueryBuilder availableAmmunition = Inventory.newQuery().filter(new Filter<SpriteItem>() {
        @Override
        public boolean accepts(SpriteItem spriteItem) {
            String itemName = null;
            if (spriteItem != null) {
                ItemDefinition itemDefinition = spriteItem.getDefinition();
                if (itemDefinition != null) itemName = itemDefinition.getName();
            }
            return itemName != null && (itemName.contains("arrow") || itemName.contains("bolt"));
        }
    });

    private SpriteItemQueryBuilder getMatchingAmmunition() {
        SpriteItemQueryBuilder matchingAmmunition = Inventory.newQuery().filter(Filters.DECLINE_ALL);
        if (getCurrentAmmunition() != null && getCurrentAmmunition().getDefinition() != null) {
            String currentAmmoName = getCurrentAmmunition().getDefinition().getName();
            if (currentAmmoName != null) {
                matchingAmmunition = availableAmmunition.filter(item -> item != null && item.getDefinition() != null && item.getDefinition().getName().equals(currentAmmoName));
            }
        }
        return matchingAmmunition;
    }

    private SpriteItem getCurrentAmmunition() {
        return Equipment.getItemIn(Equipment.Slot.AMMUNITION);
    }

    private boolean shouldEquip() {
        return (getCurrentAmmunition() != null && !getMatchingAmmunition().results().isEmpty()) || getCurrentAmmunition() == null;
    }


    public boolean validate() {
        return !availableAmmunition.results().isEmpty() && shouldEquip();
    }

    @Override
    public void execute() {
        SpriteItem targetAmmo;
        if (getCurrentAmmunition() != null) {
            targetAmmo = getMatchingAmmunition().results().random();
        } else {
            targetAmmo = availableAmmunition.results().random();
        }
        if (targetAmmo != null && targetAmmo.interact("Wield")) {
            Execution.delayUntil(() -> !targetAmmo.isValid(), 2000);
        }
    }

}
