package scripts.MassFighter;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import scripts.MassFighter.Data.Food;
import scripts.MassFighter.Framework.CombatProfile;
import scripts.MassFighter.Profiles.Powerfighting;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
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
    private JCheckBox cbBuryBones;

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
        cmbProfiles.addActionListener(e -> {
            if (!(cmbProfiles.getSelectedItem() instanceof Powerfighting)) {
                int confResult = JOptionPane.showConfirmDialog(null, "Are you sure you wish to start "
                        + cmbProfiles.getSelectedItem().toString() + "?", "Profile Alert", JOptionPane.YES_NO_OPTION);
                if (confResult == JOptionPane.YES_OPTION) {
                    cmbNpcs.setEnabled(false);
                    btnNpcScan.setEnabled(false);
                    btnAddNpc.setEnabled(false);
                    btnRemoveNpc.setEnabled(false);
                    fightRegionSpinner.setEnabled(false);
                    btnAddLoot.setEnabled(false);
                    btnRemoveLoot.setEnabled(false);
                    listLoot.setEnabled(false);
                    txtLootInput.setEnabled(false);
                } else {
                    cmbProfiles.setSelectedIndex(0);
                }
            } else {
                btnAddNpc.setEnabled(true);
                btnRemoveNpc.setEnabled(true);
                cmbNpcs.setEnabled(true);
                btnNpcScan.setEnabled(true);
                fightRegionSpinner.setEnabled(true);
                btnAddLoot.setEnabled(true);
                btnRemoveLoot.setEnabled(true);
                listLoot.setEnabled(true);
                txtLootInput.setEnabled(true);
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
            if (listNpcs.getModel().getSize() > 0 || !(cmbProfiles.getSelectedItem() instanceof Powerfighting)) {

                // Shared settings
                MassFighter.useFood = cbEating.isSelected();
                if (cmbFoodType.isEnabled()) MassFighter.food = (Food) cmbFoodType.getSelectedItem();
                MassFighter.eatValue = (Integer) eatValueSpinner.getValue();
                MassFighter.useSoulsplit = cbUseSoulsplit.isSelected();
                MassFighter.useAbilities = cbUseAbilities.isSelected();
                MassFighter.buryBones = cbBuryBones.isSelected();
                MassFighter.fightRadius = (Integer) fightRegionSpinner.getValue();
                MassGUI.this.setVisible(false);


                CombatProfile profile = (CombatProfile) cmbProfiles.getSelectedItem();
                if (!(profile instanceof Powerfighting)) {
                    MassFighter.looting = (profile.getLootNames() != null && profile.getLootNames().length > 0);
                    MassFighter.combatProfile = profile;
                } else {
                    Powerfighting powerProfile = new Powerfighting();
                    List<String> lootItems = new ArrayList<>();
                    if (listLoot.getModel().getSize() > 0) {
                        MassFighter.looting = true;
                        for (int i = 0; i < listLoot.getModel().getSize(); i++) {
                            lootItems.add(listLoot.getModel().getElementAt(i));
                        }
                        powerProfile.setLootNames(lootItems.toArray(new String[(lootItems.size())]));
                    }
                    // Assign npcs
                    List<String> npcItems = new ArrayList<>();
                    for (int i = 0; i < listNpcs.getModel().getSize(); i++) {
                        npcItems.add(listNpcs.getModel().getElementAt(i));
                    }
                    // Assign fight areas
                    List<Area> areas = new ArrayList<>();
                    areas.add(new Area.Circular(Players.getLocal().getPosition(), MassFighter.fightRadius));
                    powerProfile.setFightAreas(areas);
                    powerProfile.setNpcNames(npcItems.toArray(new String[(npcItems.size())]));
                    MassFighter.combatProfile = powerProfile;
                }
            }
        });
        this.pack();
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
        mainPanel.setLayout(new GridLayoutManager(15, 4, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.setForeground(new Color(-4507801));
        cbEating = new JCheckBox();
        cbEating.setText("");
        mainPanel.add(cbEating, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Fight if I am above this many hitpoints:");
        mainPanel.add(label1, new GridConstraints(6, 0, 2, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Eating?");
        mainPanel.add(label2, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Food Type?");
        mainPanel.add(label3, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mainPanel.add(cmbFoodType, new GridConstraints(8, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Size of fight area? (Radius of a circle with the player as the center)");
        mainPanel.add(label4, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fightRegionSpinner = new JSpinner();
        mainPanel.add(fightRegionSpinner, new GridConstraints(9, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnStart = new JButton();
        btnStart.setText("Fight");
        mainPanel.add(btnStart, new GridConstraints(14, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Recommended Settings: OldSchool UI + Full Manual Combat");
        mainPanel.add(label5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Use abilities? (ONLY Abilities on the ActionBar sorted Ult->Basic)");
        mainPanel.add(label6, new GridConstraints(12, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cbUseAbilities = new JCheckBox();
        cbUseAbilities.setText("");
        mainPanel.add(cbUseAbilities, new GridConstraints(12, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Use soulsplit? (Will exit when no pots/flasks left)");
        mainPanel.add(label7, new GridConstraints(13, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cbUseSoulsplit = new JCheckBox();
        cbUseSoulsplit.setEnabled(true);
        cbUseSoulsplit.setText("");
        mainPanel.add(cbUseSoulsplit, new GridConstraints(13, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("What do you want to loot?");
        mainPanel.add(label8, new GridConstraints(10, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtLootInput = new JTextField();
        mainPanel.add(txtLootInput, new GridConstraints(10, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        btnAddLoot = new JButton();
        btnAddLoot.setText("Add Loot");
        mainPanel.add(btnAddLoot, new GridConstraints(10, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        listLoot.setLayoutOrientation(0);
        mainPanel.add(listLoot, new GridConstraints(10, 3, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        eatValueSpinner = new JSpinner();
        mainPanel.add(eatValueSpinner, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnRemoveLoot = new JButton();
        btnRemoveLoot.setText("Remove Loot");
        mainPanel.add(btnRemoveLoot, new GridConstraints(11, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnNpcScan = new JButton();
        btnNpcScan.setText("NPC Scan");
        mainPanel.add(btnNpcScan, new GridConstraints(2, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setBackground(new Color(-1564368));
        label9.setEnabled(true);
        label9.setText("Choose a profile: (Fighting Mode)");
        mainPanel.add(label9, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cmbProfiles.setEnabled(true);
        mainPanel.add(cmbProfiles, new GridConstraints(1, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cmbNpcs.setEnabled(false);
        mainPanel.add(cmbNpcs, new GridConstraints(2, 1, 2, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnAddNpc = new JButton();
        btnAddNpc.setText("Add NPC");
        mainPanel.add(btnAddNpc, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        listNpcs.setLayoutOrientation(0);
        mainPanel.add(listNpcs, new GridConstraints(2, 3, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        btnRemoveNpc = new JButton();
        btnRemoveNpc.setText("Remove NPC");
        mainPanel.add(btnRemoveNpc, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("Loot and bury Bones?");
        mainPanel.add(label10, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cbBuryBones = new JCheckBox();
        cbBuryBones.setText("");
        mainPanel.add(cbBuryBones, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
