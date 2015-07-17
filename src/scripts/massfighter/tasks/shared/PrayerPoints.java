package scripts.massfighter.tasks.shared;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import com.runemate.game.api.script.framework.task.TaskScript;
import scripts.massfighter.framework.Methods;
import scripts.massfighter.gui.Settings;

import static scripts.massfighter.framework.Methods.out;


public class PrayerPoints extends Task {

    private final SpriteItemQueryBuilder validPrayerItems = Inventory.newQuery().filter(new Filter<SpriteItem>() {
        @Override
        public boolean accepts(SpriteItem spriteItem) {
            if (spriteItem != null && spriteItem.getDefinition() != null) {
                String name = spriteItem.getDefinition().getName();
                return name.contains("Prayer potion") ||
                        name.contains("Prayer flask");
            }
            return false;
        }
    });

    public boolean validate() {
        return Methods.varyValue(Methods.prayerPercentage(), 5) < Settings.prayPercentage;
    }

    @Override
    public void execute() {

        if (validPrayerItems.results().isEmpty()) {
            out("PrayerPoints: We have no prayer pots");
                out("PrayerPoints: Turning off script prayer usage");
                Settings.useSoulsplit = false;
                Settings.quickPray = false;
                System.out.println("PrayerPoints: Trying to remove pray task");
                TaskScript rootScript = (TaskScript) Environment.getScript();
                rootScript.getTasks().stream().filter(task -> task != null && task instanceof PrayerPoints).forEach(task -> {
                    System.out.println("PrayerPoints: Successfully removed pray task");
                    rootScript.remove(task);
                });
        } else if (Methods.getPrayPoints() != -1) {
            out("PrayerPoints: We have pots, getting prayer points");
            final int startPP = Methods.getPrayPoints();
            final SpriteItem targetPrayerFuel = validPrayerItems.results().random();
            if (targetPrayerFuel != null) {
                if (targetPrayerFuel.interact("Drink", targetPrayerFuel.getDefinition().getName())) {
                    out("PrayerPoints: Successfully used a prayer pot");
                    Execution.delayUntil(() -> Methods.getPrayPoints() > startPP, Random.nextInt(1600, 2000));
                }
            } else {
                out("PrayerPoints: The target prayer pot is invalid");
            }
        } else {
            System.out.println("PrayerPoints: Failed to get OSRS prayer points value");
        }

    }
}
