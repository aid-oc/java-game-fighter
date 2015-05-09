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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import scripts.MassFighter.Data.Potion;
import scripts.MassFighter.Framework.UserProfile;
import scripts.MassFighter.MassFighter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
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
    private ListView<Potion> availableBoosts;
    @FXML
    private ListView<Potion> selectedBoosts;
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

    private UserProfile currentProfile;

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

        ManagedProperties storedSettings = Environment.getScript().getSettings();
        if (storedSettings.contains("selectedNpcs")) {
            settingsStatus.setText("You have settings available in the cloud.");
        } else {
            settingsStatus.setText("You do not have any stored cloud settings.");
        }

        // Temporary Updates Solution
        List<String> updates = new ArrayList<>();
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
        updates.stream().filter(n -> !updatesList.getItems().contains(n)).forEach(n -> {
            updatesList.getItems().add(n);
        });

        availableBoosts.getItems().addAll(Potion.values());

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
        boostButton.setOnAction(event -> {
            if (!selectedBoosts.getSelectionModel().getSelectedItems().isEmpty()) {
                selectedBoosts.getItems().removeAll(selectedBoosts.getSelectionModel().getSelectedItems());
            } else if (!availableBoosts.getSelectionModel().getSelectedItems().isEmpty()) {
                availableBoosts.getSelectionModel().getSelectedItems().stream().filter(s -> !selectedBoosts.getItems().contains(s)).forEach(s -> {
                    selectedBoosts.getItems().add(s);
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
            if (save()) {
                settingsStatus.setText("Your settings have been saved to the cloud and loaded for you.");
                currentProfile = load();
            } else {
                settingsStatus.setText("Unable to save your settings, check you have setup correctly.");
            }
        });

        btnLoad.setOnAction(event ->  {
            UserProfile loadedProfile = load();
            if (loadedProfile != null) {
                currentProfile = loadedProfile;
                populateUI(currentProfile);
                settingsStatus.setText("Your settings have been downloaded.");
            }
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
        lootByValue.setOnAction(event -> lootValue.setDisable(lootByValue.isSelected()));

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

    private void populateUI(UserProfile profile) {
        if (profile != null) {
            if (profile.getNpcNames() != null) {
                selectedMonsters.getItems().addAll(profile.getNpcNames());
            }
            if (!profile.settings.selectedPotions.isEmpty()) {
                selectedBoosts.getItems().addAll(profile.settings.selectedPotions);
                boostRefreshPercentage.setValue(profile.settings.boostRefreshPercentage);
            }
            targetSlider.setValue(profile.settings.targetSelection);
            tagMode.setSelected(profile.settings.tagMode);
            tagSlider.setValue(profile.settings.tagSelection);
            criticalHitpoints.setText(Integer.toString(profile.settings.criticalHitpoints));
            abilities.setSelected(profile.settings.useAbilities);
            revolutionMode.setSelected(profile.settings.revolutionMode);
            if (profile.settings.foodNames != null && profile.settings.foodNames.length > 0) {
                foodSelection.getItems().addAll(profile.settings.foodNames);
            }
            eatValue.setText(Integer.toString(profile.settings.eatValue));
            stopWhenOutOfFood.setSelected(profile.settings.exitOutFood);
            lootByValue.setSelected(profile.settings.lootByValue);
            lootValue.setText(Double.toString(profile.settings.lootValue));
            lootInCombat.setSelected(profile.settings.lootInCombat);
            buryBones.setSelected(profile.settings.buryBones);
            waitLoot.setSelected(profile.settings.waitForLoot);
            reequipAmmunition.setSelected(profile.settings.equipAmmunition);
            if (profile.getLootNames() != null && profile.getLootNames().length > 0) {
                selectedLoot.getItems().setAll(profile.getLootNames());
            }
            if (profile.getAlchLoot() != null && profile.getAlchLoot().length > 0) {
                selectedAlchLoot.getItems().setAll(profile.getAlchLoot());
            }
            if (profile.getNotepaperLoot() != null && profile.getNotepaperLoot().length > 0) {
                selectedNotepaperLoot.getItems().setAll(profile.getNotepaperLoot());
            }
            soulsplit.setSelected(profile.settings.useSoulsplit);
            attackCombatMonsters.setSelected(profile.settings.attackCombatMonsters);
            bypassReachable.setSelected(profile.settings.bypassReachable);
            soulsplitPerm.setSelected(profile.settings.soulsplitPermanent);
            if (!profile.settings.soulsplitPermanent) {
                soulsplitPercentage.setValue((double)profile.settings.soulsplitPercentage);
            }
            quickPray.setSelected(profile.settings.quickPray);
            prayValue.setText(Integer.toString(profile.settings.prayValue));
            exitPrayer.setSelected(profile.settings.exitOnPrayerOut);
        }
    }

    private void togglePrayer(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(soulsplit)) {
            quickPray.setSelected(false);
        } else {
            soulsplit.setSelected(false);
        }
    }


    // Start button pressed, start the script
    public void start(ActionEvent actionEvent) {
        if (currentProfile != null && currentProfile.getNpcNames() != null) {
            MassFighter.userProfile = currentProfile;
            MassFighter.setupRunning = false;
            closeUI();
        } else {
            if (createNoSave()) {
                MassFighter.userProfile = currentProfile;
                MassFighter.setupRunning = false;
                closeUI();
            } else {
                System.out.println("Failed to start, incorrect settings?");
            }
        }
    }

    public boolean createNoSave() {
        if (Pattern.matches("\\d+", tileRange.getText())) {
            if (!selectedMonsters.getItems().isEmpty()) {
                if (Pattern.matches("\\d+", prayValue.getText()) && Pattern.matches("\\d+", eatValue.getText()) && Pattern.matches("\\d+", criticalHitpoints.getText())) {
                    UserProfile profile = new UserProfile();
                    // Create a settings object and store the settings
                    Settings settings = new Settings();
                    if (!foodSelection.getItems().isEmpty()) {
                        settings.useFood = true;
                        List<String> foodNames = foodSelection.getItems().stream().map(String::toLowerCase).collect(Collectors.toList());
                        settings.foodNames = foodNames.toArray(new String[foodNames.size()]);
                        settings.eatValue = Integer.valueOf(eatValue.getText());
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
                    settings.attackCombatMonsters = attackCombatMonsters.isSelected();
                    settings.bypassReachable = bypassReachable.isSelected();
                    settings.showOutline = showOutline.isSelected();
                    settings.waitForLoot = waitLoot.isSelected();
                    settings.targetSelection = (int) targetSlider.getValue();
                    settings.lootInCombat = lootInCombat.isSelected();
                    settings.useAbilities = abilities.isSelected();
                    settings.fightRadius = Integer.valueOf(tileRange.getText());
                    settings.revolutionMode = revolutionMode.isSelected();
                    settings.soulsplitPermanent = soulsplitPerm.isSelected();
                    settings.soulsplitPercentage = (int)soulsplitPercentage.getValue();
                    settings.quickPray = quickPray.isSelected();
                    settings.useSoulsplit = soulsplit.isSelected();
                    settings.exitOnPrayerOut = exitPrayer.isSelected();
                    settings.criticalHitpoints = Integer.valueOf(criticalHitpoints.getText());
                    settings.exitOutFood = stopWhenOutOfFood.isSelected();
                    settings.buryBones = buryBones.isSelected();
                    settings.equipAmmunition = reequipAmmunition.isSelected();
                    if (!selectedBoosts.getItems().isEmpty()) {
                        settings.selectedPotions = selectedBoosts.getItems();
                        settings.boostRefreshPercentage = boostRefreshPercentage.getValue();
                    }
                    if (!selectedLoot.getItems().isEmpty() || lootByValue.isSelected() && Pattern.matches("\\d+", lootValue.getText())) {
                        if (lootByValue.isSelected() && Pattern.matches("\\d+", lootValue.getText())) {
                            settings.lootByValue = true;
                            settings.lootValue = Double.valueOf(lootValue.getText());
                        }
                        settings.looting = true;
                    }
                    if (!selectedAlchLoot.getItems().isEmpty()) {
                        List<String> alchLoot = new ArrayList<>();
                        alchLoot.addAll(selectedAlchLoot.getItems().stream().map(String::toLowerCase).collect(Collectors.toList()));
                        profile.setAlchLoot(alchLoot.toArray(new String[alchLoot.size()]));
                    }
                    if (!selectedNotepaperLoot.getItems().isEmpty()) {
                        List<String> notepaperLoot = new ArrayList<>();
                        notepaperLoot.addAll(selectedNotepaperLoot.getItems().stream().map(String::toLowerCase).collect(Collectors.toList()));
                        profile.setNotepaperLoot(notepaperLoot.toArray(new String[notepaperLoot.size()]));
                    }
                    profile.settings = settings;
                    List<String> lootNames = new ArrayList<>();
                    lootNames.addAll(selectedLoot.getItems().stream().map(String::toLowerCase).collect(Collectors.toList()));
                    profile.setLootNames(lootNames.toArray(new String[lootNames.size()]));
                    profile.setNpcNames(selectedMonsters.getItems().toArray(new String[(selectedMonsters.getItems().size())]));
                    currentProfile = profile;
                    return true;
                }
            }
        }
        return false;
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

    public UserProfile load() {
        // Get managed properties
        ManagedProperties properties = Environment.getScript().getSettings();
        UserProfile profile = new UserProfile();
        Settings settings = new Settings();
        if (properties.containsKey("tileRange")) {
            settings.fightRadius = Integer.valueOf(properties.getProperty("tileRange"));
        }
        if (properties.containsKey("showOutline")) {
            settings.showOutline = Boolean.valueOf(properties.getProperty("showOutline"));
        }
        if (properties.containsKey("targetLimit")) {
            settings.targetSelection = Double.valueOf(properties.getProperty("targetLimit")).intValue();
        }
        if (properties.containsKey("tagMode")) {
            settings.tagMode = Boolean.valueOf(properties.getProperty("tagMode"));
        }
        if (properties.containsKey("tagLimit")) {
            settings.tagSelection = Double.valueOf(properties.getProperty("tagLimit")).intValue();
        }
        if (properties.containsKey("attackCombatMonsters")) {
            settings.attackCombatMonsters = Boolean.valueOf(properties.getProperty("attackCombatMonsters"));
        }
        if (properties.containsKey("attackUnreachable")) {
            settings.bypassReachable = Boolean.valueOf(properties.getProperty("attackUnreachable"));
        }
        if (properties.containsKey("criticalHitpoints")) {
            settings.criticalHitpoints = Integer.valueOf(properties.getProperty("criticalHitpoints"));
        }
        if (properties.containsKey("selectedNpcs")) {
            String[] npcNames = properties.getProperty("selectedNpcs").split(",");
            profile.setNpcNames(npcNames);
        }
        if (properties.containsKey("useAbilities")) {
            settings.useAbilities = Boolean.valueOf(properties.getProperty("useAbilities"));
        }
        if (properties.containsKey("revolutionMode")) {
            settings.revolutionMode = Boolean.valueOf(properties.getProperty("revolutionMode"));
        }
        if (properties.containsKey("selectedFood")) {
            String[] foodNames = properties.getProperty("selectedFood").split(",");
            for (int i = 0; i < foodNames.length; i++) {
                foodNames[i] = foodNames[i].toLowerCase();
            }
            settings.useFood = true;
            settings.foodNames = foodNames;
        }
        if (properties.containsKey("eatValue")) {
            settings.eatValue = Integer.valueOf(properties.getProperty("eatValue"));
        }
        if (properties.containsKey("stopWhenOutOfFood")) {
            settings.exitOutFood = Boolean.valueOf(properties.getProperty("stopWhenOutOfFood"));
        }
        if (properties.containsKey("lootByValue")) {
            settings.lootByValue = Boolean.valueOf(properties.getProperty("lootByValue"));
        }
        if (properties.containsKey("lootValue")) {
            settings.lootValue = Double.valueOf(properties.getProperty("lootValue"));
        }
        if (properties.containsKey("lootInCombat")) {
            settings.lootInCombat = Boolean.valueOf(properties.getProperty("lootInCombat"));
        }
        if (properties.containsKey("buryBones")) {
            settings.buryBones = Boolean.valueOf(properties.getProperty("buryBones"));
        }
        if (properties.containsKey("reequipAmmunition")) {
            settings.equipAmmunition = Boolean.valueOf(properties.getProperty("reequipAmmunition"));
        }
        if (properties.containsKey("selectedLoot")) {
            String[] lootNames = properties.getProperty("selectedLoot").split(",");
            for (int i = 0; i < lootNames.length; i++) {
                lootNames[i] = lootNames[i].toLowerCase();
            }
            profile.setLootNames(lootNames);
        }
        if (properties.containsKey("selectedNotepaperLoot")) {
            String[] notepaperLootNames = properties.getProperty("selectedLootNames").split(",");
            for (int i = 0; i < notepaperLootNames.length; i++) {
                notepaperLootNames[i] = notepaperLootNames[i].toLowerCase();
            }
            profile.setNotepaperLoot(notepaperLootNames);
        }
        if (properties.containsKey("selectedAlchLoot")) {
            String[] alchLootNames = properties.getProperty("selectedAlchLoot").split(",");
            for (int i = 0; i < alchLootNames.length; i++) {
                alchLootNames[i] = alchLootNames[i].toLowerCase();
            }
            profile.setAlchLoot(alchLootNames);
        }
        if (properties.containsKey("useSoulsplit")) {
            settings.useSoulsplit = Boolean.valueOf(properties.getProperty("useSoulsplit"));
        }
        if (properties.containsKey("soulsplitPerm")) {
            settings.soulsplitPermanent = Boolean.valueOf(properties.getProperty("soulsplitPerm"));
        }
        if (properties.containsKey("soulsplitPercentage")) {
            settings.soulsplitPercentage = Double.valueOf(properties.getProperty("soulsplitPercentage")).intValue();
        }
        if (properties.containsKey("useQuickPrayers")) {
            settings.quickPray = Boolean.valueOf(properties.getProperty("useQuickPrayers"));
        }
        if (properties.containsKey("prayValue")) {
            settings.prayValue = Integer.valueOf(properties.getProperty("prayValue"));
        }
        if (properties.containsKey("exitOnPrayerOut")) {
            settings.exitOnPrayerOut = Boolean.valueOf(properties.getProperty("exitOnPrayerOut"));
        }
        if (properties.containsKey("selectedBoosts")) {
            List<Potion> potions = new ArrayList<>();
            String[] potionNames = properties.getProperty("selectedBoosts").split(",");
            for (String potionName : potionNames) {
                potions.add(Potion.valueOf(potionName));
            }
            settings.selectedPotions = potions;
        }
        if (properties.containsKey("boostRefreshPercentage")) {
            settings.boostRefreshPercentage = Double.valueOf(properties.getProperty("boostRefreshPercentage"));
        }
        profile.settings = settings;
        System.out.println("Loaded saved settings");
        return profile;
    }

    public boolean save()
    {
        String numericRegex = "\\d+";
        // Must-have settings
        String profileTileRange = tileRange.getText();
        List<String> profileMonsters = selectedMonsters.getItems();
        if (!profileTileRange.isEmpty() && profileTileRange.matches(numericRegex) && !profileMonsters.isEmpty()) {
            // Get managed properties
            ManagedProperties properties = Environment.getScript().getSettings();
            // Fight radius
            properties.setProperty("tileRange", profileTileRange);
            // Draw area whilst running
            properties.setProperty("showOutline", Boolean.toString(showOutline.isSelected()));
            // Target limits
            properties.setProperty("targetLimit", Double.toString(targetSlider.getValue()));
            // Tag mode
            properties.setProperty("tagMode", Boolean.toString(tagMode.isSelected()));
            // If they are using tag mode, get the limit they set
            if (tagMode.isSelected()) {
                properties.setProperty("tagLimit", Double.toString(tagSlider.getValue()));
            }
            // Attack monsters that are in combat
            properties.setProperty("attackCombatMonsters", Boolean.toString(attackCombatMonsters.isSelected()));
            // Attack unreachable monsters (caged etc.)
            properties.setProperty("attackUnreachable", Boolean.toString(bypassReachable.isSelected()));
            // Critical hipoints value (logout)
            if (criticalHitpoints.getText().matches(numericRegex)) {
                properties.setProperty("criticalHitpoints", criticalHitpoints.getText());
            }
            // Selected NPC's
            String npcString = String.join(",", selectedMonsters.getItems());
            properties.setProperty("selectedNpcs", npcString);
            // Use abilities
            properties.setProperty("useAbilities", Boolean.toString(abilities.isSelected()));
            // Revolution mode
            properties.setProperty("revolutionMode", Boolean.toString(revolutionMode.isSelected()));

            // Food options
            if (!foodSelection.getItems().isEmpty() && !eatValue.getText().isEmpty() && eatValue.getText().matches(numericRegex)) {
                String foodString = String.join(",", foodSelection.getItems());
                properties.setProperty("selectedFood", foodString);
                properties.setProperty("eatValue", eatValue.getText());
                properties.setProperty("stopWhenOutOfFood", Boolean.toString(stopWhenOutOfFood.isSelected()));
            }

            // Loot options
            // Get loot value if selected
            if (lootByValue.isSelected() && !lootValue.getText().isEmpty() && lootValue.getText().matches(numericRegex)) {
                properties.setProperty("lootByValue", Boolean.toString(lootByValue.isSelected()));
                properties.setProperty("lootValue", lootValue.getText());
            }
            properties.setProperty("lootInCombat", Boolean.toString(lootInCombat.isSelected()));
            properties.setProperty("buryBones", Boolean.toString(buryBones.isSelected()));
            properties.setProperty("reequipAmmunition", Boolean.toString(reequipAmmunition.isSelected()));

            if (!selectedLoot.getItems().isEmpty()) {
                String lootString = String.join(",", selectedLoot.getItems());
                properties.setProperty("selectedLoot", lootString);
            }
            if (!selectedNotepaperLoot.getItems().isEmpty()) {
                String notepaperLootString = String.join(",", selectedNotepaperLoot.getItems());
                properties.setProperty("selectedNotepaperLoot", notepaperLootString);
            }
            if (!selectedAlchLoot.getItems().isEmpty()) {
                String alchLootString = String.join(",", selectedAlchLoot.getItems());
                properties.setProperty("selectedAlchLoot", alchLootString);
            }

            // Prayer options
            properties.setProperty("useSoulsplit", Boolean.toString(soulsplit.isSelected()));
            properties.setProperty("soulsplitPerm", Boolean.toString(soulsplitPerm.isSelected()));
            // Get soulsplit activation value
            if (!soulsplitPerm.isSelected()) {
                properties.setProperty("soulsplitPercentage", Double.toString(soulsplitPercentage.getValue()));
            }
            properties.setProperty("useQuickPrayers", Boolean.toString(quickPray.isSelected()));
            if (!prayValue.getText().isEmpty() && prayValue.getText().matches(numericRegex)) {
                properties.setProperty("prayValue", prayValue.getText());
            }
            properties.setProperty("exitOnPrayerOut", Boolean.toString(exitPrayer.isSelected()));

            // Boost options
            if (!selectedBoosts.getItems().isEmpty()) {
                List<String> potionNames = selectedBoosts.getItems().stream().map(Potion::toString).collect(Collectors.toList());
                String potionNamesString = String.join(",", potionNames);
                properties.setProperty("selectedBoosts", potionNamesString);
                properties.setProperty("boostRefreshPercentage", Double.toString(boostRefreshPercentage.getValue()));
            }
            System.out.println("Save successful");
            return true;
        }
        System.out.println("Save unsuccessful");
        return false;
    }

    public void closeUI() {
        Stage stage = Main.stage;
        if (stage != null && stage.isShowing()) {
            stage.close();
        }
    }

}
