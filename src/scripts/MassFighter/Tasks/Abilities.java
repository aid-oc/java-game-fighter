package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.ActionBar;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.SlotAction;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Data.Ability;
import scripts.MassFighter.MassFighter;

import java.util.ArrayList;
import java.util.List;

import static scripts.MassFighter.Framework.Methods.*;

public class Abilities extends Task implements Runnable {


    private List<SlotAction> ultimates = new ArrayList<>();
    private List<SlotAction> thresholds = new ArrayList<>();
    private List<SlotAction> basics = new ArrayList<>();
    private List<SlotAction> specials = new ArrayList<>();

    private List<SlotAction> abilities = new ArrayList<>();


    public Abilities() {
        abilities = sortAbilities(ActionBar.getActions());
    }

    @Override
    public boolean validate() {
        return Players.getLocal().getTarget() != null;
    }

    @Override
    public void execute() {
        if (!abilities.isEmpty()) {
            for (SlotAction ability : abilities) {
                if (ability != null) {
                    if (ability.getName() != null && ability.isActivatable() && ability.isReady()) {
                        if (ability.activate()) {
                            out("Abilities: Used: " + ability.getName());
                            Execution.delayUntil(() -> !ability.isReady(), 1000, 1600);
                            break;
                        }
                    }
                }
            }
        } else {
            abilities = sortAbilities(ActionBar.getActions());
        }
    }


    private List<SlotAction> sortAbilities(List<SlotAction> abilities) {

        List<SlotAction> sortedAbilities = new ArrayList<>();

        for (Ability enumAbility : Ability.values()) {
            for (SlotAction barAbility : abilities) {
                if (barAbility.getName() != null && barAbility.getType().equals(SlotAction.Type.ABILITY)) {
                    if (barAbility.getName().toLowerCase().equals("death's swiftness")) {
                        ultimates.add(barAbility);
                    }
                    if (enumAbility.getName().toLowerCase().equals(barAbility.getName().replaceAll(" ", "_").toLowerCase())) {
                        switch (enumAbility.getAbilityCategory()) {
                            case "Ultimate":
                                ultimates.add(barAbility);
                                break;
                            case "Threshold":
                                thresholds.add(barAbility);
                                break;
                            case "Basic":
                                if (!MassFighter.settings.revolutionMode) {
                                    basics.add(barAbility);
                                }
                                break;
                            case "Special":
                                specials.add(barAbility);
                                break;
                        }
                    }
                }
            }
        }

        sortedAbilities.addAll(specials);
        sortedAbilities.addAll(ultimates);
        sortedAbilities.addAll(thresholds);
        sortedAbilities.addAll(basics);

        if (!ActionBar.isLocked()) ActionBar.toggleLock();

        return sortedAbilities;
    }

    @Override
    public void run() {
        if (this.validate()) {
            this.execute();
        }
    }
}
