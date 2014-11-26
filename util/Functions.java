package util;

import com.runemate.game.api.hybrid.local.Varps;
import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.rs3.local.hud.Powers;
import scripts.MassFighter.Data.Settings;

public class Functions {

    public static Boolean readyToFight() {
        if (Settings.useSoulsplit) {
            return  Powers.Prayer.getPoints() > Powers.Prayer.getMaximumPoints() / 2 && isSoulsplitActive();
        } else return Health.getCurrent() > Settings.eatValue;
    }

    public static Boolean isSoulsplitActive()
    {
        return Varps.getAt(3275).getBits() == 262144;
    }
}
