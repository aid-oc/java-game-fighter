package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceComponent;
import com.runemate.game.api.hybrid.local.hud.interfaces.Interfaces;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.queries.InterfaceComponentQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.InterfaceComponentQueryResults;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.rs3.local.hud.interfaces.LootInventory;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import helpers.Movement;
import scripts.MassFighter.Framework.Methods;
import scripts.MassFighter.MassFighter;

import java.util.Arrays;
import java.util.List;

/**
 * ozzy.
 */
public class LootMenu extends Task {

    private List<String> selectedLoot = Arrays.asList(MassFighter.userProfile.getLootNames());

    private InterfaceComponentQueryBuilder lootAllButton = Interfaces.newQuery().texts("Loot All");
    private InterfaceComponentQueryBuilder lootAvailable = Interfaces.newQuery().containers(1622);

    public boolean validate() {
        return LootInventory.isOpen();
    }

    @Override
    public void execute() {
        SpriteItemQueryResults lootOnInventory = LootInventory.newQuery().filter(new Filter<SpriteItem>() {
            @Override
            public boolean accepts(SpriteItem spriteItem) {
                return selectedLoot.contains(spriteItem.getDefinition().getName().toLowerCase()) && MassFighter.methods.hasRoomForItem(spriteItem);
            }
        }).results();
        if (!lootOnInventory.isEmpty()) {
            Methods.out("Here 2");
            if (LootInventory.getItems().equals(lootOnInventory)) {
                Methods.out("Here 1");
                InterfaceComponentQueryResults<InterfaceComponent> buttonResults = lootAllButton.results();
                if (!buttonResults.isEmpty() && buttonResults.first() != null) {
                    InterfaceComponent button = buttonResults.first();
                    if (button.click()) {
                        Execution.delayUntil(() -> !LootInventory.isOpen(), 1000, 2000);
                    }
                }
                // Appears to not work currently
                /*
                if (LootInventory.takeAll()) {
                    Execution.delayUntil(() -> !LootInventory.isOpen(), 1000, 2000);
                }
                */
            } else {
                Methods.out("Here");
                InterfaceComponentQueryResults<InterfaceComponent> availableLootInMenu = lootAvailable.results();
                if (!availableLootInMenu.isEmpty()) {
                    availableLootInMenu.shuffle();
                    availableLootInMenu.stream().filter(component -> component.getActions() != null && !component.getActions().isEmpty()).forEach(component -> {
                        String action = component.getActions().get(0);
                        if (action != null) {
                            for (SpriteItem item : lootOnInventory) {
                                String itemName = item.getDefinition().getName();
                                if (action.contains(itemName)) {
                                    if (component.interact("Take " + itemName)) {
                                        Execution.delayUntil(() -> !LootInventory.getItems().contains(item), 2000);
                                    }
                                }
                            }
                        }
                    });
                }
                // Appears to not work currently
                /*
                if (LootInventory.take(lootOnInventory.random())) {
                    Execution.delayUntil(() -> !LootInventory.getItems().equals(lootOnInventory), 1000, 2000);
                }
                */
            }
        } else {
            Methods.out("No loot in menu");
            Coordinate returnPoint = MassFighter.userProfile.getFightArea().getRandomCoordinate();
            if (returnPoint != null) {
                Movement.pathToLocatable(returnPoint);
            }
        }
    }
}
