package scripts.MassFighter;

import com.runemate.game.api.client.paint.PaintListener;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.util.StopWatch;
import com.runemate.game.api.hybrid.util.calculations.CommonMath;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.ActionBar;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.TaskScript;
import scripts.MassFighter.Tasks.*;

import java.awt.*;
import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

import static scripts.MassFighter.Data.Settings.*;

public class MassFighter extends TaskScript implements PaintListener {

    private static MassGUI ui;
    private final StopWatch runningTime = new StopWatch();
    private final NumberFormat numberFormat = NumberFormat.getNumberInstance();
    private int startExp;

    public void onStart(String... args) {

        // Loop & GUI Setup
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


        // Used to track exp gains  to display in the paint
        startExp = Skill.STRENGTH.getExperience() + Skill.ATTACK.getExperience() + Skill.DEFENCE.getExperience()
                + Skill.CONSTITUTION.getExperience() + Skill.PRAYER.getExperience();


        // Turns on auto retaliation, may make this an option as it worked well at waterfiends
        // but this may not be the case elsewhere
        if (Environment.isRS3()) {
            if (!ActionBar.isAutoRetaliating()) {
                ActionBar.toggleAutoRetaliation();
            }
        }

        if (useSoulsplit && Environment.isRS3()) {
            // Enables Soulsplit and ensures it stays active throughout runtime
            add(new PrayerHandler());
        }

        if (usingFood) {
            // Handles eating food, eats around the desired value inputted by the user (defaults to 5000)
            add(new FoodHandler());
        }

        if (lootCharms) {
            // Loots the users desired items between combat
            add(new LootHandler());
        }

        // Add Tasks
        if (useAbilities && Environment.isRS3()) {
            // Handles ability usage on RS3 - Sort abilities Ultimate -> Threshold -> Basic
            add(new AbilityHandler());
        }

        // Targets and initates fights with the target npc set by the user
        add(new CombatHandler());

        // Start the runtime
        runningTime.start();
    }


    @Override
    public void onPaint(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        int expGained = Skill.STRENGTH.getExperience() + Skill.ATTACK.getExperience() + Skill.DEFENCE.getExperience()
                + Skill.CONSTITUTION.getExperience() + Skill.PRAYER.getExperience() - startExp;
        //g2d.drawString("Picking up:  " + Arrays.toString(lootChoices), 36, 258);
        g2d.drawString("Ozzy's MassFighter", 36, 166);
        g2d.drawString("Status: " + status, 36, 189);
        g2d.drawString("Exp Gained: " + expGained + " (" + numberFormat.format((int) CommonMath.rate(TimeUnit.HOURS, runningTime.getRuntime(), expGained)) + " p/h)", 36, 212);
        g2d.drawString("Runtime: " + runningTime.getRuntimeAsString(), 36, 235);
    }
}
