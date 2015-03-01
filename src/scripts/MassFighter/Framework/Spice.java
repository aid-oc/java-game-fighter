package scripts.MassFighter.Framework;

import com.runemate.game.api.hybrid.entities.details.Locatable;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.local.hud.interfaces.*;
import com.runemate.game.api.hybrid.location.navigation.Path;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.location.navigation.cognizant.RegionPath;
import com.runemate.game.api.hybrid.queries.results.InterfaceComponentQueryResults;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.Execution;

// Some methods just to add some randomness to the paths etc. the script uses to hopefully reduce patterns between users
public class Spice {

    /* Randomly chooses to turn the camera or path to a non-visible locatable, with a bias to using web paths
    if the locatable is further than 2 tiles away
    Also has a chance of waiting until camera movement is complete before performing future actions
     */
    public static void moveToLocatable(Locatable l) {
        if (l != null && l.getPosition() != null) {
            double distance = Distance.between(Players.getLocal(), l);
            if (distance >= Random.nextInt(5, 10)) {
                if (Random.nextBoolean()) {
                    pathToLocatable(l);
                } else {
                    Camera.turnTo(l);
                }
            } else {
                if (Random.nextBoolean()) {
                    Camera.turnTo(l);
                } else {
                    pathToLocatable(l);
                }
            }
        }
    }

    /* Randomly chooses between generating Web/Region Path's with a bias for Region/Web when a locatable
    * is a decent distance away */
    public static void pathToLocatable(Locatable l) {
        if (l != null && l.getPosition() != null) {
            Path toLocatable;
            double distance = Distance.between(Players.getLocal(), l);
            if (distance > Random.nextInt(15, 20)) {
                toLocatable = Traversal.getDefaultWeb().getPathBuilder().buildTo(l);
            } else {
                toLocatable = RegionPath.buildTo(l);
                if (toLocatable == null) {
                    toLocatable = BresenhamPath.buildTo(l);
                }
            }
            if (toLocatable != null) {
                toLocatable.step(true);
            }
        }
    }

    /* Hovers over a skill with a random chance of switching back to the inventory */
    public static void hoverOverRandomSkill(Skill skill) {
        if (skill != null) {
            if (!InterfaceWindows.getSkills().isOpen()) InterfaceWindows.getSkills().open();
            System.out.println("Looking for: " + skill.toString());
            InterfaceComponentQueryResults skillSquareResults = Interfaces.newQuery().containers(1466).texts(Integer.toString(skill.getCurrentLevel())).results();
            if (!skillSquareResults.isEmpty()) {
                InterfaceComponent skillSquare = (InterfaceComponent) skillSquareResults.first();
                if (skillSquare != null) {
                    if (skillSquare.getBounds() != null) {
                        if (skillSquare.getBounds().hover()) {
                            Execution.delay(1000, 4000);
                            if (Random.nextBoolean()) {
                                InterfaceWindows.getInventory().open();
                            }
                        }
                    }
                }
            }
        }
    }

}
