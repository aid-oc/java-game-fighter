package scripts.MassFighter.Methods;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.RuneScape;
import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import scripts.MassFighter.MassFighter;
import scripts.MassFighter.Tasks.Pray;

import static scripts.MassFighter.MassFighter.settings;
import static scripts.MassFighter.MassFighter.userProfile;

public class Methods  {


    public Boolean isInCombat() {
        return !Npcs.newQuery().within(userProfile.getFightArea()).actions("Attack").reachable().targeting(Players.getLocal()).results().isEmpty();
    }

    public void logout() {
        if (!MassFighter.methods.isInCombat()) {
            if (RuneScape.logout()) {
                MassFighter.status = "No Supplies";
                Environment.getScript().pause();
            }
        }
    }

    public Boolean readyToFight() {
        if (RuneScape.isLoggedIn()) {
            if (settings.useSoulsplit || settings.quickPray) {
                return Pray.getPrayPoints() >= settings.prayValue;
            } else if (settings.useFood) {
                return Inventory.contains(settings.foodName) && Health.getCurrent() >= settings.eatValue;
            } else if (Health.getCurrent() < settings.criticalHitpoints) {
                logout();
                return false;
            }
            return true;
        }
        return false;
    }

}
