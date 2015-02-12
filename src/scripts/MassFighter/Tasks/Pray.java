package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.rs3.local.hud.Powers;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import com.runemate.game.api.script.framework.task.TaskScript;
import scripts.MassFighter.MassFighter;

import static scripts.MassFighter.MassFighter.settings;

public class Pray extends Task {

    final SpriteItemQueryBuilder validPrayerItems = Inventory.newQuery().filter(new Filter<SpriteItem>() {
        @Override
        public boolean accepts(SpriteItem spriteItem) {
            String name = spriteItem.getDefinition().getName();
            return name.contains("Prayer potion") ||
                    name.contains("Prayer flask");
        }
    });
    public boolean validate() {
        return  (Powers.Prayer.getPoints() < settings.prayValue) ||
                (settings.useSoulsplit && !Powers.Prayer.Curse.SOUL_SPLIT.isActivated() && Powers.Prayer.getPoints() >= settings.prayValue)
                || (settings.quickPray && !Powers.Prayer.isQuickPraying() && Powers.Prayer.getPoints() >= settings.prayValue);
    }

    @Override
    public void execute() {

        System.out.println("Need to top up prayer?: " + (Powers.Prayer.getPoints() < settings.prayValue && !validPrayerItems.results().isEmpty()));
        System.out.println("Need to turn on SS?: " + ((settings.useSoulsplit && !Powers.Prayer.Curse.SOUL_SPLIT.isActivated() && Powers.Prayer.getPoints() >= settings.prayValue)));
        System.out.println("Need to turn on quickprayer?: " + (settings.quickPray && !Powers.Prayer.isQuickPraying() && Powers.Prayer.getPoints() >= settings.prayValue));


        if (settings.quickPray && !Powers.Prayer.isQuickPraying() && Powers.Prayer.getPoints() >= settings.prayValue) {
            MassFighter.status = "Quickpray: ON";
            if (Powers.Prayer.toggleQuickPrayers()) {
                Execution.delayUntil(Powers.Prayer::isQuickPraying, 1600, 2000);
            }
        }

        // turn on soulsplit if it is not on
        if (settings.useSoulsplit && !Powers.Prayer.Curse.SOUL_SPLIT.isActivated() && Powers.Prayer.getPoints() >= settings.prayValue) {
            MassFighter.status = "Sousplit: ON";
            if (Powers.Prayer.Curse.SOUL_SPLIT.toggle()) {
                Execution.delayUntil(() -> !Powers.Prayer.Curse.SOUL_SPLIT.isActivated(), 1600,2000);
            }
        }

        // Drinks a prayer pot/flask (starting with flasks)in order to restore prayer points
        // At the moment this occurs if prayer points fall below 50% of the maximum possible amount of points
        // Delays until prayer points have increased or 2s pass
        if (Powers.Prayer.getPoints() < settings.prayValue) {
            if (validPrayerItems.results().isEmpty()) {
                if (settings.exitOnPrayerOut) {
                    MassFighter.methods.logout();
                } else {
                    settings.useSoulsplit = false;
                    settings.quickPray = false;
                    System.out.println("Trying to remove Prayer Handler");
                    TaskScript rootScript = (TaskScript)Environment.getScript();
                    rootScript.getTasks().stream().filter(task -> task != null && task instanceof Pray).forEach(task -> {
                        System.out.println("Removed Prayer Handler");
                        rootScript.remove(task);
                    });
                }
            } else {
                MassFighter.status = "Getting Prayer";
                final int startPP = Powers.Prayer.getPoints();
                final SpriteItem targetPrayerFuel = validPrayerItems.results().random();
                if (targetPrayerFuel != null) {
                    if (targetPrayerFuel.interact("Drink", targetPrayerFuel.getDefinition().getName())) {
                        Execution.delayUntil(() -> Powers.Prayer.getPoints() > startPP, Random.nextInt(1600, 2000));
                    }
                }
            }
        }
    }
}
