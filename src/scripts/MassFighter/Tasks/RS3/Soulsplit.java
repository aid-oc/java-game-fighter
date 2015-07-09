package scripts.MassFighter.Tasks.RS3;

import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.rs3.local.hud.Powers;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Framework.Methods;
import scripts.MassFighter.GUI.Settings;
import scripts.MassFighter.MassFighter;

import static scripts.MassFighter.Framework.Methods.out;

public class Soulsplit extends Task {
    @Override
    public boolean validate() {
        if (Settings.useSoulsplit && Methods.getPrayPoints() >= Settings.prayValue) {
            if (Settings.soulsplitPermanent) {
                return (!Powers.Prayer.Curse.SOUL_SPLIT.isActivated());
            } else {
                return ((Health.getCurrentPercent() < (Settings.soulsplitPercentage + Random.nextInt(5, 10)) && !Powers.Prayer.Curse.SOUL_SPLIT.isActivated()) || (Health.getCurrentPercent() > (90 + Random.nextInt(0, 9)) && Powers.Prayer.Curse.SOUL_SPLIT.isActivated()));
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
