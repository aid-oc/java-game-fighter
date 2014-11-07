package util;

import com.runemate.game.api.hybrid.entities.Actor;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.details.Locatable;
import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;
import com.runemate.game.api.hybrid.region.Banks;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.Execution;
import scripts.MassFighter.Data.Food;

import java.util.concurrent.Callable;

/**
 * Created by Aidan on 05/11/2014.
 */
public class Functions {

    /**
     * Returns a random tree that is located near to te player
     * @param typeName the in-game name of the tree
     * @return randomly selected local tree
     */
    public static GameObject getNearestGameObjRand(String typeName) {
        return GameObjects.newQuery().names(typeName).results().sortByDistance().limit(3).random();
    }


    /**
     * Returns a random nearby npc with the given name
     * @param npcName
     * @return Npc npc
     */
    public static Npc getNearestNpcRand(String npcName) {
        return Npcs.newQuery().names(npcName).results().sortByDistance().limit(4).random();
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

    /**
     * Waits until a condition is met or the method times out
     * @param condition
     * @param timeout
     * @return boolean : success
     */
    public static Boolean waitFor(final Boolean condition, int timeout) {
        return Execution.delayUntil(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return condition;
            }
        }, timeout);
    }

    /**
     * Returns true if a player is idle and not interacting
     * @param actor
     * @return boolean character is not busy
     */
    public static Boolean isInteracting(Actor actor) {
        return (actor.getAnimationId() != -1);
    }

    /**
     * Returns true if the player has food and is above or equal to the health percetnage given
     * @param food
     * @param healthyPercentage
     * @return boolean can fight
     */
    public static Boolean canPlayerFight(Food food, double healthyPercentage) {
        return Inventory.containsAnyOf(food.getId())
                && Health.getCurrent() / Health.getMaximum() * 100 >= healthyPercentage;
    }

    /**
     * Returns true if the player is above a set health percentage
     * @param healthyPercentage
     * @return
     */
    public static Boolean canPlayerFight(double healthyPercentage) {
        return Health.getCurrent() / Health.getMaximum() * 100 >= healthyPercentage;
    }

}
