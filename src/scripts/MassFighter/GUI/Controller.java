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
import scripts.MassFighter.Data.Food;
import scripts.MassFighter.Framework.UserProfile;
import scripts.MassFighter.MassFighter;

import javax.swing.*;
import javax.xml.bind.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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
    private Button btnRunNoSave;
    @FXML
    private CheckBox revolutionMode;
    @FXML
    private TextField lootValue;
    @FXML
    private CheckBox lootByValue;

    public UserProfile userProfile;
    public Graphics2D areaRender;

    private List<Coordinate> areaCoords = new ArrayList<>();
    private List<Coordinate> fightAreaCoords = new ArrayList<>();
    private List<Coordinate> bankAreaCoords = new ArrayList<>();

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
        txtProfileName.setText("None Selected");
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

        btnSave.setOnAction(event -> {
            UserProfile profile = createProfile();
            if (profile != null) {
                saveProfile(profile);
                userProfile = profile;
            } else {
                txtProfileSaved.setText("Invalid profile");
            }
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
                        JAXBContext context = JAXBContext.newInstance(UserProfile.class);
                        Unmarshaller unmarshaller = context.createUnmarshaller();
                        UserProfile profile = (UserProfile) unmarshaller.unmarshal(chosenProfile);
                        userProfile = profile;
                        txtProfileName.setText(profile.getProfileName());
                        txtProfileSaved.setText("Profile loaded!");
                    } catch (JAXBException e) {
                        e.printStackTrace();
                        txtProfileSaved.setText("Invalid profile!");
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

        btnRunNoSave.setOnAction(event -> {
            txtSetProfileName.setText("Quick Run");
            UserProfile quickProfile = createProfile();
            if (quickProfile != null) {
                userProfile = quickProfile;
                btnStart.fire();
            } else {
                txtProfileSaved.setText("Invalid profile");
            }
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
        tileRange.setText("20");
        eatValue.setText(Integer.toString(Health.getMaximum()/2));
        criticalHitpoints.setText("1000");
        prayValue.setText("200");
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
        if (userProfile != null) {
            MassFighter.userProfile = userProfile;
            MassFighter.setupRunning = false;
            closeUI();
        } else {
            txtProfileSaved.setText("Invalid Profile!");
            System.out.println("Profile Invalid");
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

    public Boolean saveProfile(UserProfile profile) {
        try {
            File file = new File(Environment.getStorageDirectory().getAbsolutePath() + "/" + profile.getProfileName() + ".xml");
            JAXBContext context = JAXBContext.newInstance(UserProfile.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(profile, file);
            return true;
        } catch (JAXBException e) {
            e.printStackTrace();
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
                        settings.fightRadius = Integer.valueOf(tileRange.getText());
                        settings.revolutionMode = revolutionMode.isSelected();
                        settings.quickPray = quickPray.isSelected();
                        settings.useSoulsplit = soulsplit.isSelected();
                        settings.exitOnPrayerOut = exitPrayer.isSelected();
                        settings.fightRadius = Integer.valueOf(tileRange.getText());
                        settings.criticalHitpoints = Integer.valueOf(criticalHitpoints.getText());
                        settings.exitOutFood = stopWhenOutOfFood.isSelected();
                        settings.buryBones = buryBones.isSelected();

                        if (!selectedLoot.getItems().isEmpty() || lootByValue.isSelected() && Pattern.matches("\\d+", lootValue.getText())) {
                            if (lootByValue.isSelected() && Pattern.matches("\\d+", lootValue.getText())) {
                                settings.lootByValue = true;
                                settings.lootValue = Double.valueOf(lootValue.getText());
                            }
                            settings.looting = true;
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
            renderCoords.parallelStream().forEach(coord -> coord.render(graphics2D));
            if (showArea.isSelected()) {
                graphics2D.setColor(Color.GREEN);
                Area area = new Area.Polygonal(renderCoords.toArray(new Coordinate[(renderCoords.size())]));
                area.render(graphics2D);
            }
        }
    }
}
