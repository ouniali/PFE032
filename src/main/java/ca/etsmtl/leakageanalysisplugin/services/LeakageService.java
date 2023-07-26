package ca.etsmtl.leakageanalysisplugin.services;

import ca.etsmtl.leakageanalysisplugin.models.leakage.AnalysisResult;

import java.util.List;

public interface LeakageService {

    AnalysisResult analyze(String filePath);

    List<AnalysisResult> analyze(List<String> filePaths);
}
