package scripts.MassFighter;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.framework.AbstractScript;
import scripts.MassFighter.Data.Food;
import scripts.MassFighter.Data.Settings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * Created by Aidan on 06/11/2014.
 */
public class MassGUI extends JFrame {
    private JButton btnNpcScan;
    private JComboBox<String> cmbNpcs;
    private JCheckBox cbEating;
    private JSpinner eatValueSpinner;
    private JComboBox<java.io.Serializable> cmbFoodType;
    private JSpinner fightRegionSpinner;
    private JPanel mainPanel;
    private JButton btnStart;
    private JCheckBox cbUseAbilities;
    private JCheckBox cbUseSoulsplit;
    private JCheckBox cbLootCharms;

    public MassGUI() {
        super("MassFighter - AIO Combat");
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                final AbstractScript script = Environment.getScript();
                if (script != null) {
                    script.stop();
                }
            }
        });


        cmbNpcs.addItem("Please perform a scan");
        cmbNpcs.setEnabled(false);

        cmbFoodType.addItem("Enable eating");
        cmbFoodType.setEnabled(false);

        btnNpcScan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final List<Npc> nearbyNpcs = Npcs.newQuery().within(new Area.Circular(Players.getLocal().getPosition(), 15))
                        .actions("Attack").results();
                cmbNpcs.removeItem("Please perform a scan");
                for (int i = 0; i < nearbyNpcs.size(); i++) {
                    if (!nearbyNpcs.get(i).getName().isEmpty())
                        cmbNpcs.addItem(nearbyNpcs.get(i).getDefinition().getName());
                }
                cmbNpcs.setEnabled(true);
            }
        });

        fightRegionSpinner.setValue(10);
        eatValueSpinner.setValue(5000);

        cbEating.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cbEating.isSelected()) {
                    cmbFoodType.removeItem("Enable eating");
                    cmbFoodType.setEnabled(true);
                    for (Food f : Food.values()) {
                        cmbFoodType.addItem(f);
                    }
                } else {
                    cmbFoodType.setEnabled(false);
                }
            }
        });



        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cmbNpcs.isEnabled()) {
                    Settings.chosenNpcName = cmbNpcs.getSelectedItem().toString();
                    if (cbEating.isSelected()) {
                        Settings.usingFood = true;
                        Settings.chosenFood = (Food) cmbFoodType.getSelectedItem();
                    } else Settings.usingFood = false;

                    Settings.lootCharms = cbLootCharms.isSelected();
                    Settings.useAbilities = cbUseAbilities.isSelected();
                    Settings.useSoulsplit = cbUseSoulsplit.isSelected();

                    Settings.eatValue = (Integer)eatValueSpinner.getValue();
                    Settings.chosenFightRegion = (Integer)fightRegionSpinner.getValue();
                    Settings.startLocation = Players.getLocal().getPosition();
                    MassGUI.this.setVisible(false);
                }
            }
        });
        pack();
    }
}
