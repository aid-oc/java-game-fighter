package scripts.massfighter.tasks.shared;

import com.runemate.game.api.hybrid.entities.GroundItem;
import com.runemate.game.api.hybrid.local.hud.Menu;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.queries.GroundItemQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.GroundItems;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.hybrid.util.Filters;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.rs3.local.hud.interfaces.LootInventory;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import helpers.Movement;
import scripts.massfighter.framework.Methods;
import scripts.massfighter.gui.Settings;
import scripts.massfighter.MassFighter;

import java.util.Arrays;
import java.util.List;

import static scripts.massfighter.framework.Methods.out;

public class Loot extends Task {

    private static int failCount = 0;

    public static GroundItemQueryBuilder getLoot()
    {
        GroundItemQueryBuilder lootQuery = GroundItems.newQuery().filter(Filters.DECLINE_ALL);
        Area fightArea = Settings.fightArea;
        String[] lootNames = Settings.lootNames;
        if (fightArea != null) {
            lootQuery = GroundItems.newQuery().within(fightArea).filter(new Filter<GroundItem>() {
                @Override
                public boolean accepts(GroundItem groundItem) {
                    if (Methods.hasRoomForItem(groundItem)) {
                        String itemName = groundItem.getDefinition().getName().toLowerCase();
                        String itemNameNoted = itemName+"*";
                        return ((Settings.lootByValue && Methods.isWorthLooting(groundItem)) || Methods.arrayIsValid(lootNames) &&
                                ((Arrays.asList(lootNames).contains(itemNameNoted) && Methods.itemIsNoted(groundItem)) || Arrays.asList(lootNames).contains(itemName)));
                    }
                    return false;
                }
            }).reachable();
        }
        return lootQuery;
    }


    @Override
    public boolean validate() {
        return (Methods.arrayIsValid(Settings.lootNames) || Settings.lootByValue) && !getLoot().results().isEmpty()
                && (Settings.lootInCombat || Methods.isNotInCombat());
    }

    @Override
    public void execute() {
        MassFighter.status = "Looting";
        out("Loot: We need to loot something");
        LocatableEntityQueryResults<GroundItem> lootResults = getLoot().results();
        GroundItem targetItem = lootResults.sortByDistance().limit(2).random();
        if (targetItem != null) {
            out("Loot: Found a valid item");
            takeGroundItem(targetItem);
        } else {
            out("Loot: Target is null");
        }
        Movement.resetCameraPitch();
    }

    private void takeGroundItem(GroundItem item) {
        int invCount = Inventory.getQuantity();
        String itemName = item.getDefinition().getName();
        if (item.isVisible()) {
            if (item.interact("Take", itemName)) {
                out("Loot: Successful");
                Execution.delayUntil(() -> Inventory.getQuantity() > invCount || LootInventory.isOpen(), 1500, 2000);
            } else {
                out("Loot: Failed to interact, making note of this");
                failCount++;
                if (failCount > 3) {
                    out("Loot: Failure limit reached, changing location");
                    Area.Rectangular itemArea = item.getArea();
                    if (itemArea != null) {
                        List<Coordinate> surroundings = itemArea.getSurroundingCoordinates();
                        if (surroundings != null) {
                            Coordinate target = Random.nextElement(surroundings);
                            if (target != null) {
                                out("Loot: Found new location, resetting failure count");
                                Movement.pathToLocatable(target);
                                failCount = 0;
                            }
                        }
                    }
                }
            }
        } else {
            out("Loot: We need to make the loot visible");
            Movement.moveToInteractable(item);
        }
    }
}
