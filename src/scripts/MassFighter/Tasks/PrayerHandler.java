package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.RuneScape;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.rs3.local.hud.Powers;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Data.Settings;
import util.Functions;

public class PrayerHandler extends Task {

    final SpriteItemQueryBuilder validPrayerItems = Inventory.newQuery().filter(new Filter<SpriteItem>() {
        @Override
        public boolean accepts(SpriteItem spriteItem) {
            String name = spriteItem.getDefinition().getName();
            return name.contains("Prayer potion") ||
                    name.contains("Prayer flask");
        }
    });

    @Override
    public boolean validate() {
        return Settings.useSoulsplit && Powers.Prayer.getPoints() < Powers.Prayer.getMaximumPoints() / 2
                || !Functions.isSoulsplitActive();
    }

    @Override
    public void execute() {
        Settings.status = "Prayer Handler is Active";

        // Enable soulsplit if it is not active
        if (!Functions.isSoulsplitActive()) {
            if (Powers.Prayer.Curse.SOUL_SPLIT.toggle()) {
                Execution.delayUntil(Functions::isSoulsplitActive, 1600,2000);
            }
        }

        // Drinks a prayer pot/flask (starting with flasks)in order to restore prayer points
        // At the moment this occurs if prayer points fall below 50% of the maximum possible amount of points
        // Delays until prayer points have increased or 2s pass
        if (Powers.Prayer.getPoints() < Powers.Prayer.getMaximumPoints()/2) {
            if (validPrayerItems.results().isEmpty()) {
                Settings.status = "Paused: out of prayer pots/flasks";
                RuneScape.logout();
                Environment.getScript().pause();
            } else {
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
