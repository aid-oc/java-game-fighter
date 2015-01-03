package scripts.MassFighterTest.Methods;

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
import scripts.MassFighterTest.MassFighterTest;

import java.util.Arrays;
import java.util.List;

import static scripts.MassFighterTest.MassFighterTest.combatProfile;

public class Methods  {

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
        Area[] areas = MassFighterTest.combatProfile.getFightAreas().toArray(new Area[(MassFighterTest.combatProfile.getFightAreas().size())]);
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
        if (!MassFighterTest.methods.isInCombat()) {
            if (RuneScape.logout()) {
                MassFighterTest.status = "Paused: out of supplies";
                Environment.getScript().pause();
            }
        }
    }

    public Boolean readyToFight() {
        if (MassFighterTest.useSoulsplit) {
            return Powers.Prayer.getPoints() > Powers.Prayer.getMaximumPoints() / 2;
        } else if (MassFighterTest.useFood) {
            return Inventory.contains(MassFighterTest.food.getName()) && Health.getCurrent() >= MassFighterTest.eatValue;
        }
        return Health.getCurrent() >= MassFighterTest.criticalHitpoints;
    }

    public GroundItemQueryBuilder validLoot = GroundItems.newQuery().within(MassFighterTest.combatProfile.getFightAreas().toArray(new Area[(MassFighterTest.combatProfile.getFightAreas().size())])).filter(new Filter<GroundItem>() {
        @Override
        public boolean accepts(GroundItem groundItem) {
            String itemName = groundItem.getDefinition().getName().toLowerCase();
            List<String> lootNames = Arrays.asList(MassFighterTest.combatProfile.getLootNames());
            for (String lootName : lootNames) {
                if (lootName.toLowerCase().equals(itemName) || MassFighterTest.buryBones && (itemName.toLowerCase().contains("bones") || itemName.toLowerCase().contains("ashes"))) {
                    return true;
                }
            }
            return false;
        }
    });
}
