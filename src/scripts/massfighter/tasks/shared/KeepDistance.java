package scripts.massfighter.tasks.shared;

import com.runemate.game.api.hybrid.entities.Actor;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.script.framework.task.Task;
import helpers.Movement;
import scripts.massfighter.framework.Methods;
import scripts.massfighter.gui.Settings;

import java.util.Collections;
import java.util.List;

public class KeepDistance extends Task {

    @Override
    public boolean validate() {
        Player player;
        Actor target;
        return (Settings.keepDistance && (player = Players.getLocal()) != null && (target = player.getTarget()) != null && player.distanceTo(target) < 4);
    }

    @Override
    public void execute() {
        Methods.out("Distance Check: Moving away from target");
        Player player = Players.getLocal();
        if (player != null) {
            Coordinate playerPosition = player.getPosition();
            if (playerPosition != null) {
                Actor target = player.getTarget();
                if (target != null) {
                    Coordinate targetPosition = target.getPosition();
                    if (targetPosition != null) {
                        List<Coordinate> possibleCoords = new Area.Circular(playerPosition, 8).getCoordinates();
                        Collections.shuffle(possibleCoords);
                        if (possibleCoords != null) {
                            for (Coordinate c : possibleCoords) {
                                if (c != null && Distance.between(c, target) >= 4 && c.isReachable()) {
                                    Movement.pathToLocatable(c);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}
