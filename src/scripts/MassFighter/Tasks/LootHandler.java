package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.entities.GroundItem;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.Menu;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.MassFighter;

public class LootHandler extends Task {

    @Override
    public boolean validate() {
        return MassFighter.methods.lootAvailable() && !MassFighter.methods.isInCombat();
    }


    @Override
    public void execute() {

        MassFighter.status = "Looting";

        // Bury bones if necessary
        final SpriteItemQueryBuilder buryItems = Inventory.newQuery().actions("Bury", "Scatter");
        if (MassFighter.buryBones && !buryItems.results().isEmpty()) {
            MassFighter.status = "Burying Bones";
            SpriteItemQueryResults bones = buryItems.results();
            bones.stream().filter(bone -> bone != null).forEach(bone -> {
                String name = bone.getDefinition().getName();
                if (name.toLowerCase().contains("bones") && bone.interact("Bury") || name.toLowerCase().contains("ashes") && bone.interact("Scatter")) {
                    Execution.delayUntil(() -> !bone.isValid(), 800, 1000);
                }
            });
        }

        // Pick up loot
        if (!MassFighter.methods.validLoot.results().isEmpty() && !Inventory.isFull()) {
            GroundItem targetLoot = MassFighter.methods.validLoot.results().nearest();
            if (targetLoot != null) {
                MassFighter.status = "Picking up " + targetLoot.getDefinition().getName();
                if (targetLoot.isVisible()) {
                    if (targetLoot.interact("Take", targetLoot.getDefinition().getName())) {
                        Execution.delayUntil(() -> !targetLoot.isValid(), 2000,2600);
                    } else if (Menu.isOpen()) {
                        Menu.close();
                    }
                } else if (Distance.to(targetLoot) > 2) {
                    BresenhamPath.buildTo(targetLoot).step(true);
                } else {
                    Camera.turnTo(targetLoot);
                }
            }
        }
    }

}
