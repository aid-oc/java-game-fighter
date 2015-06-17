package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.location.Coordinate;
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
            Coordinate returnPoint = MassFighter.userProfile.getFightArea().getRandomCoordinate();
            if (returnPoint != null) {
                Movement.pathToLocatable(returnPoint);
            }
        }
    }
}
