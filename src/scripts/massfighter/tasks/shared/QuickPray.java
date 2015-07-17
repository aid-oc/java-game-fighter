package scripts.massfighter.tasks.shared;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceComponent;
import com.runemate.game.api.hybrid.local.hud.interfaces.Interfaces;
import com.runemate.game.api.hybrid.queries.InterfaceComponentQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.InterfaceComponentQueryResults;
import com.runemate.game.api.rs3.local.hud.Powers;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.massfighter.framework.Methods;
import scripts.massfighter.gui.Settings;
import scripts.massfighter.MassFighter;

import static scripts.massfighter.framework.Methods.out;


public class QuickPray extends Task {

    private final InterfaceComponentQueryBuilder quickPrayActivateQuery = Interfaces.newQuery().names("Quick-prayers").actions("Activate");

    @Override
    public boolean validate() {
        return Settings.quickPray && Methods.prayerPercentage() > Settings.prayPercentage &&
                ((Environment.isRS3() && !Powers.Prayer.isQuickPraying())
                || (Environment.isOSRS() && !quickPrayActivateQuery.results().isEmpty()));
    }

    @Override
    public void execute() {
            MassFighter.status = "Quickpray: ON";
            out("PrayerPoints: Turning on quick prayers");
            if (Environment.isRS3()) {
                if (Powers.Prayer.toggleQuickPrayers()) {
                    out("PrayerPoints:(RS3) Successfully toggled quick prayers");
                    Execution.delayUntil(Powers.Prayer::isQuickPraying, 1600, 2000);
                } else {
                    out("PrayerPoints:(RS3) Unsuccessfully toggled quick prayers");
                }
            } else if (Environment.isOSRS()) {
                InterfaceComponentQueryResults<InterfaceComponent> quickPrayResults = quickPrayActivateQuery.results();
                if (!quickPrayResults.isEmpty()) {
                    InterfaceComponent quickButton = quickPrayResults.first();
                    if (quickButton != null) {
                        if (quickButton.click()) {
                            out("PrayerPoints:(OSRS) Successfully toggled quick prayers");
                            Execution.delayUntil(() -> quickPrayActivateQuery.results().isEmpty(), 1600, 2000);
                        } else {
                            out("PrayerPoints:(OSRS) Unsuccessfully toggled quick prayers");
                        }
                    } else {
                        out("PrayerPoints:(OSRS) The quick prayer button is invalid");
                    }
                } else {
                    out("PrayerPoints:(OSRS) Unable to find the quick prayer button");
                }
            }
    }
}
