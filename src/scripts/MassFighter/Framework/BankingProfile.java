package scripts.MassFighter.Framework;

import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.navigation.basic.PredefinedPath;


public interface BankingProfile {

    // Implemented by CombatProfiles which require banking
    // allows me to check instanceof BankingProfile in task validates
    public Area getBankArea();
    public PredefinedPath getBankPath();
}
