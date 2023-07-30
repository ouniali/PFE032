package ca.etsmtl.leakageanalysisplugin.windows;

import javax.swing.*;
import java.util.Objects;

public enum AnalysisIcon {
    EMPTY("/icons/empty.png"), // Empty Circle
    NOTDETECTED("/icons/check.png"), // Green Check Mark
    DETECTED("/icons/cancel.png"); // Red X

    private final String filePath;
    AnalysisIcon(String filePath) { this.filePath = filePath; }
    public String getFilePath() { return filePath; }
    public ImageIcon getIcon() {
        return new ImageIcon(Objects.requireNonNull(getClass().getResource(filePath)));
    }
}
