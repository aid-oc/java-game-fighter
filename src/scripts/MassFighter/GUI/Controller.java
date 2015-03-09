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
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.w3c.dom.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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


    public UserProfile userProfile;
    public Graphics2D areaRender;

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
        updates.add("09/03/2015: Added OSRS boosts");
        updates.add("03/03/2015: Added potion support + quickpraying for OSRS");
        updates.add("02/03/2015: Added more target finding options, you may need to remake profiles ");
        updates.add("02/03/2015: Made target finding faster, fixed crash");
        updates.add("01/03/2015: Updated Logic Randomness");
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

        btnSave.setOnAction(event -> {
            CreateAndSaveProfile();
        });

        btnLoad.setOnAction(event -> {
            final JFileChooser fileChooser = new JFileChooser();
            Main.stage.toBack();
            fileChooser.setCurrentDirectory(Environment.getStorageDirectory());
            int response = fileChooser.showOpenDialog(null);
            if (response == JFileChooser.APPROVE_OPTION) {
                File chosenProfile = fileChooser.getSelectedFile();
                System.out.println("Opened: " + chosenProfile.getName());
                if (chosenProfile.getName().contains("xml")) {
                    try {

                        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
                        Document document = documentBuilder.parse(chosenProfile);

                        UserProfile profile = new UserProfile();
                        Settings settings = new Settings();

                        document.getDocumentElement().normalize();

                        NodeList settingsList = document.getElementsByTagName("settings");
                        Node settingsNode = settingsList.item(0);
                        Element element = (Element) settingsNode;

                        // pull settings out
                        settings.tagSelection = (int)Double.parseDouble(element.getElementsByTagName("targetSelection").item(0).getTextContent());
                        if (element.getElementsByTagName("food").item(0) != null) {
                            settings.useFood = Boolean.valueOf(element.getElementsByTagName("useFood").item(0).getTextContent());
                            settings.exitOutFood = Boolean.valueOf(element.getElementsByTagName("exitOutFood").item(0).getTextContent());
                            settings.foodName = element.getElementsByTagName("food").item(0).getTextContent();
                        }
                        settings.showOutline = Boolean.valueOf(element.getElementsByTagName("showOutline").item(0).getTextContent());
                        settings.lootInCombat = Boolean.valueOf(element.getElementsByTagName("combatLooting").item(0).getTextContent());
                        settings.useAbilities = Boolean.valueOf(element.getElementsByTagName("useAbilities").item(0).getTextContent());
                        settings.useSoulsplit = Boolean.valueOf(element.getElementsByTagName("useSoulsplit").item(0).getTextContent());
                        settings.waitForLoot = Boolean.valueOf(element.getElementsByTagName("waitForLoot").item(0).getTextContent());
                        settings.looting = Boolean.valueOf(element.getElementsByTagName("looting").item(0).getTextContent());
                        settings.lootByValue = Boolean.valueOf(element.getElementsByTagName("lootByValue").item(0).getTextContent());
                        if (settings.lootByValue) {
                            settings.lootValue = Double.valueOf(element.getElementsByTagName("lootValue").item(0).getTextContent());
                        }
                        settings.buryBones = Boolean.valueOf(element.getElementsByTagName("buryBones").item(0).getTextContent());
                        settings.quickPray = Boolean.valueOf(element.getElementsByTagName("quickPray").item(0).getTextContent());
                        settings.exitOnPrayerOut = Boolean.valueOf(element.getElementsByTagName("exitOnPrayerOut").item(0).getTextContent());
                        settings.tagMode = Boolean.valueOf(element.getElementsByTagName("tagMode").item(0).getTextContent());
                        settings.attackCombatMonsters = Boolean.valueOf(element.getElementsByTagName("attackCombatMonsters").item(0).getTextContent());
                        settings.bypassReachable = Boolean.valueOf(element.getElementsByTagName("bypassReachable").item(0).getTextContent());
                        settings.revolutionMode = Boolean.valueOf((element.getElementsByTagName("revolutionMode").item(0).getTextContent()));
                        settings.tagSelection = (int)Double.parseDouble(element.getElementsByTagName("tagSelection").item(0).getTextContent());
                        settings.foodAmount = (int)Double.parseDouble(element.getElementsByTagName("foodAmount").item(0).getTextContent());
                        settings.fightRadius = (int)Double.parseDouble(element.getElementsByTagName("fightRadius").item(0).getTextContent());
                        settings.eatValue = (int)Double.parseDouble(element.getElementsByTagName("eatValue").item(0).getTextContent());
                        settings.prayValue = (int)Double.parseDouble(element.getElementsByTagName("prayValue").item(0).getTextContent());
                        settings.criticalHitpoints = (int)Double.parseDouble(element.getElementsByTagName("criticalHitpoints").item(0).getTextContent());


                        List<Potion> potions = new ArrayList<>();
                        NodeList potionList = document.getElementsByTagName("selectedPotions");
                        NodeList potionNodes = potionList.item(0).getChildNodes();
                        for (int i = 0; i < potionNodes.getLength(); i++) {
                            potions.add(Potion.valueOf(potionNodes.item(i).getTextContent()));
                        }
                        settings.selectedPotions = potions;

                        profile.settings = settings;

                        String profileName = document.getElementsByTagName("profileName").item(0).getTextContent();
                        profile.setProfileName(profileName);

                        List<String> npcNames = new ArrayList<>();
                        NodeList npcList = document.getElementsByTagName("npcNames");
                        NodeList npcNodes = npcList.item(0).getChildNodes();
                        for (int i = 0; i < npcNodes.getLength(); i++) {
                            npcNames.add(npcNodes.item(i).getTextContent());
                        }
                        profile.setNpcNames(npcNames.toArray(new String[npcNames.size()]));

                        List<String> lootNames = new ArrayList<>();
                        NodeList lootList = document.getElementsByTagName("lootNames");
                        NodeList lootNodes = lootList.item(0).getChildNodes();
                        for (int i = 0; i < lootNodes.getLength(); i++) {
                            System.out.println("Added loot item: " + lootNodes.item(i).getTextContent());
                            lootNames.add(lootNodes.item(i).getTextContent().toLowerCase());
                        }
                        profile.setLootNames(lootNames.toArray(new String[lootNames.size()]));

                        List<String> alchLootNames = new ArrayList<>();
                        NodeList alchLootList = document.getElementsByTagName("alchLoot");
                        NodeList alchLootNodes = alchLootList.item(0).getChildNodes();
                        for (int i = 0; i < alchLootNodes.getLength(); i++) {
                            alchLootNames.add(alchLootNodes.item(i).getTextContent().toLowerCase());
                        }
                        profile.setAlchLoot(alchLootNames.toArray(new String[alchLootNames.size()]));


                        List<Coordinate> fightAreaLocations = new ArrayList<>();
                        Node fightAreaNode = document.getElementsByTagName("fightArea").item(0);
                        NodeList coordinateNodes = fightAreaNode.getChildNodes();
                        for (int i = 0; i < coordinateNodes.getLength(); i++) {
                            NodeList coordinateComponents = coordinateNodes.item(i).getChildNodes();
                            int x = Integer.parseInt(coordinateComponents.item(0).getTextContent());
                            int y = Integer.parseInt(coordinateComponents.item(1).getTextContent());
                            int z = Integer.parseInt(coordinateComponents.item(2).getTextContent());
                            fightAreaLocations.add(new Coordinate(x,y,z));
                        }
                        // Remove duplicates
                        HashSet<Coordinate> fightAreas = new HashSet<>();
                        fightAreas.addAll(fightAreaLocations);
                        fightAreaLocations.clear();
                        fightAreaLocations.addAll(fightAreas);
                        System.out.println("-- Start Fight Area --");
                        fightAreaLocations.forEach(System.out::println);
                        System.out.println("-- End Fight Area --");
                        profile.setFightAreaCoords(fightAreaLocations);

                        List<Coordinate> bankAreaLocations = new ArrayList<>();
                        Node bankAreaNode = document.getElementsByTagName("bankArea").item(0);
                        NodeList bankCoordinateNodes = bankAreaNode.getChildNodes();
                        for (int i = 0; i < bankCoordinateNodes.getLength(); i++) {
                            NodeList coordinateComponents = bankCoordinateNodes.item(i).getChildNodes();
                            int x = Integer.parseInt(coordinateComponents.item(0).getTextContent());
                            int y = Integer.parseInt(coordinateComponents.item(1).getTextContent());
                            int z = Integer.parseInt(coordinateComponents.item(2).getTextContent());
                            bankAreaLocations.add(new Coordinate(x,y,z));
                        }
                        // remove duplicates
                        HashSet<Coordinate> bankAreas = new HashSet<>();
                        bankAreas.addAll(bankAreaLocations);
                        bankAreaLocations.clear();
                        bankAreaLocations.addAll(bankAreas);
                        System.out.println("-- Start Bank Area --");
                        bankAreaLocations.forEach(System.out::println);
                        System.out.println("-- End Bank Area --");
                        profile.setBankAreaCoords(bankAreaLocations);

                        userProfile = profile;
                        populateUI(profile);

                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    } catch (SAXException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }



                }
            }
            Main.stage.toFront();
        });

        btnSaveFightArea.setOnAction(event -> {
            if (!areaCoords.isEmpty()) {
                fightAreaCoords.addAll(areaCoords);
                removeAllCoordinates(areaCoords, areaCoords);
                txtAreaStatus.setText("Saved as fight area!");
            }
        });

        btnSaveBankArea.setOnAction(event -> {
            if (!areaCoords.isEmpty()) {
                bankAreaCoords.addAll(areaCoords);
                removeAllCoordinates(areaCoords, areaCoords);
                txtAreaStatus.setText("Saved as bank area!");
            }
        });

        btnResetArea.setOnAction(event -> {
            removeAllCoordinates(areaCoords, areaCoords);
        });

        abilities.setOnAction(event -> {
            if (abilities.isSelected()) {
                revolutionMode.setDisable(false);
            } else {
                revolutionMode.setDisable(true);
            }
        });

        lootByValue.setOnAction(event -> {
            if (lootByValue.isSelected()) {
                lootValue.setDisable(false);
            } else {
                lootValue.setDisable(true);
            }
        });

        btnAddToAlch.setOnAction(event -> {
            String selectedItem = selectedLoot.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                if (!selectedAlchLoot.getItems().contains(selectedItem)) {
                    selectedAlchLoot.getItems().add(selectedItem);
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
        eatValue.setText(Integer.toString(Health.getMaximum()/2));
        if (Environment.isRS3()) {
            criticalHitpoints.setText("500");
            prayValue.setText("50");
        } else {
            criticalHitpoints.setText("5");
            prayValue.setText("5");
        }

    }

    private void populateUI(UserProfile profile) {
        if (profile != null) {
            selectedMonsters.getItems().setAll(profile.getNpcNames());
            if (!profile.settings.selectedPotions.isEmpty()) {
                selectedBoosts.getItems().addAll(profile.settings.selectedPotions);
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
            selectedLoot.getItems().setAll(profile.getLootNames());
            selectedAlchLoot.getItems().setAll(profile.getAlchLoot());
            soulsplit.setSelected(profile.settings.useSoulsplit);
            attackCombatMonsters.setSelected(profile.settings.attackCombatMonsters);
            bypassReachable.setSelected(profile.settings.bypassReachable);
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
            txtProfileSaved.setText("Profile loaded and ready to go!");
        }
    }

    private void removeAllCoordinates(List<Coordinate> items, List<Coordinate> target) {
        for (Iterator<Coordinate> iterator = target.iterator(); iterator.hasNext();) {
            Coordinate coord = iterator.next();
            if (items.contains(coord)) {
                iterator.remove();
            }
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

    public Boolean CreateAndSaveProfile() {

        if (!fightAreaCoords.isEmpty() || Pattern.matches("\\d+", tileRange.getText())) {
            if (!selectedMonsters.getItems().isEmpty()) {
                if (Pattern.matches("\\d+", prayValue.getText()) && Pattern.matches("\\d+", foodAmount.getText()) && Pattern.matches("\\d+", eatValue.getText()) && Pattern.matches("\\d+", criticalHitpoints.getText())) {
                    if (!txtSetProfileName.getText().isEmpty()) {

                        File file = new File(Environment.getStorageDirectory().getAbsolutePath() + "/" + txtSetProfileName.getText() + ".xml");
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

                            Element prayRefreshValue = document.createElement("prayValue");
                            prayRefreshValue.appendChild(document.createTextNode(prayValue.getText()));
                            settings.appendChild(prayRefreshValue);

                            Element criticalHp = document.createElement("criticalHitpoints");
                            criticalHp.appendChild(document.createTextNode(criticalHitpoints.getText()));
                            settings.appendChild(criticalHp);

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
                            System.out.println("Profile saved");
                            return true;
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
                        settings.quickPray = quickPray.isSelected();
                        settings.useSoulsplit = soulsplit.isSelected();
                        settings.exitOnPrayerOut = exitPrayer.isSelected();
                        settings.fightRadius = Integer.valueOf(tileRange.getText());
                        settings.criticalHitpoints = Integer.valueOf(criticalHitpoints.getText());
                        settings.exitOutFood = stopWhenOutOfFood.isSelected();
                        settings.buryBones = buryBones.isSelected();
                        if (!selectedBoosts.getItems().isEmpty()) {
                            settings.selectedPotions = selectedBoosts.getItems();
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
                        aroundPlayer.getCoordinates().stream().filter(coord -> coord.contains(mousePosition)).forEach(coord -> {
                            areaCoords.add(coord);
                        });
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
        areaRender = graphics2D;
        List<Coordinate> renderCoords = areaCoords;
        if (!renderCoords.isEmpty()) {
            graphics2D.setColor(Color.WHITE);
            renderCoords.parallelStream().forEach(coord ->  {
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
