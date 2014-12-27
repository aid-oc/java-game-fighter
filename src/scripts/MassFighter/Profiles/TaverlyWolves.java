package scripts.MassFighter.Profiles;

import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.basic.PredefinedPath;
import com.runemate.game.api.hybrid.util.collections.Pair;
import scripts.MassFighter.Framework.BankingProfile;
import scripts.MassFighter.Framework.CombatProfile;

import java.util.ArrayList;
import java.util.List;

public class TaverlyWolves extends CombatProfile implements BankingProfile {

    @Override
    public Area getBankArea() {
        return new Area.Polygonal(new Coordinate(2873, 3413, 0), new Coordinate(2872, 3417, 0), new Coordinate(2874, 3421, 0), new Coordinate(2878, 3419, 0), new Coordinate(2878, 3414, 0));
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
        return new String[]{"White wolf", "Adolescent White wolf"};
    }

    @Override
    public String[] getLootNames() {
        return new String[]{"Wolf bones"};
    }

    @Override
    public List<Area> getFightAreas() {
        List<Area> areas = new ArrayList<>();
        areas.add(new Area.Polygonal(new Coordinate(2875, 3431, 0), new Coordinate(2868, 3431, 0), new Coordinate(2864, 3437, 0), new Coordinate(2864, 3442, 0), new Coordinate(2858, 3446, 0), new Coordinate(2854, 3458, 0), new Coordinate(2859, 3460, 0), new Coordinate(2866, 3460, 0), new Coordinate(2875, 3452, 0), new Coordinate(2874, 3445, 0), new Coordinate(2876, 3438, 0)));
        areas.add(new Area.Polygonal(new Coordinate(2857, 3482, 0), new Coordinate(2853, 3486, 0), new Coordinate(2862, 3489, 0), new Coordinate(2863, 3509, 0), new Coordinate(2850, 3512, 0)));
        areas.add(new Area.Polygonal(new Coordinate(2877, 3518, 0), new Coordinate(2886, 3522, 0), new Coordinate(2889, 3509, 0), new Coordinate(2879, 3510, 0)));
        return areas;
    }

    @Override
    public String toString() {
        return "Taverly Wolves - Loots/Banks Wolf Bones for money";
    }
}
