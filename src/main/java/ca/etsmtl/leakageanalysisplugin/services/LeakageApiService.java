package ca.etsmtl.leakageanalysisplugin.services;

import com.intellij.openapi.components.Service;
import okhttp3.*;

import java.io.File;
import java.io.IOException;

// instantiated as service (check https://plugins.jetbrains.com/docs/intellij/plugin-services.html#example)
@Service
public final class LeakageApiService {

    public static final String API_URL = "http://localhost:5000/upload";

    public void uploadFile(String filePath) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        String url = String.format("%s/upload", API_URL);
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody subBody = RequestBody.create(MediaType.parse("application/octet-stream"),
                new File(filePath));
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", filePath, subBody)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .build();
        Response response = client.newCall(request).execute();

        // returns filepath saved in server
        if (response.body() != null) {
            String serverFilePath = response.body().string();
            System.out.println(serverFilePath);
        }
    }

    public void analyzeFile(String filePath) throws IOException {
        String url = String.format("%s/analyze/%s", API_URL, filePath);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url(url)
                .method("GET", body)
                .build();
        Response response = client.newCall(request).execute();

        // returns html report
        if (response.body() != null) {
            String html = response.body().string();
            System.out.println(html);
        }
    }
}
