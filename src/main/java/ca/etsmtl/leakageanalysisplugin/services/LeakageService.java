package ca.etsmtl.leakageanalysisplugin.services;

import org.json.JSONObject;

public interface LeakageService {
    JSONObject analyze(String filePath);
}
