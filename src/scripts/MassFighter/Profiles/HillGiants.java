package scripts.MassFighter.Profiles;

import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.basic.PredefinedPath;
import com.runemate.game.api.hybrid.util.collections.Pair;
import scripts.MassFighter.Framework.BankingProfile;
import scripts.MassFighter.Framework.CombatProfile;

import java.util.ArrayList;
import java.util.List;

public class HillGiants extends CombatProfile implements BankingProfile {

    @Override
    public Pair<int[], int[]> getForcedModel() {
        return null;
    }

    @Override
    public String[] getNpcNames() {
        return new String[]{"Hill Giant"};
    }

    @Override
    public String[] getLootNames() {
        return new String[]{"Big bones", "Limpwurt root"};
    }

    @Override
    public List<Area> getFightAreas() {
        List<Area> areas = new ArrayList<>();
        areas.add(new Area.Polygonal(new Coordinate(3120, 9854, 0), new Coordinate(3114, 9853, 0), new Coordinate(3108, 9851, 0), new Coordinate(3107, 9846, 0), new Coordinate(3104, 9841, 0), new Coordinate(3099, 9840, 0), new Coordinate(3095, 9838, 0), new Coordinate(3094, 9828, 0), new Coordinate(3099, 9823, 0), new Coordinate(3105, 9821, 0), new Coordinate(3112, 9823, 0), new Coordinate(3115, 9826, 0), new Coordinate(3119, 9827, 0), new Coordinate(3123, 9831, 0), new Coordinate(3126, 9835, 0), new Coordinate(3125, 9840, 0), new Coordinate(3127, 9844, 0), new Coordinate(3126, 9848, 0)));
        return areas;
    }

    @Override
    public String toString() {
        return "Kills Hill Giants for bones and roots, requires a Brass Key!";
    }


    @Override
    public Area getBankArea() {
        return new Area.Polygonal(new Coordinate(3188, 3432, 0), new Coordinate(3190, 3437, 0), new Coordinate(3181, 3432, 0), new Coordinate(3181, 3446, 0), new Coordinate(3190, 3446, 0));
    }

    @Override
    public PredefinedPath getBankPath() {
        return null;
    }
}
