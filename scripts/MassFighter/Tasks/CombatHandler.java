package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.queries.NpcQueryBuilder;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Data.Settings;
import util.Functions;

import java.util.concurrent.Callable;

public class CombatHandler extends Task {

    // Accessed by the Loot Handler
    public static Area fightArea = new Area.Circular(Players.getLocal().getPosition(), Settings.chosenFightRegion);
    private Player player = Players.getLocal();
    private final NpcQueryBuilder npcQuery = Npcs.newQuery().within(fightArea).names(Settings.chosenNpcName).reachable();

    public boolean validate() {
        return  // Player is looting
                Settings.lootCharms && needsTarget() && (LootHandler.suitableGroundItemQuery.results().isEmpty() || Inventory.isFull())
                // Player is not looting
                || !Settings.lootCharms && needsTarget();
    }

    @Override
    public void execute() {
        Settings.status = "Combat Handler is Active";
        final Npc targetNpc;
        targetNpc = npcQuery.filter(new Filter<Npc>() {
            @Override
            public boolean accepts(Npc npc) {
                return npc.getHealthGauge() == null && npc.isValid() && npc.getAnimationId() == -1;
            }
        }).results().sortByDistance().limit(3).random();
        if (targetNpc != null) {
            Settings.targetNpc = targetNpc;
            if (targetNpc.isVisible()) {
                if (targetNpc.interact("Attack", targetNpc.getName())) {
                    Execution.delayUntil(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return player.getTarget() == null;
                        }
                    }, 1600, 2000);
                } else {
                    // Possible solution to an issue where a NPC is located behind the ActionBar
                    Camera.turnTo(targetNpc);
                }
            } else if (Distance.to(targetNpc) < 4) {
                Camera.turnTo(targetNpc);
            } else {
                BresenhamPath.buildTo(targetNpc).step(true);
                Camera.turnTo(targetNpc);
            }
        }
    }

    private Boolean needsTarget() {
        return player.getTarget() == null && Functions.readyToFight();
    }

}

