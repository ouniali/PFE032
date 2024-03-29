package ca.etsmtl.leakageanalysisplugin.tasks;

import ca.etsmtl.leakageanalysisplugin.models.analysis.AnalysisResult;
import com.intellij.util.messages.Topic;

import java.util.List;

public interface AnalyzeTaskListener {

    Topic<AnalyzeTaskListener> TOPIC = Topic.create("UpdateLeakages", AnalyzeTaskListener.class);

    void updateResults(List<AnalysisResult> results);
}
