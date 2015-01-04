package scripts.MassFighter.ProfileTasks;


import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;
import com.runemate.game.api.hybrid.queries.GameObjectQueryBuilder;
import com.runemate.game.api.hybrid.region.Banks;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Framework.BankingProfile;
import scripts.MassFighter.MassFighter;

import javax.swing.*;

public class HillGiantsRoute extends Task{

    @Override
    public boolean validate() {
        return (Inventory.isFull() || (Inventory.containsAnyOf(MassFighter.combatProfile.getLootNames()) && !MassFighter.methods.inFightAreas(Players.getLocal()))) || !Inventory.containsAnyOf(MassFighter.combatProfile.getLootNames()) && !MassFighter.methods.inFightAreas(Players.getLocal());
    }

    @Override
    public void execute() {
        MassFighter.status = "Hill Giants: Following Route";
        if (Inventory.contains("Brass key")) {

            final Area fightArea = MassFighter.combatProfile.getFightAreas().get(0);
            final BankingProfile bankProfile = (BankingProfile) MassFighter.combatProfile;
            final Area bankArea = bankProfile.getBankArea();
            final Player player = Players.getLocal();

            final Area hutLadderArea = new Area.Polygonal(new Coordinate(3118, 3454, 0), new Coordinate(3112, 3454, 0), new Coordinate(3112, 3450, 0), new Coordinate(3118, 3450, 0));
            final Area caveLadderArea = new Area.Polygonal(new Coordinate(3114, 9853, 0), new Coordinate(3114, 9850, 0),
                    new Coordinate(3116, 9848, 0), new Coordinate(3119, 9850, 0), new Coordinate(3119, 9854, 0), new Coordinate(3115, 9855, 0));
            final GameObjectQueryBuilder closedHutDoorQuery = GameObjects.newQuery().actions("Open");

            if (Inventory.isFull() && !bankArea.contains(player)) {
                if (fightArea.contains(player)) {
                    if (!caveLadderArea.contains(player)) {
                        System.out.println("Walking to ladder (below)");
                        BresenhamPath.buildTo(caveLadderArea).step(true);
                    } else {
                        final GameObject caveLadder = GameObjects.newQuery().within(caveLadderArea).actions("Climb-up").results().nearest();
                        if (caveLadder != null) {
                            if (caveLadder.isVisible()) {
                                System.out.println("Climbing up the ladder");
                                if (caveLadder.interact("Climb-up")) {
                                    Execution.delayUntil(() -> !caveLadderArea.contains(player), 1500, 2000);
                                }
                            } else {
                                Camera.turnTo(caveLadder);
                            }
                        }
                    }
                } else if (hutLadderArea.contains(player) && !closedHutDoorQuery.results().isEmpty()) {
                    GameObject door = closedHutDoorQuery.results().nearest();
                    if (door != null) {
                        if (door.isVisible()) {
                            System.out.println("Opening door");
                            if (door.interact("Open")) {
                                Execution.delayUntil(() -> !door.getDefinition().getActions().contains("Open"), 1500, 2000);
                            }
                        } else {
                            Camera.turnTo(door);
                        }
                    }
                } else {
                    WebPath toBank = Traversal.getDefaultWeb().getPathBuilder().buildTo(bankArea);
                    if (toBank != null) {
                        System.out.println("Walking WEB to bank");
                        toBank.step(true);
                    } else {
                        System.out.println("Walking BACKUP to bank");
                        BresenhamPath.buildTo(bankArea).step(true);
                    }
                }
            } else if (Inventory.containsAnyOf(MassFighter.combatProfile.getLootNames()) && bankArea.contains(player)) {
                LocatableEntity bank = Banks.getLoaded().nearest();
                if (bank != null) {
                    if (bank.isVisible()) {
                        if (Bank.open()) {
                            Bank.depositAllExcept(new Filter<SpriteItem>() {
                                @Override
                                public boolean accepts(SpriteItem spriteItem) {
                                    String name = spriteItem.getDefinition().getName();
                                    return (MassFighter.food != null && (MassFighter.food.getName().equals(name)) || name.equals("Brass key"));
                                }
                            });
                            if (MassFighter.useFood && MassFighter.food != null && Inventory.getQuantity(MassFighter.food.getName()) < 10) {
                                System.out.println("Withdrawing food");
                                if (Bank.withdraw(MassFighter.food.getName(), 10)) {
                                    Execution.delayUntil(() -> Inventory.contains(MassFighter.food.getName()));
                                }
                            }
                            Bank.close();
                        }
                    }
                }
            } else if (!Inventory.containsAnyOf(MassFighter.combatProfile.getLootNames()) && !fightArea.contains(player)) {
                final Area doorArea = new Area.Polygonal(new Coordinate(3119, 3449, 0),
                        new Coordinate(3115, 3449, 0), new Coordinate(3116, 3445, 0), new Coordinate(3119, 3446, 0));
                if (Bank.isOpen()){
                    Bank.close();
                }
                if (!doorArea.contains(player) && !hutLadderArea.contains(player)) {
                    WebPath toDoor = Traversal.getDefaultWeb().getPathBuilder().buildTo(doorArea);
                    if (toDoor != null) {
                        System.out.println("Walking WEB to door");
                        toDoor.step(true);
                    } else {
                        System.out.println("Walking BACKUP to door");
                        BresenhamPath.buildTo(doorArea).step(true);
                    }
                } else if (doorArea.contains(player)) {
                    System.out.println("In door area");
                    if (!closedHutDoorQuery.results().isEmpty()) {
                        GameObject door = closedHutDoorQuery.results().nearest();
                        if (door != null) {
                            if (door.isVisible()) {
                                System.out.println("Opening door");
                                if (door.interact("Open")) {
                                    Execution.delayUntil(() -> !door.getDefinition().getActions().contains("Open"), 1500, 2000);
                                }
                            } else {
                                Camera.turnTo(door);
                            }
                        }
                    } else {
                        System.out.println("No doors!");
                    }
                } else if (hutLadderArea.contains(player)) {
                    final GameObject hutLadder = GameObjects.newQuery().within(hutLadderArea).actions("Climb-Down").results().nearest();
                    if (hutLadder != null) {
                        if (hutLadder.isVisible()) {
                            System.out.println("Climbing down");
                            if (hutLadder.interact("Climb-Down")) {
                                Execution.delayUntil(() -> fightArea.contains(player), 1500, 2000);
                            }
                        } else {
                            Camera.turnTo(hutLadder);
                        }
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "You need a Brass key to run this profile", "MassFighter", JOptionPane.WARNING_MESSAGE);
        }
    }
}
