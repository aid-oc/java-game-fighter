package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Data.Potion;
import scripts.MassFighter.MassFighter;

import java.util.ArrayList;
import java.util.List;

public class Boost extends Task {

    List<Potion> needsBoosting = new ArrayList<>();

    @Override
    public boolean validate() {
        if (!MassFighter.settings.selectedPotions.isEmpty()) {
            for (Potion p : MassFighter.settings.selectedPotions) {
                Skill skill = p.getPotionSkills()[0];
                if ((skill.getCurrentLevel() < skill.getBaseLevel() + p.getBoost()-(p.getBoost()/Random.nextInt(3, 4))) && !Inventory.newQuery().filter(new Filter<SpriteItem>() {
                    @Override
                    public boolean accepts(SpriteItem spriteItem) {
                        return spriteItem.getDefinition().getName().contains(p.getPotionName());
                    }
                }).results().isEmpty()) {
                    needsBoosting.add(p);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void execute() {
        System.out.println("Time to refresh boosts");
        for (Potion p : needsBoosting) {
            SpriteItem potion = Inventory.newQuery().filter(new Filter<SpriteItem>() {
                @Override
                public boolean accepts(SpriteItem spriteItem) {
                    return spriteItem.getDefinition().getName().contains(p.getPotionName());
                }
            }).results().random();
            if (potion != null) {
                String oldName = potion.getDefinition().getName();
                if (potion.interact("Drink")) {
                    Execution.delayUntil(() -> !potion.getDefinition().getName().equals(oldName), 1500, 2500);
                }
            }
        }

    }

}
