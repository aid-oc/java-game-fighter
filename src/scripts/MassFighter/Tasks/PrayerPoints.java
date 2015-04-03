package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import com.runemate.game.api.script.framework.task.TaskScript;
import scripts.MassFighter.MassFighter;

import static scripts.MassFighter.MassFighter.settings;
import static scripts.MassFighter.Framework.Methods.*;

public class PrayerPoints extends Task {

    private final SpriteItemQueryBuilder validPrayerItems = Inventory.newQuery().filter(new Filter<SpriteItem>() {
        @Override
        public boolean accepts(SpriteItem spriteItem) {
            String name = spriteItem.getDefinition().getName();
            return name.contains("Prayer potion") ||
                    name.contains("Prayer flask");
        }
    });

    public boolean validate() {
        return (MassFighter.methods.getPrayPoints() < settings.prayValue);
    }

    @Override
    public void execute() {

        out("PrayerPoints: Need to top up prayer points, current = " + MassFighter.methods.getPrayPoints() + " target = " + settings.prayValue);
        if (validPrayerItems.results().isEmpty()) {
            out("PrayerPoints: We have no prayer pots");
            if (settings.exitOnPrayerOut) {
                out("PrayerPoints: Out of pots, logging out");
                MassFighter.methods.logout();
            } else {
                out("PrayerPoints: Turning off script prayer usage");
                settings.useSoulsplit = false;
                settings.quickPray = false;
                System.out.println("PrayerPoints: Trying to remove pray task");
                TaskScript rootScript = (TaskScript) Environment.getScript();
                rootScript.getTasks().stream().filter(task -> task != null && task instanceof PrayerPoints).forEach(task -> {
                    System.out.println("PrayerPoints: Successfully removed pray task");
                    rootScript.remove(task);
                    MassFighter.getSimpleTasks(rootScript.getTasks());
                });
            }
        } else if (MassFighter.methods.getPrayPoints() != -1) {
            out("PrayerPoints: We have pots, getting prayer points");
            final int startPP = MassFighter.methods.getPrayPoints();
            final SpriteItem targetPrayerFuel = validPrayerItems.results().random();
            if (targetPrayerFuel != null) {
                if (targetPrayerFuel.interact("Drink", targetPrayerFuel.getDefinition().getName())) {
                    out("PrayerPoints: Successfully used a prayer pot");
                    Execution.delayUntil(() -> MassFighter.methods.getPrayPoints() > startPP, Random.nextInt(1600, 2000));
                }
            } else {
                out("PrayerPoints: The target prayer pot is invalid");
            }
        } else {
            System.out.println("PrayerPoints: Failed to get OSRS prayer points value");
        }

    }
}
