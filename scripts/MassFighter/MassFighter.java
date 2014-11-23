package scripts.MassFighter;

import com.runemate.game.api.client.paint.PaintListener;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.util.StopWatch;
import com.runemate.game.api.hybrid.util.calculations.CommonMath;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.ActionBar;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.core.LoopingThread;
import com.runemate.game.api.script.framework.task.TaskScript;
import scripts.MassFighter.Data.Settings;
import scripts.MassFighter.Tasks.*;

import java.awt.*;
import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

import static scripts.MassFighter.Data.Settings.*;

public class MassFighter extends TaskScript implements PaintListener {

    private static MassGUI ui;
    public static Boolean requestedShutdown = false;
    private final StopWatch runningTime = new StopWatch();
    private final NumberFormat numberFormat = NumberFormat.getNumberInstance();
    private int startExp;

    public void onStart(String... args) {

        // Loop & GUI Setup
        setLoopDelay(400);
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

        if (requestedShutdown) {
            this.stop();
        } else {

            // SETUP
            if (Environment.isRS3()) {
                if (!ActionBar.isAutoRetaliating()) {
                    ActionBar.toggleAutoRetaliation();
                }
            }
            // Retrieve initial exp values
            startExp = Skill.STRENGTH.getExperience() + Skill.ATTACK.getExperience() + Skill.DEFENCE.getExperience()
                    + Skill.CONSTITUTION.getExperience() + Skill.PRAYER.getExperience();
            // Start the runtime
            runningTime.start();

            // TASKS
            if (Settings.lootCharms) {
                add(new LootHandler());
            }
            if (useSoulsplit && Environment.isRS3()) {
                add(new PrayerHandler());
            }
            if (usingFood) {
                add(new FoodHandler());
            }
            add(new CombatHandler());
            // LOOPING THREADS
            if (useAbilities && Environment.isRS3()) {
                if (!ActionBar.isExpanded()) {
                    ActionBar.toggleExpansion();
                }
                new LoopingThread(new AbilityHandler(), 1600, 2000).start();
            }
        }
    }

    @Override
    public void onPaint(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        int expGained = Skill.STRENGTH.getExperience() + Skill.ATTACK.getExperience() + Skill.DEFENCE.getExperience()
                + Skill.CONSTITUTION.getExperience() + Skill.PRAYER.getExperience() - startExp;
        g2d.drawString("Ozzy's MassFighter", 36, 166);
        g2d.drawString("Task Status: " + status, 36, 189);
        //g2d.drawString("Thread Status: " + abilityStatus, 36, 212);
        g2d.drawString("Exp Gained: " + expGained + " (" + numberFormat.format((int) CommonMath.rate(TimeUnit.HOURS, runningTime.getRuntime(), expGained)) + " p/h)", 36, 212);
        g2d.drawString("Script Runtime: " + runningTime.getRuntimeAsString(), 36, 235);
    }
}
