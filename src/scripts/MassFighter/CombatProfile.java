package scripts.MassFighter;

import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.navigation.basic.PredefinedPath;
import scripts.MassFighter.Profiles.CaveHorrors;
import scripts.MassFighter.Profiles.LumbridgeCows;

import java.util.Arrays;
import java.util.List;

public abstract class CombatProfile {

    abstract public String[] getNpcNames();
    abstract public String[] getLootNames();
    abstract public List<Area> getFightAreas();
    abstract public Area getBankArea();
    abstract public PredefinedPath getBankPath();

    public static List<CombatProfile> getProfiles() {
        return Arrays.asList(new CaveHorrors(), new LumbridgeCows());
    }

}
