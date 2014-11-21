package scripts.MassFighter.Data;

import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.location.Coordinate;

/**
 * Created by Ozzy on 06/11/2014.
 */
public class Settings {

    // NPC SETTINGS
    public static String chosenNpcName;

    // FOOD SETTINGS
    public static Boolean usingFood;
    public static Food chosenFood;
    public static int eatValue;

    // COMBAT SETTINGS
    public static int chosenFightRegion;
    public static Coordinate startLocation;
    public static Boolean lootCharms;
    public static String[] lootChoices = {"Gold charm", "Green charm", "Crimson charm", "Blue charm", "Elder charm"};
    public static Boolean useAbilities;
    public static Boolean useSoulsplit;

    // MISC
    public static Npc targetNpc;
    public static String status;
    public static String abilityStatus;

}
