package scripts.MassFighter.Tasks.RS3;

import com.runemate.game.api.hybrid.entities.definitions.ItemDefinition;
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

    private SpriteItemQueryBuilder getSelectedLoot() {
        SpriteItemQueryBuilder lootOnInventoryQuery = LootInventory.newQuery().filter(Filters.DECLINE_ALL);
        String[] lootNames = Settings.lootNames;
        if (Methods.arrayIsValid(lootNames)) {
            List<String> lootList = Arrays.asList(lootNames);
            lootOnInventoryQuery = LootInventory.newQuery().filter(new Filter<SpriteItem>() {
                @Override
                public boolean accepts(SpriteItem spriteItem) {
                    ItemDefinition itemDefinition;
                    return spriteItem != null && (itemDefinition = spriteItem.getDefinition()) != null
                            && lootList.contains(itemDefinition.getName().toLowerCase()) && Methods.hasRoomForItem(spriteItem);
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
        SpriteItemQueryResults selectedLoot = getSelectedLoot().results();
        if (!selectedLoot.isEmpty()) {
            SpriteItemQueryResults allLoot = LootInventory.getItems();
            if (allLoot.containsAll(selectedLoot) && allLoot.size() == selectedLoot.size()) {
                if (LootInventory.takeAll()) {
                    Execution.delayUntil(() -> !LootInventory.isOpen(), 1000, 2000);
                }
            } else {
                if (LootInventory.take(selectedLoot.random())) {
                    Execution.delayUntil(() -> !LootInventory.getItems().equals(selectedLoot), 1000, 2000);
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
