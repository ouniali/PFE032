package ca.etsmtl.leakageanalysisplugin.actions;

import ca.etsmtl.leakageanalysisplugin.services.LeakageApiService;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
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
        PsiFile file = CommonDataKeys.PSI_FILE.getData(context);
        // No file selected or file not supported
        if (project == null || !isSupportedFile(file)) {
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
        Project project = e.getProject();
        assert project != null;
        PsiFile file = CommonDataKeys.PSI_FILE.getData(context);
        assert file != null;
        LeakageApiService service = project.getService(LeakageApiService.class);
        assert service != null;
        String filePath = file.getVirtualFile().getPath();
        JSONObject data = service.analyze(filePath);
        // TODO: display data in leakage analysis window
    }
}
