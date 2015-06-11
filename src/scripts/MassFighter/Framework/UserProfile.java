package scripts.MassFighter.Framework;

import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.region.Players;
import scripts.MassFighter.GUI.Settings;

public class UserProfile {

    public Settings settings;
    private String[] npcNames;
    private String[] lootNames;
    private String[] alchLoot;
    private String[] notepaperLoot;
    private  final String[] empty = new String[0];

    public String[] getAlchLoot() {
        if (alchLoot != null) {
            return alchLoot;
        } else {
            return empty;
        }
    }

    public void setAlchLoot(String[] loot) {
        alchLoot = loot;
    }

    public String[] getNotepaperLoot() {
        if (notepaperLoot != null) {
            return notepaperLoot;
        } else {
            return empty;
        }
    }

    public void setNotepaperLoot(String[] loot) {
        notepaperLoot = loot;
    }

    public Area getFightArea() {
        return new Area.Circular(Players.getLocal().getPosition(), settings.fightRadius);
    }

    public String[] getNpcNames() {
        return npcNames;
    }

    public void setNpcNames(String[] names) {
        npcNames = names;
    }

    public String[] getLootNames() {
        if (lootNames != null) {
            return lootNames;
        } else {
            return empty;
        }
    }

    public void setLootNames(String[] names) {
        lootNames = names;
    }



}
