package scripts.MassFighter.Data;

import com.runemate.game.api.hybrid.local.Skill;

public enum Potion {

    /*
        - Take Potion selections from GUI by populating list with enum contents
        - Loop through each selection constant in the enum
        - Check if the current boost is a set % range below the ideal boost
        - Renew the boost
     */

    // MULTIPLE


    // ATTACK POTIONS
    ATTACK_POTION("Attack potion", getIncrease(Skill.ATTACK, 1, 8), Skill.ATTACK),
    ATTACK_MIX("Attack mix", getIncrease(Skill.ATTACK, 1, 8), Skill.ATTACK),
    SUPER_ATTACK_POTION("Super attack", getIncrease(Skill.ATTACK, 2, 12), Skill.ATTACK),
    SUPER_ATTACK_MIX("Super attack mix", getIncrease(Skill.ATTACK, 2, 12), Skill.ATTACK),
    EXTREME_ATTACK("Extreme attack", getIncrease(Skill.ATTACK, 3, 15), Skill.ATTACK);

    // todo potions with multiple stat boosts, zam brew etc

    private String potionName;
    private Skill[] potionSkills;
    private int potionBoost;

    public int getBoost() {
        return potionBoost;
    }

    public Skill[] getPotionSkills() {
        return potionSkills;
    }

    public String getPotionName() {
        return potionName;
    }

    private Potion(String name, int boost,  Skill... skills) {
        potionName = name;
        potionSkills = skills;
        potionBoost = boost;
    }

    private static int getIncrease(Skill skill, int increase, int percent) {
        return increase + (skill.getBaseLevel()/100*percent);
    }





}
