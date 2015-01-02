package scripts.MassFighter.Tasks;

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
        return MassFighter.useSoulsplit && (Powers.Prayer.getPoints() < Powers.Prayer.getMaximumPoints() / 2
                || !Powers.Prayer.Curse.SOUL_SPLIT.isActivated()) && !validPrayerItems.results().isEmpty() ;
    }

    @Override
    public void execute() {
        MassFighter.status = "Drinking prayer pots";

        // Enable soulsplit if it is not active
        if (!Powers.Prayer.Curse.SOUL_SPLIT.isActivated()) {
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
