package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.entities.definitions.ItemDefinition;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.MassFighter;

import java.util.Arrays;

public class MagicNotepaper extends Task {

    private SpriteItemQueryBuilder noteableItemQuery = Inventory.newQuery().filter(new Filter<SpriteItem>() {
        @Override
        public boolean accepts(SpriteItem spriteItem) {
            return !itemIsStacked(spriteItem) && Arrays.asList(MassFighter.userProfile.getNotepaperLoot()).contains(spriteItem.getDefinition().getName().toLowerCase());
        }
    });

    public boolean validate() {
        return Inventory.contains("Magic notepaper") && !noteableItemQuery.results().isEmpty();
    }

    @Override
    public void execute() {
        MassFighter.status = "Using Notepaper";
        SpriteItem targetItem = noteableItemQuery.results().random();
        SpriteItem notepaper = Inventory.getItems("Magic notepaper").first();
        if (notepaper != null && targetItem != null) {
            if (targetItem.click()) {
                if (notepaper.click()) {
                    Execution.delayUntil(() -> Inventory.getQuantity(targetItem.getId()) == 0, 1000, 2000);
                }
            }
        }
    }

    private boolean itemIsStacked(SpriteItem i)
    {
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
