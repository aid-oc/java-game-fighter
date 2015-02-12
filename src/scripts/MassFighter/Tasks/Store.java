package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.Menu;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;
import com.runemate.game.api.hybrid.region.Banks;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Framework.UserProfile;
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
        final UserProfile userProfile = MassFighter.userProfile;
        final Area bankArea = MassFighter.userProfile.getBankArea();
        final Area fightArea = userProfile.getFightArea();

        if (!bankArea.contains(Players.getLocal()) && (Inventory.isFull() || !MassFighter.methods.readyToFight())) {
            WebPath toBank = Traversal.getDefaultWeb().getPathBuilder().buildTo(bankArea);
            if (toBank != null) {
                if (Menu.isOpen()) {
                    Menu.close();
                } else {
                    toBank.step(true);
                }
            } else {
                BresenhamPath path = BresenhamPath.buildTo(bankArea);
                if (path != null) {
                    path.step(true);
                }
            }
        } else if (bankArea.contains(Players.getLocal())) {
            LocatableEntity bank = Banks.getLoaded().nearest();
            if (bank != null) {
                if (bank.isVisible()) {
                    if (com.runemate.game.api.hybrid.local.hud.interfaces.Bank.open()) {
                        if (com.runemate.game.api.hybrid.local.hud.interfaces.Bank.depositInventory()) {
                            Execution.delayUntil(() -> !Inventory.isFull(), 800,1000);
                        }
                        if (settings.useFood && Inventory.getQuantity(settings.foodName) < settings.foodAmount) {
                            com.runemate.game.api.hybrid.local.hud.interfaces.Bank.withdraw(settings.foodName, settings.foodAmount);
                        }
                        com.runemate.game.api.hybrid.local.hud.interfaces.Bank.close();
                    }
                } else {
                    Camera.turnTo(bank);
                }
            }
        } else if (!Inventory.isFull() || MassFighter.userProfile.getLootNames() != null && !Inventory.containsAnyOf(MassFighter.userProfile.getLootNames())) {
            WebPath toFightArea = Traversal.getDefaultWeb().getPathBuilder().buildTo(fightArea);
            if (toFightArea != null) {
                if (Menu.isOpen()) {
                    Menu.close();
                } else {
                    toFightArea.step(true);
                }
            } else {
                BresenhamPath.buildTo(fightArea).step(true);
            }
        }
    }
}
