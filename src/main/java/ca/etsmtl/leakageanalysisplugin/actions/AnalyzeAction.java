package ca.etsmtl.leakageanalysisplugin.actions;

import ca.etsmtl.leakageanalysisplugin.tasks.AnalyzeTask;
import ca.etsmtl.leakageanalysisplugin.util.FilesUtil;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class AnalyzeAction extends AnAction {

    private static void getSupportedFiles(@NotNull PsiDirectory directory, List<PsiFile> files) {
        for (PsiElement element : directory.getChildren()) {
            if (element instanceof PsiDirectory subDirectory) {
                getSupportedFiles(subDirectory, files);
            } else if (element instanceof PsiFile file && isFileSupported(file)) {
                files.add(file);
            }
        }
    }

    public static boolean isFileSupported(@NotNull PsiFile file) {
        return FilesUtil.isFileSupported(file.getVirtualFile().getPath());
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
        if (element instanceof PsiFile file && !isFileSupported(file)) {
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
        List<String> filePaths = new ArrayList<>();
        if (element instanceof PsiFile file) {
            filePaths.add(file.getVirtualFile().getPath());
        } else if (element instanceof PsiDirectory directory) {
            List<PsiFile> files = new ArrayList<>();
            getSupportedFiles(directory, files);
            filePaths = files.stream().map(f -> f.getVirtualFile().getPath()).toList();
        }
        new AnalyzeTask(project, filePaths).queue();
    }
}
