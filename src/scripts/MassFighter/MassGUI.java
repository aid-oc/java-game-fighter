package scripts.MassFighter;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import scripts.MassFighter.Data.Settings;
import scripts.MassFighter.Data.Food;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;

public class MassGUI extends JFrame {

    private JButton btnNpcScan;
    private JComboBox<String> cmbNpcs;
    private JCheckBox cbEating;
    private JSpinner eatValueSpinner;
    private JComboBox<Food> cmbFoodType;
    private JSpinner fightRegionSpinner;
    private JPanel mainPanel;
    private JButton btnStart;
    private JCheckBox cbUseAbilities;
    private JCheckBox cbUseSoulsplit;
    private JTextField txtLootInput;
    private JButton btnAddLoot;
    private JButton btnRemoveLoot;
    private JList<String> listLoot;
    private JComboBox<CombatProfile> cmbProfiles;
    private JButton btnAddNpc;
    private JButton btnRemoveNpc;
    private JList<String> listNpcs;

    public MassGUI() {
        super("MassFighter - AIO Combat");
        $$$setupUI$$$();
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                MassFighter.requestedShutdown = true;
            }
        });


        CombatProfile.getProfiles().stream().forEach(cmbProfiles::addItem);
        cmbProfiles.setSelectedItem(null);
        cmbProfiles.addActionListener(e -> {
            if (cmbProfiles.getSelectedItem() != null) {
                CombatProfile selectedProfile = (CombatProfile) cmbProfiles.getSelectedItem();
                int confResult = JOptionPane.showConfirmDialog(null, "Are you sure you wish to start " + selectedProfile.toString() + "?", "Profile Alert", JOptionPane.YES_NO_OPTION);
                if (confResult == JOptionPane.YES_OPTION) {
                    MassFighter.combatProfile = selectedProfile;
                    cmbNpcs.setEnabled(false);
                    btnNpcScan.setEnabled(false);
                    fightRegionSpinner.setEnabled(false);
                    btnAddLoot.setEnabled(false);
                    btnRemoveLoot.setEnabled(false);
                    listLoot.setEnabled(false);
                    txtLootInput.setEnabled(false);
                } else {
                    cmbProfiles.setSelectedItem(null);
                }
            }
        });

        cmbNpcs.addItem("Please perform a scan");
        cmbNpcs.setEnabled(false);
        cmbFoodType.setEnabled(false);

        btnNpcScan.addActionListener(e -> {
            final Collection<Npc> nearbyNpcs = Npcs.newQuery().within(new Area.Circular(Players.getLocal().getPosition(), 15))
                    .actions("Attack").results();
            cmbNpcs.removeItem("Please perform a scan");
            for (Npc n : nearbyNpcs) {
                if (n != null)
                    cmbNpcs.addItem(n.getDefinition().getName());
            }
            cmbNpcs.setEnabled(true);
        });

        // Set recommended fight region
        fightRegionSpinner.setValue(10);

        // Set default hitpoints values depending on game type
        if (Environment.isOSRS()) {
            eatValueSpinner.setValue(30);
        } else {
            eatValueSpinner.setValue(3000);
        }

        cbEating.addActionListener(e -> {
            if (cbEating.isSelected()) {
                cmbFoodType.removeItem("Enable eating");
                cmbFoodType.setEnabled(true);
                for (Food f : Food.values()) {
                    cmbFoodType.addItem(f);
                }
            } else {
                cmbFoodType.setEnabled(false);
            }
        });


        DefaultListModel<String> npcModel = new DefaultListModel<>();
        listNpcs.setModel(npcModel);

        btnAddNpc.addActionListener(e -> {
            if (cmbNpcs.getSelectedItem() != null) {
                if (!npcModel.contains(cmbNpcs.getSelectedItem().toString())) {
                    npcModel.addElement(cmbNpcs.getSelectedItem().toString());
                    listNpcs.setSelectedValue(cmbNpcs.getSelectedItem().toString(), false);
                }
            }
        });

        btnRemoveNpc.addActionListener(e -> {
            if (npcModel.contains(listNpcs.getSelectedValue())) {
                npcModel.removeElement(listNpcs.getSelectedValue());
            }
        });

        //

        DefaultListModel<String> lootModel = new DefaultListModel<>();
        listLoot.setModel(lootModel);

        btnAddLoot.addActionListener(e -> {
            if (!txtLootInput.getText().isEmpty()) {
                if (!lootModel.contains(txtLootInput.getText())) {
                    lootModel.addElement(txtLootInput.getText());
                    txtLootInput.setText("");
                }
            }
        });

        btnRemoveLoot.addActionListener(e -> {
            if (listLoot.getSelectedValue() != null) {
                if (lootModel.contains(listLoot.getSelectedValue())) {
                    lootModel.removeElement(listLoot.getSelectedValue());
                }
            }

        });

        btnStart.addActionListener(e -> {

            if (MassFighter.combatProfile != null) {

                // SET UP PROFILE SETTINGS
                CombatProfile selectedProfile = MassFighter.combatProfile;
                Settings.lootChoices = Arrays.asList(selectedProfile.getLootNames());
                Collections.addAll(Settings.chosenNpcNames, selectedProfile.getNpcNames());
                Settings.fightAreas = selectedProfile.getFightAreas();
                if (selectedProfile.getBankArea() != null) {
                    Settings.profileBankArea = selectedProfile.getBankArea();
                }
                // SET UP GENERIC FIGHTER SETTINGS
                Settings.useAbilities = cbUseAbilities.isSelected();
                Settings.useSoulsplit = cbUseSoulsplit.isSelected();
                Settings.eatValue = (Integer) eatValueSpinner.getValue();
                if (cbEating.isSelected()) {
                    Settings.usingFood = true;
                    Settings.chosenFood = (Food) cmbFoodType.getSelectedItem();
                } else Settings.usingFood = false;
                Settings.isLooting = selectedProfile.getLootNames().length > 0;
                MassGUI.this.setVisible(false);

            } else if (MassFighter.combatProfile == null && listNpcs.getModel().getSize() > 0) {

                // Assign food
                if (cbEating.isSelected()) {
                    Settings.usingFood = true;
                    Settings.chosenFood = (Food) cmbFoodType.getSelectedItem();
                } else Settings.usingFood = false;
                // Assign loot
                List<String> lootItems = new ArrayList<>();
                Settings.isLooting = listLoot.getModel().getSize() > 0;
                if (listLoot.getModel().getSize() > 0) {
                    for (int i = 0; i < listLoot.getModel().getSize(); i++) {
                        lootItems.add(listLoot.getModel().getElementAt(i));
                    }
                }
                // Assign options
                Settings.lootChoices = lootItems;
                List<String> npcItems = new ArrayList<>();
                for (int i = 0; i < listNpcs.getModel().getSize(); i++) {
                    npcItems.add(listNpcs.getModel().getElementAt(i));
                }
                Settings.chosenNpcNames = npcItems;
                Settings.useAbilities = cbUseAbilities.isSelected();
                Settings.useSoulsplit = cbUseSoulsplit.isSelected();
                Settings.eatValue = (Integer) eatValueSpinner.getValue();
                Settings.chosenFightRegion = (Integer) fightRegionSpinner.getValue();
                Settings.startLocation = Players.getLocal().getPosition();
                MassGUI.this.setVisible(false);

            }
        });
        pack();
    }


    private void createUIComponents() {
        cmbNpcs = new JComboBox<>();
        cmbFoodType = new JComboBox<>();
        listLoot = new JList<>();
        cmbProfiles = new JComboBox<>();
        listNpcs = new JList<>();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        mainPanel = new JPanel();
        mainPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(14, 4, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.setForeground(new Color(-4507801));
        cbEating = new JCheckBox();
        cbEating.setText("");
        mainPanel.add(cbEating, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Fight if I am above this many hitpoints:");
        mainPanel.add(label1, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 2, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Eating?");
        mainPanel.add(label2, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Food Type?");
        mainPanel.add(label3, new com.intellij.uiDesigner.core.GridConstraints(7, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mainPanel.add(cmbFoodType, new com.intellij.uiDesigner.core.GridConstraints(7, 1, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Size of fight area? (Radius of a circle with the player as the center)");
        mainPanel.add(label4, new com.intellij.uiDesigner.core.GridConstraints(8, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fightRegionSpinner = new JSpinner();
        mainPanel.add(fightRegionSpinner, new com.intellij.uiDesigner.core.GridConstraints(8, 1, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnStart = new JButton();
        btnStart.setText("Fight");
        mainPanel.add(btnStart, new com.intellij.uiDesigner.core.GridConstraints(13, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Recommended Settings: OldSchool UI + Full Manual Combat");
        mainPanel.add(label5, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Use abilities? (ONLY Abilities on the ActionBar sorted Ult->Basic)");
        mainPanel.add(label6, new com.intellij.uiDesigner.core.GridConstraints(11, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cbUseAbilities = new JCheckBox();
        cbUseAbilities.setText("");
        mainPanel.add(cbUseAbilities, new com.intellij.uiDesigner.core.GridConstraints(11, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Use soulsplit? (Will exit when no pots/flasks left)");
        mainPanel.add(label7, new com.intellij.uiDesigner.core.GridConstraints(12, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cbUseSoulsplit = new JCheckBox();
        cbUseSoulsplit.setEnabled(true);
        cbUseSoulsplit.setText("");
        mainPanel.add(cbUseSoulsplit, new com.intellij.uiDesigner.core.GridConstraints(12, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("What do you want to loot?");
        mainPanel.add(label8, new com.intellij.uiDesigner.core.GridConstraints(9, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtLootInput = new JTextField();
        mainPanel.add(txtLootInput, new com.intellij.uiDesigner.core.GridConstraints(9, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        btnAddLoot = new JButton();
        btnAddLoot.setText("Add Loot");
        mainPanel.add(btnAddLoot, new com.intellij.uiDesigner.core.GridConstraints(9, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mainPanel.add(listLoot, new com.intellij.uiDesigner.core.GridConstraints(9, 3, 2, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        eatValueSpinner = new JSpinner();
        mainPanel.add(eatValueSpinner, new com.intellij.uiDesigner.core.GridConstraints(6, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnRemoveLoot = new JButton();
        btnRemoveLoot.setText("Remove Loot");
        mainPanel.add(btnRemoveLoot, new com.intellij.uiDesigner.core.GridConstraints(10, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnNpcScan = new JButton();
        btnNpcScan.setText("NPC Scan");
        mainPanel.add(btnNpcScan, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 2, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("Profile: (Coming soon, see thread)");
        mainPanel.add(label9, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cmbProfiles.setEnabled(false);
        mainPanel.add(cmbProfiles, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cmbNpcs.setEnabled(false);
        mainPanel.add(cmbNpcs, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 2, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnAddNpc = new JButton();
        btnAddNpc.setText("Add NPC");
        mainPanel.add(btnAddNpc, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mainPanel.add(listNpcs, new com.intellij.uiDesigner.core.GridConstraints(2, 3, 2, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        btnRemoveNpc = new JButton();
        btnRemoveNpc.setText("Remove NPC");
        mainPanel.add(btnRemoveNpc, new com.intellij.uiDesigner.core.GridConstraints(3, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
