package scripts.MassFighter;

import com.runemate.game.api.client.paint.PaintListener;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.util.StopWatch;
import com.runemate.game.api.hybrid.util.calculations.CommonMath;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.ActionBar;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.core.LoopingThread;
import com.runemate.game.api.script.framework.task.TaskScript;
import javafx.application.Platform;
import scripts.MassFighter.Framework.UserProfile;
import scripts.MassFighter.GUI.Main;
import scripts.MassFighter.GUI.Settings;
import scripts.MassFighter.Methods.Methods;
import scripts.MassFighter.Tasks.*;
import scripts.MassFighter.Tasks.Heal;
import scripts.MassFighter.Tasks.Store;

import java.awt.*;
import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class MassFighter extends TaskScript implements PaintListener {

    public static Methods methods;
    public static Settings settings;
    public static LocatableEntity targetEntity;
    public static String status;
    public static int currentTargetCount;
    public static UserProfile userProfile;
    public static Boolean requestedShutdown;
    public static Boolean setupRunning;
    public static Graphics2D graphics;

    private final StopWatch runningTime = new StopWatch();
    private final NumberFormat numberFormat = NumberFormat.getNumberInstance();
    private int startExp = 0;
    private int startExpNoHp = 0;
    private int profit = 0;

    private int constitutionLevel = Skill.CONSTITUTION.getBaseLevel();
    private int attackLevel = Skill.ATTACK.getBaseLevel();
    private int strengthLevel = Skill.STRENGTH.getBaseLevel();
    private int defenceLevel = Skill.DEFENCE.getBaseLevel();
    private int rangedLevel = Skill.RANGED.getBaseLevel();
    private int mageLevel = Skill.MAGIC.getBaseLevel();
    private int prayerLevel = Skill.PRAYER.getBaseLevel();

    public void onStart(String... args) {
        reset();
        // Loop & GUI Setup
        setLoopDelay(400, 600);
        getEventDispatcher().addListener(this);
        showAndWaitGUI();
        settings = userProfile.settings;
        methods = new Methods();
        if (Environment.isRS3()) {
            if (!ActionBar.isAutoRetaliating()) {
                ActionBar.toggleAutoRetaliation();
            }
        }
        startExpNoHp = Skill.STRENGTH.getExperience() + Skill.RANGED.getExperience() + Skill.MAGIC.getExperience() + Skill.ATTACK.getExperience() + Skill.DEFENCE.getExperience()
                 + Skill.PRAYER.getExperience();
        startExp = Skill.STRENGTH.getExperience() + Skill.RANGED.getExperience() + Skill.MAGIC.getExperience() + Skill.ATTACK.getExperience() + Skill.DEFENCE.getExperience()
                + Skill.PRAYER.getExperience() + Skill.CONSTITUTION.getExperience();
        runningTime.start();

        if (userProfile.getBankArea() != null) {
            add(new Store());
        }
        if (settings.quickPray || (settings.useSoulsplit && Environment.isRS3())) {
            add(new Pray());
        }
        if (settings.useFood) {
            add(new Heal());
        }
        if (userProfile.alchLoot != null && userProfile.alchLoot.length > 0) {
            add(new Alchemy());
        }
        if (userProfile.getLootNames() != null || userProfile.getAlchLoot() != null || settings.buryBones) {
            add(new Loot());
        }
        add(new Combat());
        if (settings.useAbilities && Environment.isRS3()) {
            if (!ActionBar.isExpanded()) {
                ActionBar.toggleExpansion();
            }
            new LoopingThread(new Abilities(), 1000, 1200).start();
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
        methods = null;
        settings = null;
        requestedShutdown = false;
        setupRunning = true;
        targetEntity = null;
        userProfile = null;
        status = "Setting up";
    }

    @Override
    public void onPaint(Graphics2D g2d) {
        if (userProfile != null) {
            int expGainedNoHP = Skill.STRENGTH.getExperience() + Skill.RANGED.getExperience() + Skill.MAGIC.getExperience() + Skill.ATTACK.getExperience() + Skill.DEFENCE.getExperience()
                    + Skill.PRAYER.getExperience() - startExpNoHp;
            int expGained = Skill.STRENGTH.getExperience() + Skill.RANGED.getExperience() + Skill.MAGIC.getExperience() + Skill.ATTACK.getExperience() + Skill.DEFENCE.getExperience()
                    + Skill.CONSTITUTION.getExperience() + Skill.PRAYER.getExperience() - startExp;
            graphics = g2d;
            g2d.setFont(new Font("Purisa", Font.PLAIN, 11));
            g2d.setColor(Color.black);
            g2d.fillRect(0, 0, 170, 350);
            g2d.setColor(Color.white);
            g2d.drawString("-- MassFighter --", 20, 20);
            g2d.drawString("Status: " + status, 20, 40);
            g2d.drawString("Runtime: " + runningTime.getRuntimeAsString(), 20, 60);
            g2d.drawString("Profile: " + userProfile.getProfileName(), 20, 80);
            g2d.drawString(" -- Stats -- ", 20, 100);
            g2d.drawString("Constitution: " + Skill.CONSTITUTION.getCurrentLevel() + "(" + getLevelGain(constitutionLevel, Skill.CONSTITUTION) + ")", 20, 120);
            g2d.drawString("Attack: " + Skill.ATTACK.getCurrentLevel() + "(" + getLevelGain(attackLevel, Skill.ATTACK) + ")", 20, 140);
            g2d.drawString("Strength: " + Skill.STRENGTH.getCurrentLevel() + "(" + getLevelGain(strengthLevel, Skill.STRENGTH) + ")", 20, 160);
            g2d.drawString("Defence: " + Skill.DEFENCE.getCurrentLevel() + "(" + getLevelGain(defenceLevel, Skill.DEFENCE) + ")", 20, 180);
            g2d.drawString("Ranged: " + Skill.RANGED.getCurrentLevel() + "(" + getLevelGain(rangedLevel, Skill.RANGED) + ")", 20, 200);
            g2d.drawString("Magic: " + Skill.MAGIC.getCurrentLevel() + "(" + getLevelGain(mageLevel, Skill.MAGIC) + ")", 20, 220);
            g2d.drawString("Prayer: " + Skill.PRAYER.getCurrentLevel() + "(" + getLevelGain(prayerLevel, Skill.PRAYER) + ")", 20, 240);
            g2d.drawString("-- Rates -- ", 20, 260);
            g2d.drawString("Exp Gain (No HP): " + expGainedNoHP, 20, 280);
            g2d.drawString("("+numberFormat.format((int) CommonMath.rate(TimeUnit.HOURS, runningTime.getRuntime(), expGainedNoHP)) + " p/h)", 20, 300);
            g2d.drawString("Exp Gain (Total): " + expGained, 20, 320);
            g2d.drawString("("+numberFormat.format((int) CommonMath.rate(TimeUnit.HOURS, runningTime.getRuntime(), expGained)) + " p/h)", 20, 340);
            if (targetEntity != null) {
                Coordinate targetPosition = targetEntity.getPosition();
                if (targetPosition != null) {
                    g2d.setColor(Color.red);
                    targetEntity.getPosition().render(g2d);
                }
            }
            if (settings != null && settings.showOutline) {
                Area area = userProfile.getFightArea();
                if (area != null && area.isValid() && area.isVisible()) {
                    g2d.setColor(Color.orange);
                    java.util.List<Coordinate> surroundingCoords = area.getArea().getSurroundingCoordinates();
                    surroundingCoords.parallelStream().forEach(new Consumer<Coordinate>() {
                        @Override
                        public void accept(Coordinate coordinate) {
                            if (coordinate != null) {
                                coordinate.render(g2d);
                            }
                        }
                    });
                }
            }
        }
    }

    private int getLevelGain(int start, Skill skill) {
        return skill.getCurrentLevel() - start;
    }

}
