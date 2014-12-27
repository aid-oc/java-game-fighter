package scripts.MassFighter.Methods;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.RuneScape;
import com.runemate.game.api.hybrid.entities.Actor;
import com.runemate.game.api.hybrid.entities.GroundItem;
import com.runemate.game.api.hybrid.entities.Npc;
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

import java.util.Arrays;
import java.util.List;

import static scripts.MassFighter.MassFighter.combatProfile;

public class Methods  {

    public Methods() {

    }

    public Boolean inFightAreas(Actor actor) {
        List<Area> areas = combatProfile.getFightAreas();
        for (Area a : areas) {
            if (a.contains(actor)) {
                return true;
            }
        }
        return false;
    }

    public int getFightAreaIndex() {
        Area[] areas = MassFighter.combatProfile.getFightAreas().toArray(new Area[(MassFighter.combatProfile.getFightAreas().size())]);
        for (int i = 0; i < areas.length; i++) {
            if (areas[i].contains(Players.getLocal())) {
                return i;
            }
        }
        return -1;
    }

    public Area[] fightAreasAsArray() {
        return combatProfile.getFightAreas() != null ? combatProfile.getFightAreas().toArray(new Area[combatProfile.getFightAreas().size()]) : new Area[]{new Area.Circular(Players.getLocal().getPosition(), 10)};
    }

    public Boolean isInCombat() {
        Area[] fightArea = combatProfile.getFightAreas()
                .toArray(new Area[combatProfile.getFightAreas().size()]);
        return Players.getLocal().getTarget() != null || !Npcs.newQuery().within(fightArea).reachable().filter(new Filter<Npc>() {
            @Override
            public boolean accepts(Npc npc) {
                return npc.getTarget() != null && npc.getTarget().equals(Players.getLocal());
            }
        }).results().isEmpty();
    }

    public void logout() {
        if (!MassFighter.methods.isInCombat()) {
            if (RuneScape.logout()) {
                MassFighter.status = "Paused: out of supplies";
                Environment.getScript().pause();
            }
        }
    }

    public Boolean readyToFight() {
        if (MassFighter.useSoulsplit) {
            return Powers.Prayer.getPoints() > Powers.Prayer.getMaximumPoints() / 2
                    && Powers.Prayer.Curse.SOUL_SPLIT.isActivated();
        } else if (MassFighter.useFood) {
            return Inventory.contains(MassFighter.food.getName()) && Health.getCurrent() >= MassFighter.eatValue;
        }
        return Health.getCurrent() >= MassFighter.eatValue;
    }

    public GroundItemQueryBuilder validLoot = GroundItems.newQuery().within(MassFighter.combatProfile.getFightAreas().toArray(new Area[(MassFighter.combatProfile.getFightAreas().size())])).filter(new Filter<GroundItem>() {
        @Override
        public boolean accepts(GroundItem groundItem) {
            String itemName = groundItem.getDefinition().getName().toLowerCase();
            List<String> lootNames = Arrays.asList(MassFighter.combatProfile.getLootNames());
            for (String lootName : lootNames) {
                if (lootName.toLowerCase().equals(itemName) || MassFighter.buryBones && (itemName.toLowerCase().contains("bones") || itemName.toLowerCase().contains("ashes"))) {
                    return true;
                }
            }
            return false;
        }
    });

    public boolean lootAvailable() {
        return (MassFighter.buryBones || MassFighter.looting || MassFighter.combatProfile.getLootNames().length > 0) && !validLoot.results().isEmpty();
    }
}
