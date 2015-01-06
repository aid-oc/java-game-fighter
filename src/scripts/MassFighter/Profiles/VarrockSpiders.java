package scripts.MassFighter.Profiles;

import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.navigation.basic.PredefinedPath;
import com.runemate.game.api.hybrid.util.collections.Pair;
import scripts.MassFighter.Framework.BankingProfile;
import scripts.MassFighter.Framework.CombatProfile;

import java.util.List;

public class VarrockSpiders extends CombatProfile implements BankingProfile {

    @Override
    public Pair<int[], int[]> getForcedModel() {
        return null;
    }

    @Override
    public String[] getNpcNames() {
        return new String[0];
    }

    @Override
    public String[] getLootNames() {
        return new String[0];
    }

    @Override
    public List<Area> getFightAreas() {
        return null;
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public Area getBankArea() {
        return null;
    }

    @Override
    public PredefinedPath getBankPath() {
        return null;
    }
}
