package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceComponent;
import com.runemate.game.api.hybrid.local.hud.interfaces.Interfaces;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.InterfaceComponentQueryBuilder;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.InterfaceComponentQueryResults;
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
    final InterfaceComponentQueryBuilder quickPrayActivateQuery = Interfaces.newQuery().containers(548).names("Quick-prayers").actions("Activate");
    public boolean validate() {
        return  (getPrayPoints() < settings.prayValue) ||
                (settings.useSoulsplit && Environment.isRS3() && !Powers.Prayer.Curse.SOUL_SPLIT.isActivated())
                || (settings.quickPray && ((Environment.isOSRS() && !Powers.Prayer.isQuickPraying()) || (Environment.isOSRS() && quickPrayActivateQuery.results().isEmpty())) );
    }

    @Override
    public void execute() {

        if (settings.quickPray && getPrayPoints() >= settings.prayValue) {
            MassFighter.status = "Quickpray: ON";
            if (Environment.isRS3() && !Powers.Prayer.isQuickPraying()) {
                if (Powers.Prayer.toggleQuickPrayers()) {
                    Execution.delayUntil(Powers.Prayer::isQuickPraying, 1600, 2000);
                }
            } else if (Environment.isOSRS()) {
                InterfaceComponentQueryResults<InterfaceComponent> quickPrayResults = quickPrayActivateQuery.results();
                if (!quickPrayResults.isEmpty()) {
                    InterfaceComponent quickButton = quickPrayResults.first();
                    if (quickButton != null) {
                        if (quickButton.click()) {
                            Execution.delayUntil(() -> quickPrayActivateQuery.results().isEmpty(), 1600, 2000);
                        }
                    }
                }
            }

        }

        // turn on soulsplit if it is not on
        if (Environment.isRS3() && settings.useSoulsplit && !Powers.Prayer.Curse.SOUL_SPLIT.isActivated() && Powers.Prayer.getPoints() >= settings.prayValue) {
            MassFighter.status = "Sousplit: ON";
            if (Powers.Prayer.Curse.SOUL_SPLIT.toggle()) {
                Execution.delayUntil(() -> !Powers.Prayer.Curse.SOUL_SPLIT.isActivated(), 1600,2000);
            }
        }

        // Drinks a prayer pot/flask (starting with flasks)in order to restore prayer points
        // At the moment this occurs if prayer points fall below 50% of the maximum possible amount of points
        // Delays until prayer points have increased or 2s pass
        if (getPrayPoints() < settings.prayValue) {
            System.out.println("Prayer points: " + getPrayPoints());
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
                        MassFighter.getSimpleTasks(rootScript.getTasks());
                    });
                }
            } else if (getPrayPoints() != -1) {
                MassFighter.status = "Getting Prayer";
                final int startPP = getPrayPoints();
                final SpriteItem targetPrayerFuel = validPrayerItems.results().random();
                if (targetPrayerFuel != null) {
                    if (targetPrayerFuel.interact("Drink", targetPrayerFuel.getDefinition().getName())) {
                        Execution.delayUntil(() -> getPrayPoints() > startPP, Random.nextInt(1600, 2000));
                    }
                }
            } else {
                System.out.println("Failed to get OSRS prayer points");
            }
        }
    }

    private int getPrayPoints() {
        if (Environment.isRS3()) {
            return Powers.Prayer.getPoints();
        } else {
            InterfaceComponent prayerValue = Interfaces.getAt(548, 87);
            return prayerValue != null ? Integer.valueOf(prayerValue.getText()) : -1;
        }
    }
}
