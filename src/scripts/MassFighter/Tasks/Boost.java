package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.entities.definitions.ItemDefinition;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Data.Potion;
import scripts.MassFighter.MassFighter;

import static scripts.MassFighter.Framework.Methods.*;

public class Boost extends Task {

    private Potion potionToBoost;

    @Override
    public boolean validate() {
        if (!MassFighter.settings.selectedPotions.isEmpty()) {
            for (Potion p : MassFighter.settings.selectedPotions) {
                Skill skill = p.getPotionSkills()[0];

                double currentBoost = skill.getCurrentLevel() - skill.getBaseLevel();
                float differencePercentage = (float) currentBoost / (float) p.getBoost() * 100;
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
                ItemDefinition oldDefinition = potion.getDefinition();
                if (potion.interact("Drink")) {
                    out("Boost: Interacted with the boost item");
                    Execution.delayUntil(() -> potion.getDefinition() == null || !potion.getDefinition().equals(oldDefinition), 1500, 2500);
                    potionToBoost = null;
                }
            } else {
                out("Boost: Target boost item is invalid");
            }
        } else {
            out("Boost: Target boost is invalid");
        }
    }

}
