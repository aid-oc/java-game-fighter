package util;

import com.runemate.game.api.hybrid.local.Varps;

public class Functions {

    // Temporary
    public static Boolean isSoulsplitActive()
    {
        return Varps.getAt(3275).getBit(18) == 1;
    }

}
