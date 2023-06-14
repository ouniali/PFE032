package etsmtl.ca.leakanalysis.toolWindow;

import etsmtl.ca.leakanalysis.leakageAnalysis.ILeakageType;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class LeakageTypeGUI
{
    // TODO update to valid icons

    // TODO Create Task: Define and find states of analysis and icons
    public static final String NO_ANALYSIS = "/toolWindow/empty.png"; // Empty Circle
    public static final String LEAKAGE_NOT_DETECTED = "/toolWindow/check.png"; // Green Check Mark
    public static final String LEAKAGE_DETECTED = "/toolWindow/cancel.png"; // Red X

    protected ILeakageType leakageType;
    protected JPanel panel;
    protected JLabel nameLabel;
    protected JLabel iconLabel;

    public LeakageTypeGUI(String name, ILeakageType ILeakageType)
    {
        this.leakageType = ILeakageType;

        panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));

        nameLabel = new JLabel();
        nameLabel.setText(name);
        panel.add(nameLabel, BorderLayout.WEST);

        iconLabel = new JLabel();
        setIconLabel(NO_ANALYSIS);
        panel.add(iconLabel, BorderLayout.EAST);
    }

    public JPanel getPanel()
    {
        return panel;
    }

    private void setIconLabel(String image)
    {
        iconLabel.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource(image))));
    }

    public void analyze(String[] files)
    {
        // TODO Eventually list the files affected by the leakage
        if (leakageType.analyze(files))
        {
            setIconLabel(LEAKAGE_DETECTED);
        }
        else
        {
            setIconLabel(LEAKAGE_NOT_DETECTED);
        }
    }

    public void analyze(String file)
    {
        if (leakageType.analyze(file))
        {
            setIconLabel(LEAKAGE_DETECTED);
        }
        else
        {
            setIconLabel(LEAKAGE_NOT_DETECTED);
        }
    }

    public void reset()
    {
        setIconLabel(NO_ANALYSIS);
    }
}
