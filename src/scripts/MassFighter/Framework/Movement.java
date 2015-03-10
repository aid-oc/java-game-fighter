package scripts.MassFighter.Framework;

import com.runemate.game.api.hybrid.entities.details.Locatable;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.location.navigation.Path;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.cognizant.RegionPath;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.hybrid.util.calculations.Random;

public class Movement {

    public static void moveToLocatable(Locatable l) {
        if (l != null) {
            if (Distance.to(l) > Random.nextInt(8, 10)) {
                pathToLocatable(l);
            } else if (Random.nextBoolean()) {
                Camera.turnTo(l);
            } else {
                pathToLocatable(l);
            }
        }
    }

    public static void pathToLocatable(Locatable l) {
        if (l != null) {
            Path toLocatable = RegionPath.buildTo(l);
            if (toLocatable == null) toLocatable = Traversal.getDefaultWeb().getPathBuilder().buildTo(l);
            if (toLocatable != null) {
                toLocatable.step(true);
            }
        }
    }

}
