package scripts.MassFighter;

import com.runemate.game.api.client.paint.PaintListener;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.TaskScript;
import scripts.MassFighter.Data.Settings;
import scripts.MassFighter.Tasks.Fight;
import scripts.MassFighter.Tasks.Heal;

import java.awt.*;

public class MassFighter extends TaskScript implements PaintListener {

    private static MassGUI ui;
    private final long startTime = System.currentTimeMillis();

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
        add(new Fight(), new Heal());

    }

    @Override
    public void onPaint(Graphics2D g2d) {
        if (Settings.targetNpc != null)
            Settings.targetNpc.render(g2d);
        g2d.drawString("Ozzy's MassFighter", 36, 166);
        g2d.drawString("Status: " + Settings.status, 35, 189);
        long millis = System.currentTimeMillis() - startTime;
        long second = (millis / 1000) % 60;
        long minute = (millis / (1000 * 60)) % 60;
        long hour = (millis / (1000 * 60 * 60)) % 24;
        String time = String.format("%02d:%02d:%02d", hour, minute, second);
        g2d.drawString("Runtime: " + time, 34, 210);
    }
}
