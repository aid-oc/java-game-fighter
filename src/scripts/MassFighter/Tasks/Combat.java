package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.queries.NpcQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.hybrid.util.StopWatch;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Framework.Movement;
import scripts.MassFighter.MassFighter;

import java.util.concurrent.TimeUnit;

import static scripts.MassFighter.MassFighter.settings;
import static scripts.MassFighter.MassFighter.userProfile;

public class Combat extends Task {

    private final NpcQueryBuilder underAttackQuery = Npcs.newQuery().targeting(Players.getLocal()).reachable();
    private final NpcQueryBuilder validTargets = Npcs.newQuery().within(userProfile.getFightArea()).names(userProfile.getNpcNames())
            .filter(new Filter<Npc>() {
                @Override
                public boolean accepts(Npc npc) {
                    return (settings.attackCombatMonsters ? npc != null && npc.isValid()
                            : npc != null && npc.isValid() && npc.getTarget() == null && npc.getHealthGauge() == null) && (settings.bypassReachable || npc.getPosition().isReachable());
                }
            });
    private final StopWatch timeSinceLastCombat = new StopWatch();

    public boolean validate() {
        return userProfile.getBankArea() != null ? MassFighter.methods.readyToFight() && Loot.validLoot.results().isEmpty()
                : MassFighter.methods.readyToFight() && Loot.validLoot.results().isEmpty();
    }

    @Override
    public void execute() {
        final Player player = Players.getLocal();
        final LocatableEntityQueryResults<Npc> npcsTargettingUs = underAttackQuery.results();

        if (npcsTargettingUs.isEmpty() || (MassFighter.settings.tagMode && npcsTargettingUs.size() < MassFighter.settings.tagSelection) || ((!timeSinceLastCombat.isRunning()
                || timeSinceLastCombat.getRuntime(TimeUnit.SECONDS) > Random.nextInt(2, 4)) && player.getTarget() == null && player.getAnimationId() == -1 && player.getHealthGauge() == null)) {
            final LocatableEntityQueryResults<Npc> validTargetResults = validTargets.results();
            if (!validTargetResults.isEmpty()) {
                MassFighter.status = "Finding Target";
                final Npc targetNpc = validTargetResults.sortByDistance().limit(settings.targetSelection).random();
                if (targetNpc != null && targetNpc.getSpotAnimationIds().isEmpty()) {
                    MassFighter.targetEntity = targetNpc;
                    if (targetNpc.isVisible()) {
                        if (targetNpc.interact("Attack", targetNpc.getDefinition().getName())) {
                            Execution.delayUntil(() -> targetNpc.getTarget() != null, 1000, 2000);
                            if (timeSinceLastCombat.isRunning()) timeSinceLastCombat.stop();
                            timeSinceLastCombat.reset();
                            timeSinceLastCombat.start();
                        }
                    } else {
                        Movement.moveToLocatable(targetNpc);
                    }
                }
            } else {
                MassFighter.status = "No Targets";
            }
        } else {
            MassFighter.status = "In Combat";
            LocatableEntity target = player.getTarget();
            if (target != null) MassFighter.targetEntity = target;
        }

    }
}

