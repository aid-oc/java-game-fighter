package scripts.massfighter.tasks.shared;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.definitions.ItemDefinition;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import com.runemate.game.api.script.framework.task.TaskScript;
import scripts.massfighter.framework.Methods;
import scripts.massfighter.gui.Settings;


import static scripts.massfighter.framework.Methods.out;

public class Antifire extends Task {

    // RS3 Container for potion active = 1485, 51
    public static boolean hasAntifireActive = false;

    private SpriteItemQueryBuilder getAntifires() {
        return Inventory.newQuery().filter(new Filter<SpriteItem>() {
            @Override
            public boolean accepts(SpriteItem spriteItem) {
                ItemDefinition itemDefinition;
                return spriteItem != null && (itemDefinition = spriteItem.getDefinition()) != null && itemDefinition.getName().toLowerCase().contains("antifire");
            }
        });
    }
    public boolean validate() {
        return Settings.useAntifire && !hasAntifireActive;
    }

    @Override
    public void execute() {

        SpriteItemQueryResults antifires = getAntifires().results();
        if (!antifires.isEmpty()) {
            antifires.sort((o1, o2) -> {
                int itemOneDose = Integer.valueOf(o1.getDefinition().getName().replaceAll("[^0-9]", ""));
                int itemTwoDose = Integer.valueOf(o1.getDefinition().getName().replaceAll("[^0-9]", ""));
                return itemOneDose - itemTwoDose;
            });
            SpriteItem idealPotion = antifires.first();
            if (idealPotion != null) {
                String itemName = idealPotion.getDefinition().getName();
                if (idealPotion.interact("Drink")) {
                    Methods.out("Antifire: Now Active");
                    hasAntifireActive = true;
                    Execution.delayUntil(() -> !idealPotion.getDefinition().getName().equals(itemName), 2000);
                }
            }
        } else {
            out("Antifire: Attempting to remove antifire task");
            TaskScript rootScript = (TaskScript) Environment.getScript();
            rootScript.getTasks().stream().filter(task -> task != null && task instanceof Antifire).forEach(task -> {
                out("Antifire: Successfully removed antifire task");
                rootScript.remove(task);
            });
        }


    }
}
