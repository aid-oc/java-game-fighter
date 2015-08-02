package scripts.massfighter.tasks.shared;

import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.definitions.NpcDefinition;
import com.runemate.game.api.hybrid.local.hud.Menu;
import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.queries.NpcQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.hybrid.util.Filters;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import helpers.Movement;
import scripts.massfighter.framework.Methods;
import scripts.massfighter.gui.Settings;
import scripts.massfighter.MassFighter;

import static scripts.massfighter.framework.Methods.out;

public class Attack extends Task {

    private LocatableEntityQueryResults<Npc> getAttackingNpcs() {
        return Settings.bypassReachable ? Methods.attackingNpcs.results() : Methods.attackingReachableNpcs.results();
    }

    private LocatableEntityQueryResults<Npc> getSuitableNpcs() {
        NpcQueryBuilder suitableNpcQuery = Npcs.newQuery().filter(Filters.DECLINE_ALL);
        Area fightArea = Settings.fightArea;
        String[] npcNames = Settings.npcNames;
        if (fightArea != null && Methods.arrayIsValid(npcNames)) {
            suitableNpcQuery = Npcs.newQuery().within(fightArea).names(npcNames)
                    .filter(new Filter<Npc>() {
                        @Override
                        public boolean accepts(Npc npc) {
                            return npc != null && npc.getAnimationId() == -1 && npc.getId() != 1273 && (Settings.attackCombatMonsters || npc.getHealthGauge() == null);
                        }
                    });
            if (!Settings.bypassReachable) suitableNpcQuery = suitableNpcQuery.reachable();
        }
        return suitableNpcQuery.results();
    }


    public boolean validate() {
        return Health.getCurrent() >= Settings.criticalHitpoints
                && (Methods.isNotInCombat() || (Settings.tagMode && getAttackingNpcs().size() < Settings.tagSelection))
                && Loot.getLoot().results().isEmpty();
    }

    @Override
    public void execute() {

        LocatableEntityQueryResults<Npc> npcsTargettingUs = getAttackingNpcs();
        LocatableEntityQueryResults<Npc> validTargetResults = getSuitableNpcs();

        if (!validTargetResults.isEmpty()) {
            out("Combat: We need a new target");

            Npc targetNpc;
            if (npcsTargettingUs.isEmpty() || (Settings.tagMode && npcsTargettingUs.size() < Settings.tagSelection)) {
                targetNpc = validTargetResults.sortByDistance().limit(Settings.targetSelection).random();
                MassFighter.status = "Getting new target";
            } else {
                targetNpc = npcsTargettingUs.nearest();
                MassFighter.status = "Aggressive target found";
            }
            if (targetNpc != null) {
                NpcDefinition targetDefinition = targetNpc.getDefinition();
                if (targetDefinition != null) {
                    targetDefinition = targetDefinition.getLocalState() != null ? targetDefinition.getLocalState() : targetDefinition;
                    if (targetNpc.isVisible()) {
                        MassFighter.targetEntity = targetNpc;
                        if (targetNpc.interact("Attack", targetDefinition.getName())) {
                            out("Combat: Attacked a target");
                            final Npc target = targetNpc;
                            Execution.delayUntil(() -> target.getTarget() != null, 1000, 2000);
                        } else {
                            if (Menu.isOpen()) Menu.close();
                            Movement.moveToInteractable(targetNpc);
                        }
                    } else {
                        Movement.moveToInteractable(targetNpc);
                    }
                } else {
                    out("Combat: Failed to get info on our target");
                }
            } else {
                out("Combat: Invalid Target");
            }
        } else {
            out("Combat: Waiting for new targets");
            MassFighter.status = "No Targets";
        }
        Movement.resetCameraPitch();
    }

}

