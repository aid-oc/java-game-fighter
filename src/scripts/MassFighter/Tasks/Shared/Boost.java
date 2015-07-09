package scripts.MassFighter.Tasks.Shared;

import com.runemate.game.api.hybrid.entities.definitions.ItemDefinition;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Data.SkillPotion;
import scripts.MassFighter.Framework.Methods;
import scripts.MassFighter.GUI.Settings;
import scripts.MassFighter.MassFighter;

import static scripts.MassFighter.Framework.Methods.out;

public class Boost extends Task {

    private SkillPotion skillPotionToBoost;

    private Boolean boostNeedsRefreshing() {
        String[] selectedPotions = Settings.selectedPotions;
        if (Methods.arrayIsValid(selectedPotions)) {
            for (String s : selectedPotions) {
                if (potionExists(s)) {
                    SkillPotion p = SkillPotion.valueOf(s);
                    if (!p.isActive() && !Inventory.newQuery().filter(new Filter<SpriteItem>() {
                        @Override
                        public boolean accepts(SpriteItem spriteItem) {
                            return spriteItem.getDefinition().getName().contains(p.getPotionName());
                        }
                    }).results().isEmpty()) {
                        skillPotionToBoost = p;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Boolean potionExists(String name) {
        SkillPotion[] validSkillPotions = SkillPotion.values();
        for (SkillPotion p : validSkillPotions) {
            if (p.name().equals(name)) return true;
        }
        return false;
    }



    @Override
    public boolean validate() {
        return Methods.arrayIsValid(Settings.selectedPotions) && boostNeedsRefreshing();
    }

    @Override
    public void execute() {
        MassFighter.status = "Boosting";
        if (skillPotionToBoost != null) {
            SpriteItem potion = Inventory.newQuery().filter(new Filter<SpriteItem>() {
                @Override
                public boolean accepts(SpriteItem spriteItem) {
                    return spriteItem.getDefinition().getName().contains(skillPotionToBoost.getPotionName());
                }
            }).results().random();
            if (potion != null) {
                ItemDefinition oldDefinition = potion.getDefinition();
                if (potion.interact("Drink")) {
                    out("Boost: Interacted with the boost item");
                    Execution.delayUntil(() -> potion.getDefinition() == null || !potion.getDefinition().equals(oldDefinition), 1500, 2500);
                    skillPotionToBoost = null;
                }
            } else {
                out("Boost: Target boost item is invalid");
            }
        } else {
            out("Boost: Target boost is invalid");
        }
    }

}
