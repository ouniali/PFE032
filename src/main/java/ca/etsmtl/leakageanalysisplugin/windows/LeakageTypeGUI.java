package ca.etsmtl.leakageanalysisplugin.windows;


import ca.etsmtl.leakageanalysisplugin.models.leakage.LeakageType;

import javax.swing.*;
import java.awt.*;

public class LeakageTypeGUI {
    private interface LeakageInstance {

    }

    private final LeakageType leakageType;

    protected JPanel mainPanel;
    protected JLabel nameAndCountLabel;
    protected JLabel iconLabel;

    public LeakageTypeGUI(LeakageType leakageType) {
        this.leakageType = leakageType;

        setupMainPanel();
        reset();

        JPanel leakageInstancesPanel = new JPanel(); // TODO

    }

    private void setupMainPanel() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));

        nameAndCountLabel = new JLabel();
        mainPanel.add(nameAndCountLabel, BorderLayout.WEST);

        iconLabel = new JLabel();
        mainPanel.add(iconLabel, BorderLayout.EAST);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void setCount(String count) {
        nameAndCountLabel.setText(String.format("%s: %s", leakageType.getName(), count));
    }

    private void setIcon(ImageIcon icon) {
        iconLabel.setIcon(icon);
    }

    public void reset() {
        setCount(String.valueOf(0));
        setIcon(AnalysisIcon.EMPTY.getIcon());
    }

    public LeakageType getType() {
        return leakageType;
    }
}
