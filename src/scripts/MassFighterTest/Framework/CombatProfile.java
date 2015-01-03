package scripts.MassFighterTest.Framework;

import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.util.collections.Pair;
import scripts.MassFighterTest.Profiles.CaveHorrors;
import scripts.MassFighterTest.Profiles.HillGiants;
import scripts.MassFighterTest.Profiles.LumbridgeCows;
import scripts.MassFighterTest.Profiles.Powerfighting;

import java.util.Arrays;
import java.util.List;

public abstract class CombatProfile {

    abstract public Pair<int[], int[]> getForcedModel();
    abstract public String[] getNpcNames();
    abstract public String[] getLootNames();
    abstract public List<Area> getFightAreas();
    abstract public String toString();

    public static List<CombatProfile> getProfiles() {
        return Arrays.asList(new Powerfighting(), new CaveHorrors(), new LumbridgeCows(), new HillGiants());
    }

}
