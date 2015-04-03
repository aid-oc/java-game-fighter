package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.rs3.local.hud.Powers;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.MassFighter;

import static scripts.MassFighter.Framework.Methods.out;
import static scripts.MassFighter.MassFighter.settings;

public class Soulsplit extends Task {
    @Override
    public boolean validate() {
        if (settings.useSoulsplit && Environment.isRS3() && MassFighter.methods.getPrayPoints() > settings.prayValue) {
            if (settings.soulsplitPermanent) {
                return (!Powers.Prayer.Curse.SOUL_SPLIT.isActivated());
            } else {
                return ((Health.getCurrentPercent() < (settings.soulsplitPercentage+Random.nextInt(5, 10)) && !Powers.Prayer.Curse.SOUL_SPLIT.isActivated()) || (Health.getCurrentPercent() > (90+Random.nextInt(0, 9)) && Powers.Prayer.Curse.SOUL_SPLIT.isActivated()));
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
