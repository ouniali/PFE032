package ca.etsmtl.leakageanalysisplugin.tasks;

import ca.etsmtl.leakageanalysisplugin.models.analysis.AnalysisResult;
import ca.etsmtl.leakageanalysisplugin.models.analysis.AnalysisStatus;
import ca.etsmtl.leakageanalysisplugin.notifications.Notifier;
import ca.etsmtl.leakageanalysisplugin.services.LeakageService;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ca.etsmtl.leakageanalysisplugin.util.AnalysisUtil.getFileName;

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

    private static void notifyResults(List<AnalysisResult> results) {
        int successCount = (int) results.stream().filter(r -> r.getStatus().equals(AnalysisStatus.SUCCESS)).count();
        int failedCount = results.size() - successCount;
        if (successCount > 0) {
            String successTitle = String.format("Success analyzing %d file(s).", successCount);
            Notifier.notifyInformation(successTitle, "");
        }
        if (failedCount > 0) {
            notifyErrors(results, failedCount);
        }
    }

    private static void notifyErrors(List<AnalysisResult> results, int failedCount) {
        Optional<AnalysisResult> firstFailedResult = results.stream()
                .filter(r -> r.getStatus().equals(AnalysisStatus.FAILED)).findFirst();
        firstFailedResult.ifPresent(result -> {
            // displays first error in notification
            String filePath = result.getFilePath();
            String errors = result.getErrors().stream()
                    .map(AnalyzeTask::getErrorText).collect(Collectors.joining("\n"));
            String firstFailedError = """
                    <p>Errors in %s</p>
                    <ul>%s</ul>
                    """.formatted(getFileName(filePath), errors);
            String failedTitle = String.format("Failed analyzing %d file(s).", failedCount);
            Notifier.notifyError(failedTitle, firstFailedError);
        });
    }

    private static String getErrorText(Exception e) {
        if (e.getCause() == null) {
            return "<li>%s</li>".formatted(e.getMessage());
        }
        return "<li>%s -> %s</li>".formatted(e.getMessage(), e.getCause().getMessage());
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        if (filePaths == null) {
            return;
        }
        // TODO: use progress indicator
        List<AnalysisResult> results = service.analyze(filePaths);
        MessageBus bus = ProjectManager.getInstance().getDefaultProject().getMessageBus();
        AnalyzeTaskListener listener = bus.syncPublisher(AnalyzeTaskListener.TOPIC);
        listener.updateResults(results);
        notifyResults(results);
    }
}
