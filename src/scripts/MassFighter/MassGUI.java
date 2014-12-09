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
import java.util.Collection;

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
    private JCheckBox cbLootCharms;

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

        fightRegionSpinner.setValue(10);
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


        btnStart.addActionListener(e -> {
            if (cmbNpcs.isEnabled()) {
                Settings.chosenNpcName = cmbNpcs.getSelectedItem().toString();
                if (cbEating.isSelected()) {
                    Settings.usingFood = true;
                    Settings.chosenFood = (Food) cmbFoodType.getSelectedItem();
                } else Settings.usingFood = false;

                Settings.lootCharms = cbLootCharms.isSelected();
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
        btnNpcScan = new JButton();
        btnNpcScan.setText("NPC Scan");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(btnNpcScan, gbc);
        cmbNpcs.setEnabled(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(cmbNpcs, gbc);
        cbEating = new JCheckBox();
        cbEating.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(cbEating, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("Eat Value? (Script exits if you have no food and you go below this value)");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridheight = 9;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(label1, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Eating?");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(label2, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("Food Type?");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 12;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(label3, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 12;
        gbc.gridwidth = 3;
        gbc.gridheight = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(cmbFoodType, gbc);
        eatValueSpinner = new JSpinner();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.gridheight = 4;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(eatValueSpinner, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Size of fight area? (Radius of a circle with the player as the center)");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 14;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(label4, gbc);
        fightRegionSpinner = new JSpinner();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 14;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(fightRegionSpinner, gbc);
        btnStart = new JButton();
        btnStart.setText("Fight");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 18;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(btnStart, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("Recommended Settings: OldSchool UI + Full Manual Combat");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(label5, gbc);
        final JLabel label6 = new JLabel();
        label6.setEnabled(true);
        label6.setText("Loot charms? (Don't have a full inventory)");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 15;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(label6, gbc);
        final JLabel label7 = new JLabel();
        label7.setText("Use abilities? (ONLY Abilities on the ActionBar sorted Ult->Basic)");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 16;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(label7, gbc);
        cbUseAbilities = new JCheckBox();
        cbUseAbilities.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 16;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(cbUseAbilities, gbc);
        final JLabel label8 = new JLabel();
        label8.setText("Use soulsplit? (Will exit when no pots/flasks left)");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 17;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(label8, gbc);
        cbUseSoulsplit = new JCheckBox();
        cbUseSoulsplit.setEnabled(true);
        cbUseSoulsplit.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 17;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(cbUseSoulsplit, gbc);
        cbLootCharms = new JCheckBox();
        cbLootCharms.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 15;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(cbLootCharms, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
