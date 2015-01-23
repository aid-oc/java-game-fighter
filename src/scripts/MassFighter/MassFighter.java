package scripts.MassFighter;

import com.runemate.game.api.client.paint.PaintListener;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.RuneScape;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.util.StopWatch;
import com.runemate.game.api.hybrid.util.calculations.CommonMath;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.ActionBar;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.core.LoopingThread;
import com.runemate.game.api.script.framework.task.TaskScript;
import javafx.application.Platform;
import scripts.MassFighter.Framework.BankingProfile;
import scripts.MassFighter.Framework.CombatProfile;
import scripts.MassFighter.GUI.Main;
import scripts.MassFighter.GUI.Settings;
import scripts.MassFighter.Methods.Methods;
import scripts.MassFighter.ProfileTasks.HillGiantsRoute;
import scripts.MassFighter.ProfileTasks.LumbridgeCowsRoute;
import scripts.MassFighter.Profiles.HillGiants;
import scripts.MassFighter.Profiles.LumbridgeCows;
import scripts.MassFighter.Tasks.*;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

public class MassFighter extends TaskScript implements PaintListener {

    public static Methods methods;
    public static Settings settings;
    public static Npc targetNpc;
    public static String status;
    public static CombatProfile combatProfile;
    public static Boolean requestedShutdown;
    public static Boolean setupRunning;
    public static Graphics2D graphics;

    private final StopWatch runningTime = new StopWatch();
    private final NumberFormat numberFormat = NumberFormat.getNumberInstance();
    private int startExp;

    public void onStart(String... args) {

        if (RuneScape.isLoggedIn()) {
            reset();
            // Loop & GUI Setup
            setLoopDelay(400, 600);
            getEventDispatcher().addListener(this);
            showAndWaitGUI();
            methods = new Methods();
            if (Environment.isRS3()) {
                if (!ActionBar.isAutoRetaliating()) {
                    ActionBar.toggleAutoRetaliation();
                }
            }
            startExp = Skill.STRENGTH.getExperience() + Skill.ATTACK.getExperience() + Skill.DEFENCE.getExperience()
                    + Skill.CONSTITUTION.getExperience() + Skill.PRAYER.getExperience();
            runningTime.start();

            if (combatProfile instanceof LumbridgeCows) {
                add(new LumbridgeCowsRoute());
            } else if (combatProfile instanceof HillGiants) {
                add(new HillGiantsRoute());
            } else if (combatProfile instanceof BankingProfile) {
                add(new BankHandler());
            }
            if (settings.quickPray || (settings.useSoulsplit && Environment.isRS3())) {
                add(new PrayerHandler());
            }
            if (settings.useFood) {
                add(new FoodHandler());
            }
            add(new CombatHandler());
            if (settings.useAbilities && Environment.isRS3()) {
                if (!ActionBar.isExpanded()) {
                    ActionBar.toggleExpansion();
                }
                new LoopingThread(new AbilityHandler(), 1000, 1200).start();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please start the script logged in", "MassFighter", JOptionPane.WARNING_MESSAGE);
            this.stop();
        }
    }

    private void showAndWaitGUI() {
        // Don't change this
        Platform.runLater(() -> new Main());
        while (setupRunning) {
            Execution.delay(200);
        }
    }

    private void reset() {
        requestedShutdown = false;
        setupRunning = true;
        targetNpc = null;
        combatProfile = null;
        status = "Setting up";
    }


    @Override
    public void onPaint(Graphics2D g2d) {
        graphics = g2d;
        int expGained = Skill.STRENGTH.getExperience() + Skill.ATTACK.getExperience() + Skill.DEFENCE.getExperience()
                + Skill.CONSTITUTION.getExperience() + Skill.PRAYER.getExperience() - startExp;
        g2d.setColor(new Color(145, 138, 138, 178));
        g2d.fillRect(17, 143, 155, 130);
        g2d.setFont(new Font("Arial", 0, 11));
        g2d.setColor(new Color(0, 0, 0, 178));
        g2d.drawString("Exp Gain: " + expGained + " (" + numberFormat.format((int) CommonMath.rate(TimeUnit.HOURS, runningTime.getRuntime(), expGained)) + " p/h)", 24, 244);
        g2d.drawString("Status: " + status, 24, 226);
        g2d.drawString("Runtime: " + runningTime.getRuntimeAsString(), 24, 208);
        if (combatProfile != null) g2d.drawString("Profile: " + combatProfile.toString(), 24, 190);
        g2d.setFont(new Font("Arial", Font.BOLD, 15));
        g2d.drawString("MassFighter", 43, 165);
        if (targetNpc != null && targetNpc.getModel() != null) {
            if (targetNpc.getModel().getBoundingModel() != null) {
                g2d.setColor(Color.red);
                targetNpc.getModel().getBoundingModel().render(g2d);
            }
        }
    }
}
