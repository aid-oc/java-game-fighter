package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Framework.Movement;
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
            Movement.pathToLocatable(returnPoint);
        }

    }
}
