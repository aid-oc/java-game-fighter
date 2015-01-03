package scripts.MassFighterTest;

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
import javafx.stage.Stage;
import scripts.MassFighterTest.Data.Food;
import scripts.MassFighterTest.Framework.BankingProfile;
import scripts.MassFighterTest.Framework.CombatProfile;
import scripts.MassFighterTest.GUI.Main;
import scripts.MassFighterTest.Methods.Methods;
import scripts.MassFighterTest.ProfileTasks.HillGiantsRoute;
import scripts.MassFighterTest.ProfileTasks.LumbridgeCowsRoute;
import scripts.MassFighterTest.Profiles.HillGiants;
import scripts.MassFighterTest.Profiles.LumbridgeCows;
import scripts.MassFighterTest.Tasks.*;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class MassFighterTest extends TaskScript implements PaintListener {

    public static Methods methods;
    public static Npc targetNpc;
    public static int targetSelection;
    public static CombatProfile combatProfile;
    public static Boolean requestedShutdown;
    public static String status;
    public static Boolean useFood;
    public static Boolean exitOutFood;
    public static Boolean lootInCombat;
    public static Boolean useAbilities;
    public static Boolean useSoulsplit;
    public static Boolean waitForLoot;
    public static Boolean looting;
    public static Boolean buryBones;
    public static Food food;
    public static int fightRadius;
    public static int eatValue;
    public static int criticalHitpoints;
    private static Main ui;
    public static Boolean setupRunning;

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
            if (useSoulsplit && Environment.isRS3()) {
                add(new PrayerHandler());
            }
            if (useFood) {
                add(new FoodHandler());
            }
            add(new CombatHandler());
            if (useAbilities && Environment.isRS3()) {
                if (!ActionBar.isExpanded()) {
                    ActionBar.toggleExpansion();
                }
                new LoopingThread(new AbilityHandler(), 1600, 2000).start();
            }
            System.out.println("You are using profile: " + combatProfile.toString());
            System.out.println("Selected Monsters: " + Arrays.toString(combatProfile.getNpcNames()));
            System.out.println("Selected Loot: " + Arrays.toString(combatProfile.getLootNames()));
            System.out.println("Eating at " + eatValue + " hitpoints");
            System.out.println("Exit on food out?: " + exitOutFood);
            System.out.println("Will exit below " + criticalHitpoints + " hitpoints");
        } else {
            JOptionPane.showMessageDialog(null, "Please start the script logged in", "MassFighterTest", JOptionPane.WARNING_MESSAGE);
            this.stop();
        }
    }

    private void showAndWaitGUI() {
        ui = new Main();
        Platform.runLater(() -> {
            try {
                ui.start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        while (setupRunning) {
            Execution.delay(100);
        }
        Platform.runLater(ui::close);
    }

    private void reset() {
        food = null;
        fightRadius = eatValue = targetSelection = 0;
        criticalHitpoints = 1000;
        requestedShutdown = useFood = useAbilities = useSoulsplit = looting = buryBones = lootInCombat = exitOutFood = waitForLoot = false;
        setupRunning = true;
        targetNpc = null;
        combatProfile = null;
        status = "Setting up - if using food, have it in your inventory";
    }

    @Override
    public void onPaint(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        g2d.setColor(Color.white);
        int expGained = Skill.STRENGTH.getExperience() + Skill.ATTACK.getExperience() + Skill.DEFENCE.getExperience()
                + Skill.CONSTITUTION.getExperience() + Skill.PRAYER.getExperience() - startExp;
        g2d.drawString("Ozzy's MassFighterTest", 36, 166);
        if (combatProfile != null) g2d.drawString("Profile: " + combatProfile.toString(), 36, 189);
        g2d.drawString("Task Status: " + status, 36, 212);
        g2d.drawString("Exp Gained: " + expGained + " (" + numberFormat.format((int) CommonMath.rate(TimeUnit.HOURS, runningTime.getRuntime(), expGained)) + " p/h)", 36, 235);
        g2d.drawString("Script Runtime: " + runningTime.getRuntimeAsString(), 36, 258);
    }

}
