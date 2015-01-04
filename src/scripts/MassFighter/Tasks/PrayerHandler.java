package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.rs3.local.hud.Powers;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.MassFighter;

public class PrayerHandler extends Task {

    final SpriteItemQueryBuilder validPrayerItems = Inventory.newQuery().filter(new Filter<SpriteItem>() {
        @Override
        public boolean accepts(SpriteItem spriteItem) {
            String name = spriteItem.getDefinition().getName();
            return name.contains("Prayer potion") ||
                    name.contains("Prayer flask");
        }
    });
    public boolean validate() {
        if (!MassFighter.useSoulsplit) return false;
        return
                // We need to get more prayer points and we have pots/flasks remaining
                (Powers.Prayer.getPoints() < Powers.Prayer.getMaximumPoints() / 2 && !validPrayerItems.results().isEmpty())
                // We need to turn soulsplit off as we have enough health now
                || (Powers.Prayer.Curse.SOUL_SPLIT.isActivated() && Health.getCurrentPercent() > 80)
                // We need to turn soulsplit on as we are losing health
                || (!Powers.Prayer.Curse.SOUL_SPLIT.isActivated() && Health.getCurrentPercent() < 65 && Powers.Prayer.getPoints() > Powers.Prayer.getMaximumPoints() / 2);
    }

    @Override
    public void execute() {

        // Disable soulsplit if necessary
        if (Powers.Prayer.Curse.SOUL_SPLIT.isActivated() && Health.getCurrentPercent() > 80) {
            MassFighter.status = "Turning off Soulsplit";
            if (Powers.Prayer.Curse.SOUL_SPLIT.toggle()) {
                Execution.delayUntil(() -> !Powers.Prayer.Curse.SOUL_SPLIT.isActivated(), 1600,2000);
            }
        }

        // Enable soulsplit if necessary
        if (!Powers.Prayer.Curse.SOUL_SPLIT.isActivated() && Health.getCurrentPercent() < 65 && Powers.Prayer.getPoints() > Powers.Prayer.getMaximumPoints() / 2) {
            MassFighter.status = "Turning on Soulsplit";
            if (Powers.Prayer.Curse.SOUL_SPLIT.toggle()) {
                Execution.delayUntil(Powers.Prayer.Curse.SOUL_SPLIT::isActivated, 1600,2000);
            }
        }

        // Drinks a prayer pot/flask (starting with flasks)in order to restore prayer points
        // At the moment this occurs if prayer points fall below 50% of the maximum possible amount of points
        // Delays until prayer points have increased or 2s pass
        if (Powers.Prayer.getPoints() < Powers.Prayer.getMaximumPoints()/2) {
            if (validPrayerItems.results().isEmpty() && !(MassFighter.useFood && Inventory.contains(MassFighter.food.getName()))) {
                MassFighter.methods.logout();
            } else {
                MassFighter.status = "Drinking prayer pots/flasks";
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
