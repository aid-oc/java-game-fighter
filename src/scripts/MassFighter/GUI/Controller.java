package scripts.MassFighter.GUI;

import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.queries.NpcQueryBuilder;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import scripts.MassFighter.Data.Food;
import scripts.MassFighter.Framework.CombatProfile;
import scripts.MassFighter.MassFighter;
import scripts.MassFighter.Profiles.Powerfighting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;


public class Controller {

    @FXML
    private ListView<String> availableMonsters;
    @FXML
    private ListView<String> selectedLoot;
    @FXML
    private Button addLoot;
    @FXML
    private Button removeLoot;
    @FXML
    private TextField lootName;
    @FXML
    private Button addCharms;
    @FXML
    private ListView<String> selectedMonsters;
    @FXML
    private TextField eatValue;
    @FXML
    private TextField tileRange;
    @FXML
    private ListView<Food> foodSelection;
    @FXML
    private CheckBox abilities;
    @FXML
    private CheckBox soulsplit;
    @FXML
    private CheckBox stopWhenOutOfFood;
    @FXML
    private CheckBox lootInCombat;
    @FXML
    private CheckBox buryBones;
    @FXML
    private Slider targetSlider;
    @FXML
    private CheckBox waitLoot;
    @FXML
    private ChoiceBox<CombatProfile> profileSelector;
    @FXML
    private Button npcButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Tab lootTab;
    @FXML
    private Label profileStatus;
    @FXML
    private TextField criticalHitpoints;
    @FXML
    private Button btnStart;
    @FXML
    private CheckBox quickPray;
    @FXML
    private TextField prayValue;
    @FXML
    private CheckBox exitPrayer;
    @FXML
    private CheckBox tagMode;
    @FXML
    private Slider tagSlider;
    @FXML
    private TextField foodAmount;

    public Settings settings;

    private List<String> getAvailableMonsters(Area area, String action) {
        List<String> availableNpcs = new ArrayList<>();
        NpcQueryBuilder getNearbyNpcs = Npcs.newQuery().within(area).actions("Attack");
        Collection<Npc> npcs = getNearbyNpcs.results();
        if (!npcs.isEmpty()) {
            npcs.stream().filter(n -> n != null && !availableNpcs.contains(n.getName())).forEach(n -> {
                availableNpcs.add(n.getName());
            });
        }
        return availableNpcs;
    }

    // Initial setup
    public void initialize() {
        settings = new Settings();

        // Add onAction event handlers
        // update
        refreshButton.setOnAction(event -> {
            availableMonsters.getItems().remove(0, availableMonsters.getItems().size());
            final Player player = Players.getLocal();
            if (player != null) {
                availableMonsters.getItems().addAll(getAvailableMonsters(new Area.Circular(player.getPosition(), 20), "Attack"));
            }
        });
        npcButton.setOnAction(event -> {
            if (!selectedMonsters.getSelectionModel().getSelectedItems().isEmpty()) {
                selectedMonsters.getItems().removeAll(selectedMonsters.getSelectionModel().getSelectedItems());
            } else if (!availableMonsters.getSelectionModel().getSelectedItems().isEmpty()) {
                availableMonsters.getSelectionModel().getSelectedItems().stream().filter(s -> !selectedMonsters.getItems().contains(s)).forEach(s -> {
                    selectedMonsters.getItems().add(s);
                });
            }
        });
        tagMode.setOnAction(event -> {
            if (tagMode.isSelected()) {
                tagSlider.setDisable(false);
            } else {
                tagSlider.setDisable(true);
            }
        });

        // Disable fields with requirements
        tagSlider.setDisable(true);


        addLoot.setOnAction(this::lootChange);
        removeLoot.setOnAction(this::lootChange);
        addCharms.setOnAction(this::lootChange);
        soulsplit.setOnAction(this::togglePrayer);
        quickPray.setOnAction(this::togglePrayer);
        btnStart.setOnAction(this::start);

        // Init fields
        profileSelector.getItems().addAll(CombatProfile.getProfiles());
        profileSelector.getSelectionModel().select(0);
        foodSelection.getItems().addAll(Food.values());
        foodAmount.setText("0");
        tileRange.setText("20");
        eatValue.setText(Integer.toString(Health.getMaximum()/2));
        criticalHitpoints.setText("1000");
        prayValue.setText("2000");
        profileChanged();
        profileStatus.setText("Thanks for using MassFighter, please report any issues you have - Ozzy");
    }

