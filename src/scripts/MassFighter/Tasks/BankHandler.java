package scripts.MassFighter.Tasks;

import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Data.Settings;

public class BankHandler extends Task {

    @Override
    public boolean validate() {
        return Settings.profileBankArea != null && (Inventory.isFull() || (!Inventory.isFull()) && notInFightAreas());


    }

    @Override
    public void execute() {
        if (Inventory.isFull()) {
            if (!Settings.profileBankArea.contains(Players.getLocal())) {
                WebPath routeToBank = Traversal.getDefaultWeb().getPathBuilder().buildTo(Settings.profileBankArea);
                if (routeToBank != null) {
                    System.out.println("Successfully generated route to Bank");
                    routeToBank.step(true);
                }
            } else {
                System.out.println("We're at the bank - banking");
                if (Bank.open()) {
                    for (String i : Settings.lootChoices.toArray(new String[Settings.lootChoices.size()])) {
                        if (Inventory.contains(i)) {
                            Bank.deposit(i, Inventory.getQuantity(i));
                        }
                    }
                }
            }
        } else {
            System.out.println("Returning to fight areas");
            WebPath routeToFightArea = Traversal.getDefaultWeb().getPathBuilder().buildTo(Settings.fightAreas.toArray(new Area[Settings.fightAreas.size()])[0]);
            if (routeToFightArea != null) {
                System.out.println("Successfully generated route to fight area");
                routeToFightArea.step(true);
            }
        }

    }

    private Boolean notInFightAreas() {
        int areaCheck = 0;
        for (Area a : Settings.fightAreas) {
            if (!a.contains(Players.getLocal())) {
                areaCheck++;
            }
        }
        System.out.println("Area check result: " + (areaCheck == Settings.fightAreas.size()));
        return areaCheck == Settings.fightAreas.size();
    }
}
