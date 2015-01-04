package scripts.MassFighter.ProfileTasks;

import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.Menu;
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
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;
import scripts.MassFighter.Framework.BankingProfile;
import scripts.MassFighter.MassFighter;
import scripts.MassFighter.Profiles.LumbridgeCows;

public class LumbridgeCowsRoute extends Task {

    @Override
    public boolean validate() {
        return MassFighter.combatProfile instanceof LumbridgeCows && Inventory.isFull() || !MassFighter.methods.inFightAreas(Players.getLocal()) && !Inventory.isFull();
    }

    @Override
    public void execute() {
        MassFighter.status = "Lumbridge Cows: Following Route";

        final Area gateArea = new Area.Polygonal(new Coordinate(3251, 3265, 0), new Coordinate(3254, 3265, 0), new Coordinate(3254, 3268, 0), new Coordinate(3251, 3268, 0));
        final Player player = Players.getLocal();
        GameObjectQueryBuilder obstacleQuery = GameObjects.newQuery().names("Gate").actions("Open").within(gateArea);
        final BankingProfile profile = (BankingProfile)MassFighter.combatProfile;

        if (Menu.isOpen()) {
            Menu.close();
        }
        if (!Inventory.isFull() && !MassFighter.methods.inFightAreas(Players.getLocal())) {
            if (Bank.isOpen()) {
                Bank.close();
            }
            if (!gateArea.contains(player)) {
                System.out.println("Going to gate area");
                BresenhamPath.buildTo(gateArea).step(true);
            } else {
                if (!obstacleQuery.results().isEmpty()) {
                    System.out.println("Going to obstacle");
                    GameObject obstacle = obstacleQuery.results().nearest();
                    if (obstacle != null) {
                        if (Distance.to(obstacle) > 4) {
                            BresenhamPath.buildTo(obstacle).step(true);
                        } else if (!obstacle.isVisible()) {
                            Camera.turnTo(obstacle);
                        } else {
                            if (obstacle.interact("Open")) {
                                System.out.println("Opened gate");
                            }
                        }
                    }
                } else {
                    System.out.println("Going to fight area");
                    BresenhamPath.buildTo(MassFighter.combatProfile.getFightAreas().get(0)).step(true);
                }
            }
        } else if (Inventory.isFull()) {
            if (!obstacleQuery.results().isEmpty() && MassFighter.methods.inFightAreas(Players.getLocal())) {
                GameObject obstacle = obstacleQuery.results().nearest();
                if (obstacle != null) {
                    if (Distance.to(obstacle) > 4) {
                        BresenhamPath.buildTo(obstacle).step(true);
                    } else if (!obstacle.isVisible()) {
                        Camera.turnTo(obstacle);
                    } else {
                        if (obstacle.interact("Open")) {
                            System.out.println("Opened gate");
                        }
                    }
                }
            } else {
                final Area bankArea = profile.getBankArea();
                if (!bankArea.contains(player)) {
                    WebPath routeToBank = Traversal.getDefaultWeb().getPathBuilder().buildTo(bankArea);
                    if (routeToBank != null) {
                        routeToBank.step(true);
                    } else {
                        BresenhamPath.buildTo(bankArea).step(true);
                    }
                } else {
                    LocatableEntity bank = Banks.getLoaded().nearest();
                    if (bank != null) {
                        if (bank.isVisible()) {
                            if (Bank.open()) {
                                Bank.depositAllExcept(new Filter<SpriteItem>() {
                                    @Override
                                    public boolean accepts(SpriteItem spriteItem) {
                                        String name = spriteItem.getDefinition().getName();
                                        return (MassFighter.food != null && (MassFighter.food.getName().equals(name)));
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
                }
            }
        }
    }
}
