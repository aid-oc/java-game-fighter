package scripts.massfighter.tasks.rs3;

import com.runemate.game.api.hybrid.entities.definitions.ItemDefinition;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;


import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.massfighter.framework.Methods;
import scripts.massfighter.gui.Settings;
import scripts.massfighter.MassFighter;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static scripts.massfighter.framework.Methods.out;

public class MagicNotepaper extends Task {

    private SpriteItemQueryBuilder getNotableItems() {
        SpriteItemQueryBuilder notableItemsQuery = Inventory.newQuery().filter(o -> false);
        String[] notepaperLoot = Settings.notepaperLoot;
        if (Methods.arrayIsValid(notepaperLoot)) {
            List<String> lootList = Arrays.asList(notepaperLoot);
            notableItemsQuery = Inventory.newQuery().filter(new Predicate<SpriteItem>() {
                @Override
                public boolean test(SpriteItem spriteItem) {
                    ItemDefinition def;
                    return (spriteItem != null && (def = spriteItem.getDefinition()) != null && !def.stacks() &&
                            lootList.contains(def.getName().toLowerCase()));
                }
            });
        }
        return notableItemsQuery;
    }

    public boolean validate() {
        return Methods.arrayIsValid(Settings.notepaperLoot) && Inventory.isFull() && Inventory.contains("Magic notepaper")
                && !getNotableItems().results().isEmpty();
    }

    @Override
    public void execute() {
        MassFighter.status = "Using Notepaper";
        SpriteItem targetItem = getNotableItems().results().random();
        SpriteItem notepaper = Inventory.getItems("Magic notepaper").first();
        if (notepaper != null && targetItem != null) {
            int notepaperId = notepaper.getId();
            int notepaperCount = Inventory.getQuantity(notepaperId);
            if (targetItem.interact("Use")) {
                out("MagicNotepaper: Used an item");
                if (notepaper.click()) {
                    out("MagicNotepaper: Clicked the notepaper");
                    Execution.delayUntil(() -> Inventory.getQuantity(notepaperId) < notepaperCount, 1000, 2000);
                }
            }
        } else {
            out("MagicNotepaper: Invalid items");
        }
    }

}
