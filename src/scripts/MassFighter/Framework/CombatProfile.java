package scripts.MassFighter.Framework;

import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.util.collections.Pair;
import scripts.MassFighter.Profiles.*;

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
