package scripts.MassFighter.Profiles;

import com.runemate.game.api.hybrid.location.Area;
import scripts.MassFighter.Framework.CombatProfile;

import java.util.List;

public class Powerfighting extends CombatProfile {

    private String[] npcNames;
    private String[] lootNames;
    private static List<Area> fightAreas;

    public void setNpcNames(String[] names) {
        npcNames = names;
    }

    @Override
    public String[] getNpcNames() {
        return npcNames;
    }

    public void setLootNames(String[] names) {
        lootNames = names;
    }

    @Override
    public String[] getLootNames() {
        return lootNames;
    }

    public void setFightAreas(List<Area> areas) {
        fightAreas = areas;
    }

    @Override
    public List<Area> getFightAreas() {
        return fightAreas;
    }

    @Override
    public String toString() {
        return "Powerfighting";
    }

}
