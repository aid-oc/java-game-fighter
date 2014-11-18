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

    private Player player = Players.getLocal();
    public static Area fightArea = new Area.Circular(Players.getLocal().getPosition(), Settings.chosenFightRegion);

    public boolean validate() {
        return Settings.lootCharms &&  player.getTarget() == null && Functions.readyToFight() &&
                (LootHandler.suitableGroundItemQuery.results().isEmpty() || Inventory.isFull())
                || !Settings.lootCharms && player.getTarget() == null && Functions.readyToFight();
    }

    @Override
    public void execute() {
        Settings.status = "Combat Handler is Active";

        final Npc targetNpc;
        NpcQueryBuilder idealNpcQuery = Npcs.newQuery().within(fightArea).names(Settings.chosenNpcName).reachable();
        NpcQueryBuilder possibleNpcQuery = Npcs.newQuery().reachable().filter(new Filter<Npc>() {
            @Override
            public boolean accepts(Npc npc) {
                return npc.getTarget() == player;
            }
        });
        if (!possibleNpcQuery.results().isEmpty()) {
            targetNpc = possibleNpcQuery.results().nearest();
        } else {
            targetNpc = idealNpcQuery.results().nearest();
        }

        if (targetNpc != null) {
            if (targetNpc.isVisible()) {
                if (targetNpc.interact("Attack", targetNpc.getName())) {
                    Execution.delayUntil(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return targetNpc.getTarget() != player;
                        }
                    }, 1600, 2000);
                } else {
                    // Possible solution to an issue where a NPC is located behind the ActionBar
                    Camera.turnTo(targetNpc);
                }
            } else if (Distance.to(targetNpc) < 4) {
                BresenhamPath.buildTo(targetNpc).step(true);
                Camera.turnTo(targetNpc);
            } else {
                Camera.turnTo(targetNpc);
            }
        }
    }
}

