package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.MassFighter;

import static scripts.MassFighter.Framework.Methods.*;

public class BuryBones extends Task {

    private final SpriteItemQueryBuilder validBuryItems = Inventory.newQuery().actions("Bury", "Scatter");

    @Override
    public boolean validate() {
        return MassFighter.settings.buryBones && !validBuryItems.results().isEmpty();
    }

    @Override
    public void execute() {
        final SpriteItemQueryResults bones = validBuryItems.results();
        MassFighter.status = "Burying";
        out("BuryBones: We have bones, burying them");
        bones.stream().filter(bone -> bone != null).forEach(bone -> {
            String name = bone.getDefinition().getName();
            if (name.toLowerCase().contains("bones") && bone.interact("Bury") || name.toLowerCase().contains("ashes") && bone.interact("Scatter")) {
                out("BuryBones: Buried a bone");
                Execution.delayUntil(() -> !bone.isValid(), 1000, 1400);
            }
        });
    }
}

