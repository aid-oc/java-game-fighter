package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.script.framework.task.Task;
import helpers.Movement;
import scripts.MassFighter.MassFighter;


public class ReturnToArea extends Task {

    @Override
    public boolean validate() {
        return !MassFighter.userProfile.getFightArea().contains(Players.getLocal()) && !MassFighter.methods.isInCombat();
    }

    @Override
    public void execute() {
        Coordinate returnPoint = MassFighter.userProfile.getFightArea().getRandomCoordinate();
        if (returnPoint != null) {
            if (Distance.to(returnPoint) < 40) {
                Movement.pathToLocatable(returnPoint);
            } else {
                MassFighter.methods.logout();
            }
        }
    }
}
