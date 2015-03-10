package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.region.Banks;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Framework.Movement;
import scripts.MassFighter.MassFighter;

import static scripts.MassFighter.MassFighter.settings;

public class Store extends Task {

    @Override
    public boolean validate() {
        return  (Inventory.isFull() || !MassFighter.methods.readyToFight());
    }

    @Override
    public void execute() {
        MassFighter.status = "Banking";
        final Area bankArea = MassFighter.userProfile.getBankArea();

        if (!bankArea.contains(Players.getLocal()) && (Inventory.isFull() || !MassFighter.methods.readyToFight())) {
            Movement.pathToLocatable(bankArea);
        } else if (bankArea.contains(Players.getLocal())) {
            LocatableEntity bank = Banks.getLoaded().nearest();
            if (bank != null) {
                if (bank.isVisible()) {
                    if (Bank.open()) {
                        if (Bank.depositInventory()) {
                            Execution.delayUntil(() -> !Inventory.isFull(), 800,1000);
                        }
                        if (settings.useFood && Inventory.getQuantity(settings.foodName) < settings.foodAmount) {
                            Bank.withdraw(settings.foodName, settings.foodAmount);
                        }
                        Bank.close();
                    }
                } else {
                    Movement.moveToLocatable(bank);
                }
            }
        }
    }
}
