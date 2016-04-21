package scripts.massfighter.data;

public enum Ability {

    // Attack Abilities
    SLAUGHTER(113, "Attack", "Threshold", 30, 14213),
    SLICE(17, "Attack", "Basic", 3, 14207),
    OVERPOWER(161, "Attack", "Ultimate", 30, 14216),
    HAVOC(65, "Attack", "Basic", 10, 14210),
    BACKHAND(97, "Attack", "Basic", 15, 14212),
    FORCEFUL_BACKHAND(257, "Attack", "Threshold", 15, 22752),
    SMASH(81, "Attack", "Basic", 10, 14211),
    BARGE(33, "Attack", "Basic", 20, 14208),
    FLURRY(129, "Attack", "Threshold", 20, 14214),
    SEVER(49, "Attack", "Basic", 15, 14209),
    HURRICANE(145, "Attack", "Threshold", 20, 14215),
    MASSACRE(177, "Attack", "Ultimate", 60, 14217),
    METEOR_STRIKE(193, "Attack", "Ultimate", 60, 14218),
    BLOOD_TENDRILS(0, "Attack", "Threshold", 45, 0),
    BALANCED_STRIKE(0, "Attack", "Ultimate", 120, 0),
    // Strength Abilities
    STOMP(210, "Strength", "Threshold", 15, 22753),
    KICK(34, "Strength", "Basic", 15, 14256),
    PUNISH(50, "Strength", "Basic", 3, 14257),
    DISMEMBER(18, "Strength", "Basic", 15, 14255),
    FURY(66, "Strength", "Basic", 5, 14258),
    DESTROY(146, "Strength", "Threshold", 20, 14263),
    QUAKE(130, "Strength", "Threshold", 20, 14262),
    BERSERK(162, "Strength", "Ultimate", 60, 14264),
    CLEAVE(98, "Strength", "Basic", 10, 14260),
    ASSAULT(114, "Strength", "Threshold", 30, 14261),
    DECIMATE(82, "Strength", "Basic", 7, 14259),
    PULVERISE(194, "Strength", "Ultimate", 60, 14266),
    FRENZY(178, "Strength", "Ultimate", 60, 14265),
    // Magic Abilities
    ASPHYXIATE(118, "Magic", "Threshold", 20, 14237),
    WRACK(22, "Magic", "Basic", 3, 14231),
    OMNIPOWER(198, "Magic", "Ultimate", 30, 14242),
    DRAGON_BREATH(102, "Magic", "Basic", 10, 14236),
    SONIC_WAVE(2646, "Magic", "Basic", 5, 9314),
    IMPACT(54, "Magic", "Basic", 15, 14234),
    CONCENTRATED_BLAST(2662, "Magic", "Basic", 5, 8684),
    DEEP_IMPACT(2742, "Magic", "Threshold", 15, 22755),
    COMBUST(86, "Magic", "Basic", 15, 14235),
    SURGE(38, "Magic", "Basic", 20, 14233),
    DETONATE(134, "Magic", "Threshold", 30, 14238),
    CHAIN(70, "Magic", "Basic", 10, 14232),
    WILD_MAGIC(150, "Magic", "Threshold", 20, 14239),
    METAMORPHOSIS(166, "Magic", "Ultimate", 60, 14241),
    TSUNAMI(182, "Magic", "Ultimate", 60, 14240),
    SMOKE_TENDRILS(0, "Magic", "Threshold", 45, 0),
    SUNSHINE(0, "Magic", "Ultimate", 60, 0),
    // Ranged Abilities
    PIERCING_SHOT(21, "Ranged", "Basic", 3, 14243),
    SNAP_SHOT(117, "Ranged", "Threshold", 20, 14249),
    DEADSHOT(197, "Ranged", "Ultimate", 30, 14254),
    SNIPE(69, "Ranged", "Basic", 10, 14246),
    DAZING_SHOT(245, "Ranged", "Basic", 5, 9316),
    BINDING_SHOT(37, "Ranged", "Basic", 15, 14244),
    NEEDLE_STRIKE(261, "Ranged", "Basic", 5, 9315),
    TIGHT_BINDINGS(293, "Ranged", "Threshold", 15, 22754),
    FRAGMENTATION_SHOT(85, "Ranged", "Basic", 15, 14247),
    ESCAPE(53, "Ranged", "Basic", 20, 14245),
    RAPID_FIRE(133, "Ranged", "Threshold", 20, 14250),
    RICOCHET(101, "Ranged", "Basic", 10, 14248),
    BOMBARDMENT(149, "Ranged", "Threshold", 30, 14251),
    INCENDIARY_SHOT(165, "Ranged", "Ultimate", 60, 14252),
    UNLOAD(181, "Ranged", "Ultimate", 60, 14253),
    SHADOW_TENDRILS(0, "Ranged", "Threshold", 75, 0),
    DEATH_SWIFTNESS(0, "Ranged", "Ultimate", 60, 0),
    // Defence Abilities
    ANTICIPATION(19, "Defence", "Basic", 24, 14219),
    BASH(99, "Defence", "Basic", 15, 14224),
    REVENGE(147, "Defence", "Threshold", 45, 14227),
    PROVOKE(51, "Defence", "Basic", 10, 14221),
    IMMORTALITY(195, "Defence", "Ultimate", 120, 14230),
    FREEDOM(35, "Defence", "Basic", 30, 14220),
    REFLECT(115, "Defence", "Threshold", 30, 14225),
    RESONANCE(67, "Defence", "Basic", 30, 14222),
    REJUVENATE(179, "Defence", "Ultimate", 300, 14229),
    DEBILITATE(131, "Defence", "Threshold", 30, 14226),
    PREPARATION(83, "Defence", "Basic", 20, 14223),
    BARRICADE(163, "Defence", "Ultimate", 60, 14228),
    // Constitution Abilities - There is a new type of ability here
    // All it does is activate a weapons special (if there is one)
    WEAPON_SPECIAL_ATTACK(260, "Constitution", "Special", 0, 10594),
    // TODO get id, this is temp
    HEALING_BLADE(260, "Constitution", "Special", 0, 10594),
    REGENERATE(20, "Constitution", "Basic", 0, 14267),
    SIPHON(244, "Constitution", "Basic", 0, 14674),
    INCITE(36, "Constitution", "Basic", 0, 14268);

    private final int abilityID;
    private String abilityType;
    private final String abilityCategory;
    private final int cooldownSeconds;
    private final int textureID;

    Ability(int abilityID, String abilityType, String abilityCategory, int cooldownSeconds, int textureID) {
        this.abilityID = abilityID;
        this.abilityType = abilityType;
        this.abilityCategory = abilityCategory;
        this.cooldownSeconds = cooldownSeconds;
        this.textureID = textureID;
    }

    public String getAbilityCategory() {
        return abilityCategory;
    }

    public String getName() {
        return name();
    }


}
