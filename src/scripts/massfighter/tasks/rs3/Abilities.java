package scripts.massfighter.tasks.rs3;

import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.queries.NpcQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.ActionBar;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.SlotAction;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.massfighter.data.Ability;
import scripts.massfighter.gui.Settings;

import java.util.ArrayList;
import java.util.List;

import static scripts.massfighter.framework.Methods.out;

public class Abilities extends Task implements Runnable {

    private List<SlotAction> abilities = new ArrayList<>();
    private NpcQueryBuilder getNearbyTargets(Player player) {
        return player != null ? Npcs.newQuery().targeting(player).actions("Attack") : null;
    }

    public boolean validate() {
        Player player;
        Npc target;
        LocatableEntityQueryResults<Npc> nearbyAttackableNcps;
        return (player = Players.getLocal()) != null && (target = (Npc)player.getTarget()) != null
                && (nearbyAttackableNcps = getNearbyTargets(player).results()) != null && !nearbyAttackableNcps.isEmpty() && nearbyAttackableNcps.contains(target);
    }

    @Override
    public void execute() {
        if (!ActionBar.isExpanded()) ActionBar.toggleExpansion();
        if (!ActionBar.isLocked()) ActionBar.toggleLock();
        if (!ActionBar.isAutoRetaliating()) ActionBar.toggleAutoRetaliation();
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
        List<SlotAction> ultimates = new ArrayList<>();
        List<SlotAction> thresholds = new ArrayList<>();
        List<SlotAction> basics = new ArrayList<>();
        List<SlotAction> specials = new ArrayList<>();
        List<SlotAction> sortedAbilities = new ArrayList<>();
        for (Ability enumAbility : Ability.values()) {
            for (SlotAction barAbility : abilities) {
                String abilityName;
                if ((abilityName = barAbility.getName()) != null && barAbility.getType().equals(SlotAction.Type.ABILITY)) {
                    if (abilityName.toLowerCase().equals("death's swiftness")) {
                        ultimates.add(barAbility);
                    }
                    if (enumAbility.getName().toLowerCase().equals(abilityName.replaceAll(" ", "_").toLowerCase())) {
                        switch (enumAbility.getAbilityCategory()) {
                            case "Ultimate":
                                ultimates.add(barAbility);
                                break;
                            case "Threshold":
                                thresholds.add(barAbility);
                                break;
                            case "Basic":
                                if (!Settings.revolutionMode) {
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
        return sortedAbilities;
    }

    @Override
    public void run() {
        if (this.validate()) {
            this.execute();
        }
    }
}
