package scripts.MassFighter.GUI;

import scripts.MassFighter.Data.Potion;

import java.util.ArrayList;
import java.util.List;

public class Settings {

    public int targetSelection = 1;
    public Boolean useFood = false;
    public Boolean showOutline = true;
    public Boolean exitOutFood = false;
    public Boolean lootInCombat = false;
    public Boolean useAbilities = false;
    public Boolean useSoulsplit = false;
    public Boolean waitForLoot = false;
    public Boolean looting = false;
    public Boolean buryBones = false;
    public Boolean quickPray = false;
    public Boolean exitOnPrayerOut = false;
    public Boolean tagMode = false;
    public Boolean attackCombatMonsters = false;
    public Boolean bypassReachable = false;
    public Boolean revolutionMode = false;
    public Boolean lootByValue = false;
    public Boolean equipAmmunition = false;
    public Boolean soulsplitPermanent = false;
    public int soulsplitPercentage = 100;
    public double lootValue = 0;
    public int tagSelection = 0;
    public int foodAmount = 0;
    public String[] foodNames;
    public int fightRadius = 20;
    public int eatValue = 0;
    public int prayValue = 0;
    public int criticalHitpoints = 0;
    public List<Potion> selectedPotions = new ArrayList<>();
    public double boostRefreshPercentage = 55;


}
