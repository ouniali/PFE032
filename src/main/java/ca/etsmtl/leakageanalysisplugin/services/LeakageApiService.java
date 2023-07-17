package ca.etsmtl.leakageanalysisplugin.services;

import com.intellij.openapi.components.Service;
import okhttp3.*;
import org.apache.http.util.EntityUtils;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONObject;

// instantiated as service (check https://plugins.jetbrains.com/docs/intellij/plugin-services.html#example)
@Service
public final class LeakageApiService
{
    private final String API_URL = "http://localhost:5000";
    private final Long timeout = 60L;

    private OkHttpClient client;

    public LeakageApiService()
    {
        client = new OkHttpClient().newBuilder()
                .callTimeout(timeout, TimeUnit.SECONDS)
                .build();
    }

    public void analyse(String filePath)
    {
        //TODO combine both calls
    }

    public void uploadFile(String filePath)
    {
        String url = String.format("%s/upload", API_URL);

        File file = new File(filePath);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                        "file",
                        file.getName(),
                        RequestBody.create(MediaType.parse("application/octet-stream"),file))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try {
            Response response = client
                    .newCall(request)
                    .execute();

            // Returns filepath saved in server
            if (response.body() != null)
            {
                String serverFilePath = response.body().string();
                System.out.println(serverFilePath);
            }
        }
        catch (IOException e)
        {
            //TODO Handle errors
            throw new RuntimeException(e);
        }
    }

    public JSONObject analyzeFile(String filePath)
    {
        String url = String.format("%s/analyze/%s", API_URL, filePath);

        Request request = new Request.Builder()
                .url(url)
                .build();

        try
        {
            Response response = client
                    .newCall(request)
                    .execute();

            // returns html report
            if (response.body() == null)
            {
                return null;
            }

            return  new JSONObject(response.body().string());
        }
        catch (IOException e)
        {
            //TODO Handle errors
            throw new RuntimeException(e);
        }
    }

    public void notification()
    {
        // TODO
    }
}
