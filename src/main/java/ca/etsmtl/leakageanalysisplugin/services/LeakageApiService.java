package ca.etsmtl.leakageanalysisplugin.services;

import ca.etsmtl.leakageanalysisplugin.models.leakage.Leakage;
import ca.etsmtl.leakageanalysisplugin.models.leakage.LeakageResult;
import ca.etsmtl.leakageanalysisplugin.models.leakage.LeakageType;
import ca.etsmtl.leakageanalysisplugin.notifications.Notifier;
import com.intellij.openapi.components.Service;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

// instantiated as service (check https://plugins.jetbrains.com/docs/intellij/plugin-services.html#example)
@Service(Service.Level.PROJECT)
public final class LeakageApiService implements LeakageService {
    private final String API_URL = "http://localhost:5000";
    private final Long timeout = 120L;

    private OkHttpClient client;

    public LeakageApiService() {
        client = new OkHttpClient().newBuilder()
                //.callTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .build();
    }

    public LeakageResult analyze(String filePath) {
        String fileName = uploadFile(filePath);
        return toAnalysisResult(filePath, analyzeFile(fileName));
    }

    public String uploadFile(String filePath) {
        String url = String.format("%s/upload", API_URL);

        File file = new File(filePath);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                        "file",
                        file.getName(),
                        RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try {
            Response response = client
                    .newCall(request)
                    .execute();

            if (response.body() == null) {
                throw new IOException("Body is null");
            }

            String serverFilePath = response.body().string();
            String title = "Success uploading the file %s.";
            Notifier.notifyInformation(String.format(title, serverFilePath), "");

            return serverFilePath;
        } catch (IOException e) {
            throw new RuntimeException("There was an error uploading the file.", e);
        }
    }

    public JSONObject analyzeFile(String filePath) {
        String url = String.format("%s/analyze/%s", API_URL, filePath);

        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client
                    .newCall(request)
                    .execute();

            if (response.body() == null) {
                throw new IOException("Body is null");
            }

            String title = "Success analysing the file %s.";
            Notifier.notifyInformation(String.format(title, filePath), "");

            return new JSONObject(response.body().string());
        } catch (IOException e) {
            throw new RuntimeException("There was an error analysing the file.", e);
        }
    }

    private LeakageResult toAnalysisResult(String filePath, JSONObject jsonObject) {
        List<Leakage> leakages = new ArrayList<>();
        for (String key : jsonObject.keySet()) {
            LeakageType leakageType = LeakageType.getLeakageType(key);
            if (leakageType != null) {
                JSONObject jsonLeakage = jsonObject.getJSONObject(key);
                JSONArray jsonLocations = jsonLeakage.getJSONArray("location");
                int detected = jsonLeakage.getInt("# detected");
                List<Integer> locations = new ArrayList<>();
                for (int i = 0; i < jsonLocations.length(); i++) {
                    locations.add(jsonLocations.getInt(i));
                }
                leakages.add(new Leakage(leakageType, locations, detected));
            }
        }
        return new LeakageResult(filePath, leakages);
    }

}
