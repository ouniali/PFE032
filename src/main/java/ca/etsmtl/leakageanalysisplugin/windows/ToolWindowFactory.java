// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package ca.etsmtl.leakageanalysisplugin.windows;

import ca.etsmtl.leakageanalysisplugin.models.analysis.AnalysisResult;
import ca.etsmtl.leakageanalysisplugin.models.leakage.LeakageInstance;
import ca.etsmtl.leakageanalysisplugin.models.leakage.LeakageType;
import ca.etsmtl.leakageanalysisplugin.notifications.Notifier;
import ca.etsmtl.leakageanalysisplugin.tasks.AnalyzeTask;
import ca.etsmtl.leakageanalysisplugin.tasks.AnalyzeTaskListener;
import ca.etsmtl.leakageanalysisplugin.util.FilesUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ToolWindowFactory implements com.intellij.openapi.wm.ToolWindowFactory, DumbAware {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        MessageBusConnection busConnection = ProjectManager.getInstance().getDefaultProject().getMessageBus().connect();
        LeakageToolWindowContent toolWindowContent = new LeakageToolWindowContent(toolWindow, busConnection);
        Content content = ContentFactory.getInstance().createContent(toolWindowContent.getContentPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

    private static class LeakageToolWindowContent {
        private final JPanel contentPanel = new JPanel();
        private final ArrayList<LeakageTypeGUI> leakageTypeContainers = new ArrayList<>();

        public LeakageToolWindowContent(ToolWindow toolWindow, MessageBusConnection busConnection) {
            busConnection.subscribe(AnalyzeTaskListener.TOPIC,
                    (AnalyzeTaskListener) LeakageToolWindowContent.this::updateResults);
            contentPanel.setLayout(new BorderLayout(0, 20));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
            contentPanel.add(setupLeakagePanel(), BorderLayout.PAGE_START);
            contentPanel.add(setupButtonsPanel(toolWindow), BorderLayout.PAGE_END);
        }

        @NotNull
        private JPanel setupLeakagePanel() {
            JPanel leakagePanel = new JPanel();
            leakagePanel.setLayout(new VerticalFlowLayout());

            for (LeakageType leakageType : LeakageType.values()) {
                LeakageTypeGUI leakageTypeGUI = new LeakageTypeGUI(leakageType);
                leakageTypeContainers.add(leakageTypeGUI);
                leakagePanel.add(leakageTypeGUI.getMainPanel());
            }

            return leakagePanel;
        }

        @NotNull
        private JPanel setupButtonsPanel(ToolWindow toolWindow) {
            JPanel controlsPanel = new JPanel();

            JButton analyzeSelectedFileButton = new JButton("Browse files");
            analyzeSelectedFileButton.addActionListener(e ->
            {
                analyzeSelectedFileButton.setEnabled(false);
                analyzeSelectedFileButton.setOpaque(true);
                analyzeSelectedFile();
                analyzeSelectedFileButton.setEnabled(true);
                analyzeSelectedFileButton.setOpaque(false);
            });
            controlsPanel.add(analyzeSelectedFileButton);

            JButton analyzeCurrentFileButton = new JButton("Analyze current file");
            analyzeCurrentFileButton.addActionListener(e ->
            {
                analyzeCurrentFile();
            });
            controlsPanel.add(analyzeCurrentFileButton);

            JButton resetButton = new JButton("Reset");
            resetButton.addActionListener(e ->
            {
                reset();
            });
            controlsPanel.add(resetButton);

            return controlsPanel;
        }

        private void updateResults(List<AnalysisResult> results) {
            if (results.isEmpty()) {
                return;
            }

            for (LeakageTypeGUI container: leakageTypeContainers) {
                List<LeakageInstance> instances = new ArrayList<>();
                for (AnalysisResult result: results) {
                    instances.addAll(result.getLeakages(container.getType()));
                }
                container.update(instances);
            }
        }

        private void analyzeSelectedFile() {
            Project project = ProjectManager.getInstance().getOpenProjects()[0];
            if (project == null) {
                return;
            }

            VirtualFile chooseFile = project.getBaseDir();
            FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor()
                    .withFileFilter(file -> FilesUtil.isExtensionSupported(file.getExtension()));
            VirtualFile file = FileChooser.chooseFile(descriptor, project, chooseFile);
            if (file == null) {
                return;
            }

            String filePath = file.getPath();
            new AnalyzeTask(project, List.of(filePath)).queue();
        }

        private void analyzeCurrentFile() {
            Project project = ProjectManager.getInstance().getOpenProjects()[0];
            Editor view = FileEditorManager.getInstance(project).getSelectedTextEditor();
            if (view == null) {
                Notifier.notifyError(
                        "No file currently selected",
                        "You first need to open a file in the editor to start the analysis.");
                return;
            }

            VirtualFile currentFile = FileDocumentManager.getInstance().getFile(view.getDocument());

            if (currentFile == null) {
                return;
            }
            String filePath = currentFile.getPath();
            new AnalyzeTask(project, List.of(filePath)).queue();
        }

        private void reset() {
            for (LeakageTypeGUI leakageType : leakageTypeContainers) {
                leakageType.reset();
            }
        }

        public JPanel getContentPanel() {
            return contentPanel;
        }
    }
}
