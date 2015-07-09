package scripts.MassFighter.Tasks.RS3;

import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.rs3.entities.SummonedFamiliar;
import com.runemate.game.api.rs3.local.hud.interfaces.Summoning;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Framework.Methods;
import scripts.MassFighter.GUI.Settings;

/**
 * ozzy.
 */
public class SummonFamiliar extends Task {

    @Override
    public boolean validate() {
        final Player player = Players.getLocal();
        return Settings.useSummoning && Settings.chosenFamiliar != null && hasPouch()
                && player.getFamiliar() == null ||  !player.getFamiliar().getInfo().equals(Settings.chosenFamiliar);
    }

    @Override
    public void execute() {
        final SummonedFamiliar familiar = Players.getLocal().getFamiliar();
        final Summoning.Familiar chosenFamiliar = Settings.chosenFamiliar;
        if (familiar != null) {
            Summoning.FamiliarOption.DISMISS_FOLLOWER.select();
        }
        int pointsRequired = chosenFamiliar.getSummoningCost();
        if (Summoning.getPoints() >= pointsRequired) {
            String pouchName = Settings.chosenFamiliar.getPouch().getName();
            SpriteItem pouch = Inventory.getItems(pouchName).first();
            if (pouch != null && pouch.interact("Summon")) {
                Methods.out("Summoned a " + pouchName);
            }
        } else {
            SpriteItemQueryResults summoningBoosts = Inventory.newQuery().names("Summoning").actions("Drink").results();
            if (!summoningBoosts.isEmpty()) {
                SpriteItem boost = summoningBoosts.random();
                if (boost != null && boost.interact("Drink")) {
                    Methods.out("Restored some summoning points");
                }
            }
        }

    }

    private boolean hasPouch() {
        return Inventory.contains(Settings.chosenFamiliar.getPouch().getName());
    }


}
