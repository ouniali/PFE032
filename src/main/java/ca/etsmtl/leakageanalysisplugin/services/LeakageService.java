package ca.etsmtl.leakageanalysisplugin.services;

import ca.etsmtl.leakageanalysisplugin.models.leakage.LeakageResult;

public interface LeakageService {

    LeakageResult analyze(String filePath);
}
