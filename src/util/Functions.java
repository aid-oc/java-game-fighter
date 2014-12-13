package util;

import com.runemate.game.api.rs3.local.hud.Powers;

public class Functions {

    // Temporary
    public static Boolean isSoulsplitActive()
    {
        return Powers.Prayer.Curse.SOUL_SPLIT.isActivated();
    }

}
