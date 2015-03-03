package scripts.MassFighter;

import com.runemate.game.api.client.paint.PaintListener;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.util.StopWatch;
import com.runemate.game.api.hybrid.util.calculations.*;
import com.runemate.game.api.osrs.net.Zybez;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.ActionBar;
import com.runemate.game.api.rs3.net.GrandExchange;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.core.LoopingThread;
import com.runemate.game.api.script.framework.listeners.InventoryListener;
import com.runemate.game.api.script.framework.listeners.events.ItemEvent;
import com.runemate.game.api.script.framework.task.Task;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class MassFighter extends TaskScript implements PaintListener, InventoryListener {

    public static Methods methods;
    public static Settings settings;
    public static LocatableEntity targetEntity;
    public static String status;
    public static int currentTargetCount;
    public static UserProfile userProfile;
    public static Boolean requestedShutdown;
    public static Boolean setupRunning;
    public static Graphics2D graphics;
    public static List<String> runningTaskNames;

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

        if (settings.quickPray || (settings.useSoulsplit && Environment.isRS3())) {
            add(new Pray());
        }
        if (userProfile.getBankArea() != null) {
            add(new Store());
        }
        if (settings.useFood) {
            add(new Heal());
        }
        if (userProfile.getAlchLoot() != null && userProfile.getAlchLoot().length > 0) {
            add(new Alchemy());
        }
        if (userProfile.getLootNames() != null && userProfile.getLootNames().length > 0 || settings.buryBones) {
            add(new Loot());
        }
        if (!settings.selectedPotions.isEmpty()) {
            add(new Boost());
        }
        add(new Combat());
        if (settings.useAbilities && Environment.isRS3()) {
            if (!ActionBar.isExpanded()) {
                ActionBar.toggleExpansion();
            }
            new LoopingThread(new Abilities(), 1000, 1200).start();
        }
        getSimpleTasks(getTasks());
    }

    public static void getSimpleTasks(List<Task> tasks) {
        List<String> taskNames = new ArrayList<>();
        tasks.stream().filter(task -> task != null).forEach(task -> taskNames.add(task.getClass().getSimpleName()));
        runningTaskNames = taskNames;
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
    public void onItemAdded(ItemEvent event) {
        if (event.getItem() != null) {
            String itemName = event.getItem().getDefinition().getName();
            int itemId = event.getItem().getId();
            int itemValue = 0;
            if (Loot.itemPrices.containsKey(itemName)) {
                itemValue = Loot.itemPrices.get(itemName);
            } else {
                if (Environment.isRS3()) {
                    GrandExchange.Item item = GrandExchange.lookup(itemId);
                    if (item != null) {
                        itemValue = item.getPrice();
                    }
                } else if (Environment.isOSRS()) {
                    itemValue = Zybez.getAveragePrice(itemName);
                }
                Loot.itemPrices.put(itemName, itemValue);
            }
            profit += itemValue;
        }
    }

    @Override
    public void onPaint(Graphics2D g2d) {
        if (userProfile != null) {

            int expGainedNoHP = Skill.STRENGTH.getExperience() + Skill.RANGED.getExperience() + Skill.MAGIC.getExperience() + Skill.ATTACK.getExperience() + Skill.DEFENCE.getExperience()
                    + Skill.PRAYER.getExperience() - startExpNoHp;
            int expGained = Skill.STRENGTH.getExperience() + Skill.RANGED.getExperience() + Skill.MAGIC.getExperience() + Skill.ATTACK.getExperience() + Skill.DEFENCE.getExperience()
                    + Skill.CONSTITUTION.getExperience() + Skill.PRAYER.getExperience() - startExp;
            graphics = g2d;

            Color color1 = new Color(0, 0, 0, 110);
            Color color2 = new Color(255, 255, 255);
            BasicStroke stroke1 = new BasicStroke(1);
            Font font1 = new Font("Arial", Font.BOLD, 16);
            Font font2 = new Font("Arial", Font.BOLD, 14);
            Font font3 = new Font("Arial", Font.PLAIN, 12);

            g2d.setColor(color1);
            g2d.fillRoundRect(1, 0, 560, 130, 16, 16);
            g2d.setColor(color2);
            g2d.setStroke(stroke1);
            g2d.drawRoundRect(1, 0, 560, 130, 16, 16);
            g2d.setFont(font1);
            g2d.drawString("MassFighter", 211, 23);
            g2d.setFont(font2);
            // Titles
            g2d.drawString("Core", 52, 44);
            g2d.drawString("Stats", 239, 44);
            g2d.drawString("Rates", 396, 44);
            // Timers
            g2d.setFont(font3);
            // Core info
            g2d.drawString("Status: " + status, 7, 79);
            g2d.drawString("Runtime: " + runningTime.getRuntimeAsString(), 7, 96);
            g2d.drawString("Profile: " + userProfile.getProfileName(), 7, 61);
            g2d.drawString("Running Tasks: " + runningTaskNames, 7, 120);
            // Level info
            g2d.drawString("Constitution: " + Skill.CONSTITUTION.getCurrentLevel() + "(" + getLevelGain(constitutionLevel, Skill.CONSTITUTION) + ")" , 161, 61);
            g2d.drawString("Attack: " + Skill.ATTACK.getCurrentLevel() + "(" + getLevelGain(attackLevel, Skill.ATTACK) + ")", 161, 75);
            g2d.drawString("Strength: " + Skill.STRENGTH.getCurrentLevel() + "(" + getLevelGain(strengthLevel, Skill.STRENGTH) + ")", 161, 89);
            g2d.drawString("Defence: " + Skill.DEFENCE.getCurrentLevel() + "(" + getLevelGain(defenceLevel, Skill.DEFENCE) + ")", 276, 62);
            g2d.drawString("Ranged: " + Skill.RANGED.getCurrentLevel() + "(" + getLevelGain(rangedLevel, Skill.RANGED) + ")", 276, 77);
            g2d.drawString("Magic: " + Skill.MAGIC.getCurrentLevel() + "(" + getLevelGain(mageLevel, Skill.MAGIC) + ")", 276, 92);
            g2d.drawString("Prayer: " + Skill.PRAYER.getCurrentLevel() + "(" + getLevelGain(prayerLevel, Skill.PRAYER) + ")", 161, 103);
            // Rates
            g2d.drawString("Exp (No HP): " + expGainedNoHP + "("+numberFormat.format((int) CommonMath.rate(TimeUnit.HOURS, runningTime.getRuntime(), expGainedNoHP)) + " p/h)", 365, 62);
            g2d.drawString("Exp (Total): " + expGained + "("+numberFormat.format((int) CommonMath.rate(TimeUnit.HOURS, runningTime.getRuntime(), expGained)) + " p/h)", 365, 76);
            g2d.drawString("Profit: " + profit + "("+numberFormat.format((int) CommonMath.rate(TimeUnit.HOURS, runningTime.getRuntime(), profit)) + " p/h)", 365, 90);

            // Render current target entity
            if (targetEntity != null) {
                Coordinate targetPosition = targetEntity.getPosition();
                if (targetPosition != null) {
                    g2d.setColor(Color.red);
                    targetEntity.getPosition().render(g2d);
                }
            }
            // Render the fight area's outline
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
