package scripts.MassFighter.Framework;

import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.region.Players;
import scripts.MassFighter.GUI.Settings;

import java.util.ArrayList;
import java.util.List;


public class UserProfile {

    public Settings settings;
    private String profileName;
    private String[] npcNames;
    private String[] lootNames;
    private String[] alchLoot;
    private String[] notepaperLoot;
    private List<XMLCoordinate> fightAreaCoords = new ArrayList<>();
    private List<XMLCoordinate> bankAreaCoords = new ArrayList<>();

    public String[] getAlchLoot() {
        return alchLoot;
    }

    public void setAlchLoot(String[] loot) {
        alchLoot = loot;
    }

    public String[] getNotepaperLoot() {
        return notepaperLoot;
    }

    public void setNotepaperLoot(String[] loot) {
        notepaperLoot = loot;
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

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String name) {
        profileName = name;
    }

    public String[] getNpcNames() {
        return npcNames;
    }

    public void setNpcNames(String[] names) {
        npcNames = names;
    }

    public String[] getLootNames() {
        String[] lowercaseLoot = new String[lootNames.length];
        for (int i = 0; i < lootNames.length; i++) {
            lowercaseLoot[i] = lootNames[i].toLowerCase();
        }
        return lowercaseLoot;
    }

    public void setLootNames(String[] names) {
        lootNames = names;
    }

    public void setFightAreaCoords(List<Coordinate> coords) {
        for (Coordinate c : coords) {
            XMLCoordinate xmlCoordinate = new XMLCoordinate(c);
            fightAreaCoords.add(xmlCoordinate);
        }
    }

    public void setBankAreaCoords(List<Coordinate> coords) {
        for (Coordinate c : coords) {
            XMLCoordinate xmlCoordinate = new XMLCoordinate(c);
            bankAreaCoords.add(xmlCoordinate);
        }
    }


}
