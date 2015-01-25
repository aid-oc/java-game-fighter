package scripts.MassFighter.Methods;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.RuneScape;
import com.runemate.game.api.hybrid.entities.Actor;
import com.runemate.game.api.hybrid.entities.GroundItem;
import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.queries.GroundItemQueryBuilder;
import com.runemate.game.api.hybrid.region.GroundItems;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.rs3.local.hud.Powers;
import scripts.MassFighter.MassFighter;

import java.util.List;

import static scripts.MassFighter.MassFighter.combatProfile;
import static scripts.MassFighter.MassFighter.settings;

public class Methods  {

    private List<Area> areas;
    private Area[] areaArray;
    private String[] loot;

    public Methods() {
        areas = combatProfile.getFightAreas();
        areaArray = combatProfile.getFightAreas().toArray(new Area[(combatProfile.getFightAreas().size())]);
        loot = combatProfile.getLootNames();
    }


    public Boolean inFightAreas(Actor actor) {
        for (Area a : areas) {
            if (a.contains(actor)) {
                return true;
            }
        }
        return false;
    }

    public int getFightAreaIndex() {
        for (int i = 0; i < areaArray.length; i++) {
            if (areaArray[i].contains(Players.getLocal())) {
                return i;
            }
        }
        return -1;
    }

    public Area[] fightAreasAsArray() {
        return areas != null ? areaArray : new Area[]{new Area.Circular(Players.getLocal().getPosition(), 10)};
    }

    public Boolean isInCombat() {
        return Players.getLocal().getTarget() != null || !Npcs.newQuery().within(areaArray).reachable().targeting(Players.getLocal()).results().isEmpty();
    }

    public void logout() {
        if (!MassFighter.methods.isInCombat()) {
            if (RuneScape.logout()) {
                MassFighter.status = "Paused: Supplies";
                Environment.getScript().pause();
            }
        }
    }

    public Boolean readyToFight() {
        if (settings.useSoulsplit || settings.quickPray) {
            return (Powers.Prayer.getPoints() >= settings.prayValue) || (Powers.Prayer.isQuickPraying() || Powers.Prayer.Curse.SOUL_SPLIT.isActivated());
        } else if (settings.useFood) {
            return Inventory.contains(settings.food.getName()) && Health.getCurrent() >= settings.eatValue;
        } else if (Health.getCurrent() < settings.criticalHitpoints) {
            logout();
            return false;
        } else {
            return true;
        }
    }


    public GroundItemQueryBuilder validLoot = GroundItems.newQuery().within(fightAreasAsArray()).filter(new Filter<GroundItem>() {
        @Override
        public boolean accepts(GroundItem groundItem) {
            String itemName = groundItem.getDefinition().getName().toLowerCase();
            if (loot != null && loot.length > 0) {
                for (String lootName : loot) {
                    if (lootName.toLowerCase().equals(itemName)) {
                        return true;
                    }
                }
            }
            return settings.buryBones && (itemName.toLowerCase().contains("bones") || itemName.toLowerCase().contains("ashes"));
        }
    }).reachable();
}
