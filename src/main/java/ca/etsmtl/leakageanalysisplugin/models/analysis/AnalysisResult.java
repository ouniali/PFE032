package ca.etsmtl.leakageanalysisplugin.models.analysis;

import ca.etsmtl.leakageanalysisplugin.models.leakage.Leakage;

import java.util.List;

public class AnalysisResult {

    private String filePath;

    private List<Leakage> leakages;

    private AnalysisStatus status;
    private List<Exception> errors;

    public AnalysisResult(String filePath, List<Leakage> leakages) {
        this.filePath = filePath;
        this.leakages = leakages;
    }

    public AnalysisResult(String filePath) {
        this.filePath = filePath;
    }

    public List<Exception> getErrors() {
        return errors;
    }

    public void setErrors(List<Exception> errors) {
        this.errors = errors;
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
        return "AnalysisResult{" +
                "filePath='" + filePath + '\'' +
                ", leakages=" + leakages +
                ", status=" + status +
                ", errors=" + errors +
                '}';
    }

    public AnalysisStatus getStatus() {
        return status;
    }

    public void setStatus(AnalysisStatus status) {
        this.status = status;
    }

    public boolean isSuccessful() {
        return status.equals(AnalysisStatus.SUCCESS);
    }
}
