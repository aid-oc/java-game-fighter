package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.hybrid.util.Filters;
import com.runemate.game.api.rs3.local.hud.interfaces.LootInventory;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import helpers.Movement;
import scripts.MassFighter.Framework.Methods;
import scripts.MassFighter.GUI.Settings;

import java.util.Arrays;
import java.util.List;

/**
 * ozzy.
 */
public class LootMenu extends Task {

    private SpriteItemQueryBuilder getLootOnInventory() {
        SpriteItemQueryBuilder lootOnInventoryQuery = LootInventory.newQuery().filter(Filters.DECLINE_ALL);
        String[] lootNames = Settings.lootNames;
        if (Methods.arrayIsValid(lootNames)) {
            List<String> lootList = Arrays.asList(lootNames);
            lootOnInventoryQuery = LootInventory.newQuery().filter(new Filter<SpriteItem>() {
                @Override
                public boolean accepts(SpriteItem spriteItem) {
                    return spriteItem != null && spriteItem.getDefinition() != null && lootList.contains(spriteItem.getDefinition().getName().toLowerCase()) && Methods.hasRoomForItem(spriteItem);
                }
            });
        }
        return lootOnInventoryQuery;
    }

    public boolean validate() {
        return LootInventory.isOpen();
    }

    @Override
    public void execute() {
        SpriteItemQueryResults lootOnInventory = getLootOnInventory().results();
        if (!lootOnInventory.isEmpty()) {
            if (LootInventory.getItems().containsAll(lootOnInventory) && LootInventory.getItems().size() == lootOnInventory.size()) {
                if (LootInventory.takeAll()) {
                    Execution.delayUntil(() -> !LootInventory.isOpen(), 1000, 2000);
                }
            } else {
                if (LootInventory.take(lootOnInventory.random())) {
                    Execution.delayUntil(() -> !LootInventory.getItems().equals(lootOnInventory), 1000, 2000);
                }
            }
        } else {
            Methods.out("No loot in menu");
            Area fightArea = Settings.fightArea;
            if (fightArea != null) {
                Coordinate returnPoint = fightArea.getRandomCoordinate();
                if (returnPoint != null) {
                    Movement.pathToLocatable(returnPoint);
                }
            }
        }
    }
}
