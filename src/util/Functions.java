package util;

import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.details.Locatable;
import com.runemate.game.api.hybrid.local.Varps;
import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;
import com.runemate.game.api.hybrid.region.Banks;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.rs3.local.hud.Powers;
import scripts.MassFighter.Data.Settings;


/**
 * Created by Aidan on 05/11/2014.
 */

public class Functions {


    public static Boolean readyToFight() {
        if (Settings.useSoulsplit) {
            return  Powers.Prayer.getPoints() > Powers.Prayer.getMaximumPoints() / 2 && isSoulsplitActive();
        } else return Health.getCurrent() > Settings.eatValue;
    }


    /**
     * Returns a random tree that is located near to the player
     * @param objName the in-game name of the tree
     * @param limit limit the amount objects which will be shuffled
     * @return randomly selected local tree
     */
    public static GameObject getNearestGameObjRand(String objName, int limit) {
        return GameObjects.newQuery().names(objName).results().sortByDistance().limit(limit).random();
    }


    /**
     * Returns a random nearby npc with the given name
     * @param npcName
     * @param limit limit the amount objects which will be shuffled
     * @return Npc npc
     */
    public static Npc getNearestNpcRand(String npcName, int limit) {
        return Npcs.newQuery().names(npcName).results().sortByDistance().limit(limit).random();
    }

    /**
     * Returns the nearest bank to the player
     * @return the locatable entity of the bank
     */
    public static LocatableEntity getNearestBank() {
        return Banks.getLoaded().nearestTo(Players.getLocal());
    }

    /**
     * Returns a WebPath to the given location which can then be traversed
     * @param location the locatable of the the desired location
     * @return traversable path to desired location
     */
    public static WebPath constructPathTo(Locatable location) {
        return Traversal.getDefaultWeb().getPathBuilder().buildTo(location);
    }

    /**
     * Constructs a circular area around the given coordinate with a set radius
     * @param cord center coordinate
     * @param radius radius of the circle area
     * @return the circular area
     */
    public static Area constructCircularArea(Coordinate cord, double radius) {
        return new Area.Circular(cord, radius);
    }

    public static Boolean isSoulsplitActive()
    {
        return Varps.getAt(3275).getBits() == 262144;
    }
}
