package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.local.hud.Menu;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Framework.BankingProfile;
import scripts.MassFighter.MassFighter;

import java.util.List;

public class BankHandler extends Task {
    @Override
    public boolean validate() {
        return MassFighter.combatProfile instanceof BankingProfile
                && ((Inventory.isFull() || (!Inventory.isFull()) && !inFightAreas())) ||
                (!CombatHandler.readyToFight() && MassFighter.useFood && MassFighter.food != null && !Inventory.containsAnyOf(MassFighter.food.getName()));


    }

    @Override
    public void execute() {
        final BankingProfile profile = (BankingProfile)MassFighter.combatProfile;

        if (Menu.isOpen()) {
            Menu.close();
        }
        if (Inventory.isFull() || MassFighter.useFood && MassFighter.food != null && !Inventory.containsAnyOf(MassFighter.food.getName())) {
            if (!profile.getBankArea().contains(Players.getLocal())) {
                WebPath toBank = Traversal.getDefaultWeb().getPathBuilder().buildTo(profile.getBankArea());
                if (toBank != null) {
                    toBank.step(true);
                }
            } else {
                System.out.println("We're at the bank - banking");
                if (Bank.open()) {
                    if (Inventory.containsAnyOf(MassFighter.combatProfile.getLootNames())) {
                        for (String i : MassFighter.combatProfile.getLootNames()) {
                            if (Inventory.contains(i)) {
                                Bank.deposit(i, Inventory.getQuantity(i));
                            }
                        }
                    }
                    if (MassFighter.useFood && MassFighter.food != null && !Inventory.containsAnyOf(MassFighter.food.getName())) {
                        if (Bank.withdraw(MassFighter.food.getName(), 28)) {
                            System.out.println("Withdrew Food");
                        }
                    }
                }
                Bank.close();
            }
        } else {
            System.out.println("Returning to fight areas");
            WebPath toFight = Traversal.getDefaultWeb().getPathBuilder().buildTo(MassFighter.combatProfile.getFightAreas().get(0));
            if (toFight != null) {
                toFight.step(true);
            }
        }

    }

    private Boolean inFightAreas() {
        List<Area> areas = MassFighter.combatProfile.getFightAreas();
        for (Area a : areas) {
            if (a.contains(Players.getLocal())) {
                return true;
            }
        }
        return false;
    }
}
