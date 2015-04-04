package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.entities.GroundItem;
import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.entities.definitions.NpcDefinition;
import com.runemate.game.api.hybrid.queries.GroundItemQueryBuilder;
import com.runemate.game.api.hybrid.queries.NpcQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.GroundItems;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.hybrid.util.StopWatch;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Framework.Movement;
import scripts.MassFighter.MassFighter;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static scripts.MassFighter.MassFighter.settings;
import static scripts.MassFighter.MassFighter.userProfile;
import static scripts.MassFighter.Framework.Methods.*;

public class Combat extends Task {

    private GroundItemQueryBuilder validLoot = GroundItems.newQuery().within(userProfile.getFightArea()).filter(new Filter<GroundItem>() {
        @Override
        public boolean accepts(GroundItem groundItem) {
            if (MassFighter.methods.hasRoomForItem(groundItem)) {
                String itemName = groundItem.getDefinition().getName().toLowerCase();
                return ((settings.lootByValue && MassFighter.methods.isWorthLooting(groundItem)) || Arrays.asList(userProfile.getLootNames()).contains(itemName) || (settings.buryBones && (itemName.contains("bones") || itemName.contains("ashes"))));
            }
            return false;
        }
    });
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
        return MassFighter.methods.readyToFight() && validLoot.results().isEmpty();
    }

    @Override
    public void execute() {
        final Player player = Players.getLocal();
        final LocatableEntityQueryResults<Npc> npcsTargettingUs = underAttackQuery.results();

        if (npcsTargettingUs.isEmpty() || (MassFighter.settings.tagMode && npcsTargettingUs.size() < MassFighter.settings.tagSelection) || ((!timeSinceLastCombat.isRunning()
                || timeSinceLastCombat.getRuntime(TimeUnit.SECONDS) > Random.nextInt(2, 4)) && player.getTarget() == null && player.getAnimationId() == -1 && player.getHealthGauge() == null)) {
            final LocatableEntityQueryResults<Npc> validTargetResults = validTargets.results();
            if (!validTargetResults.isEmpty()) {
                out("Combat: We need a new target");
                MassFighter.status = "Finding Target";
                final Npc targetNpc = validTargetResults.sortByDistance().limit(settings.targetSelection).random();
                if (targetNpc != null) {
                    final NpcDefinition targetNpcDefinition = targetNpc.getDefinition();
                    if (targetNpcDefinition != null) {
                        if (targetNpc.isVisible()) {
                            MassFighter.targetEntity = targetNpc;
                            if (targetNpc.interact("Attack", targetNpcDefinition.getName())) {
                                out("Combat: Attacked a target");
                                Execution.delayUntil(() -> targetNpc.getTarget() != null, 1000, 2000);
                                if (!timeSinceLastCombat.isRunning()) {
                                    timeSinceLastCombat.start();
                                }
                                timeSinceLastCombat.reset();
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
            if (timeSinceLastCombat.isRunning() && (timeSinceLastCombat.getRuntime(TimeUnit.SECONDS) % 5 == 0)) {
                out("Combat: Fighting - Last interaction was " + timeSinceLastCombat.getRuntime(TimeUnit.SECONDS) + " seconds ago");
            }
            LocatableEntity target = player.getTarget();
            if (target != null) MassFighter.targetEntity = target;
        }

    }
}

