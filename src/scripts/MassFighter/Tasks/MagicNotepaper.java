package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.MassFighter;

import java.util.Arrays;

import static scripts.MassFighter.MassFighter.userProfile;

public class MagicNotepaper extends Task {

    private SpriteItemQueryBuilder noteableItemQuery = Inventory.newQuery().filter(new Filter<SpriteItem>() {
        @Override
        public boolean accepts(SpriteItem spriteItem) {
            return Arrays.asList(userProfile.getLootNames()).contains(spriteItem.getDefinition().getName().toLowerCase()) && spriteItem.getQuantity() == 1;
        }
    });
    public boolean validate() {
        return MassFighter.settings.useMagicNotepaper && Inventory.contains("Magic notepaper") && !noteableItemQuery.results().isEmpty();
    }

    @Override
    public void execute() {
        SpriteItem targetItem = noteableItemQuery.results().random();
        if (targetItem != null) {
            if (targetItem.click()) {
                SpriteItem notepaper = Inventory.getItems("Magic notepaper").random();
                if (notepaper != null) {
                    if (notepaper.click()) {
                        Execution.delayUntil(() -> !targetItem.isValid(), 1000, 2000);
                    }
                }
            }
        }
    }
}
