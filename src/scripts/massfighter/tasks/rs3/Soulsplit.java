package scripts.massfighter.tasks.rs3;

import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.rs3.local.hud.Powers;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.massfighter.framework.Methods;
import scripts.massfighter.gui.Settings;
import scripts.massfighter.MassFighter;

import static scripts.massfighter.framework.Methods.out;

public class Soulsplit extends Task {
    @Override
    public boolean validate() {
        if (Settings.useSoulsplit && Methods.prayerPercentage() >= Settings.prayPercentage) {
            if (Settings.soulsplitPermanent) {
                return (!Powers.Prayer.Curse.SOUL_SPLIT.isActivated());
            } else {
                return ((Methods.varyValue(Health.getCurrentPercent(), 5) < (Settings.soulsplitPercentage) && !Powers.Prayer.Curse.SOUL_SPLIT.isActivated()) || ((100 - Methods.varyValue(Health.getCurrentPercent(), 5) < 10) && Powers.Prayer.Curse.SOUL_SPLIT.isActivated()));
            }
        }
        return false;
    }

    @Override
    public void execute() {
        MassFighter.status = "Toggling Sousplit";
        out("PrayerPoints:(RS3) Turning on soul split");
        boolean soulsplitCurrent = Powers.Prayer.Curse.SOUL_SPLIT.isActivated();
        if (Powers.Prayer.Curse.SOUL_SPLIT.toggle()) {
            out("PrayerPoints:(RS3) Successfully toggled soul split");
            Execution.delayUntil(() -> Powers.Prayer.Curse.SOUL_SPLIT.isActivated() != soulsplitCurrent, 1600, 2000);
        } else {
            out("PrayerPoints:(RS3) Unsuccessfully toggled soul split");
        }
    }
}
