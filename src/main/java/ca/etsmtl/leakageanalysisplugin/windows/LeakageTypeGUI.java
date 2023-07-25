package ca.etsmtl.leakageanalysisplugin.windows;


import ca.etsmtl.leakageanalysisplugin.models.leakage.LeakageType;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class LeakageTypeGUI {
    public static final String EMPTY_ANALYSIS_ICON = "/icons/empty.png"; // Empty Circle
    public static final String NO_LEAKAGE_ICON = "/icons/check.png"; // Green Check Mark
    public static final String LEAKAGE_DETECTED_ICON = "/icons/cancel.png"; // Red X

    private final String name;

    private final LeakageType leakageType;

    protected JPanel mainPanel;
    protected JLabel nameLabel;
    protected JLabel countLabel;
    protected JLabel iconLabel;

    public LeakageTypeGUI(String name, LeakageType leakageType) {
        this.name = name;
        this.leakageType = leakageType;

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));

        nameLabel = new JLabel();
        setCount(String.valueOf(0));
        mainPanel.add(nameLabel, BorderLayout.WEST);

        iconLabel = new JLabel();
        setIcon(EMPTY_ANALYSIS_ICON);
        mainPanel.add(iconLabel, BorderLayout.EAST);

        JPanel leakageInstancesPanel = new JPanel(); // TODO

    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void setCount(String count) {
        nameLabel.setText(String.format("%s: %s", name, count));
    }

    private void setIcon(String image) {
        iconLabel.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource(image))));
    }

    public void parseData(JSONObject data) {
        int count = data.getInt("# detected");

        setCount(String.valueOf(count));
        setIcon(count == 0 ? NO_LEAKAGE_ICON : LEAKAGE_DETECTED_ICON);

        // TODO PARSE INSTANCES OF LEAKAGE
    }

    public void reset() {
        setCount(String.valueOf(0));
        setIcon(EMPTY_ANALYSIS_ICON);
    }

    public LeakageType getType() {
        return leakageType;
    }
}
