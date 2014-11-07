package util;

import com.runemate.game.api.script.Execution;

/**
 * Created by Aidan on 05/11/2014.
 */
public class Waiting {

    public static boolean waitFor(final Condition c, final long timeout) {
        final long start = System.currentTimeMillis();
        System.out.println("Waiting on a condition for max " + timeout + " ms");
        while (System.currentTimeMillis() - start < timeout && !c.validate()) {
            Execution.delay(20, 30);
        }
        System.out.println("Waited, returning " + c.validate());
        return c.validate();
    }

    public interface Condition {
        public boolean validate();
    }



}
