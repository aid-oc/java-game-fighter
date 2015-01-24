package scripts.MassFighter.Profiles;

import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.basic.PredefinedPath;
import com.runemate.game.api.hybrid.util.collections.Pair;
import scripts.MassFighter.Framework.BankingProfile;
import scripts.MassFighter.Framework.CombatProfile;

import java.util.ArrayList;
import java.util.List;

public class FaladorGuards extends CombatProfile implements BankingProfile {


    @Override
    public Area getBankArea() {
        return new Area.Polygonal(new Coordinate(2943, 3368, 0), new Coordinate(2950, 3368, 0), new Coordinate(2948, 3370, 0), new Coordinate(2943, 3373, 0));
    }

    @Override
    public PredefinedPath getBankPath() {
        return null;
    }

    @Override
    public Pair<int[], int[]> getForcedModel() {
        return null;
    }

    @Override
    public String[] getNpcNames() {
        return new String[]{"Guard"};
    }

    @Override
    public String[] getLootNames() {
        return new String[]{"Gold", "Iron Bolts", "Grapes", "Bones", "Blood rune", "Nature rune", "Iron platebody", "Steel bar", ""};
    }

    @Override
    public List<Area> getFightAreas() {
        List<Area> areas = new ArrayList<>();
        areas.add( new Area.Polygonal(new Coordinate(2971, 3388, 0), new Coordinate(2971, 3400, 0), new Coordinate(2965, 3404, 0), new Coordinate(2959, 3397, 0), new Coordinate(2961, 3383, 0)));
        return areas;
    }

    @Override
    public String toString() {
        return "Falador Guards";
    }
}
