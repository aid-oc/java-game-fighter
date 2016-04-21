package scripts.massfighter.tasks.rs3;

import com.runemate.game.api.hybrid.entities.Actor;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.queries.NpcQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.ActionBar;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.massfighter.data.Ability;
import scripts.massfighter.gui.Settings;

import java.util.ArrayList;
import java.util.List;

import static scripts.massfighter.framework.Methods.out;

public class Abilities extends Task implements Runnable {

    private List<ActionBar.Slot> abilities = new ArrayList<>();
    private NpcQueryBuilder getNearbyTargets(Player player) {
        return player != null ? Npcs.newQuery().targeting(player).actions("Attack") : null;
    }

    public boolean validate() {
        Player player;
        Npc target;
        Actor potentialTarget;
        LocatableEntityQueryResults<Npc> nearbyAttackableNcps;
        return Settings.useAbilities && (player = Players.getLocal()) != null && (potentialTarget = player.getTarget()) != null && (potentialTarget instanceof Npc) && (target = (Npc)potentialTarget) != null
                && (nearbyAttackableNcps = getNearbyTargets(player).results()) != null && !nearbyAttackableNcps.isEmpty() && nearbyAttackableNcps.contains(target);
    }

    @Override
    public void execute() {
        if (!ActionBar.isExpanded()) ActionBar.toggleExpansion();
        if (!ActionBar.isLocked()) ActionBar.toggleLock();
        if (!ActionBar.isAutoRetaliating()) ActionBar.toggleAutoRetaliation();
        if (!abilities.isEmpty()) {
            for (ActionBar.Slot ability : abilities) {
                if (ability != null) {
                    if (ability.getName() != null && ability.isReady()) {
                        if (ability.activate()) {
                            out("Abilities: Used: " + ability.getName());
                            Execution.delayUntil(() -> !ability.isReady(), 1000, 1600);
                            break;
                        }
                    }
                }
            }
        } else {
            abilities = sortAbilities(ActionBar.getFilledSlots().asList());
        }
    }


    private List<ActionBar.Slot> sortAbilities(List<ActionBar.Slot> abilities) {
        List<ActionBar.Slot> ultimates = new ArrayList<>();
        List<ActionBar.Slot> thresholds = new ArrayList<>();
        List<ActionBar.Slot> basics = new ArrayList<>();
        List<ActionBar.Slot> specials = new ArrayList<>();
        List<ActionBar.Slot> sortedAbilities = new ArrayList<>();
        for (Ability enumAbility : Ability.values()) {
            for (ActionBar.Slot barAbility : abilities) {
                String abilityName;
                if ((abilityName = barAbility.getName()) != null/* && barAbility.getType().equals(ActionBar.Slot.ContentType.ABILITY)*/) {
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
