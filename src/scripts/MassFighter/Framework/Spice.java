package scripts.MassFighter.Framework;

import com.runemate.game.api.hybrid.entities.details.Locatable;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceComponent;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceWindows;
import com.runemate.game.api.hybrid.local.hud.interfaces.Interfaces;
import com.runemate.game.api.hybrid.location.navigation.Path;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.queries.results.InterfaceComponentQueryResults;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.StopWatch;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.Execution;

import java.util.concurrent.Future;

public class Spice implements Runnable {

    public static StopWatch timesSinceLast = new StopWatch();

    /* Randomly chooses to turn the camera or path to a non-visible locatable, with a bias to using web paths
    if the locatable is further than 2 tiles away
    Also has a chance of waiting until camera movement is complete before performing future actions
     */
    public static void moveToLocatable(Locatable l) {

        double random = Math.random();
        double distance = Distance.to(l);
        BresenhamPath toLocatable = null;

        if (distance >= Random.nextInt(3, 10)) {
            System.out.println("Locatable is far away");
            if (random > Random.nextDouble(0.2, 0.3)) {
                System.out.println("Pathing to locatable (far)");
                toLocatable = BresenhamPath.buildTo(l);
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
                toLocatable = BresenhamPath.buildTo(l);
            }
        }
        if (toLocatable != null && (toLocatable.getNext().getPosition().getPlane() == Players.getLocal().getPosition().getPlane())) {
            toLocatable.step(true);
        }

    }

    /* Randomly chooses between generating WebPath's and BresenPath's with a bias for WebPaths when a locatable
    * is a decent distance away */
    public static void pathToLocatable(Locatable l) {
        Path toLocatable = null;
        double random = Math.random();
        if (Distance.to(l) > Random.nextInt(24, 35)) {
            if (random > Random.nextDouble(0.2, 0.3)) {
                toLocatable = Traversal.getDefaultWeb().getPathBuilder().buildTo(l);
            }
        } else {
            if (random > Random.nextDouble(0.8, 0.9)) {
                toLocatable = Traversal.getDefaultWeb().getPathBuilder().buildTo(l);
            }
        }
        if (toLocatable == null) {
            toLocatable = BresenhamPath.buildTo(l);
        }
        if (toLocatable != null) {
            System.out.println("Walking: " + toLocatable.getClass().toString());
            toLocatable.step(true);
        }
    }

    public static void hoverOverCombatSkill(Skill skill) {
        if (!InterfaceWindows.getSkills().isOpen()) {
            InterfaceComponentQueryResults skillSquareResults = Interfaces.newQuery().texts(skill.toString(), "xp", "next level").results();
            if (!skillSquareResults.isEmpty()) {
                InterfaceComponent skillSquare = (InterfaceComponent)skillSquareResults.first();
                if (skillSquare != null) {
                    if (skillSquare.getBounds().hover()) {
                        Execution.delay(Random.nextInt(1000, 2000), Random.nextInt(1000, 5000));
                    }
                }
            }
        }
    }


    // TODO test, not currently in use
    // Just randomly picks skills to hover over at the moment
    @Override
    public void run() {
        System.out.println("(Attempt) Hover Skill");
        if (!timesSinceLast.isRunning()) timesSinceLast.start();
        if (timesSinceLast.getRuntime() > (Random.nextInt(60, 900) * 1000)) {
            double random = Math.random();
            // between a 4-6% chance of happening
            // to do, allow them to increase/reduce chance
            if (random > Random.nextDouble(0.6, 0.7)) {
                System.out.println("Antiban Hovering Activated");
                int index = Random.nextInt(Skill.values().length);
                hoverOverCombatSkill(Skill.values()[index]);
                timesSinceLast.reset();
            }
        }
    }


}
