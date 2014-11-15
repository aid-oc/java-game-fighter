package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.queries.NpcQueryBuilder;
import com.runemate.game.api.hybrid.region.GroundItems;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Data.Settings;
import util.Functions;

import java.util.concurrent.Callable;

/**
 * Created by Ozzy on 06/11/2014.
 */
public class CombatHandler extends Task {

    // Accepts an NPC which has a valid name, the "Attack" option, is on screen, is not interacting with anything
    // and is not in combat
    private final NpcQueryBuilder suitableNpcQuery = Npcs.newQuery().names(Settings.chosenNpcName).actions("Attack").visible().filter(new Filter<Npc>() {
        @Override
        public boolean accepts(Npc npc) {
            // NPC is not interacting with something and is not in combat
            return npc.getTarget() == null && npc.getHealthGauge() == null;
        }
    });

    private Npc targetNpc;
    private Player player = Players.getLocal();
    public static Area fightArea;

    public boolean validate() {
        fightArea = new Area.Circular(Players.getLocal().getPosition(), Settings.chosenFightRegion);
        if (Settings.lootCharms)
            return  Players.getLocal().getTarget() == null && Functions.readyToFight() && !Functions.isBusy() && GroundItems.newQuery().within(CombatHandler.fightArea).names(Settings.lootChoices).reachable().results().isEmpty();
        else return Players.getLocal().getTarget() == null && Functions.readyToFight() && !Functions.isBusy();
    }

    @Override
    public void execute() {
        Settings.status = "Combat Handler is Active";

        // Set our target to the NPC attacking us if that is the case
        final NpcQueryBuilder underAttack = Npcs.newQuery().within(fightArea).actions("Attack").filter(new Filter<Npc>() {
            @Override
            public boolean accepts(Npc npc) {
                return npc.getTarget() == player;
            }
        });
        if (!underAttack.results().isEmpty()) {
            targetNpc = underAttack.results().nearest();
        }

        /* Thanks Cloud for NpcQuery I use here */
        // Collect and store a valid npc
        if (targetNpc == null || !suitableNpcQuery.accepts(targetNpc)) {
            targetNpc = suitableNpcQuery.results().sortByDistance().limit(3).random();
        }

        // Ensure npc is valid
        if (targetNpc != null) {
            // Visibility check
            if (!targetNpc.isVisible()) {
                // Builds a path using the Bresenham line algorithm
                // This is suitable for short distances which don't require the player to navigate obstacles
                BresenhamPath.buildTo(targetNpc).step(true);
                Camera.turnTo(targetNpc);
            } else if (targetNpc.interact("Attack", targetNpc.getName())) {
                // Interaction successful
                Settings.targetNpc = targetNpc;
                Execution.delayUntil(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return !suitableNpcQuery.accepts(targetNpc);
                    }
                }, Random.nextInt(1000, 3000));
            } else {
                Camera.turnTo(targetNpc);
            }
        }
    }
}
