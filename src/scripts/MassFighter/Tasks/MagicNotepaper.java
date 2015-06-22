package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.entities.definitions.ItemDefinition;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.hybrid.util.Filters;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Framework.Methods;
import scripts.MassFighter.GUI.Settings;
import scripts.MassFighter.MassFighter;

import java.util.Arrays;
import java.util.List;

import static scripts.MassFighter.Framework.Methods.out;

public class MagicNotepaper extends Task {

    private SpriteItemQueryBuilder getNotableItems() {
        SpriteItemQueryBuilder notableItemsQuery = Inventory.newQuery().filter(Filters.DECLINE_ALL);
        String[] notepaperLoot = Settings.notepaperLoot;
        if (Methods.arrayIsValid(notepaperLoot)) {
            List<String> lootList = Arrays.asList(notepaperLoot);
            notableItemsQuery = Inventory.newQuery().filter(new Filter<SpriteItem>() {
                @Override
                public boolean accepts(SpriteItem spriteItem) {
                    return spriteItem != null && spriteItem.getDefinition() != null && itemIsNotStackable(spriteItem) && lootList.contains(spriteItem.getDefinition().getName().toLowerCase());
                }
            });
        }
        return notableItemsQuery;
    }

    public boolean validate() {
        return Inventory.isFull() && Inventory.contains("Magic notepaper") && !getNotableItems().results().isEmpty();
    }

    @Override
    public void execute() {
        MassFighter.status = "Using Notepaper";
        SpriteItem targetItem = getNotableItems().results().random();
        SpriteItem notepaper = Inventory.getItems("Magic notepaper").first();
        if (notepaper != null && targetItem != null) {
            if (targetItem.interact("Use")) {
                out("MagicNotepaper: Used an item");
                if (notepaper.click()) {
                    out("MagicNotepaper: Clicked the notepaper");
                    Execution.delayUntil(() -> Inventory.getQuantity(targetItem.getId()) == 0, 1000, 2000);
                }
            }
        } else {
            out("MagicNotepaper: Invalid items");
        }
    }

    private boolean itemIsNotStackable(SpriteItem i) {
        if (i != null) {
            final ItemDefinition def = i.getDefinition();
            if (def != null) {
                int itemId = def.getId();
                int notedId = def.getNotedId();
                return (itemId == notedId || notedId == -1);
            }
        }
        return false;
    }

}
