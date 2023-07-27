package ca.etsmtl.leakageanalysisplugin.services;

import ca.etsmtl.leakageanalysisplugin.models.leakage.AnalysisResult;
import ca.etsmtl.leakageanalysisplugin.models.leakage.AnalysisStatus;
import ca.etsmtl.leakageanalysisplugin.models.leakage.Leakage;
import ca.etsmtl.leakageanalysisplugin.models.leakage.LeakageType;
import com.intellij.openapi.components.Service;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static ca.etsmtl.leakageanalysisplugin.util.AnalysisUtil.isFileSupported;

@Service(Service.Level.PROJECT)
public final class HttpClientLeakageService implements LeakageService {
    private final String API_URL = "http://localhost:5000";
    private final Long timeout = 120L;

    private OkHttpClient client;

    public HttpClientLeakageService() {
        client = new OkHttpClient().newBuilder()
                //.callTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS).build();
    }

    public AnalysisResult analyze(String filePath) {
        AnalysisResult result;
        try {
            String fileName = uploadFile(filePath);
            result = toAnalysisResult(filePath, analyzeFile(fileName));
            result.setStatus(AnalysisStatus.SUCCESS);
        } catch (RuntimeException e) {
            result = new AnalysisResult(filePath);
            result.setStatus(AnalysisStatus.FAILED);
            result.setErrors(List.of(e));
        }
        return result;
    }

    public String uploadFile(String filePath) {
        if (!isFileSupported(filePath)) {
            throw new IllegalArgumentException("File not supported.");
        }
        String url = String.format("%s/upload", API_URL);

        File file = new File(filePath);
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file)).build();

        Request request = new Request.Builder().url(url).post(requestBody).build();

        try {
            Response response = client.newCall(request).execute();

            if (response.body() == null) {
                throw new IOException("Body is null");
            }

            return response.body().string();
        } catch (IOException e) {
            throw new RuntimeException("There was an error uploading the file.", e);
        }
    }

    public JSONObject analyzeFile(String filePath) {
        String url = String.format("%s/analyze/%s", API_URL, filePath);

        Request request = new Request.Builder().url(url).build();

        try {
            Response response = client.newCall(request).execute();

            if (response.body() == null) {
                throw new IOException("Body is null");
            }

            return new JSONObject(response.body().string());
        } catch (IOException e) {
            throw new RuntimeException("There was an error analysing the file.", e);
        }
    }

    private AnalysisResult toAnalysisResult(String filePath, JSONObject jsonObject) {
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
        return new AnalysisResult(filePath, leakages);
    }
}
