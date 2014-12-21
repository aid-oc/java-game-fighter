package scripts.MassFighter;

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
        this.setSize(new Dimension(800, 450));
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
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setForeground(new Color(-4507801));
        cbEating = new JCheckBox();
        cbEating.setText("");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(cbEating, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("Fight if I am above this many hitpoints:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridheight = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(label1, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Eating?");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(label2, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("Food Type?");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(label3, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(cmbFoodType, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Size of fight area? (Radius of a circle with the player as the center)");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(label4, gbc);
        fightRegionSpinner = new JSpinner();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(fightRegionSpinner, gbc);
        btnStart = new JButton();
        btnStart.setText("Fight");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 14;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(btnStart, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("Recommended Settings: OldSchool UI + Full Manual Combat");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(label5, gbc);
        final JLabel label6 = new JLabel();
        label6.setText("Use abilities? (ONLY Abilities on the ActionBar sorted Ult->Basic)");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(label6, gbc);
        cbUseAbilities = new JCheckBox();
        cbUseAbilities.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 12;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(cbUseAbilities, gbc);
        final JLabel label7 = new JLabel();
        label7.setText("Use soulsplit? (Will exit when no pots/flasks left)");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 13;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(label7, gbc);
        cbUseSoulsplit = new JCheckBox();
        cbUseSoulsplit.setEnabled(true);
        cbUseSoulsplit.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 13;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(cbUseSoulsplit, gbc);
        final JLabel label8 = new JLabel();
        label8.setText("What do you want to loot?");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(label8, gbc);
        txtLootInput = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 10;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(txtLootInput, gbc);
        btnAddLoot = new JButton();
        btnAddLoot.setText("Add Loot");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 10;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(btnAddLoot, gbc);
        listLoot.setLayoutOrientation(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 10;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(listLoot, gbc);
        eatValueSpinner = new JSpinner();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(eatValueSpinner, gbc);
        btnRemoveLoot = new JButton();
        btnRemoveLoot.setText("Remove Loot");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 11;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(btnRemoveLoot, gbc);
        btnNpcScan = new JButton();
        btnNpcScan.setText("NPC Scan");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(btnNpcScan, gbc);
        final JLabel label9 = new JLabel();
        label9.setBackground(new Color(-1564368));
        label9.setEnabled(true);
        label9.setText("Choose a profile: (Fighting Mode)");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(label9, gbc);
        cmbProfiles.setEnabled(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(cmbProfiles, gbc);
        cmbNpcs.setEnabled(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridheight = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(cmbNpcs, gbc);
        btnAddNpc = new JButton();
        btnAddNpc.setText("Add NPC");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(btnAddNpc, gbc);
        listNpcs.setLayoutOrientation(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(listNpcs, gbc);
        btnRemoveNpc = new JButton();
        btnRemoveNpc.setText("Remove NPC");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(btnRemoveNpc, gbc);
        final JLabel label10 = new JLabel();
        label10.setText("Loot and bury Bones?");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(label10, gbc);
        cbBuryBones = new JCheckBox();
        cbBuryBones.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(cbBuryBones, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
