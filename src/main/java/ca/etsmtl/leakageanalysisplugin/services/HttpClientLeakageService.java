package ca.etsmtl.leakageanalysisplugin.services;

import ca.etsmtl.leakageanalysisplugin.models.analysis.AnalysisResult;
import ca.etsmtl.leakageanalysisplugin.models.analysis.AnalysisStatus;
import ca.etsmtl.leakageanalysisplugin.models.leakage.Leakage;
import ca.etsmtl.leakageanalysisplugin.models.leakage.LeakageInstance;
import ca.etsmtl.leakageanalysisplugin.models.leakage.LeakageType;
import com.intellij.openapi.components.Service;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static ca.etsmtl.leakageanalysisplugin.util.FilesUtil.isFileSupported;

@Service(Service.Level.PROJECT)
public final class HttpClientLeakageService implements LeakageService {
    private static final String BASE_URL = "http://localhost:5000";
    private static final Long timeout = 120L;

    private final OkHttpClient client;

    public HttpClientLeakageService() {
        client = new OkHttpClient().newBuilder()
                //.callTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS).build();
    }

    public AnalysisResult analyze(String filePath) {
        if (!isFileSupported(filePath)) {
            throw new IllegalArgumentException("File not supported.");
        }
        String url = String.format("%s/upload", BASE_URL);

        File file = new File(filePath);
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file)).build();

        Request request = new Request.Builder().url(url).post(requestBody).build();

        try {
            Response response = client.newCall(request).execute();

            if (response.body() == null) {
                throw new IOException("Body is null");
            }

            return toAnalysisResult(filePath, analyzeFile(response.body().string()));
        } catch (IOException e) {
            Exception exception = new RuntimeException("There was an error uploading the file.", e);
            return new AnalysisResult(filePath, List.of(exception));
        }
    }

    public JSONObject analyzeFile(String filePath) {
        String url = String.format("%s/analyze/%s", BASE_URL, filePath);

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
        HashMap<LeakageType, ArrayList<LeakageInstance>> leakages = new HashMap();
        for (LeakageType leakageType: LeakageType.values()) {
            leakages.put(leakageType, new ArrayList<LeakageInstance>());
        }

        for (String key : jsonObject.keySet()) {
            LeakageType leakageType = LeakageType.getLeakageType(key);
            if (leakageType == null) {
                continue;
            }

            ArrayList<LeakageInstance> instances = leakages.get(LeakageType.getLeakageType(key));
            JSONObject jsonLeakage = jsonObject.getJSONObject(key);
            JSONArray jsonLocations = jsonLeakage.getJSONArray("location");

            for (int i = 0; i < jsonLocations.length(); i++) {
                instances.add(new LeakageInstance(i));
            }
        }
        return new AnalysisResult(filePath, leakages);
    }
}
