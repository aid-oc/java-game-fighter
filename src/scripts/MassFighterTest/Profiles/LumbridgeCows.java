package scripts.MassFighterTest.Profiles;

import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.basic.PredefinedPath;
import com.runemate.game.api.hybrid.util.collections.Pair;
import scripts.MassFighterTest.Framework.BankingProfile;
import scripts.MassFighterTest.Framework.CombatProfile;

import java.util.ArrayList;
import java.util.List;

public class LumbridgeCows extends CombatProfile implements BankingProfile {

    @Override
    public Pair<int[], int[]> getForcedModel() {
        return null;
    }

    @Override
    public String[] getNpcNames() {
        return new String[]{"Cow", "Cow calf"};
    }

    @Override
    public String[] getLootNames() {
        return new String[]{"Cowhide"};
    }

    @Override
    public List<Area> getFightAreas() {
        List<Area> areas = new ArrayList<>();
        areas.add(new Area.Polygonal(new Coordinate(3253, 3254, 0), new Coordinate(3252, 3271, 0), new Coordinate(3250, 3274, 0), new Coordinate(3248, 3277, 0), new Coordinate(3245, 3278, 0), new Coordinate(3243, 3280, 0), new Coordinate(3239, 3286, 0), new Coordinate(3241, 3289, 0), new Coordinate(3241, 3293, 0), new Coordinate(3239, 3296, 0), new Coordinate(3240, 3298, 0), new Coordinate(3268, 3299, 0), new Coordinate(3266, 3254, 0)));
        return areas;
    }

    @Override
    public String toString() {
        return "Lumbridge Cows - loots and banks cowhides";
    }

    @Override
    public Area getBankArea() {
        return new Area.Polygonal(new Coordinate(3211, 3260, 0), new Coordinate(3212, 3254, 0), new Coordinate(3217, 3257, 0));
    }

    @Override
    public PredefinedPath getBankPath() {
        return null;
    }


}
