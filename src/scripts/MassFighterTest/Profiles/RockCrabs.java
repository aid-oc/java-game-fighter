package scripts.MassFighterTest.Profiles;

import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.util.collections.Pair;
import scripts.MassFighterTest.Framework.CombatProfile;

import java.util.List;

public class RockCrabs extends CombatProfile {

    @Override
    public Pair<int[], int[]> getForcedModel() {
        return null;
    }

    @Override
    public String[] getNpcNames() {
        return new String[]{"Rock Crab", "Rock crab"};
    }

    @Override
    public String[] getLootNames() {
        return new String[]{"Coins"};
    }

    @Override
    public List<Area> getFightAreas() {
        return null;
    }

    @Override
    public String toString() {
        return "Rock Crabs";
    }
}
