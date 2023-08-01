package ca.etsmtl.leakageanalysisplugin.services;

import ca.etsmtl.leakageanalysisplugin.models.analysis.AnalysisResult;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface LeakageService {

    AnalysisResult analyzeFile(String filePath);

    default List<AnalysisResult> analyzeFiles(@NotNull List<String> filePaths) {
        List<AnalysisResult> results = new ArrayList<>();
        for (String filePath : filePaths) {
            AnalysisResult result = analyzeFile(filePath);
            results.add(result);
        }
        return results;
    }
}
