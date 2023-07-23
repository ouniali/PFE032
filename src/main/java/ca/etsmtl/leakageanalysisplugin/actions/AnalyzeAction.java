package ca.etsmtl.leakageanalysisplugin.actions;

import ca.etsmtl.leakageanalysisplugin.services.LeakageApiService;
import ca.etsmtl.leakageanalysisplugin.services.LeakageService;
import ca.etsmtl.leakageanalysisplugin.windows.UpdateLeakagesListener;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class AnalyzeAction extends AnAction {

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
        PsiFile file = CommonDataKeys.PSI_FILE.getData(context);
        // No file selected or file not supported
        if (!isSupportedFile(file)) {
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
        // Using the event, create and show a dialog
        DataContext context = e.getDataContext();
        System.out.println("context");
        Project project = e.getProject();
        assert project != null;
        PsiFile file = CommonDataKeys.PSI_FILE.getData(context);
        assert file != null;
        String filePath = file.getVirtualFile().getPath();
        MessageBus bus = ProjectManager.getInstance().getDefaultProject().getMessageBus();
        UpdateLeakagesListener listener = bus.syncPublisher(UpdateLeakagesListener.TOPIC);
        LeakageService service = project.getService(LeakageService.class);
        JSONObject data = service.analyze(filePath);
        listener.updateLeakages(data);
        System.out.println("data: " + data);
    }
}
