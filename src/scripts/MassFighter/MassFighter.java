package scripts.MassFighter;

import com.runemate.game.api.client.paint.PaintListener;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.region.Players;
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
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MassFighter extends TaskScript implements PaintListener {

    public static Npc targetNpc;
    public static CombatProfile combatProfile;
    private static MassGUI ui;
    public static Boolean requestedShutdown = false;
    private final StopWatch runningTime = new StopWatch();
    private final NumberFormat numberFormat = NumberFormat.getNumberInstance();
    private int startExp;
    private int[] initialLevels;

    public void onStart(String... args) {
        combatProfile = null;
        // Loop & GUI Setup
        setLoopDelay(400, 600);
        getEventDispatcher().addListener(this);
        try {
            EventQueue.invokeAndWait(() -> {
                ui = new MassGUI();
                ui.setVisible(true);
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
            if (Environment.isRS3()) {
                if (!ActionBar.isAutoRetaliating()) {
                    ActionBar.toggleAutoRetaliation();
                }
            }

            startExp = Skill.STRENGTH.getExperience() + Skill.ATTACK.getExperience() + Skill.DEFENCE.getExperience()
                    + Skill.CONSTITUTION.getExperience() + Skill.PRAYER.getExperience();
            runningTime.start();

            if (combatProfile == null) {
                java.util.List<Area> areas = new ArrayList<>();
                areas.add(new Area.Circular(Players.getLocal().getPosition(), Settings.chosenFightRegion));
                Settings.fightAreas = areas;
            }

            if (Settings.useSoulsplit && Environment.isRS3()) {
                add(new PrayerHandler());
            }
            if (Settings.usingFood) {
                add(new FoodHandler());
            }
            add(new CombatHandler());
            if (Settings.useAbilities && Environment.isRS3()) {
                if (!ActionBar.isExpanded()) {
                    ActionBar.toggleExpansion();
                }
                new LoopingThread(new AbilityHandler(), 1600, 2000).start();
            }
            if (MassFighter.combatProfile != null) {
                System.out.println("You are using profile: " + MassFighter.combatProfile.toString());
            } else {
                System.out.println("You are not using a profile");
            }
            System.out.println("Loot choices: " + Settings.lootChoices);
            System.out.println("NPC choices: " + Settings.chosenNpcNames);
            System.out.println("Area choices: " + Settings.fightAreas);
        }
    }

    @Override
    public void onPaint(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        if (targetNpc != null) {
            if (targetNpc.getArea() != null) {
                g2d.setColor(Color.red);
                targetNpc.getArea().getCenter().render(g2d);
            }
        }
        g2d.setColor(Color.white);
        int expGained = Skill.STRENGTH.getExperience() + Skill.ATTACK.getExperience() + Skill.DEFENCE.getExperience()
                + Skill.CONSTITUTION.getExperience() + Skill.PRAYER.getExperience() - startExp;
        g2d.drawString("Ozzy's MassFighter", 36, 166);
        g2d.drawString("Task Status: " + Settings.status, 36, 189);
        g2d.drawString("Exp Gained: " + expGained + " (" + numberFormat.format((int) CommonMath.rate(TimeUnit.HOURS, runningTime.getRuntime(), expGained)) + " p/h)", 36, 212);
        g2d.drawString("Script Runtime: " + runningTime.getRuntimeAsString(), 36, 235);
    }
}
