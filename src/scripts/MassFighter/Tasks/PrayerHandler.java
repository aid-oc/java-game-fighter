package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.rs3.local.hud.Powers;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Data.Potion;
import scripts.MassFighter.Data.Settings;
import util.Functions;

import java.util.concurrent.Callable;

public class PrayerHandler extends Task {
    @Override
    public boolean validate() {
        return Settings.useSoulsplit && Powers.Prayer.getPoints() < Powers.Prayer.getMaximumPoints() / 2 || !Functions.isSoulsplitActive();
    }

    @Override
    public void execute() {
        Settings.status = "Prayer Handler is Active";

        if (!Functions.isSoulsplitActive()) {
            if (Powers.Prayer.Curse.SOUL_SPLIT.toggle()) {
                Execution.delayUntil(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return Functions.isSoulsplitActive();
                    }
                }, 1000,2500);
            }
        }

        // Drinks a prayer pot/flask (starting with flasks)in order to restore prayer points
        // At the moment this occurs if prayer points fall below 50% of the maximum possible amount of points
        // Delays until prayer points have increased or 2s pass
        if (Powers.Prayer.getPoints() < Powers.Prayer.getMaximumPoints()/2) {
            if (!Inventory.containsAnyOf(Potion.PRAYER_FLASK.getIds()) && !Inventory.containsAnyOf(Potion.PRAYER_POTION.getIds())) {
                Environment.getScript().stop();
            } else {
                final int startPP = Powers.Prayer.getPoints();
                final SpriteItem targetPrayerFuel;
                if (Inventory.containsAnyOf(Potion.PRAYER_FLASK.getIds())) {
                    targetPrayerFuel = Inventory.getFirstItem(Potion.PRAYER_FLASK.getIds());
                } else {
                    targetPrayerFuel = Inventory.getFirstItem(Potion.PRAYER_POTION.getIds());
                }
                if (targetPrayerFuel != null) {
                    if (targetPrayerFuel.interact("Drink")) {
                        Execution.delayUntil(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                return Powers.Prayer.getPoints() > startPP;
                            }
                        }, 1000,2000);
                    }
                }
            }
        }

    }


}
