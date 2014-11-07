package scripts.MassFighter.Data;

import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.location.Coordinate;

/**
 * Created by Aidan on 06/11/2014.
 */
public class Settings {

    /* START USER INPUT VARS */

    // NPC SETTINGS
    public static String chosenNpcName;

    // FOOD SETTINGS
    public static Boolean usingFood;
    public static Food chosenFood;
    public static int eatValue;

    // COMBAT SETTINGS
    public static int chosenFightRegion;
    public static Coordinate startLocation;
    public static Boolean isLooting;
    public static String[] lootChoices;
    public static Boolean useAbilities;

    /* END USER INPUT VARS */

    /* START MISC VARS */

    public static Npc targetNpc;
    public static String status;


    /* END MISC VARS */




}
