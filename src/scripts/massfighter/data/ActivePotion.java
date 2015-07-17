package scripts.massfighter.data;


import com.runemate.game.api.hybrid.local.Varpbits;

/**
 * ozzy.
 */
public enum ActivePotion {

    ACTIVE_POTION("Placeholder", 0, 1);

    /* Contains potions which have an active effect (Antifire etc) */

    private final String potionName;
    private final int potionVarpPosition;
    private final int potionActiveValue;

    ActivePotion(String name, int varpPosition, int activeValue) {
        potionName = name;
        potionVarpPosition = varpPosition;
        potionActiveValue = activeValue;
    }

    public String getPotionName() {
        return potionName;
    }

    public boolean isActive() {
        Varpbits potionVarp = Varpbits.getAt(potionVarpPosition);
        return potionVarp != null && potionVarp.getValue() == potionActiveValue;
    }


}
