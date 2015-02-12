package scripts.MassFighter.Framework;

import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.region.Players;
import scripts.MassFighter.GUI.Settings;

import java.util.ArrayList;
import java.util.List;


public class UserProfile {

    public String profileName;
    public String[] npcNames;
    public String[] lootNames;
    public String[] alchLoot;
    public List<XMLCoordinate> fightAreaCoords = new ArrayList<>();
    public List<XMLCoordinate> bankAreaCoords = new ArrayList<>();

    public void setAlchLoot(String[] loot) {
        alchLoot = loot;
    }

    public String[] getAlchLoot() {
        return alchLoot;
    }

    public Area getBankArea() {
        if (bankAreaCoords.isEmpty()) return null;
        List<Coordinate> coords = new ArrayList<>();
        for (XMLCoordinate c : bankAreaCoords) {
            Coordinate coordinate = new Coordinate(c.x, c.y, c.z);
            coords.add(coordinate);
        }
        return new Area.Polygonal(coords.toArray(new Coordinate[coords.size()]));
    }

    public Area getFightArea() {
        if (fightAreaCoords != null && !fightAreaCoords.isEmpty()) {
            List<Coordinate> coords = new ArrayList<>();
            for (XMLCoordinate c : fightAreaCoords) {
                Coordinate coordinate = new Coordinate(c.x, c.y, c.z);
                coords.add(coordinate);
            }
            return new Area.Polygonal(coords.toArray(new Coordinate[coords.size()]));
        }
        return new Area.Circular(Players.getLocal().getPosition(), settings.fightRadius);
    }

    public Settings settings;

    public void setProfileName(String name) {
        profileName = name;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setNpcNames(String[] names) {
        npcNames = names;
    }

    public String[] getNpcNames() {
        return npcNames;
    }

    public void setLootNames(String[] names) {
        lootNames = names;
    }

    public String[] getLootNames() {
        return lootNames;
    }

    public void setFightAreaCoords(List<Coordinate> coords) {
        for (Coordinate c : coords) {
             XMLCoordinate xmlCoordinate = new XMLCoordinate(c);
            fightAreaCoords.add(xmlCoordinate);
        }
    }

    public List<XMLCoordinate> getFightAreaCoords() {
        return fightAreaCoords;
    }

    public void setBankAreaCoords(List<Coordinate> coords) {
        for (Coordinate c : coords) {
            XMLCoordinate xmlCoordinate = new XMLCoordinate(c);
            bankAreaCoords.add(xmlCoordinate);
        }
    }

    public List<XMLCoordinate> getBankAreaCoords() {
        return bankAreaCoords;
    }


}
