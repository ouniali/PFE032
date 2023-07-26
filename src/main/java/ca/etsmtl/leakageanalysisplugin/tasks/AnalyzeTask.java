package ca.etsmtl.leakageanalysisplugin.tasks;

import ca.etsmtl.leakageanalysisplugin.models.leakage.AnalysisResult;
import ca.etsmtl.leakageanalysisplugin.models.leakage.AnalysisStatus;
import ca.etsmtl.leakageanalysisplugin.notifications.Notifier;
import ca.etsmtl.leakageanalysisplugin.services.LeakageService;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AnalyzeTask extends Task.Backgroundable {
    public static final String TITLE = "Analyzing file(s)...";
    private List<String> filePaths;
    private LeakageService service;

    public AnalyzeTask(@Nullable Project project, List<String> filePaths) {
        super(project, TITLE);
        if (project != null) {
            this.service = project.getService(LeakageService.class);
            this.filePaths = filePaths;
        }
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        if (filePaths == null) {
            return;
        }
        MessageBus bus = ProjectManager.getInstance().getDefaultProject().getMessageBus();
        AnalyzeTaskListener listener = bus.syncPublisher(AnalyzeTaskListener.TOPIC);
        List<AnalysisResult> results = new ArrayList<>();
        // TODO: use progress indicator
        if (filePaths.size() == 1) {
            String filePath = filePaths.get(0);
            AnalysisResult result = service.analyze(filePath);
            results.add(result);
            listener.updateResults(result);
            notifyResult(result);
        } else if (filePaths.size() > 1) {
            results = service.analyze(filePaths);
            listener.updateResults(results);
            notifyResults(results);
        }
    }

    private void notifyResult(AnalysisResult result) {
        if (result == null) {
            return;
        }
        String title;
        String content;
        if (result.isSuccessful()) {
            title = "Success analysing the file %s.";
            content = "";
        } else {
            Exception error = result.getErrors().get(0);
            title = error.getMessage();
            content = error.getCause().getMessage();
        }
        File file = new File(result.getFilePath());
        Notifier.notifyInformation(String.format(title, file.getName()), content);
    }

    private static void notifyResults(List<AnalysisResult> results) {
        int successCount = (int) results.stream().filter(r -> r.getStatus().equals(AnalysisStatus.SUCCESS)).count();
        int failedCount = results.size() - successCount;
        String title = String.format("Success analyzing %d file(s).", successCount);
        if (failedCount > 0) {
            title = title.concat(String.format(" Failed analyzing %d file(s).", failedCount));
        }
        Notifier.notifyInformation(title, "");
    }
}
