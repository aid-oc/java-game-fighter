package helpers;

import com.runemate.game.api.hybrid.entities.details.Interactable;
import com.runemate.game.api.hybrid.entities.details.Locatable;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.location.navigation.Path;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.location.navigation.cognizant.RegionPath;
import com.runemate.game.api.script.Execution;
import scripts.massfighter.gui.Settings;

import java.util.concurrent.Future;

public class Movement {

    public static void moveToInteractable(Interactable i) {
        if (i != null && i instanceof Locatable) {
            Locatable l = (Locatable) i;
            if (Settings.lockCamera) {
                pathToLocatable(l);
            } else {
                Future<Boolean> cameraMovement = Camera.concurrentlyTurnTo(l);
                if (!cameraMovement.isCancelled()) {
                    Execution.delayUntil(cameraMovement::get, 2000, 4000);
                    if (!i.isVisible()) {
                        pathToLocatable(l);
                    }
                }
            }
        }
    }

    public static void resetCameraPitch() {
        if (Settings.resetPitch && Camera.getPitch() < 1.0) {
            Camera.turnTo(1.0);
        }
    }

    public static void pathToLocatable(Locatable l) {
        if (l != null) {
            Path toLocatable = RegionPath.buildTo(l);
            if (toLocatable == null) toLocatable = Traversal.getDefaultWeb().getPathBuilder().buildTo(l);
            if (toLocatable == null) toLocatable = BresenhamPath.buildTo(l);
            if (toLocatable != null) {
                toLocatable.step(true);
            }
        }
    }

}
