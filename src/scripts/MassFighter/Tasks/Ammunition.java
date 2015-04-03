package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.MassFighter;

import static scripts.MassFighter.Framework.Methods.*;

public class Ammunition extends Task {

    private final SpriteItemQueryBuilder playerHasEquippedAmmunition = Equipment.newQuery().filter(new Filter<SpriteItem>() {
        @Override
        public boolean accepts(SpriteItem spriteItem) {
            String spriteItemName = spriteItem.getDefinition().getName().toLowerCase();
            return spriteItemName.contains("arrow") || spriteItemName.contains("bolt");
        }
    });

    private final SpriteItemQueryBuilder inventoryContainsAmmunition = Inventory.newQuery().filter(new Filter<SpriteItem>() {
        @Override
        public boolean accepts(SpriteItem spriteItem) {
            String spriteItemName = spriteItem.getDefinition().getName().toLowerCase();
            return spriteItemName.contains("arrow") || spriteItemName.contains("bolt");
        }
    });


    public boolean validate() {
        return !playerHasEquippedAmmunition.results().isEmpty() && inventoryContainsAmmunition.results().isEmpty();
    }

    @Override
    public void execute() {
        SpriteItem equippedAmmunition = playerHasEquippedAmmunition.results().first();
        if (equippedAmmunition != null) {
            String equippedAmmunitionName = equippedAmmunition.getDefinition().getName();
            SpriteItemQueryResults inventoryAmmunitionResults = inventoryContainsAmmunition.filter(new Filter<SpriteItem>() {
                @Override
                public boolean accepts(SpriteItem spriteItem) {
                    return spriteItem.getDefinition().getName().equals(equippedAmmunitionName);
                }
            }).results();
            if (!inventoryAmmunitionResults.isEmpty()) {
                MassFighter.status = "Equipping Arrows";
                out("Ammunition: Found a valid type");
                SpriteItem targetAmmunition = inventoryAmmunitionResults.first();
                if (targetAmmunition != null && targetAmmunition.interact("Wield")) {
                    out("Ammunition: Equipped the item");
                    Execution.delayUntil(() -> !Inventory.contains(targetAmmunition.getId()), 1500, 2000);
                }
            } else {
                out("Ammunition: This item is not what we have equipped");
            }
        } else {
            out("Ammunition: Can't get info on what we have equipped");
        }
    }

}
