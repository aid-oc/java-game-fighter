package scripts.MassFighter.GUI;

import com.runemate.game.api.client.paint.PaintListener;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.input.Mouse;
import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.queries.NpcQueryBuilder;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import scripts.MassFighter.Data.Food;
import scripts.MassFighter.Data.Potion;
import scripts.MassFighter.Framework.UserProfile;
import scripts.MassFighter.MassFighter;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Controller implements MouseListener, PaintListener {

    public UserProfile userProfile;
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
    private TextField foodAmount;
    @FXML
    private Label txtProfileName;
    @FXML
    private Button btnStart;
    @FXML
    private Button btnLoad;
    @FXML
    private Button btnSave;
    @FXML
    private Label txtProfileSaved;
    @FXML
    private TextField txtSetProfileName;
    @FXML
    private Button btnSaveFightArea;
    @FXML
    private Button btnSaveBankArea;
    @FXML
    private Label txtAreaStatus;
    @FXML
    private Tab tabAreas;
    @FXML
    private CheckBox showArea;
    @FXML
    private Button btnResetArea;
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


    private List<Coordinate> areaCoords = new LinkedList<>();
    private List<Coordinate> fightAreaCoords = new LinkedList<>();
    private List<Coordinate> bankAreaCoords = new LinkedList<>();

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

        Environment.getScript().getEventDispatcher().addListener(this);

        // Temporary Updates Solution
        List<String> updates = new ArrayList<>();
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


        txtProfileName.setText("None Selected");
        availableBoosts.getItems().addAll(Potion.values());
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

        btnSave.setOnAction(event -> saveProfile());

        btnLoad.setOnAction(event -> {
            File chosenProfile = chooseProfile();
            UserProfile builtProfile = buildProfile(chosenProfile);
            if (builtProfile != null) {
                userProfile = builtProfile;
                txtProfileName.setText(userProfile.getProfileName());
            }
        });

        btnSaveFightArea.setOnAction(event -> {
            if (!areaCoords.isEmpty()) {
                fightAreaCoords.addAll(areaCoords);
                areaCoords.clear();
                txtAreaStatus.setText("Saved as fight area!");
            }
        });

        btnSaveBankArea.setOnAction(event -> {
            if (!areaCoords.isEmpty()) {
                bankAreaCoords.addAll(areaCoords);
                areaCoords.clear();
                txtAreaStatus.setText("Saved as bank area!");
            }
        });

        btnResetArea.setOnAction(event -> {
            areaCoords.clear();
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
        foodSelection.getItems().addAll(Food.values());
        foodAmount.setText("0");
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
            selectedMonsters.getItems().setAll(profile.getNpcNames());
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
            if (!profile.settings.foodName.isEmpty()) {
                if (Food.valueOf(profile.settings.foodName.toUpperCase()) != null) {
                    foodSelection.getSelectionModel().select(Food.valueOf(profile.settings.foodName.toUpperCase()));
                }
            }
            eatValue.setText(Integer.toString(profile.settings.eatValue));
            stopWhenOutOfFood.setSelected(profile.settings.exitOutFood);
            foodAmount.setText(Integer.toString(profile.settings.foodAmount));
            lootByValue.setSelected(profile.settings.lootByValue);
            lootValue.setText(Double.toString(profile.settings.lootValue));
            lootInCombat.setSelected(profile.settings.lootInCombat);
            buryBones.setSelected(profile.settings.buryBones);
            waitLoot.setSelected(profile.settings.waitForLoot);
            reequipAmmunition.setSelected(profile.settings.equipAmmunition);
            selectedLoot.getItems().setAll(profile.getLootNames());
            selectedAlchLoot.getItems().setAll(profile.getAlchLoot());
            selectedNotepaperLoot.getItems().setAll(profile.getNotepaperLoot());
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

            if (profile.getFightArea() != null) {
                fightAreaCoords = profile.getFightArea().getCoordinates();
            }
            if (profile.getBankArea() != null) {
                bankAreaCoords = profile.getBankArea().getCoordinates();
            }

            txtProfileName.setText(profile.getProfileName());
            txtSetProfileName.setText(profile.getProfileName());
            txtProfileSaved.setText("Profile loaded and ready to go!");
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
        if (userProfile == null) {
            txtSetProfileName.setText("Quick Run");
            userProfile = createProfile();
        }
        if (userProfile != null) {
            MassFighter.userProfile = userProfile;
            MassFighter.setupRunning = false;
            closeUI();
        } else {
            txtProfileSaved.setText("Invalid Profile!");
            System.out.println("Profile Invalid");
            eatValue.setText("3000");
            tileRange.setText("20");
            criticalHitpoints.setText("10");
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

    private Double getDoubleValue(String s) {
        if (s != null) {
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private Node getNode(NodeList list, int position) {
        if (list != null) {
            Node node = list.item(position);
            if (node != null) return node;
        }
        return null;
    }

    public UserProfile buildProfile(File chosenProfile)
    {
        if (chosenProfile.getName().contains("xml")) {
            try {

                DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
                Document document = documentBuilder.parse(chosenProfile);

                UserProfile profile = new UserProfile();
                Settings settings = new Settings();

                document.getDocumentElement().normalize();

                NodeList settingsList = document.getElementsByTagName("settings");
                if (settingsList != null) {
                    Node settingsNode = settingsList.item(0);
                    if (settingsNode != null) {
                        Element element = (Element) settingsNode;

                        Node tagNode = getNode(element.getElementsByTagName("targetSelection"), 0);
                        if (tagNode != null) {
                            Double tagValue = getDoubleValue(tagNode.getTextContent());
                            if (tagValue != null) settings.tagSelection = tagValue.intValue();
                        }

                        Node useFoodNode = getNode(element.getElementsByTagName("useFood"), 0);
                        if (useFoodNode != null) {
                            settings.useFood = Boolean.valueOf(useFoodNode.getTextContent());
                        }

                        Node exitOutFoodNode = getNode(element.getElementsByTagName("exitOutFood"), 0);
                        if (exitOutFoodNode != null) {
                            settings.exitOutFood = Boolean.valueOf(exitOutFoodNode.getTextContent());
                        }

                        Node foodNameNode = getNode(element.getElementsByTagName("food"), 0);
                        if (foodNameNode != null) {
                            settings.foodName = element.getElementsByTagName("food").item(0).getTextContent();
                        }

                        Node showOutlineNode = getNode(element.getElementsByTagName("showOutline"), 0);
                        if (showOutlineNode != null) {
                            settings.showOutline = Boolean.valueOf(showOutlineNode.getTextContent());
                        }

                        Node lootInCombatNode = getNode(element.getElementsByTagName("combatLooting"), 0);
                        if (lootInCombatNode != null) {
                            settings.lootInCombat = Boolean.valueOf(lootInCombatNode.getTextContent());
                        }

                        Node useAbilitiesNode = getNode(element.getElementsByTagName("useAbilities"), 0);
                        if (useAbilitiesNode  != null) {
                            settings.useAbilities = Boolean.valueOf(useAbilitiesNode.getTextContent());
                        }

                        Node useSoulsplitNode = getNode(element.getElementsByTagName("useSoulsplit"), 0);
                        if (useSoulsplitNode != null) {
                            settings.useSoulsplit = Boolean.valueOf(useSoulsplitNode.getTextContent());
                        }

                        Node waitForLootNode = getNode(element.getElementsByTagName("waitForLoot"), 0);
                        if (waitForLootNode != null) {
                            settings.waitForLoot = Boolean.valueOf(waitForLootNode.getTextContent());
                        }

                        Node lootingNode = getNode(element.getElementsByTagName("looting"), 0);
                        if (lootingNode != null) {
                            settings.looting = Boolean.valueOf(lootingNode.getTextContent());
                        }

                        Node equipAmmunitionNode = getNode(element.getElementsByTagName("reequipAmmunition"), 0);
                        if (equipAmmunitionNode != null) {
                            settings.equipAmmunition = Boolean.valueOf(equipAmmunitionNode.getTextContent());
                        }

                        Node lootByValueNode = getNode(element.getElementsByTagName("lootByValue"), 0);
                        if (lootByValueNode != null) {
                            settings.lootByValue = Boolean.valueOf(lootByValueNode.getTextContent());
                        }

                        Node lootValueNode = getNode(element.getElementsByTagName("lootValue"), 0);
                        if (lootValueNode != null && !lootValueNode.getTextContent().isEmpty()) {
                            Double retrievedLootValue = getDoubleValue(lootValueNode.getTextContent());
                            if (retrievedLootValue != null) {
                                settings.lootValue = retrievedLootValue;
                            }
                        }

                        Node buryBonesNode = getNode(element.getElementsByTagName("buryBones"), 0);
                        if (buryBonesNode != null) {
                            settings.buryBones = Boolean.valueOf(element.getElementsByTagName("buryBones").item(0).getTextContent());
                        }

                        Node soulsplitPercentageNode = getNode(element.getElementsByTagName("soulsplitPercentage"), 0);
                        if (soulsplitPercentageNode != null) {
                            Double percentageValue = getDoubleValue(soulsplitPercentageNode.getTextContent());
                            if (percentageValue != null) settings.soulsplitPercentage = percentageValue.intValue();
                        }

                        Node soulsplitPermNode = getNode(element.getElementsByTagName("soulsplitPerm"), 0);
                        if (soulsplitPermNode != null) {
                            settings.soulsplitPermanent = Boolean.valueOf(soulsplitPermNode.getTextContent());
                        }

                        Node quickPrayNode = getNode(element.getElementsByTagName("quickPray"), 0);
                        if (quickPrayNode != null) {
                            settings.quickPray = Boolean.valueOf(quickPrayNode.getTextContent());
                        }

                        Node exitOnPrayerOutNode = getNode(element.getElementsByTagName("exitOnPrayerOut"), 0);
                        if (exitOnPrayerOutNode != null) {
                            settings.exitOnPrayerOut = Boolean.valueOf(exitOnPrayerOutNode.getTextContent());
                        }

                        Node tagModeNode = getNode(element.getElementsByTagName("tagMode"), 0);
                        if (tagModeNode != null) {
                            settings.tagMode = Boolean.valueOf(tagModeNode.getTextContent());
                        }

                        Node attackCombatMonstersNode = getNode(element.getElementsByTagName("attackCombatMonsters"), 0);
                        if (attackCombatMonstersNode != null) {
                            settings.attackCombatMonsters = Boolean.valueOf(attackCombatMonstersNode.getTextContent());
                        }

                        Node bypassReachableNode = getNode(element.getElementsByTagName("bypassReachable"), 0);
                        if (bypassReachableNode != null) {
                            settings.bypassReachable = Boolean.valueOf(bypassReachableNode.getTextContent());
                        }

                        Node revolutionModeNode = getNode(element.getElementsByTagName("revolutionMode"), 0);
                        if (revolutionModeNode != null) {
                            settings.revolutionMode = Boolean.valueOf(revolutionModeNode.getTextContent());
                        }

                        Node foodAmountNode = getNode(element.getElementsByTagName("foodAmount"), 0);
                        if (foodAmountNode != null) {
                            Double foodAmountValue = getDoubleValue(foodAmountNode.getTextContent());
                            if (foodAmountValue != null) settings.foodAmount = foodAmountValue.intValue();
                        }

                        Node fightRadiusNode = getNode(element.getElementsByTagName("fightRadius"), 0);
                        if (fightRadiusNode != null) {
                            Double fightRadiusValue = getDoubleValue(fightRadiusNode.getTextContent());
                            if (fightRadiusValue != null) settings.fightRadius = fightRadiusValue.intValue();
                        }

                        Node eatValueNode = getNode(element.getElementsByTagName("eatValue"), 0);
                        if (eatValueNode != null) {
                            Double eatValue = getDoubleValue(eatValueNode.getTextContent());
                            if (eatValue != null) settings.eatValue = eatValue.intValue();
                        }

                        Node prayValueNode = getNode(element.getElementsByTagName("prayValue"), 0);
                        if (prayValueNode != null) {
                            Double prayValue = getDoubleValue(prayValueNode.getTextContent());
                            if (prayValue != null) settings.prayValue = prayValue.intValue();
                        }

                        Node criticalHitpointsNode = getNode(element.getElementsByTagName("criticalHitpoints"), 0);
                        if (criticalHitpointsNode != null) {
                            Double criticalHitpointsValue = getDoubleValue(criticalHitpointsNode.getTextContent());
                            if (criticalHitpointsValue != null)
                                settings.criticalHitpoints = criticalHitpointsValue.intValue();
                        }


                        List<Potion> potions = new ArrayList<>();
                        NodeList potionList = document.getElementsByTagName("selectedPotions");
                        if (potionList != null) {
                            Node potionNode = getNode(potionList, 0);
                            if (potionNode != null) {
                                NodeList potionNodes = potionNode.getChildNodes();
                                if (potionNodes != null) {
                                    for (int i = 0; i < potionNodes.getLength(); i++) {
                                        Node potion = getNode(potionNodes, i);
                                        if (potion != null) {
                                            try {
                                                Potion potionEnumValue = Potion.valueOf(potion.getTextContent());
                                                if (potionEnumValue != null) potions.add(potionEnumValue);
                                            } catch (IllegalArgumentException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    if (!potions.isEmpty()) {
                                        settings.selectedPotions = potions;
                                        Node boostRefreshPercentageNode = getNode(element.getElementsByTagName("boostRefreshPercentage"), 0);
                                        if (boostRefreshPercentageNode != null) {
                                            Double boostRefreshPercentageValue = getDoubleValue(boostRefreshPercentageNode.getTextContent());
                                            if (boostRefreshPercentageValue != null) {
                                                settings.boostRefreshPercentage = boostRefreshPercentageValue;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        profile.settings = settings;

                        // End of settings
                        // Start of user profile

                        Node profileNameNode = getNode(document.getElementsByTagName("profileName"), 0);
                        if (profileNameNode != null) {
                            String profileName = profileNameNode.getTextContent();
                            profile.setProfileName(profileName);
                        }

                        List<String> npcNames = new ArrayList<>();
                        NodeList npcList = document.getElementsByTagName("npcNames");
                        if (npcList != null) {
                            Node npcNodesParent = getNode(npcList, 0);
                            if (npcNodesParent != null) {
                                NodeList npcChildNodes = npcNodesParent.getChildNodes();
                                if (npcChildNodes != null) {
                                    for (int i = 0; i < npcChildNodes.getLength(); i++) {
                                        Node npcNode = getNode(npcChildNodes, i);
                                        if (npcNode != null) {
                                            npcNames.add(npcNode.getTextContent());
                                        }
                                    }
                                    profile.setNpcNames(npcNames.toArray(new String[npcNames.size()]));
                                }
                            }
                        }

                        List<String> lootNames = new ArrayList<>();
                        NodeList lootList = document.getElementsByTagName("lootNames");
                        if (lootList != null) {
                            Node lootNodesParent = getNode(lootList, 0);
                            if (lootNodesParent != null) {
                                NodeList lootChildNodes = lootNodesParent.getChildNodes();
                                if (lootChildNodes != null) {
                                    for (int i = 0; i < lootChildNodes.getLength(); i++) {
                                        Node lootNode = getNode(lootChildNodes, i);
                                        if (lootNode != null) {
                                            lootNames.add(lootNode.getTextContent().toLowerCase());
                                        }
                                    }
                                    profile.setLootNames(lootNames.toArray(new String[lootNames.size()]));
                                }
                            }
                        }

                        List<String> alchLootNames = new ArrayList<>();
                        NodeList alchLootList = document.getElementsByTagName("alchLoot");
                        if (alchLootList != null) {
                            Node alchNodesParent = getNode(alchLootList, 0);
                            if (alchNodesParent != null) {
                                NodeList alchLootChildNodes = alchNodesParent.getChildNodes();
                                if (alchLootChildNodes != null) {
                                    for (int i = 0; i < alchLootChildNodes.getLength(); i++) {
                                        Node alchLootNode = getNode(alchLootChildNodes, i);
                                        if (alchLootNode != null) {
                                            alchLootNames.add(alchLootNode.getTextContent().toLowerCase());
                                        }
                                    }
                                    profile.setAlchLoot(alchLootNames.toArray(new String[alchLootNames.size()]));
                                }
                            }
                        }

                        List<String> notepaperLootNames = new ArrayList<>();
                        NodeList notepaperLootList = document.getElementsByTagName("notepaperLoot");
                        if (notepaperLootList != null) {
                            Node notepaperLootParent = getNode(notepaperLootList, 0);
                            if (notepaperLootParent != null) {
                                NodeList notepaperLootChildNodes = notepaperLootParent.getChildNodes();
                                if (notepaperLootChildNodes != null) {
                                    for (int i = 0; i < notepaperLootChildNodes.getLength(); i++) {
                                        Node notepaperLoot = getNode(notepaperLootChildNodes, i);
                                        if (notepaperLoot != null) {
                                            notepaperLootNames.add(notepaperLoot.getTextContent().toLowerCase());
                                        }
                                    }
                                    profile.setNotepaperLoot(notepaperLootNames.toArray(new String[notepaperLootNames.size()]));
                                }
                            }
                        }


                        List<Coordinate> fightAreaLocations = new ArrayList<>();
                        Node fightAreaNode = getNode(document.getElementsByTagName("fightArea"), 0);
                        if (fightAreaNode != null) {
                            NodeList coordinateNodes = fightAreaNode.getChildNodes();
                            if (coordinateNodes != null) {
                                for (int i = 0; i < coordinateNodes.getLength(); i++) {
                                    Node coordinateParent = getNode(coordinateNodes, i);
                                    if (coordinateParent != null) {
                                        NodeList coordinateComponents = coordinateParent.getChildNodes();
                                        if (coordinateComponents != null) {
                                            Node xNode = getNode(coordinateComponents, 0);
                                            if (xNode != null) {
                                                int x = Integer.parseInt(xNode.getTextContent());
                                                Node yNode = getNode(coordinateComponents, 1);
                                                if (yNode != null) {
                                                    int y = Integer.parseInt(yNode.getTextContent());
                                                    Node zNode = getNode(coordinateComponents, 2);
                                                    if (zNode != null) {
                                                        int z = Integer.parseInt(zNode.getTextContent());
                                                        fightAreaLocations.add(new Coordinate(x, y, z));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if (!fightAreaLocations.isEmpty()) {
                                    // Remove duplicates
                                    HashSet<Coordinate> fightAreas = new HashSet<>();
                                    fightAreas.addAll(fightAreaLocations);
                                    fightAreaLocations.clear();
                                    fightAreaLocations.addAll(fightAreas);
                                    profile.setFightAreaCoords(fightAreaLocations);
                                }
                            }
                        }



                        List<Coordinate> bankAreaLocations = new ArrayList<>();
                        Node bankAreaNode = getNode(document.getElementsByTagName("bankArea"), 0);
                        if (bankAreaNode != null) {
                            NodeList bankCoordinateNodes = bankAreaNode.getChildNodes();
                            if (bankCoordinateNodes != null) {
                                for (int i = 0; i < bankCoordinateNodes.getLength(); i++) {
                                    Node coordinateNode = getNode(bankCoordinateNodes, i);
                                    if (coordinateNode != null) {
                                        NodeList coordinateComponents = coordinateNode.getChildNodes();
                                        if (coordinateComponents != null) {
                                            Node xNode = getNode(coordinateComponents, 0);
                                            if (xNode != null) {
                                                int x = Integer.parseInt(xNode.getTextContent());
                                                Node yNode = getNode(coordinateComponents, 1);
                                                if (yNode != null) {
                                                    int y = Integer.parseInt(yNode.getTextContent());
                                                    Node zNode = getNode(coordinateComponents, 2);
                                                    if (zNode != null) {
                                                        int z = Integer.parseInt(zNode.getTextContent());
                                                        bankAreaLocations.add(new Coordinate(x, y, z));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if (!bankAreaLocations.isEmpty()) {
                                    // remove duplicates
                                    HashSet<Coordinate> bankAreas = new HashSet<>();
                                    bankAreas.addAll(bankAreaLocations);
                                    bankAreaLocations.clear();
                                    bankAreaLocations.addAll(bankAreas);
                                    profile.setBankAreaCoords(bankAreaLocations);
                                }
                            }
                        }
                        populateUI(profile);
                        return profile;
                    }
                }
            } catch(ParserConfigurationException | SAXException | IOException e){
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Loads an XML file and attempts to construct UserProfile/Settings objects from the values contained within
     */
    public File chooseProfile() {
        final JFileChooser fileChooser = new JFileChooser();
        Main.stage.toBack();
        fileChooser.setCurrentDirectory(Environment.getStorageDirectory());
        int response = fileChooser.showOpenDialog(null);
        if (response == JFileChooser.APPROVE_OPTION) {
            File chosenProfile = fileChooser.getSelectedFile();
            System.out.println("Opened: " + chosenProfile.getName());
            return chosenProfile;
        }
        Main.stage.toFront();
        return null;
    }

    /**
     * Takes the GUI settings and constructs an XML file which can then be read to construct a UserProfile
     * Saves to: ~/RuneMate/bots/storage/MassFighter
     *
     * @return if the save was successful
     */
    public Boolean saveProfile() {

        if (!fightAreaCoords.isEmpty() || Pattern.matches("\\d+", tileRange.getText())) {
            if (!selectedMonsters.getItems().isEmpty()) {
                if (Pattern.matches("\\d+", prayValue.getText()) && Pattern.matches("\\d+", foodAmount.getText()) && Pattern.matches("\\d+", eatValue.getText()) && Pattern.matches("\\d+", criticalHitpoints.getText())) {
                    if (!txtSetProfileName.getText().isEmpty()) {
                        File file;
                        try {
                            file = new File(Environment.getStorageDirectory().getAbsolutePath() + "/" + txtSetProfileName.getText() + ".xml");
                            if (file.exists()) {
                                file.delete();
                                file.createNewFile();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            return false;
                        }
                        try {
                            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
                            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

                            // Add "userProfile" root element
                            Document document = documentBuilder.newDocument();
                            Element rootElement = document.createElement("userProfile");
                            document.appendChild(rootElement);

                        /* START OF SETTINGS */
                            Element settings = document.createElement("settings");
                            rootElement.appendChild(settings);

                            // Populate settings element
                            Element targetSelection = document.createElement("targetSelection");
                            targetSelection.appendChild(document.createTextNode(Double.toString(targetSlider.getValue())));
                            settings.appendChild(targetSelection);

                            Element useFood = document.createElement("useFood");
                            useFood.appendChild(document.createTextNode(Boolean.toString(!foodSelection.getSelectionModel().isEmpty())));
                            settings.appendChild(useFood);

                            Element areaOutline = document.createElement("showOutline");
                            areaOutline.appendChild(document.createTextNode(Boolean.toString(showOutline.isSelected())));
                            settings.appendChild(areaOutline);


                            if (!foodSelection.getSelectionModel().isEmpty()) {
                                Element food = document.createElement("food");
                                food.appendChild(document.createTextNode(foodSelection.getSelectionModel().getSelectedItem().getName()));
                                settings.appendChild(food);

                                Element exitOutFood = document.createElement("exitOutFood");
                                exitOutFood.appendChild(document.createTextNode(Boolean.toString(stopWhenOutOfFood.isSelected())));
                                settings.appendChild(exitOutFood);
                            }

                            Element combatLooting = document.createElement("combatLooting");
                            combatLooting.appendChild(document.createTextNode(Boolean.toString(lootInCombat.isSelected())));
                            settings.appendChild(combatLooting);

                            Element useAbilities = document.createElement("useAbilities");
                            useAbilities.appendChild(document.createTextNode(Boolean.toString(abilities.isSelected())));
                            settings.appendChild(useAbilities);

                            Element useSoulsplit = document.createElement("useSoulsplit");
                            useSoulsplit.appendChild(document.createTextNode(Boolean.toString(soulsplit.isSelected())));
                            settings.appendChild(useSoulsplit);

                            Element waitForLoot = document.createElement("waitForLoot");
                            waitForLoot.appendChild(document.createTextNode(Boolean.toString(waitLoot.isSelected())));
                            settings.appendChild(waitForLoot);

                            Element looting = document.createElement("looting");
                            looting.appendChild(document.createTextNode(Boolean.toString((!selectedLoot.getItems().isEmpty() || lootByValue.isSelected() && Pattern.matches("\\d+", lootValue.getText())))));
                            settings.appendChild(looting);

                            Element equipAmmunition = document.createElement("reequipAmmunition");
                            equipAmmunition.appendChild(document.createTextNode(Boolean.toString(reequipAmmunition.isSelected())));
                            settings.appendChild(equipAmmunition);

                            Element lootingByValue = document.createElement("lootByValue");
                            lootingByValue.appendChild(document.createTextNode(Boolean.toString(lootByValue.isSelected())));
                            settings.appendChild(lootingByValue);

                            Element lootingValue = document.createElement("lootValue");
                            lootingValue.appendChild(document.createTextNode(lootValue.getText()));
                            settings.appendChild(lootingValue);

                            Element buryingBones = document.createElement("buryBones");
                            buryingBones.appendChild(document.createTextNode(Boolean.toString(buryBones.isSelected())));
                            settings.appendChild(buryingBones);

                            Element quickPraying = document.createElement("quickPray");
                            quickPraying.appendChild(document.createTextNode(Boolean.toString(quickPray.isSelected())));
                            settings.appendChild(quickPraying);

                            Element exitOnPrayerOut = document.createElement("exitOnPrayerOut");
                            exitOnPrayerOut.appendChild(document.createTextNode(Boolean.toString(exitPrayer.isSelected())));
                            settings.appendChild(exitOnPrayerOut);

                            Element useTagMode = document.createElement("tagMode");
                            useTagMode.appendChild(document.createTextNode(Boolean.toString(tagMode.isSelected())));
                            settings.appendChild(useTagMode);

                            Element attackCombatNpcs = document.createElement("attackCombatMonsters");
                            attackCombatNpcs.appendChild(document.createTextNode(Boolean.toString(attackCombatMonsters.isSelected())));
                            settings.appendChild(attackCombatNpcs);

                            Element attackUnreachable = document.createElement("bypassReachable");
                            attackUnreachable.appendChild(document.createTextNode(Boolean.toString(bypassReachable.isSelected())));
                            settings.appendChild(attackUnreachable);

                            Element useRevolutionMode = document.createElement("revolutionMode");
                            useRevolutionMode.appendChild(document.createTextNode(Boolean.toString(revolutionMode.isSelected())));
                            settings.appendChild(useRevolutionMode);

                            Element tagModeQuantity = document.createElement("tagSelection");
                            tagModeQuantity.appendChild(document.createTextNode(Double.toString(tagSlider.getValue())));
                            settings.appendChild(tagModeQuantity);

                            Element foodWithdrawQuantity = document.createElement("foodAmount");
                            foodWithdrawQuantity.appendChild(document.createTextNode(foodAmount.getText()));
                            settings.appendChild(foodWithdrawQuantity);

                            Element fightRadius = document.createElement("fightRadius");
                            fightRadius.appendChild(document.createTextNode(tileRange.getText()));
                            settings.appendChild(fightRadius);

                            Element eatAtValue = document.createElement("eatValue");
                            eatAtValue.appendChild(document.createTextNode(eatValue.getText()));
                            settings.appendChild(eatAtValue);

                            Element soulsplitPermanentValue = document.createElement("soulsplitPerm");
                            soulsplitPermanentValue.appendChild(document.createTextNode(Boolean.toString(soulsplitPerm.isSelected())));
                            settings.appendChild(soulsplitPermanentValue);

                            Element soulsplitPercentageValue = document.createElement("soulsplitPercentage");
                            soulsplitPercentageValue.appendChild(document.createTextNode(Double.toString(soulsplitPercentage.getValue())));
                            settings.appendChild(soulsplitPercentageValue);

                            Element prayRefreshValue = document.createElement("prayValue");
                            prayRefreshValue.appendChild(document.createTextNode(prayValue.getText()));
                            settings.appendChild(prayRefreshValue);

                            Element criticalHp = document.createElement("criticalHitpoints");
                            criticalHp.appendChild(document.createTextNode(criticalHitpoints.getText()));
                            settings.appendChild(criticalHp);

                            Element boostPercentage = document.createElement("boostRefreshPercentage");
                            boostPercentage.appendChild(document.createTextNode(Double.toString(boostRefreshPercentage.getValue())));
                            settings.appendChild(boostPercentage);

                        /* END OF SETTINGS */

                            Element bankArea = document.createElement("bankArea");
                            for (Coordinate coordinate : bankAreaCoords) {
                                Element coord = document.createElement("coordinate");
                                Element x = document.createElement("x");
                                Element y = document.createElement("y");
                                Element z = document.createElement("z");
                                x.appendChild(document.createTextNode(Integer.toString(coordinate.getX())));
                                y.appendChild(document.createTextNode(Integer.toString(coordinate.getY())));
                                z.appendChild(document.createTextNode(Integer.toString(coordinate.getPlane())));
                                coord.appendChild(x);
                                coord.appendChild(y);
                                coord.appendChild(z);
                                bankArea.appendChild(coord);
                            }
                            rootElement.appendChild(bankArea);

                            Element fightArea = document.createElement("fightArea");
                            for (Coordinate coordinate : fightAreaCoords) {
                                Element coord = document.createElement("coordinate");
                                Element x = document.createElement("x");
                                Element y = document.createElement("y");
                                Element z = document.createElement("z");
                                x.appendChild(document.createTextNode(Integer.toString(coordinate.getX())));
                                y.appendChild(document.createTextNode(Integer.toString(coordinate.getY())));
                                z.appendChild(document.createTextNode(Integer.toString(coordinate.getPlane())));
                                coord.appendChild(x);
                                coord.appendChild(y);
                                coord.appendChild(z);
                                fightArea.appendChild(coord);
                            }
                            rootElement.appendChild(fightArea);

                            Element loot = document.createElement("lootNames");
                            for (String lootName : selectedLoot.getItems()) {
                                Element lootItem = document.createElement("item");
                                lootItem.appendChild(document.createTextNode(lootName));
                                loot.appendChild(lootItem);
                            }
                            rootElement.appendChild(loot);

                            Element selectedPotions = document.createElement("selectedPotions");
                            if (!selectedBoosts.getItems().isEmpty()) {
                                for (Potion p : selectedBoosts.getItems()) {
                                    Element potionItem = document.createElement("selectedPotion");
                                    potionItem.appendChild(document.createTextNode(p.name()));
                                    selectedPotions.appendChild(potionItem);
                                }
                            }
                            rootElement.appendChild(selectedPotions);


                            Element alchLoot = document.createElement("alchLoot");
                            for (String alchLootName : selectedAlchLoot.getItems()) {
                                Element alchItem = document.createElement("alchItem");
                                alchItem.appendChild(document.createTextNode(alchLootName));
                                alchLoot.appendChild(alchItem);
                            }
                            rootElement.appendChild(alchLoot);

                            Element notepaperLoot = document.createElement("notepaperLoot");
                            for (String notePaperLootName : selectedNotepaperLoot.getItems()) {
                                Element notepaperItem = document.createElement("notepaperItem");
                                notepaperItem.appendChild(document.createTextNode(notePaperLootName));
                                notepaperLoot.appendChild(notepaperItem);
                            }
                            rootElement.appendChild(notepaperLoot);

                            Element npcs = document.createElement("npcNames");
                            for (String npcName : selectedMonsters.getItems()) {
                                Element npcItem = document.createElement("npc");
                                npcItem.appendChild(document.createTextNode(npcName));
                                npcs.appendChild(npcItem);
                            }
                            rootElement.appendChild(npcs);

                            Element profileName = document.createElement("profileName");
                            profileName.appendChild(document.createTextNode(txtSetProfileName.getText()));
                            rootElement.appendChild(profileName);

                        /* CREATE XML FILE */

                            TransformerFactory transformerFactory = TransformerFactory.newInstance();
                            Transformer transformer = transformerFactory.newTransformer();
                            DOMSource source = new DOMSource(document);
                            StreamResult result = new StreamResult(file);
                            transformer.transform(source, result);

                            // Load that profile
                            UserProfile builtProfile = buildProfile(file);
                            if (builtProfile != null) {
                                userProfile = builtProfile;
                                populateUI(userProfile);
                                System.out.println("Profile saved and loaded");
                                return true;
                            }
                        } catch (ParserConfigurationException | TransformerException pce) {
                            pce.printStackTrace();
                        }
                    }
                }
            }
        }
        return false;
    }

    public UserProfile createProfile() {
        if (!fightAreaCoords.isEmpty() || Pattern.matches("\\d+", tileRange.getText())) {
            if (!selectedMonsters.getItems().isEmpty()) {
                if (Pattern.matches("\\d+", prayValue.getText()) && Pattern.matches("\\d+", foodAmount.getText()) && Pattern.matches("\\d+", eatValue.getText()) && Pattern.matches("\\d+", criticalHitpoints.getText())) {
                    if (!txtSetProfileName.getText().isEmpty()) {
                        UserProfile profile = new UserProfile();

                        // Create a settings object and store the settings
                        Settings settings = new Settings();
                        if (!foodSelection.getSelectionModel().isEmpty()) {
                            settings.useFood = true;
                            settings.foodName = foodSelection.getSelectionModel().getSelectedItem().getName();
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
                        profile.setFightAreaCoords(fightAreaCoords);
                        if (!bankAreaCoords.isEmpty()) {
                            profile.setBankAreaCoords(bankAreaCoords);
                        }
                        List<String> lootNames = new ArrayList<>();
                        lootNames.addAll(selectedLoot.getItems().stream().map(String::toLowerCase).collect(Collectors.toList()));
                        profile.setLootNames(lootNames.toArray(new String[lootNames.size()]));
                        profile.setNpcNames(selectedMonsters.getItems().toArray(new String[(selectedMonsters.getItems().size())]));
                        profile.setProfileName(txtSetProfileName.getText());
                        return profile;
                    } else {
                        txtProfileSaved.setText("Please give your profile a name");
                    }
                }
            }
        } else {
            txtProfileSaved.setText("You need to make a fight area first!");
        }
        return null;
    }

    public void closeUI() {
        Stage stage = Main.stage;
        if (stage != null && stage.isShowing()) {
            stage.close();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (tabAreas.isSelected()) {
            if (e.getButton() == 2) {
                final Point mousePosition = Mouse.getPosition();
                final Player player = Players.getLocal();
                if (player != null) {
                    Area aroundPlayer = new Area.Circular(player.getPosition(), 15);
                    if (mousePosition != null) {
                        Optional<Coordinate> coordinate =  aroundPlayer.getCoordinates().stream().filter(coord -> coord != null && coord.contains(mousePosition)).findFirst();
                        if (coordinate.isPresent()) {
                            areaCoords.add(coordinate.get());
                        }
                    }
                }
            }
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void onPaint(Graphics2D graphics2D) {
        List<Coordinate> renderCoords = areaCoords;
        if (!renderCoords.isEmpty()) {
            graphics2D.setColor(Color.WHITE);
            renderCoords.parallelStream().forEach(coord -> {
                if (coord != null) {
                    coord.render(graphics2D);
                }
            });
            if (showArea.isSelected()) {
                graphics2D.setColor(Color.GREEN);
                Area area = new Area.Polygonal(renderCoords.toArray(new Coordinate[(renderCoords.size())]));
                if (area.getCoordinates() != null) {
                    area.render(graphics2D);
                }
            }
        }
    }
}
