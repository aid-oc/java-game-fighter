package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.util.StopWatch;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Framework.Spice;

import java.util.concurrent.TimeUnit;

public class Distraction extends Task {

    public static StopWatch lastDistraction = new StopWatch();
    public static long nextDistraction = Random.nextInt(30, 900);

    public Distraction() {
        lastDistraction.start();
    }

    @Override
    public boolean validate() {
        // Trys to occur at minimum every 5 minutes, with a 50% chance of activating
        return (nextDistraction-lastDistraction.getRuntime(TimeUnit.SECONDS) <= 0) && Random.nextInt(11) >= 5;
    }

    @Override
    public void execute() {
        lastDistraction.reset();
        Spice.hoverOverRandomSkill(Random.nextElement(Skill.values()));
        nextDistraction = Random.nextInt(30, 900);
    }

    public static long getNext() {
        return nextDistraction-lastDistraction.getRuntime(TimeUnit.SECONDS);
    }

}
