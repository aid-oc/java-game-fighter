package scripts.MassFighter;

import com.runemate.game.api.client.paint.PaintListener;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.util.StopWatch;
import com.runemate.game.api.hybrid.util.calculations.CommonMath;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.TaskScript;
import scripts.MassFighter.Data.Settings;
import scripts.MassFighter.Tasks.Abilities;
import scripts.MassFighter.Tasks.Fight;
import scripts.MassFighter.Tasks.Heal;

import java.awt.*;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class MassFighter extends TaskScript implements PaintListener {

    private static MassGUI ui;
    private final StopWatch runningTime = new StopWatch();
    private final NumberFormat numberFormat = NumberFormat.getNumberInstance();
    private int startExp;

    public void onStart(String... args) {
        setLoopDelay(400, 600);
        getEventDispatcher().addListener(this);
        try {
            EventQueue.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    ui = new MassGUI();
                    ui.setVisible(true);
                }
            });
            if (ui != null) {
                while (ui.isVisible()) {
                    Execution.delay(100);
                }
            }
        } catch (final Throwable t) {
            t.printStackTrace();
        }
        startExp = Skill.STRENGTH.getExperience() + Skill.ATTACK.getExperience() + Skill.DEFENCE.getExperience()
                + Skill.CONSTITUTION.getExperience() + Skill.PRAYER.getExperience();

        if (Settings.useAbilities)
            add(new Abilities());

        add(new Fight(), new Heal());
        runningTime.start();

    }

    @Override
    public void onPaint(Graphics2D g2d) {

        int expGained = Skill.STRENGTH.getExperience() + Skill.ATTACK.getExperience() + Skill.DEFENCE.getExperience()
                + Skill.CONSTITUTION.getExperience() + Skill.PRAYER.getExperience() - startExp;

        if (Settings.targetNpc != null)
            Settings.targetNpc.getModel().render(g2d);

        // Added commit notes
        if (Settings.lootChoices != null) {
            g2d.drawString("Picking up:  " + Arrays.toString(Settings.lootChoices), 36, 258);
        }
        g2d.drawString("Ozzy's MassFighter", 36, 166);
        g2d.drawString("Status: " + Settings.status, 36, 189);
        g2d.drawString("Exp Gained: " + expGained + " (" + numberFormat.format((int) CommonMath.rate(TimeUnit.HOURS, runningTime.getRuntime(), expGained)) + " p/h)", 36, 212);
        g2d.drawString("Runtime: " + runningTime.getRuntimeAsString(), 36, 235);

    }
}
