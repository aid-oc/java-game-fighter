package scripts.massfighter.tasks.shared;

import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.massfighter.framework.Methods;
import scripts.massfighter.gui.Settings;
import scripts.massfighter.MassFighter;

/**
 * ozzy.
 */
public class Safety extends Task {

    @Override
    public boolean validate() {
        return (outOfFood() && (Settings.foodTeleport || Settings.foodLogout))
                ||  (Health.getCurrent() < Settings.criticalHitpoints && (Settings.healthTeleport || Settings.healthLogout));
    }

    @Override
    public void execute() {
        if ((outOfFood() && Settings.foodTeleport) || (Health.getCurrent() < Settings.criticalHitpoints && Settings.healthTeleport)) {
            MassFighter.status = "Teleporting away!";
            SpriteItemQueryResults validTeleportTabs = Inventory.newQuery().filter(item -> item != null
                    && item.getDefinition() != null && item.getDefinition().getName().toLowerCase().contains("teleport")).results();
            if (!validTeleportTabs.isEmpty()) {
                SpriteItem teleportTab = validTeleportTabs.random();
                if (teleportTab != null && teleportTab.click()) {
                    Execution.delayUntil(() -> !teleportTab.isValid(), 2500, 4000);
                }
            }
        }
        Methods.logout();
    }

    private boolean outOfFood() {
        return Methods.arrayIsValid(Settings.foodNames) && Methods.getFood().results().isEmpty();
    }

    public static void printCurrentValues() {
        System.out.println("-- Player Values -- ");
        System.out.println("-- Health Percentage: " + Health.getCurrentPercent() + " ~ Eating at: " + Settings.eatPercentage);
        System.out.println("-- Prayer Percentage: " + Methods.prayerPercentage() + " ~ Drinking at: " + Settings.prayPercentage);
        System.out.println("-- Safety: Critical HP: " + Settings.criticalHitpoints + " ~ Teleport: " + Settings.healthTeleport + " ~ Logout: " + Settings.healthLogout);
        System.out.println("-- Safety: Got Food?: " + !Methods.getFood().results().isEmpty() + " ~ Teleport: " + Settings.foodTeleport + " ~ Logout: " + Settings.foodLogout);
    }
}
