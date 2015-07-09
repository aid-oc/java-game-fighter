package scripts.MassFighter.Tasks.Shared;

import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.GUI.Settings;
import scripts.MassFighter.MassFighter;

import static scripts.MassFighter.Framework.Methods.out;

public class BuryBones extends Task {

    private final SpriteItemQueryBuilder validBuryItems = Inventory.newQuery().actions("Bury", "Scatter");

    @Override
    public boolean validate() {
        return Settings.buryBones && !validBuryItems.results().isEmpty();
    }

    @Override
    public void execute() {
        final SpriteItemQueryResults bones = validBuryItems.results();
        MassFighter.status = "Burying";
        out("BuryBones: We have bones, burying them");
        bones.stream().filter(bone -> bone != null && bone.getDefinition() != null).forEach(bone -> {
            String name = bone.getDefinition().getName().toLowerCase();
            int invCount = Inventory.getQuantity();
            if ((name.contains("bones") && bone.interact("Bury")) || (name.contains("ashes") && bone.interact("Scatter"))) {
                out("BuryBones: Buried a bone");
                Execution.delayUntil(() -> invCount != Inventory.getQuantity(), 1000, 2000);
            }
        });
    }
}

