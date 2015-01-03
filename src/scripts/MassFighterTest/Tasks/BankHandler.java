package scripts.MassFighterTest.Tasks;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.RuneScape;
import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.Menu;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;
import com.runemate.game.api.hybrid.region.Banks;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighterTest.Framework.BankingProfile;
import scripts.MassFighterTest.MassFighterTest;

public class BankHandler extends Task {

    @Override
    public boolean validate() {
        return MassFighterTest.combatProfile instanceof BankingProfile && (Inventory.isFull() || !MassFighterTest.methods.readyToFight());
    }

    @Override
    public void execute() {
        MassFighterTest.status = "Going to the bank";
        final BankingProfile profile = (BankingProfile) MassFighterTest.combatProfile;
        if (!profile.getBankArea().contains(Players.getLocal()) && Inventory.isFull() || !MassFighterTest.methods.readyToFight()) {
            WebPath toBank = Traversal.getDefaultWeb().getPathBuilder().buildTo(profile.getBankArea());
            if (toBank != null) {
                if (Menu.isOpen()) {
                    Menu.close();
                } else {
                    System.out.println("Path to the bank is VALID: traversing");
                    toBank.step(true);
                }
            } else {
                System.out.println("Path to the bank is INVALID: traversing backup");
                BresenhamPath.buildTo(profile.getBankArea()).step(true);
            }
        } else if (profile.getBankArea().contains(Players.getLocal())) {
            System.out.println("We're at the bank - banking");
            LocatableEntity bank = Banks.getLoaded().nearest();
            if (bank != null) {
                if (bank.isVisible()) {
                    if (Bank.open()) {
                        if (MassFighterTest.combatProfile.getLootNames().length > 0) {
                            if (Inventory.containsAnyOf(MassFighterTest.combatProfile.getLootNames())) {
                                for (String i : MassFighterTest.combatProfile.getLootNames()) {
                                    if (Inventory.contains(i)) {
                                        Bank.deposit(i, Inventory.getQuantity(i));
                                    }
                                }
                            }
                        }
                        if (MassFighterTest.useFood && !Inventory.containsAnyOf(MassFighterTest.food.getName())) {
                            if (Bank.withdraw(MassFighterTest.food.getName(), 28)) {
                                System.out.println("Withdrew Food");
                            } else {
                                if (RuneScape.logout()) {
                                    MassFighterTest.status = "Paused: No food in bank to refresh supplies";
                                    Environment.getScript().pause();
                                }
                            }
                        }
                        Bank.close();
                    }
                } else {
                    Camera.turnTo(bank);
                }
            }
        } else if (!Inventory.isFull() || MassFighterTest.combatProfile.getLootNames().length > 0 && !Inventory.containsAnyOf(MassFighterTest.combatProfile.getLootNames())) {
            MassFighterTest.status = "Returning to fight area";
            WebPath toFightArea = Traversal.getDefaultWeb().getPathBuilder().buildTo(MassFighterTest.methods.fightAreasAsArray()[0]);
            if (toFightArea != null) {
                if (Menu.isOpen()) {
                    Menu.close();
                    System.out.println("Path to the fight area is VALID: traversing");
                } else {
                    toFightArea.step(true);
                }
            } else {
                System.out.println("Path to the fight area is INVALID: traversing backup");
                BresenhamPath.buildTo(MassFighterTest.methods.fightAreasAsArray()[0]).step(true);
            }
        }
    }
}
