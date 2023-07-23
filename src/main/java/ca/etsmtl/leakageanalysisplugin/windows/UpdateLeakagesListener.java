package ca.etsmtl.leakageanalysisplugin.windows;

import com.intellij.util.messages.Topic;
import org.json.JSONObject;

public interface UpdateLeakagesListener {

    Topic<UpdateLeakagesListener> TOPIC =
            Topic.create("Update leakages in leakage window tool", UpdateLeakagesListener.class);

    // TODO: parameter should be something cleaner like a class (that should contain data, file, timestamp, etc)
    void updateLeakages(JSONObject data);
}
