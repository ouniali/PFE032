package ca.etsmtl.leakageanalysisplugin.services;

import ca.etsmtl.leakageanalysisplugin.models.leakage.AnalysisResult;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface LeakageService {

    AnalysisResult analyze(String filePath);

    default List<AnalysisResult> analyze(@NotNull List<String> filePaths) {
        List<AnalysisResult> results = new ArrayList<>();
        for (String filePath : filePaths) {
            AnalysisResult result = analyze(filePath);
            results.add(result);
        }
        return results;
    }
}
