package scripts.MassFighter.Tasks;

import com.runemate.game.api.script.framework.task.Task;

public class Boost extends Task {

    /*
      Handles consumables which provide players with a stat boost, potions etc.
      Activated when a player is using a boost and the associated boost is not
      currently active whilst still containing the item which provides the boost
      in their inventory
     */

    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public void execute() {

    }


}
