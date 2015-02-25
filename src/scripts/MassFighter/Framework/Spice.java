package scripts.MassFighter.Framework;

import com.runemate.game.api.hybrid.entities.details.Locatable;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.local.hud.interfaces.*;
import com.runemate.game.api.hybrid.location.navigation.Path;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.cognizant.RegionPath;
import com.runemate.game.api.hybrid.queries.results.InterfaceComponentQueryResults;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.Execution;

import java.util.concurrent.Future;

public class Spice {

    /* Randomly chooses to turn the camera or path to a non-visible locatable, with a bias to using web paths
    if the locatable is further than 2 tiles away
    Also has a chance of waiting until camera movement is complete before performing future actions
     */
    public static void moveToLocatable(Locatable l) {
        if (l != null && l.getPosition() != null) {
            double random = Math.random();
            double distance = Distance.to(l);
            if (distance >= Random.nextInt(3, 10)) {
                System.out.println("Locatable is far away");
                if (random > Random.nextDouble(0.2, 0.3)) {
                    pathToLocatable(l);
                } else {
                    System.out.println("Turning camera to locatable (far)");
                    Future<Boolean> movement = Camera.passivelyTurnTo(l);
                    // Have a chance of delaying actions until the movement is complete
                    if (random < 0.2) Execution.delayUntil(movement::isDone, 1000, 2000);
                }
            } else {
                if (random > Random.nextDouble(0.1, 0.2)) {
                    System.out.println("Turning camera to locatable (close)");
                    Future<Boolean> movement = Camera.passivelyTurnTo(l);
                    // Have a chance of delaying actions until the movement is complete
                    if (random > 0.6) Execution.delayUntil(movement::isDone, 1000, 2000);
                } else {
                    System.out.println("Pathing to locatable (close)");
                    pathToLocatable(l);
                }
            }
        } else {
            System.out.println("(Move) Position of locatable is null");
        }
    }

    /* Randomly chooses between generating WebPath's and BresenPath's with a bias for WebPaths when a locatable
    * is a decent distance away */
    public static void pathToLocatable(Locatable l) {
        if (l != null && l.getPosition() != null) {
            Path toLocatable = null;
            double random = Math.random();
            if (Distance.to(l) > Random.nextInt(24, 35)) {
                if (random > Random.nextDouble(0.2, 0.3)) {
                    toLocatable = Traversal.getDefaultWeb().getPathBuilder().buildTo(l);
                }
            } else {
                if (random > Random.nextDouble(0.8, 0.9)) {
                    toLocatable = Traversal.getDefaultWeb().getPathBuilder().buildTo(l);
                } else {
                    toLocatable = RegionPath.buildTo(l);
                }
            }
            if (toLocatable != null) {
                System.out.println("Walking: " + toLocatable.getClass().toString());
                toLocatable.step(true);
            }
        } else {
            System.out.println("(Path) Position of locatable is null");
        }
    }

    public static void hoverOverRandomSkill(Skill skill) {
        if (!InterfaceWindows.getSkills().isOpen()) InterfaceWindows.getSkills().open();
        System.out.println("Looking for: " + skill.toString());
        InterfaceComponentQueryResults skillSquareResults = Interfaces.newQuery().containers(1466).texts(Integer.toString(skill.getCurrentLevel())).results();
        if (!skillSquareResults.isEmpty()) {
            InterfaceComponent skillSquare = (InterfaceComponent)skillSquareResults.first();
            if (skillSquare != null) {
                if (skillSquare.getBounds() != null) {
                    if (skillSquare.getBounds().hover()) {
                        Execution.delay(1000, 4000);
                        if (Random.nextInt(10) >= 5) {
                            InterfaceWindows.getInventory().open();
                        }
                    }
                }
            }
        }
    }

}
