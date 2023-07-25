package ca.etsmtl.leakageanalysisplugin.actions;

import ca.etsmtl.leakageanalysisplugin.services.LeakageService;
import ca.etsmtl.leakageanalysisplugin.windows.UpdateLeakagesListener;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.Arrays;

public class AnalyzeAction extends AnAction {

    private static void analyzeFile(Project project, PsiFile file) {
        String filePath = file.getVirtualFile().getPath();
        MessageBus bus = ProjectManager.getInstance().getDefaultProject().getMessageBus();
        UpdateLeakagesListener listener = bus.syncPublisher(UpdateLeakagesListener.TOPIC);
        LeakageService service = project.getService(LeakageService.class);
        JSONObject data = service.analyze(filePath);
        listener.updateLeakages(data);
    }

    private boolean isSupportedFile(PsiFile file) {
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
        DataContext context = e.getDataContext();
        PsiElement element = context.getData(CommonDataKeys.PSI_ELEMENT);
        if (element instanceof PsiFile file) {
            analyzeFile(project, file);
        } else if (element instanceof PsiDirectory directory) {
            System.out.println("directory: " + directory);
            Arrays.stream(directory.getFiles()).forEach(file -> analyzeFile(project, file));
        }
    }
}
