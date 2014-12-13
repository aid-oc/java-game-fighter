package scripts.MassFighter.Profiles;

import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.navigation.basic.PredefinedPath;
import com.runemate.game.api.hybrid.region.Players;
import scripts.MassFighter.CombatProfile;

import java.util.ArrayList;
import java.util.List;

public class CaveHorrors extends CombatProfile {

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
        areas.add(new Area.Circular(Players.getLocal().getPosition(), 12));
        return areas;
    }

    @Override
    public Area getBankArea() {
        return null;
    }

    @Override
    public PredefinedPath getBankPath() {
        return null;
    }

    @Override
    public String toString() {
        return "Cave Horrors - Get Black Mask (10)'s!";
    }
}
