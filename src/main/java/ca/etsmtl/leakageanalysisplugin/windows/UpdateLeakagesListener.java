package ca.etsmtl.leakageanalysisplugin.windows;

import ca.etsmtl.leakageanalysisplugin.models.leakage.LeakageResult;
import com.intellij.util.messages.Topic;

public interface UpdateLeakagesListener {

    Topic<UpdateLeakagesListener> TOPIC =
            Topic.create("Update leakages in leakage window tool", UpdateLeakagesListener.class);

    void updateLeakages(LeakageResult result);
}
