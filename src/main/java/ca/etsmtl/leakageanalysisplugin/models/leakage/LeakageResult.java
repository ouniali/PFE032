package ca.etsmtl.leakageanalysisplugin.models.leakage;

import java.util.List;

public class LeakageResult {

    private String filePath;

    private List<Leakage> leakages;

    public LeakageResult(String filePath, List<Leakage> leakages) {
        this.filePath = filePath;
        this.leakages = leakages;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public List<Leakage> getLeakages() {
        return leakages;
    }

    public void setLeakages(List<Leakage> leakages) {
        this.leakages = leakages;
    }

    @Override
    public String toString() {
        return "LeakageResult{" +
                "filePath='" + filePath + '\'' +
                ", leakages=" + leakages +
                '}';
    }
}