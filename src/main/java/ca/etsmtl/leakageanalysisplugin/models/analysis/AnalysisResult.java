package ca.etsmtl.leakageanalysisplugin.models.analysis;

import ca.etsmtl.leakageanalysisplugin.models.leakage.LeakageInstance;
import ca.etsmtl.leakageanalysisplugin.models.leakage.LeakageType;

import java.util.HashMap;
import java.util.List;

public class AnalysisResult {

    private String filePath;

    private HashMap<LeakageType, List<LeakageInstance>> leakages;

    private AnalysisStatus status;
    private List<Exception> errors;

    public AnalysisResult(String filePath, HashMap<LeakageType, List<LeakageInstance>> leakages) {
        this.filePath = filePath;
        this.leakages = leakages;
        this.status = AnalysisStatus.SUCCESS;
    }

    public AnalysisResult(String filePath, List<Exception> errors) {
        this.filePath = filePath;
        this.errors = errors;
        this.status = AnalysisStatus.FAILED;
    }

    public List<Exception> getErrors() {
        return errors;
    }

    public String getFilePath() {
        return filePath;
    }

    public List<LeakageInstance> getLeakages(LeakageType leakageType) {
        return leakages.get(leakageType);
    }

    public AnalysisStatus getStatus() {
        return status;
    }

    public boolean isSuccessful() {
        return status.equals(AnalysisStatus.SUCCESS);
    }

    @Override
    public String toString() {
        return "AnalysisResult{" +
                "filePath='" + filePath + '\'' +
                ", leakages=" + leakages +
                ", status=" + status +
                ", errors=" + errors +
                '}';
    }
}
