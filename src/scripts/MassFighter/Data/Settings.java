package scripts.MassFighter.Data;

import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;

import java.util.*;

/**
 * Created by Ozzy on 06/11/2014.
 */
public class Settings {


    // PROFILE SETTINGS
    public static List<Area> fightAreas = new ArrayList<>();
    public static List<String> chosenNpcNames = new ArrayList<>();
    public static Area profileBankArea = null;

    // FOOD SETTINGS
    public static Boolean usingFood;
    public static Food chosenFood;
    public static int eatValue;

    // COMBAT SETTINGS
    public static int chosenFightRegion;
    public static Coordinate startLocation;
    public static Boolean isLooting;
    public static String[] charms = {"Gold charm", "Green charm", "Crimson charm", "Blue charm", "Elder charm"};
    public static List<String> lootChoices = new ArrayList<>();
    public static Boolean useAbilities;
    public static Boolean useSoulsplit;

    // MISC
    public static String status;
    public static String abilityStatus;

}
