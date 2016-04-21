package scripts.massfighter.data;

import com.runemate.game.api.hybrid.local.Skill;
import scripts.massfighter.gui.Settings;

public enum SkillPotion {

    /* Contains potions which provide a temporary boost to a skill */

    /* OSRS */
    // ATTACK POTIONS
    OSRS_ATTACK_POTION("Attack potion", Skill.ATTACK, 3, 10),
    OSRS_SUPER_ATTACK("Super attack", Skill.ATTACK, 5, 15),
    // STRENGTH POTIONS
    OSRS_STRENGTH_POTION("Strength potion", Skill.STRENGTH, 3, 10),
    OSRS_SUPER_STRENGTH("Super strength", Skill.STRENGTH, 5, 15),
    // DEFENCE POTIONS
    OSRS_DEFENCE_POTION("Defence potion", Skill.DEFENCE, 3, 10),
    OSRS_SUPER_DEFENCE("Super defence", Skill.DEFENCE, 5, 15),
    // RANGED POTIONS
    OSRS_RANGING_POTION("Ranging potion", Skill.RANGED, 4, 10),
    OSRS_SUPER_RANGING("Super ranging", Skill.RANGED, 5, 15),
    // MAGIC POTIONS
    OSRS_MAGIC_POTION("Magic potion", Skill.MAGIC, 4, 0),
    OSRS_SUPER_MAGIC("Super magic", Skill.MAGIC, 5, 15),

    /* RS3 */
    // MULTIPLE
    OVERLOAD("Overload", Skill.ATTACK, 3, 15),
    COMBAT_POTION("Combat potion", Skill.ATTACK, 3, 10),
    // ATTACK POTIONS
    ATTACK_POTION("Attack potion", Skill.ATTACK, 1, 8),
    ATTACK_MIX("Attack mix", Skill.ATTACK, 1, 8),
    SUPER_ATTACK("Super attack", Skill.ATTACK, 2, 12),
    SUPER_ATTACK_MIX("Super attack mix", Skill.ATTACK, 2, 12),
    EXTREME_ATTACK("Extreme attack", Skill.ATTACK, 3, 15),
    // STRENGTH POTIONS
    STRENGTH_POTION("Strength potion", Skill.STRENGTH, 1, 8),
    STRENGTH_MIX("Strength mix", Skill.STRENGTH, 1, 8),
    SUPER_STRENGTH("Super strength", Skill.STRENGTH, 2, 12),
    SUPER_STRENGTH_MIX("Super strength mix", Skill.STRENGTH, 2, 12),
    EXTREME_STRENGTH("Extreme strength", Skill.STRENGTH, 3, 15),
    // DEFENCE POTIONS
    DEFENCE_POTION("Defence potion", Skill.DEFENCE, 1, 8),
    DEFENCE_MIX("Defence mix", Skill.DEFENCE, 1, 8),
    SUPER_DEFENCE("Super defence", Skill.DEFENCE, 2, 12),
    SUPER_DEFENCE_MIX("Super defence mix", Skill.DEFENCE, 2, 12),
    EXTREME_DEFENCE("Extreme defence", Skill.DEFENCE, 3, 15),
    // RANGED POTIONS
    RANGING_POTION("Ranging potion", Skill.RANGED, 1, 8),
    RANGING_MIX("Ranging mix", Skill.RANGED, 1, 8),
    SUPER_RANGING_POTION("Super ranging potion", Skill.RANGED, 2, 12),
    SUPER_RANGING_FLASK("Super ranging flask", Skill.RANGED, 2, 12),
    SUPER_RANGING_MIX("Super ranging mix", Skill.RANGED, 2, 12),
    EXTREME_RANGING("Extreme ranging", Skill.RANGED, 3, 15),
    // MAGIC POTIONS
    MAGIC_POTION("Magic potion", Skill.MAGIC, 1, 8),
    MAGIC_MIX("Magic mix", Skill.MAGIC, 1, 8),
    SUPER_MAGIC_POTION("Super magic potion", Skill.MAGIC, 2, 12),
    SUPER_MAGIC_MIX("Super magic mix", Skill.MAGIC, 2, 12),
    EXTREME_MAGIC("Extreme magic", Skill.MAGIC, 3, 15);

    private final String potionName;
    private final Skill potionSkill;
    private final int potionFlatIncrease;
    private final int potionPercentageIncrease;

    SkillPotion(String name, Skill skill, int flatIncrease, int percentageIncrease) {
        potionName = name;
        potionSkill = skill;
        potionFlatIncrease = flatIncrease;
        potionPercentageIncrease = percentageIncrease;
    }

    private static float getIncrease(Skill skill, int increase, int percent) {
        float percentOf = ((float) percent) / ((float) skill.getBaseLevel());
        return increase + (percentOf * 100);
    }

    public Skill getPotionSkill() { return potionSkill; }

    public String getPotionName() {
        return potionName;
    }

    public boolean isActive() {
        double currentBoost = this.potionSkill.getCurrentLevel() - this.potionSkill.getBaseLevel();
        float boostPercent = getIncrease(this.potionSkill, this.potionFlatIncrease, this.potionPercentageIncrease);
        float differencePercentage = (float) currentBoost / boostPercent * 100;
        return differencePercentage >= Settings.boostRefreshPercentage;
    }





}
