package ca.etsmtl.leakageanalysisplugin.windows;


import ca.etsmtl.leakageanalysisplugin.models.leakage.LeakageInstance;
import ca.etsmtl.leakageanalysisplugin.models.leakage.LeakageType;
import ca.etsmtl.leakageanalysisplugin.util.FilesUtil;
import com.intellij.openapi.ui.VerticalFlowLayout;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.List;

public class LeakageTypeGUI {
    private final LeakageType leakageType;

    protected JPanel mainPanel;
    protected JLabel nameAndCountLabel;
    protected JLabel iconLabel;
    JPanel leakageInstancesPanel;

    public LeakageTypeGUI(LeakageType leakageType) {
        this.leakageType = leakageType;

        setupMainPanel();
        reset();
    }

    private void setupMainPanel() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));

        nameAndCountLabel = new JLabel();
        mainPanel.add(nameAndCountLabel, BorderLayout.WEST);

        iconLabel = new JLabel();
        mainPanel.add(iconLabel, BorderLayout.EAST);

        leakageInstancesPanel = new JPanel();
        leakageInstancesPanel.setLayout(new VerticalFlowLayout());

        mainPanel.add(leakageInstancesPanel, BorderLayout.SOUTH);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void setCount(int count) {
        nameAndCountLabel.setText(String.format("%s: %s", leakageType.getName(), count));
    }

    private void setIcon(ImageIcon icon) {
        iconLabel.setIcon(icon);
    }

    private void addInstance(LeakageInstance instance) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.darkGray);
        panel.setBorder(BorderFactory.createLineBorder(Color.black));
        panel.add(new JLabel(String.format("%s: %s", FilesUtil.getFileName(instance.filePath), instance.line)));
        leakageInstancesPanel.add(panel);
    }

    public void update(List<LeakageInstance> instances) {
        for (LeakageInstance instance: instances) {
            addInstance(instance);
        }

        int count = leakageInstancesPanel.getComponentCount();

        setCount(count);
        setIcon((count == 0 ? AnalysisIcon.NOTDETECTED : AnalysisIcon.DETECTED).getIcon());
    }

    public void reset() {
        setCount(0);
        setIcon(AnalysisIcon.EMPTY.getIcon());
        leakageInstancesPanel.removeAll();
    }

    public LeakageType getType() {
        return leakageType;
    }
}
