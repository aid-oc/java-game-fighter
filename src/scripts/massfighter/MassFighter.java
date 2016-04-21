package scripts.massfighter;

import com.runemate.game.api.client.paint.PaintListener;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.entities.definitions.ItemDefinition;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.util.StopWatch;
import com.runemate.game.api.hybrid.util.calculations.CommonMath;
import com.runemate.game.api.osrs.net.Zybez;
import com.runemate.game.api.hybrid.net.GrandExchange;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.core.LoopingThread;
import com.runemate.game.api.script.framework.listeners.ChatboxListener;
import com.runemate.game.api.script.framework.listeners.InventoryListener;
import com.runemate.game.api.script.framework.listeners.events.ItemEvent;
import com.runemate.game.api.script.framework.listeners.events.MessageEvent;
import com.runemate.game.api.script.framework.task.Task;
import com.runemate.game.api.script.framework.task.TaskScript;
import javafx.application.Platform;
import scripts.massfighter.framework.Methods;
import scripts.massfighter.gui.Main;
import scripts.massfighter.gui.Settings;
import scripts.massfighter.tasks.osrs.DismissDialog;
import scripts.massfighter.tasks.rs3.*;
import scripts.massfighter.tasks.shared.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MassFighter extends TaskScript implements PaintListener, MouseListener, InventoryListener, ChatboxListener {

    public static Methods methods;
    public static LocatableEntity targetEntity;
    public static String status;
    public static boolean setupRunning;
    public static final boolean debug = false;

    private final StopWatch runningTime = new StopWatch();
    private final NumberFormat numberFormat = NumberFormat.getNumberInstance();
    private int startExp = 0;
    private int startExpNoHp = 0;
    private int profit = 0;
    private final Rectangle hidePaintButton = new Rectangle(1,155,80,20);
    private boolean hidePaint = false;

    private final int constitutionLevel = Skill.CONSTITUTION.getBaseLevel();
    private final int attackLevel = Skill.ATTACK.getBaseLevel();
    private final int strengthLevel = Skill.STRENGTH.getBaseLevel();
    private final int defenceLevel = Skill.DEFENCE.getBaseLevel();
    private final int rangedLevel = Skill.RANGED.getBaseLevel();
    private final int mageLevel = Skill.MAGIC.getBaseLevel();
    private final int prayerLevel = Skill.PRAYER.getBaseLevel();

    public MassFighter() {
        setEmbeddableUI(new Main(this));
    }

    public void onStart(String... args) {

        if (Environment.getGameType() != null) {
            // Loop & GUI Setup
            setupRunning = true;
            setLoopDelay(400, 600);
            getEventDispatcher().addListener(this);
            //showAndWaitGUI();
            methods = new Methods();

            // Top priority tasks, not shuffled
            add(new Safety());
            add(new Heal());
            add(new Antifire());
            add(new KeepDistance());

            // Low-priority tasks to be shuffled
            List<Task> tasks = new ArrayList<>();
            if (Environment.isRS3()) {
                new LoopingThread(new Abilities(), 1000, 1200).start();
                tasks.add(new LootMenu());
                tasks.add(new MagicNotepaper());
                tasks.add(new Soulsplit());
            } else {
                tasks.add(new DismissDialog());
            }
            tasks.add(new Alchemy());
            tasks.add(new Ammunition());
            tasks.add(new Throwing());
            tasks.add(new Attack());
            tasks.add(new Boost());
            tasks.add(new BuryBones());
            tasks.add(new Loot());
            tasks.add(new PrayerPoints());
            tasks.add(new QuickPray());
            tasks.add(new ReturnToArea());
            Collections.shuffle(tasks);
            add(tasks.toArray(new Task[(tasks.size())]));

            startExpNoHp = Skill.STRENGTH.getExperience() + Skill.RANGED.getExperience() + Skill.MAGIC.getExperience() + Skill.ATTACK.getExperience() + Skill.DEFENCE.getExperience()
                    + Skill.PRAYER.getExperience();
            startExp = startExpNoHp + Skill.CONSTITUTION.getExperience();
            runningTime.start();
        }

    }

    /*private void showAndWaitGUI() {
        // Don't change this
        Platform.runLater(() -> new Main());
        while (setupRunning) {
            Execution.delay(200);
        }
    }*/


    @Override
    public void onItemAdded(ItemEvent event) {
        if (methods != null && event != null && event.getItem() != null) {
            ItemDefinition itemDefinition = event.getItem().getDefinition();
            if (itemDefinition != null) {
                String itemName = itemDefinition.getName();
                int itemId = itemDefinition.getUnnotedId();
                int itemValue = 0;
                if (itemName.equals("Coins")) {
                    itemValue = event.getQuantityChange();
                } else {
                    if (Methods.itemPrices.containsKey(itemName)) {
                        itemValue = Methods.itemPrices.get(itemName);
                    } else {
                        if (Environment.isRS3()) {
                            GrandExchange.Item item = GrandExchange.lookup(itemId);
                            if (item != null) {
                                itemValue = item.getPrice();
                            }
                        } else if (Environment.isOSRS()) {
                            itemValue = Zybez.getAveragePrice(itemId);
                        }
                        Methods.itemPrices.put(itemName, itemValue);
                    }
                }
                profit += itemValue*event.getQuantityChange();
            }
        }
    }


    @Override
    public void onPaint(Graphics2D g2d) {
        if (Methods.arrayIsValid(Settings.npcNames)) {

            Color blackTransparent = new Color(0, 0, 0, 110);

            // Draw minimise rectangle
            g2d.setColor(blackTransparent);
            g2d.fillRect(hidePaintButton.x, hidePaintButton.y, hidePaintButton.width, 20);
            g2d.setColor(Color.white);
            g2d.drawString("Toggle Paint", hidePaintButton.x+5, hidePaintButton.y+15);

            if (!hidePaint) {

                int expGainedNoHP = Skill.STRENGTH.getExperience() + Skill.RANGED.getExperience() + Skill.MAGIC.getExperience() + Skill.ATTACK.getExperience() + Skill.DEFENCE.getExperience()
                        + Skill.PRAYER.getExperience() - startExpNoHp;
                int expGained = Skill.STRENGTH.getExperience() + Skill.RANGED.getExperience() + Skill.MAGIC.getExperience() + Skill.ATTACK.getExperience() + Skill.DEFENCE.getExperience()
                        + Skill.CONSTITUTION.getExperience() + Skill.PRAYER.getExperience() - startExp;

                BasicStroke onepxStroke = new BasicStroke(1);
                Font boldLarge = new Font("Arial", Font.BOLD, 16);
                Font boldSmall = new Font("Arial", Font.BOLD, 14);
                Font plainSmall = new Font("Arial", Font.PLAIN, 12);
                Font plainSmallest = new Font("Arial", Font.PLAIN, 10);

                g2d.setColor(blackTransparent);
                g2d.fillRoundRect(1, 0, 500, 150, 16, 16);
                g2d.setColor(Color.white);
                g2d.setStroke(onepxStroke);
                g2d.drawRoundRect(1, 0, 500, 150, 16, 16);

                g2d.setFont(boldLarge);
                g2d.drawString("MassFighter", 211, 23);
                g2d.setFont(boldSmall);

                // Titles
                g2d.drawString("Core", 52, 44);
                g2d.drawString("Stats", 239, 44);
                g2d.drawString("Rates", 396, 44);
                // Timers
                g2d.setFont(plainSmall);
                // Core info
                g2d.drawString("Status: " + status, 7, 61);
                g2d.drawString("Runtime: " + runningTime.getRuntimeAsString(), 7, 75);
                g2d.setFont(plainSmallest);
                //g2d.drawString("", 7, 140);
                g2d.setFont(plainSmall);
                // Level info
                g2d.drawString("Constitution: " + Skill.CONSTITUTION.getCurrentLevel() + "(" + getLevelGain(constitutionLevel, Skill.CONSTITUTION) + ")", 161, 61);
                g2d.drawString("Attack: " + Skill.ATTACK.getCurrentLevel() + "(" + getLevelGain(attackLevel, Skill.ATTACK) + ")", 161, 75);
                g2d.drawString("Strength: " + Skill.STRENGTH.getCurrentLevel() + "(" + getLevelGain(strengthLevel, Skill.STRENGTH) + ")", 161, 89);
                g2d.drawString("Defence: " + Skill.DEFENCE.getCurrentLevel() + "(" + getLevelGain(defenceLevel, Skill.DEFENCE) + ")", 276, 62);
                g2d.drawString("Ranged: " + Skill.RANGED.getCurrentLevel() + "(" + getLevelGain(rangedLevel, Skill.RANGED) + ")", 276, 77);
                g2d.drawString("Magic: " + Skill.MAGIC.getCurrentLevel() + "(" + getLevelGain(mageLevel, Skill.MAGIC) + ")", 276, 92);
                g2d.drawString("Prayer: " + Skill.PRAYER.getCurrentLevel() + "(" + getLevelGain(prayerLevel, Skill.PRAYER) + ")", 161, 103);
                // Rate Titles
                g2d.drawString("Exp (No HP):", 365, 62);
                g2d.drawString("Exp (Total):", 365, 90);
                g2d.drawString("Profit:", 365, 118);
                // Info that needs to stand out
                g2d.setColor(Color.green);
                g2d.drawString(expGainedNoHP + "(" + numberFormat.format((int) CommonMath.rate(TimeUnit.HOURS, runningTime.getRuntime(), expGainedNoHP)) + " p/h)", 365, 76);
                g2d.drawString(expGained + "(" + numberFormat.format((int) CommonMath.rate(TimeUnit.HOURS, runningTime.getRuntime(), expGained)) + " p/h)", 365, 104);
                g2d.drawString(profit + "(" + numberFormat.format((int) CommonMath.rate(TimeUnit.HOURS, runningTime.getRuntime(), profit)) + " p/h)", 365, 132);
            }
        }
    }

    private int getLevelGain(int start, Skill skill) {
        return skill.getBaseLevel() - start;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (hidePaintButton.contains(e.getPoint())) {
            hidePaint = !hidePaint;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent != null && Settings.useAntifire && Antifire.hasAntifireActive) {
            String message = messageEvent.getMessage().toLowerCase();
            if (message.contains("fire is about to run out")) {
                Antifire.hasAntifireActive = false;
                Methods.out("Antifire: No longer active");
            }
        }
    }
}