    private void togglePrayer(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(soulsplit)) {
            quickPray.setSelected(false);
        } else {
            soulsplit.setSelected(false);
        }
    }

    public void profileChanged() {
        profileSelector.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!(observable.getValue() instanceof Powerfighting)) {
                togglePowerfighting(true);
            } else {
                togglePowerfighting(false);
            }
        });
    }

    public void togglePowerfighting(boolean disable) {
        availableMonsters.setDisable(disable);
        selectedMonsters.setDisable(disable);
        refreshButton.setDisable(disable);
        npcButton.setDisable(disable);
        lootTab.setDisable(disable);
        if (disable) {
            profileStatus.setText("Powerfighting features disabled, swap to Powerfighting to re-enable");
        } else {
            profileStatus.setText("Thanks for using MassFighter, please report any issues you have - Ozzy");
        }
    }


    // Start button pressed, start the script
    public void start(ActionEvent actionEvent) {
        if (Pattern.matches("\\d+", prayValue.getText()) && Pattern.matches("\\d+", foodAmount.getText()) &&  Pattern.matches("\\d+", eatValue.getText()) && Pattern.matches("\\d+", tileRange.getText()) && Pattern.matches("\\d+", criticalHitpoints.getText())) {
            if (!profileSelector.getSelectionModel().isEmpty()) {
                if (profileSelector.getSelectionModel().getSelectedItem() instanceof Powerfighting && !selectedMonsters.getItems().isEmpty()) {
                    Powerfighting profile = new Powerfighting();
                    if (!selectedLoot.getItems().isEmpty()) {
                        String[] lootNames = selectedLoot.getItems().toArray(new String[selectedLoot.getItems().size()]);
                        profile.setLootNames(lootNames);
                        settings.looting = true;
                    }
                    String[] npcNames = selectedMonsters.getItems().toArray(new String[selectedMonsters.getItems().size()]);
                    profile.setNpcNames(npcNames);
                    List<Area> areas = new ArrayList<>();
                    areas.add(new Area.Circular(Players.getLocal().getPosition(), Double.valueOf(tileRange.getText())));
                    profile.setFightAreas(areas);
                    MassFighter.combatProfile = profile;
                } else if (!(profileSelector.getSelectionModel().getSelectedItem() instanceof Powerfighting)) {
                    MassFighter.combatProfile = profileSelector.getSelectionModel().getSelectedItem();
                    settings.looting = MassFighter.combatProfile.getLootNames() != null;
                }
                if (MassFighter.combatProfile != null) {
                    if (!foodSelection.getSelectionModel().isEmpty()) {
                        settings.useFood = true;
                        settings.food = foodSelection.getSelectionModel().getSelectedItem();
                        settings.eatValue = Integer.valueOf(eatValue.getText());
                        settings.foodAmount = Integer.valueOf(foodAmount.getText());
                    } else {
                        settings.useFood = false;
                    }
                    if (quickPray.isSelected() || soulsplit.isSelected()) {
                        settings.prayValue = Integer.valueOf(prayValue.getText());
                    }
                    if (tagMode.isSelected()) {
                        settings.tagMode = true;
                        settings.tagSelection = (int) tagSlider.getValue();
                    }
                    settings.waitForLoot = waitLoot.isSelected();
                    settings.targetSelection = (int) targetSlider.getValue();
                    settings.lootInCombat = lootInCombat.isSelected();
                    settings.useAbilities = abilities.isSelected();
                    settings.quickPray = quickPray.isSelected();
                    settings.useSoulsplit = soulsplit.isSelected();
                    settings.exitOnPrayerOut = exitPrayer.isSelected();
                    settings.fightRadius = Integer.valueOf(tileRange.getText());
                    settings.criticalHitpoints = Integer.valueOf(criticalHitpoints.getText());
                    settings.exitOutFood = stopWhenOutOfFood.isSelected();
                    settings.buryBones = buryBones.isSelected();
                    MassFighter.settings = settings;
                    MassFighter.setupRunning = false;
                    closeUI();
                }
            }
        } else {
            System.out.println("Invalid value ranges");
            eatValue.setText("3000");
            tileRange.setText("20");
            criticalHitpoints.setText("1000");
        }
    }

    public void lootChange(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(addLoot)) {
            String itemName = lootName.getText();
            if (!itemName.isEmpty() && !selectedLoot.getItems().contains(itemName)) {
                selectedLoot.getItems().add(itemName);
            }
        } else if (actionEvent.getSource().equals(removeLoot)) {
            if (!selectedLoot.getSelectionModel().getSelectedItems().isEmpty()) {
                selectedLoot.getItems().removeAll(selectedLoot.getSelectionModel().getSelectedItems());
            }
        } else if (actionEvent.getSource().equals(addCharms)) {
            String[] charms = {"Crimson charm", "Gold charm", "Blue charm", "Green charm", "Elder charm"};
            for (String s : charms) {
                if (!selectedLoot.getItems().contains(s)) {
                    selectedLoot.getItems().add(s);
                }
            }
        }
    }

    public void closeUI() {
        Stage stage = (Stage) abilities.getScene().getWindow();
        stage.close();
    }
}
