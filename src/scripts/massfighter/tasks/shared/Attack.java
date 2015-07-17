package scripts.massfighter.tasks.shared;

import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.entities.definitions.NpcDefinition;
import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.queries.NpcQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
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

    private NpcQueryBuilder getAttackingNpcs() {
        NpcQueryBuilder attackingNpcQuery = Npcs.newQuery().filter(Filters.DECLINE_ALL);
        Player player = Players.getLocal();
        if (player != null) {
            attackingNpcQuery = Npcs.newQuery().actions("Attack").targeting(player).filter(new Filter<Npc>() {
                @Override
                public boolean accepts(Npc npc) {
                    return npc != null && npc.getPosition() != null;
                }
            });
            if (!Settings.bypassReachable) attackingNpcQuery = attackingNpcQuery.reachable();
        }
        return attackingNpcQuery;
    }

    private NpcQueryBuilder getSuitableNpcs() {
        NpcQueryBuilder suitableNpcQuery = Npcs.newQuery().filter(Filters.DECLINE_ALL);
        Area fightArea = Settings.fightArea;
        String[] npcNames = Settings.npcNames;
        if (fightArea != null && Methods.arrayIsValid(npcNames)) {
            suitableNpcQuery = Npcs.newQuery().within(fightArea).names(npcNames)
                    .filter(new Filter<Npc>() {
                        @Override
                        public boolean accepts(Npc npc) {
                            return npc != null && npc.getPosition() != null && (Settings.attackCombatMonsters ? npc.isValid()
                                    : npc.isValid() && npc.getId() != 1273 && npc.getTarget() == null && npc.getHealthGauge() == null);
                        }
                    });
            if (!Settings.bypassReachable) suitableNpcQuery = suitableNpcQuery.reachable();
        }
        return suitableNpcQuery;
    }


    public boolean validate() {
        return Health.getCurrent() > Settings.criticalHitpoints
                && (!Methods.isInCombat() || Settings.tagMode && getAttackingNpcs().results().size() < Settings.tagSelection)
                && Loot.getLoot().results().isEmpty();
    }

    @Override
    public void execute() {

        final Player player = Players.getLocal();
        LocatableEntityQueryResults<Npc> npcsTargettingUs = getAttackingNpcs().results();

        if (!isBusy() || npcsTargettingUs.isEmpty() && player.getTarget() == null || (Settings.tagMode && npcsTargettingUs.size() < Settings.tagSelection)) {
            final LocatableEntityQueryResults<Npc> validTargetResults = getSuitableNpcs().results();
            if (!validTargetResults.isEmpty()) {
                out("Combat: We need a new target");
                MassFighter.status = "Finding Target";
                Npc targetNpc;
                if (npcsTargettingUs.isEmpty() || Settings.tagMode && npcsTargettingUs.size() < Settings.tagSelection) {
                    targetNpc = validTargetResults.sortByDistance().limit(Settings.targetSelection).random();
                } else {
                    targetNpc = npcsTargettingUs.random();
                }
                if (targetNpc != null) {
                    final NpcDefinition targetNpcDefinition = targetNpc.getDefinition();
                    if (targetNpcDefinition != null) {
                        if (targetNpc.isVisible()) {
                            MassFighter.targetEntity = targetNpc;
                            if (targetNpc.interact("Attack", targetNpcDefinition.getName())) {
                                out("Combat: Attacked a target");
                                final Npc target = targetNpc;
                                Execution.delayUntil(() -> target.getTarget() != null, 1000, 2000);
                            } else {
                                out("Combat: NPC interaction failed");
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
        } else {
            MassFighter.status = "In Combat";
            LocatableEntity target = player.getTarget();
            if (target != null) MassFighter.targetEntity = target;
        }

    }

    private boolean isBusy() {
        Player player = Players.getLocal();
        return player != null && (player.getTarget() != null || player.getAnimationId() != -1);
    }

}

