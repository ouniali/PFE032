package ca.etsmtl.leakageanalysisplugin.services;

import org.json.JSONObject;

public interface LeakageService {

    // TODO: should return something cleaner like a class (that should contain data, file, timestamp, etc)
    JSONObject analyze(String filePath);
}
