package scripts.MassFighter.GUI;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.queries.NpcQueryBuilder;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.io.ManagedProperties;
import com.runemate.game.api.rs3.local.hud.interfaces.Summoning;
import com.runemate.game.api.script.framework.AbstractScript;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import scripts.MassFighter.Data.SkillPotion;
import scripts.MassFighter.MassFighter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


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
    private ListView<String> foodSelection;
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
    private Button btnStart;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnLoad;
    @FXML
    private Tab tabAreas;
    @FXML
    private CheckBox revolutionMode;
    @FXML
    private TextField lootValue;
    @FXML
    private CheckBox lootByValue;
    @FXML
    private Button btnAddToAlch;
    @FXML
    private ListView<String> selectedAlchLoot;
    @FXML
    private CheckBox showOutline;
    @FXML
    private ListView<String> availableBoosts;
    @FXML
    private ListView<String> selectedBoosts;
    @FXML
    private Button boostButton;
    @FXML
    private CheckBox attackCombatMonsters;
    @FXML
    private CheckBox bypassReachable;
    @FXML
    private ListView<String> updatesList;
    @FXML
    private Button btnAddToNotepaper;
    @FXML
    private ListView<String> selectedNotepaperLoot;
    @FXML
    private Slider boostRefreshPercentage;
    @FXML
    private CheckBox reequipAmmunition;
    @FXML
    private CheckBox soulsplitPerm;
    @FXML
    private Slider soulsplitPercentage;
    @FXML
    private TextField txtFoodInput;
    @FXML
    private Button btnAddFood;
    @FXML
    private Button btnRemoveFood;
    @FXML
    private Label settingsStatus;
    @FXML
    private ListView<Summoning.Familiar> listFamiliars;
    @FXML
    private Button btnDeselectFamiliar;
    @FXML
    private CheckBox safetyLogout;
    @FXML
    private CheckBox safetyTeleport;

    private List<String> getAvailableMonsters(Area area, String action) {
        List<String> availableNpcs = new ArrayList<>();
        NpcQueryBuilder getNearbyNpcs = Npcs.newQuery().within(area).actions(action);
        Collection<Npc> npcs = getNearbyNpcs.results();
        if (!npcs.isEmpty()) {
            npcs.stream().filter(n -> n != null && !availableNpcs.contains(n.getName())).forEach(n -> availableNpcs.add(n.getName()));
        }
        return availableNpcs;
    }

    public void initialize() {

        selectedBoosts.getItems().clear();

        AbstractScript script = Environment.getScript();
        if (script != null) {
            ManagedProperties managedProperties = script.getSettings();
            if (managedProperties != null) {
                String npcProperty = managedProperties.getProperty("npcNames");
                if (npcProperty != null && npcProperty.length() > 0) {
                    settingsStatus.setText("Cloud settings are available");
                } else {
                    settingsStatus.setText("You currently have no stored settings");
                }
            }
        }

        // Temporary Updates Solution
        List<String> updates = new ArrayList<>();
        updates.add("22/06/2015: Settings/GUI Fixes");
        updates.add("17/06/2015: Saving, Pet, SkillPotion fixes");
        updates.add("11/05/2015: Fix for 'click here to continue'");
        updates.add("06/06/2015: Profit calc re-enabled, new loot interface support added, bug fixes");
        updates.add("09/05/2015: Profit calc temporarily disabled");
        updates.add("07/04/2015: Saving rework, blocked experiment (level 51)");
        updates.add("06/04/2015: Custom areas DISABLED for rework");
        updates.add("06/04/2015: Combat improvements");
        updates.add("18/03/2015: Notepaper support");
        updates.add("18/03/2015: Boost revamp");
        updates.add("14/03/2015: Combat fixes");
        updates.add("14/03/2015: Noted/Stackable looting now works");
        updates.add("11/03/2015: Combat should now be fixed");
        updates.add("10/03/2015: Fixed quickpraying, fixed alchemy, looting changes");
        updates.add("09/03/2015: Added OSRS boosts");
        updates.add("03/03/2015: Added potion support + quickpraying for OSRS");
        updates.add("02/03/2015: Added more target finding options, you may need to remake profiles ");
        // List was adding 2 of each item for some reason
        updates.stream().filter(n -> !updatesList.getItems().contains(n)).forEach(n -> updatesList.getItems().add(n));

        for (SkillPotion skillPotion : SkillPotion.values()) {
            availableBoosts.getItems().add(skillPotion.toString());
        }

        listFamiliars.getItems().addAll(Summoning.Familiar.values());
        listFamiliars.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        btnDeselectFamiliar.setOnAction(event -> listFamiliars.getSelectionModel().getSelectedItems().clear());

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
                availableMonsters.getSelectionModel().getSelectedItems().stream().filter(s -> !selectedMonsters.getItems().contains(s)).forEach(s -> selectedMonsters.getItems().add(s));
            }
        });
        boostButton.setOnAction(event -> {
            if (!selectedBoosts.getSelectionModel().getSelectedItems().isEmpty()) {
                selectedBoosts.getItems().removeAll(selectedBoosts.getSelectionModel().getSelectedItems());
            } else if (!availableBoosts.getSelectionModel().getSelectedItems().isEmpty()) {
                availableBoosts.getSelectionModel().getSelectedItems().stream().filter(s -> !selectedBoosts.getItems().contains(s)).forEach(s -> selectedBoosts.getItems().add(s));
            }
        });
        tagMode.setOnAction(event -> {
            if (tagMode.isSelected()) {
                tagSlider.setDisable(false);
            } else {
                tagSlider.setDisable(true);
            }
        });

        btnAddFood.setOnAction(event -> {
            if (!txtFoodInput.getText().isEmpty() && !foodSelection.getItems().contains(txtFoodInput.getText())) {
                foodSelection.getItems().add(txtFoodInput.getText());
                txtFoodInput.clear();
            }
        });

        btnRemoveFood.setOnAction(event -> {
            if (!foodSelection.getSelectionModel().isEmpty()) {
                foodSelection.getItems().removeAll(foodSelection.getSelectionModel().getSelectedItems());
            }
        });

        btnSave.setOnAction(event -> {
            save();
        });

        btnLoad.setOnAction(event -> {
            load();
        });

        abilities.setOnAction(event -> {
            if (abilities.isSelected()) {
                revolutionMode.setDisable(false);
            } else {
                revolutionMode.setDisable(true);
            }
        });

        soulsplitPerm.setOnAction(event -> soulsplitPercentage.setDisable(soulsplitPerm.isSelected()));


        // Toggles the lootByValue boolean which sets whether or not the script will attempt to lookup
        // and loot items above the set value.
        lootByValue.setOnAction(event -> lootValue.setDisable(!lootByValue.isSelected()));

        // Adds the currently selected item in the loot list to the alchemy items list
        btnAddToAlch.setOnAction(event -> {
            String selectedItem = selectedLoot.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                if (!selectedAlchLoot.getItems().contains(selectedItem)) {
                    selectedAlchLoot.getItems().add(selectedItem);
                }
            }
        });

        // Adds the currently selected item in the alchemy list to the notepaper items list
        btnAddToNotepaper.setOnAction(event -> {
            String selectedItem = selectedLoot.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                if (!selectedNotepaperLoot.getItems().contains(selectedItem)) {
                    selectedNotepaperLoot.getItems().add(selectedItem);
                }
            }
        });

        // Disable fields with requirements
        tagSlider.setDisable(true);
        revolutionMode.setDisable(true);
        lootValue.setDisable(true);

        addLoot.setOnAction(this::lootChange);
        removeLoot.setOnAction(this::lootChange);
        addCharms.setOnAction(this::lootChange);
        soulsplit.setOnAction(this::togglePrayer);
        quickPray.setOnAction(this::togglePrayer);
        btnStart.setOnAction(this::start);

        // Init fields
        tileRange.setText("10");
        eatValue.setText(Integer.toString(Health.getMaximum() / 2));
        if (Environment.isRS3()) {
            criticalHitpoints.setText("500");
            prayValue.setText("50");
        } else {
            criticalHitpoints.setText("5");
            prayValue.setText("5");
        }
        boostRefreshPercentage.setValue(50);

    }


    private void togglePrayer(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(soulsplit)) {
            quickPray.setSelected(false);
        } else {
            soulsplit.setSelected(false);
        }
    }


    // Start button pressed, start the script
    private void start(ActionEvent actionEvent) {
        if (run()) {
            MassFighter.setupRunning = false;
            closeUI();
        } else {
            System.out.println("Failed to start, incorrect settings?");
        }
    }

    private boolean run() {
        if (!selectedMonsters.getItems().isEmpty() && isNumeric(tileRange.getText()) && isNumeric(prayValue.getText()) && isNumeric(eatValue.getText()) && isNumeric(criticalHitpoints.getText())) {
            // Create a settings object and store the settings
            if (!foodSelection.getItems().isEmpty()) {
                List<String> foodNames = foodSelection.getItems().stream().map(String::toLowerCase).collect(Collectors.toList());
                Settings.foodNames = foodNames.toArray(new String[foodNames.size()]);
                Settings.eatValue = Integer.valueOf(eatValue.getText());
            }
            if (quickPray.isSelected() || soulsplit.isSelected()) {
                Settings.prayValue = Integer.valueOf(prayValue.getText());
            }
            if (tagMode.isSelected()) {
                Settings.tagMode = true;
                Settings.tagSelection = (int) tagSlider.getValue();
            }
            Settings.attackCombatMonsters = attackCombatMonsters.isSelected();
            Settings.bypassReachable = bypassReachable.isSelected();
            Settings.showOutline = showOutline.isSelected();
            Settings.waitForLoot = waitLoot.isSelected();
            Settings.targetSelection = (int) targetSlider.getValue();
            Settings.lootInCombat = lootInCombat.isSelected();
            Settings.useAbilities = abilities.isSelected();
            Settings.fightRadius = Integer.valueOf(tileRange.getText());
            Settings.fightArea = new Area.Circular(Players.getLocal().getPosition(), Settings.fightRadius);
            Settings.revolutionMode = revolutionMode.isSelected();
            Settings.soulsplitPermanent = soulsplitPerm.isSelected();
            Settings.soulsplitPercentage = (int) soulsplitPercentage.getValue();
            Settings.quickPray = quickPray.isSelected();
            Settings.useSoulsplit = soulsplit.isSelected();
            Settings.exitOnPrayerOut = exitPrayer.isSelected();
            Settings.criticalHitpoints = Integer.valueOf(criticalHitpoints.getText());
            Settings.exitOutFood = stopWhenOutOfFood.isSelected();
            Settings.buryBones = buryBones.isSelected();
            Settings.equipAmmunition = reequipAmmunition.isSelected();
            Settings.safetyLogout = safetyLogout.isSelected();
            Settings.safetyTeleport = safetyTeleport.isSelected();
            if (!listFamiliars.getSelectionModel().getSelectedItems().isEmpty()) {
                Settings.useSummoning = true;
                Settings.chosenFamiliar = listFamiliars.getSelectionModel().getSelectedItem();
            }
            if (!selectedBoosts.getItems().isEmpty()) {
                Settings.selectedPotions = selectedBoosts.getItems().toArray(new String[(selectedBoosts.getItems().size())]);
                Settings.boostRefreshPercentage = boostRefreshPercentage.getValue();
            }
            if (!selectedLoot.getItems().isEmpty()) {
                List<String> lootNames = new ArrayList<>();
                lootNames.addAll(selectedLoot.getItems().stream().map(String::toLowerCase).collect(Collectors.toList()));
                Settings.lootNames = lootNames.toArray(new String[lootNames.size()]);
            }
            if (lootByValue.isSelected() && isNumeric(lootValue.getText())) {
                Settings.lootByValue = true;
                Settings.lootValue = Double.valueOf(lootValue.getText());
            }
            if (!selectedAlchLoot.getItems().isEmpty()) {
                List<String> alchLoot = new ArrayList<>();
                alchLoot.addAll(selectedAlchLoot.getItems().stream().map(String::toLowerCase).collect(Collectors.toList()));
                Settings.alchLoot = alchLoot.toArray(new String[alchLoot.size()]);
            }
            if (!selectedNotepaperLoot.getItems().isEmpty()) {
                List<String> notepaperLoot = new ArrayList<>();
                notepaperLoot.addAll(selectedNotepaperLoot.getItems().stream().map(String::toLowerCase).collect(Collectors.toList()));
                Settings.notepaperLoot = notepaperLoot.toArray(new String[notepaperLoot.size()]);
            }
            Settings.npcNames = selectedMonsters.getItems().toArray(new String[(selectedMonsters.getItems().size())]);
            return true;
        }
        return false;
    }


    private void lootChange(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(addLoot)) {
            String itemName = lootName.getText();
            if (!itemName.isEmpty() && !selectedLoot.getItems().contains(itemName)) {
                selectedLoot.getItems().add(itemName);
            }
        } else if (actionEvent.getSource().equals(removeLoot)) {
            if (!selectedLoot.getSelectionModel().getSelectedItems().isEmpty()) {
                selectedLoot.getItems().removeAll(selectedLoot.getSelectionModel().getSelectedItems());
            } else if (!selectedAlchLoot.getSelectionModel().getSelectedItems().isEmpty()) {
                selectedAlchLoot.getItems().removeAll(selectedAlchLoot.getSelectionModel().getSelectedItems());
            } else if (!selectedNotepaperLoot.getSelectionModel().getSelectedItems().isEmpty()) {
                selectedNotepaperLoot.getItems().removeAll(selectedNotepaperLoot.getSelectionModel().getSelectedItems());
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

    private boolean load() {
        AbstractScript script = Environment.getScript();
        if (script != null) {
            ManagedProperties managedProperties = script.getSettings();
            if (managedProperties != null) {
                String npcProperty = managedProperties.getProperty("npcNames");
                if (npcProperty != null && npcProperty.length() > 0) {
                    String[] npcNames = managedProperties.getProperty("npcNames").split(",");
                    selectedMonsters.getItems().clear();
                    selectedMonsters.getItems().addAll(npcNames);
                    //
                    String[] lootNames = managedProperties.getProperty("lootNames").split(",");
                    selectedLoot.getItems().clear();
                    selectedLoot.getItems().addAll(lootNames);
                    //
                    String[] alchLootNames = managedProperties.getProperty("alchLoot").split(",");
                    selectedAlchLoot.getItems().clear();
                    selectedAlchLoot.getItems().addAll(alchLootNames);
                    //
                    String[] notepaperLootNames = managedProperties.getProperty("notepaperLoot").split(",");
                    selectedNotepaperLoot.getItems().clear();
                    selectedNotepaperLoot.getItems().addAll(notepaperLootNames);

                    String targetSelectionString = managedProperties.getProperty("targetSelection");
                    if (isNumeric(targetSelectionString)) {
                        targetSlider.setValue(Integer.valueOf(targetSelectionString));
                    }
                    showOutline.setSelected(Boolean.valueOf(managedProperties.getProperty("showOutline")));
                    stopWhenOutOfFood.setSelected(Boolean.valueOf(managedProperties.getProperty("exitOutFood")));
                    safetyLogout.setSelected(Boolean.valueOf(managedProperties.getProperty("safetyLogout")));
                    safetyTeleport.setSelected(Boolean.valueOf(managedProperties.getProperty("safetyTeleport")));
                    lootInCombat.setSelected(Boolean.valueOf(managedProperties.getProperty("lootInCombat")));
                    abilities.setSelected(Boolean.valueOf(managedProperties.getProperty("useAbilities")));
                    soulsplit.setSelected(Boolean.valueOf(managedProperties.getProperty("useSoulsplit")));
                    waitLoot.setSelected(Boolean.valueOf(managedProperties.getProperty("waitForLoot")));
                    buryBones.setSelected(Boolean.valueOf(managedProperties.getProperty("buryBones")));
                    quickPray.setSelected(Boolean.valueOf(managedProperties.getProperty("quickPray")));
                    exitPrayer.setSelected(Boolean.valueOf(managedProperties.getProperty("exitOnPrayerOut")));
                    tagMode.setSelected(Boolean.valueOf(managedProperties.getProperty("tagMode")));
                    attackCombatMonsters.setSelected(Boolean.valueOf(managedProperties.getProperty("attackCombatMonsters")));
                    bypassReachable.setSelected(Boolean.valueOf(managedProperties.getProperty("bypassReachable")));
                    revolutionMode.setSelected(Boolean.valueOf(managedProperties.getProperty("revolutionMode")));
                    lootByValue.setSelected(Boolean.valueOf(managedProperties.getProperty("lootByValue")));
                    reequipAmmunition.setSelected(Boolean.valueOf(managedProperties.getProperty("equipAmmunition")));
                    soulsplitPerm.setSelected(Boolean.valueOf(managedProperties.getProperty("soulsplitPermanent")));
                    //
                    String soulsplitPercentageString = managedProperties.getProperty("soulsplitPercentage");
                    if (isNumeric(soulsplitPercentageString)) {
                        soulsplitPercentage.setValue(Integer.valueOf(soulsplitPercentageString));
                    }
                    //
                    String lootValueString = managedProperties.getProperty("lootValue");
                    if (isNumeric(lootValueString)) {
                        lootValue.setText(lootValueString);
                    }
                    //
                    String tagSelectionString = managedProperties.getProperty("tagSelection");
                    if (isNumeric(tagSelectionString)) {
                        tagSlider.setValue(Integer.valueOf(tagSelectionString));
                    }
                    //
                    String[] foodNames =  managedProperties.getProperty("foodNames").split(",");
                    foodSelection.getItems().clear();
                    foodSelection.getItems().addAll(foodNames);
                    //
                    String fightRadiusString = managedProperties.getProperty("fightRadius");
                    if (isNumeric(fightRadiusString)) {
                        tileRange.setText(fightRadiusString);
                    }
                    //
                    String eatValueString = managedProperties.getProperty("eatValue");
                    if (isNumeric(eatValueString)) {
                        eatValue.setText(eatValueString);
                    }
                    //
                    String prayValueString = managedProperties.getProperty("prayValue");
                    if (isNumeric(prayValueString)) {
                        prayValue.setText(prayValueString);
                    }
                    //
                    String criticalHitpointsString = managedProperties.getProperty("criticalHitpoints");
                    if (isNumeric(criticalHitpointsString)) {
                        criticalHitpoints.setText(criticalHitpointsString);
                    }
                    //
                    String[] potionNames = managedProperties.getProperty("selectedPotions").split(",");
                    selectedBoosts.getItems().clear();
                    selectedBoosts.getItems().addAll(potionNames);
                    //
                    String boostRefreshPercentageString = managedProperties.getProperty("boostRefreshPercentage");
                    if (isNumeric(boostRefreshPercentageString)) {
                        boostRefreshPercentage.setValue(Integer.valueOf(boostRefreshPercentageString));
                    }
                    return true;
                } else {
                    settingsStatus.setText("Your cloud settings are invalid, please re-save them");
                }
            }
        }
        return false;
    }

    private boolean save() {

        if (!selectedMonsters.getItems().isEmpty()) {
            AbstractScript script = Environment.getScript();
            if (script != null) {
                ManagedProperties managedProperties = Environment.getScript().getSettings();
                if (managedProperties != null) {
                    /* User Profile */
                    String npcString = String.join(",", selectedMonsters.getItems());
                    managedProperties.setProperty("npcNames", npcString);
                    //
                    String lootString = String.join(",", selectedLoot.getItems());
                    managedProperties.setProperty("lootNames", lootString);
                    //
                    String alchLootString = String.join(",", selectedAlchLoot.getItems());
                    managedProperties.setProperty("alchLoot", alchLootString);
                    //
                    String notepaperLootString = String.join(",", selectedNotepaperLoot.getItems());
                    managedProperties.setProperty("notepaperLoot", notepaperLootString);
                    managedProperties.setProperty("targetSelection", Double.toString(targetSlider.getValue()));
                    managedProperties.setProperty("useFood", Boolean.toString(!foodSelection.getItems().isEmpty()));
                    managedProperties.setProperty("showOutline", Boolean.toString(showOutline.isSelected()));
                    managedProperties.setProperty("exitOutFood", Boolean.toString(stopWhenOutOfFood.isSelected()));
                    managedProperties.setProperty("lootInCombat", Boolean.toString(lootInCombat.isSelected()));
                    managedProperties.setProperty("useAbilities", Boolean.toString(abilities.isSelected()));
                    managedProperties.setProperty("useSoulsplit", Boolean.toString(soulsplit.isSelected()));
                    managedProperties.setProperty("safetyLogout", Boolean.toString(safetyLogout.isSelected()));
                    managedProperties.setProperty("safetyTeleport", Boolean.toString(safetyTeleport.isSelected()));
                    managedProperties.setProperty("waitForLoot", Boolean.toString(waitLoot.isSelected()));
                    managedProperties.setProperty("looting", Boolean.toString(!selectedLoot.getItems().isEmpty()));
                    managedProperties.setProperty("buryBones", Boolean.toString(buryBones.isSelected()));
                    managedProperties.setProperty("quickPray", Boolean.toString(quickPray.isSelected()));
                    managedProperties.setProperty("exitOnPrayerOut", Boolean.toString(exitPrayer.isSelected()));
                    managedProperties.setProperty("tagMode", Boolean.toString(tagMode.isSelected()));
                    managedProperties.setProperty("attackCombatMonsters", Boolean.toString(attackCombatMonsters.isSelected()));
                    managedProperties.setProperty("bypassReachable", Boolean.toString(bypassReachable.isSelected()));
                    managedProperties.setProperty("revolutionMode", Boolean.toString(revolutionMode.isSelected()));
                    managedProperties.setProperty("lootByValue", Boolean.toString(lootByValue.isSelected()));
                    managedProperties.setProperty("equipAmmunition", Boolean.toString(reequipAmmunition.isSelected()));
                    managedProperties.setProperty("soulsplitPermanent", Boolean.toString(soulsplitPerm.isSelected()));
                    managedProperties.setProperty("soulsplitPercentage", Double.toString(soulsplitPercentage.getValue()));
                    managedProperties.setProperty("lootValue", lootValue.getText());
                    managedProperties.setProperty("tagSelection", Double.toString(tagSlider.getValue()));
                    //
                    String foodString = String.join(",", foodSelection.getItems());
                    managedProperties.setProperty("foodNames", foodString);
                    //
                    managedProperties.setProperty("fightRadius", tileRange.getText());
                    managedProperties.setProperty("eatValue", eatValue.getText());
                    managedProperties.setProperty("prayValue", prayValue.getText());
                    managedProperties.setProperty("criticalHitpoints", criticalHitpoints.getText());
                    //
                    String potionString = String.join(",", selectedBoosts.getItems());
                    managedProperties.setProperty("selectedPotions", potionString);
                    //
                    managedProperties.setProperty("boostRefreshPercentage", Double.toString(boostRefreshPercentage.getValue()));
                    settingsStatus.setText("Settings saved!");
                    return true;
                }
            }
        }
        System.out.println("Could not save settings at this time");
        return false;
    }

    private Boolean isNumeric(String string) {
        String numericRegex = "\\d+";
        return string.matches(numericRegex);
    }

    private void closeUI() {
        Stage stage = Main.stage;
        if (stage != null && stage.isShowing()) {
            stage.close();
        }
    }

}
