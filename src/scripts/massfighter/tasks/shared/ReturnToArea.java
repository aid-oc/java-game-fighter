package scripts.massfighter.tasks.shared;

import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.script.framework.task.Task;
import helpers.Movement;
import scripts.massfighter.framework.Methods;
import scripts.massfighter.gui.Settings;

public class ReturnToArea extends Task {

    private Area area;

    @Override
    public boolean validate() {
        area = Settings.fightArea;
        return area != null && !area.contains(Players.getLocal()) && !Methods.isInCombat();
    }

    @Override
    public void execute() {
        Coordinate returnPoint = area.getRandomCoordinate();
        if (returnPoint != null) {
            if (Distance.to(returnPoint) < 40) {
                Movement.pathToLocatable(returnPoint);
            } else {
                Methods.logout();
            }
        }
    }
}
