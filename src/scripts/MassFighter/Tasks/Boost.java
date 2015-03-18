package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Data.Potion;
import scripts.MassFighter.MassFighter;


public class Boost extends Task {

    Potion potionToBoost;

    @Override
    public boolean validate() {
        if (!MassFighter.settings.selectedPotions.isEmpty()) {
            for (Potion p : MassFighter.settings.selectedPotions) {
                Skill skill = p.getPotionSkills()[0];

                double currentBoost = skill.getCurrentLevel()-skill.getBaseLevel();
                float differencePercentage = (float)currentBoost/(float)p.getBoost()*100;
                /*
                System.out.println("------ Boost Info ------");
                System.out.println("Boost Item: " + p.getPotionName());
                System.out.println("Boost Skill: " + skill);
                System.out.println("We are currently at " + differencePercentage+"% of the max boost");
                System.out.println("Refreshing boost below " + MassFighter.settings.boostRefreshPercentage + "%");
                System.out.println("-----------------------");
                */
                if (differencePercentage < MassFighter.settings.boostRefreshPercentage && !Inventory.newQuery().filter(new Filter<SpriteItem>() {
                    @Override
                    public boolean accepts(SpriteItem spriteItem) {
                        return spriteItem.getDefinition().getName().contains(p.getPotionName());
                    }
                }).results().isEmpty()) {
                    potionToBoost = p;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void execute() {

        MassFighter.status = "Boosting";
        if (potionToBoost != null) {
            SpriteItem potion = Inventory.newQuery().filter(new Filter<SpriteItem>() {
                @Override
                public boolean accepts(SpriteItem spriteItem) {
                    return spriteItem.getDefinition().getName().contains(potionToBoost.getPotionName());
                }
            }).results().random();
            if (potion != null) {
                String oldName = potion.getDefinition().getName();
                if (potion.interact("Drink")) {
                    Execution.delayUntil(() -> !potion.getDefinition().getName().equals(oldName), 1500, 2500);
                    potionToBoost = null;
                }
            }
        }
    }

}
