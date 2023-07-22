package ca.etsmtl.leakageanalysisplugin.services;

import org.json.JSONObject;

public interface LeakageApiService {
    String uploadFile(String filePath);
    JSONObject analyzeFile(String filePath);
    JSONObject analyze(String filePath);
}
