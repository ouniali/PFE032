package ca.etsmtl.leakageanalysisplugin.services;

import ca.etsmtl.leakageanalysisplugin.models.analysis.AnalysisResult;
import ca.etsmtl.leakageanalysisplugin.models.analysis.AnalysisStatus;
import ca.etsmtl.leakageanalysisplugin.models.leakage.Leakage;
import ca.etsmtl.leakageanalysisplugin.models.leakage.LeakageType;
import okhttp3.*;
import org.apache.http.HttpException;
import org.apache.http.entity.ContentType;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static ca.etsmtl.leakageanalysisplugin.util.FilesUtil.isFileSupported;

public final class HttpClientLeakageService implements LeakageService {
    public static final MediaType ANALYZE_MEDIATYPE = MediaType.parse("application/octet-stream");
    private static final String BASE_URL = "http://localhost:5000";
    private static final Long TIMEOUT = 120L;
    private final OkHttpClient client;

    public HttpClientLeakageService() {
        client = new OkHttpClient().newBuilder()
                //.callTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS).build();
    }

    public AnalysisResult analyzeFile(String filePath) {
        AnalysisResult result;
        try {
            result = executeAnalyzeRequest(filePath);
        } catch (RuntimeException e) {
            result = new AnalysisResult(filePath);
            result.setStatus(AnalysisStatus.FAILED);
            result.setErrors(List.of(e));
        }
        return result;
    }

    private AnalysisResult executeAnalyzeRequest(String filePath) {
        try {
            Request request = buildAnalyzeRequest(filePath);
            AnalysisResult result = toAnalysisResult(filePath, getAnalyzeRequestData(request));
            result.setStatus(AnalysisStatus.SUCCESS);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("There was an error analysing the file.", e);
        }
    }

    @NotNull
    private static Request buildAnalyzeRequest(String filePath) {
        if (!isFileSupported(filePath)) {
            throw new IllegalArgumentException("File not supported.");
        }
        String url = String.format("%s/analyze", BASE_URL);
        File file = new File(filePath);
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(file, ANALYZE_MEDIATYPE)).build();
        return new Request.Builder().url(url).post(requestBody).build();
    }

    @NotNull
    private JSONObject getAnalyzeRequestData(Request request) throws Exception {
        try (Response response = client.newCall(request).execute()) {
            String contentType = response.header("Content-Type");
            if (contentType == null || !contentType.equals(ContentType.APPLICATION_JSON.getMimeType())) {
                throw new IllegalStateException("Response was not of type JSON.");
            }
            if (response.body() == null) {
                throw new IOException("Body is null.");
            }
            JSONObject data = new JSONObject(response.body().string());
            if (!response.isSuccessful()) {
                String errMessage = data.has("message") ? data.getString("message") : "unknown";
                throw new HttpException(errMessage);
            }
            return data;
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
