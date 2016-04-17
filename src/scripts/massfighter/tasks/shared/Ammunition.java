package scripts.massfighter.tasks.shared;

import com.runemate.game.api.hybrid.entities.definitions.ItemDefinition;
import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;


import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.massfighter.MassFighter;
import scripts.massfighter.framework.Methods;
import scripts.massfighter.gui.Settings;

import java.util.function.Predicate;

public class Ammunition extends Task {

    private final SpriteItemQueryBuilder availableAmmunition = Inventory.newQuery().filter(new Predicate<SpriteItem>() {
        @Override
        public boolean test(SpriteItem spriteItem) {
            String itemName = null;
            if (spriteItem != null) {
                ItemDefinition itemDefinition = spriteItem.getDefinition();
                if (itemDefinition != null) itemName = itemDefinition.getName();
            }
            return itemName != null && (itemName.contains("arrow") || itemName.contains("bolt")) && Inventory.getQuantity(spriteItem.getId()) >= Settings.ammoAmount;
        }
    });

    private SpriteItemQueryBuilder getMatchingAmmunition() {
        SpriteItemQueryBuilder matchingAmmunition = Inventory.newQuery().filter(o -> false);
        if (getCurrentAmmunition() != null && getCurrentAmmunition().getDefinition() != null) {
            String currentAmmoName = getCurrentAmmunition().getDefinition().getName();
            if (currentAmmoName != null) {
                matchingAmmunition = availableAmmunition.filter(item -> item != null && item.getDefinition() != null && item.getDefinition().getName().equals(currentAmmoName) && Inventory.getQuantity(item.getId()) >= Settings.ammoAmount);
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
        return Settings.equipAmmunition && !availableAmmunition.results().isEmpty() && shouldEquip();
    }

    @Override
    public void execute() {
        Methods.out("Ammunition: EQUIPPING START");
        MassFighter.status = "Getting ammo";
        SpriteItem targetAmmo;
        if (getCurrentAmmunition() != null) {
            Methods.out("Ammunition: EQUIPPING MATCHING AMMO");
            targetAmmo = getMatchingAmmunition().results().random();
        } else {
            Methods.out("Ammunition: EQUIPPING RANDOM AMMO");
            targetAmmo = availableAmmunition.results().random();
        }
        if (targetAmmo != null && Inventory.equip(targetAmmo)) {
            Execution.delayUntil(() -> !targetAmmo.isValid(), 2000);
        }
    }

}
