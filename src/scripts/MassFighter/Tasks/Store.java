package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.region.Banks;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import helpers.Movement;
import scripts.MassFighter.MassFighter;

import static scripts.MassFighter.MassFighter.settings;
import static scripts.MassFighter.Framework.Methods.*;

public class Store extends Task {

    @Override
    public boolean validate() {
        return (Inventory.isFull() || !MassFighter.methods.readyToFight());
    }

    @Override
    public void execute() {
        MassFighter.status = "Banking";
        final Area bankArea = MassFighter.userProfile.getBankArea();
        if (!bankArea.contains(Players.getLocal()) && (Inventory.isFull() || !MassFighter.methods.readyToFight())) {
            out("Store: Moving to the bank area");
            Movement.pathToLocatable(bankArea);
        } else if (bankArea.contains(Players.getLocal())) {
            LocatableEntity bank = Banks.getLoaded().nearest();
            out("Store: We're at the bank area");
            if (bank != null) {
                if (bank.isVisible()) {
                    if (Bank.open()) {
                        out("Store: Opened the bank");
                        if (Bank.depositInventory()) {
                            out("Store: Deposited Inventory");
                            Execution.delayUntil(() -> !Inventory.isFull(), 800, 1000);
                        }
                        if (settings.useFood && Inventory.getQuantity(settings.foodName) < settings.foodAmount) {
                            out("Store: Withdrew Food");
                            Bank.withdraw(settings.foodName, settings.foodAmount);
                        }
                        if (Bank.close()) {
                            out("Store: Closed the bank");
                        } else {
                            out("Store: Failed to close the bank");
                        }
                    } else {
                        out("Store: Failed to open the bank");
                    }
                } else {
                    out("Store: The bank is not visible");
                    Movement.moveToInteractable(bank);
                }
            } else {
                out("Store: The bank is invalid");
            }
        }
    }
}
