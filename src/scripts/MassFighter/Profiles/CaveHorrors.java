package scripts.MassFighter.Profiles;

import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.util.collections.Pair;
import scripts.MassFighter.Framework.CombatProfile;

import java.util.ArrayList;
import java.util.List;

public class CaveHorrors extends CombatProfile {

    @Override
    public Pair<int[], int[]> getForcedModel() {
        return null;
    }

    @Override
    public String[] getNpcNames() {
        return new String[]{"Cave horror"};
    }

    @Override
    public String[] getLootNames() {
        return new String[]{"Black mask (10)"};
    }

    @Override
    public List<Area> getFightAreas() {
        List<Area> areas = new ArrayList<>();
        areas.add(new Area.Polygonal(new Coordinate(3746, 9375, 0), new Coordinate(3746, 9372, 0), new Coordinate(3741, 9376, 0), new Coordinate(3741, 9370, 0), new Coordinate(3740, 9363, 0), new Coordinate(3732, 9364, 0), new Coordinate(3731, 9360, 0), new Coordinate(3742, 9357, 0), new Coordinate(3743, 9351, 0), new Coordinate(3733, 9349, 0), new Coordinate(3717, 9350, 0), new Coordinate(3716, 9359, 0), new Coordinate(3719, 9368, 0), new Coordinate(3722, 9383, 0), new Coordinate(3721, 9392, 0), new Coordinate(3725, 9402, 0), new Coordinate(3734, 9404, 0), new Coordinate(3740, 9403, 0), new Coordinate(3752, 9401, 0), new Coordinate(3759, 9398, 0), new Coordinate(3761, 9390, 0), new Coordinate(3757, 9384, 0), new Coordinate(3746, 9386, 0)));
        areas.add(new Area.Polygonal(new Coordinate(3781, 9447, 0), new Coordinate(3781, 9464, 0), new Coordinate(3787, 9468, 0), new Coordinate(3790, 9463, 0), new Coordinate(3795, 9465, 0), new Coordinate(3804, 9462, 0), new Coordinate(3802, 9454, 0), new Coordinate(3817, 9451, 0), new Coordinate(3817, 9435, 0), new Coordinate(3827, 9433, 0), new Coordinate(3836, 9423, 0), new Coordinate(3837, 9411, 0), new Coordinate(3822, 9409, 0), new Coordinate(3786, 9410, 0), new Coordinate(3779, 9416, 0), new Coordinate(3779, 9423, 0), new Coordinate(3776, 9438, 0)));
        return areas;
    }

    @Override
    public String toString() {
        return "Cave Horrors - Get Black Mask (10)'s!";
    }
}
