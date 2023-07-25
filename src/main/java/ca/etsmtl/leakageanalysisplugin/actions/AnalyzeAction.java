package ca.etsmtl.leakageanalysisplugin.actions;

import ca.etsmtl.leakageanalysisplugin.models.leakage.LeakageResult;
import ca.etsmtl.leakageanalysisplugin.services.LeakageService;
import ca.etsmtl.leakageanalysisplugin.windows.UpdateLeakagesListener;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnalyzeAction extends AnAction {

    private static void analyzeFile(Project project, PsiFile file) {
        String filePath = file.getVirtualFile().getPath();
        MessageBus bus = ProjectManager.getInstance().getDefaultProject().getMessageBus();
        UpdateLeakagesListener listener = bus.syncPublisher(UpdateLeakagesListener.TOPIC);
        LeakageService service = project.getService(LeakageService.class);
        LeakageResult result = service.analyze(filePath);
        listener.updateLeakages(result);
    }

    private static void analyzeDirectory(Project project, PsiDirectory directory) {
        List<String> filePaths = Arrays.stream(directory.getFiles()).map(f -> f.getVirtualFile().getPath()).toList();
        MessageBus bus = ProjectManager.getInstance().getDefaultProject().getMessageBus();
        UpdateLeakagesListener listener = bus.syncPublisher(UpdateLeakagesListener.TOPIC);
        LeakageService service = project.getService(LeakageService.class);
        List<PsiFile> files = new ArrayList<>();
        getSupportedFiles(directory, files);
        System.out.println(files);
        // TODO: create/use service method to analyze multiple files at once
    }

    private static void getSupportedFiles(PsiDirectory directory, List<PsiFile> files) {
        for (PsiElement element : directory.getChildren()) {
            if (element instanceof PsiDirectory subDirectory) {
                getSupportedFiles(subDirectory, files);
            } else if (element instanceof PsiFile file && isSupportedFile(file)) {
                files.add(file);
            }
        }
    }

    private static boolean isSupportedFile(PsiFile file) {
        return file != null && file.getFileType().getDefaultExtension().equals("ipynb");
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        DataContext context = e.getDataContext();
        Project project = e.getProject();
        if (project == null) {
            presentation.setVisible(false);
            presentation.setEnabled(false);
            return;
        }
        PsiElement element = context.getData(CommonDataKeys.PSI_ELEMENT);
        // Is a file
        if (element instanceof PsiFile file && !isSupportedFile(file)) {
            presentation.setVisible(false);
            presentation.setEnabled(false);
        }
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        new Task.Backgroundable(project, "Analyzing file(s)") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                DataContext context = e.getDataContext();
                PsiElement element = context.getData(CommonDataKeys.PSI_ELEMENT);
                if (element instanceof PsiFile file) {
                    analyzeFile(project, file);
                } else if (element instanceof PsiDirectory directory) {
                    analyzeDirectory(project, directory);
                }
            }
        };
    }
}
