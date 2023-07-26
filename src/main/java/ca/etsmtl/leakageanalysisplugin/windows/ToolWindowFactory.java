// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package ca.etsmtl.leakageanalysisplugin.windows;

import ca.etsmtl.leakageanalysisplugin.models.leakage.AnalysisResult;
import ca.etsmtl.leakageanalysisplugin.models.leakage.Leakage;
import ca.etsmtl.leakageanalysisplugin.models.leakage.LeakageType;
import ca.etsmtl.leakageanalysisplugin.notifications.Notifier;
import ca.etsmtl.leakageanalysisplugin.services.LeakageService;
import ca.etsmtl.leakageanalysisplugin.tasks.AnalyzeTask;
import ca.etsmtl.leakageanalysisplugin.tasks.AnalyzeTaskListener;
import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.projectView.impl.AbstractProjectViewPane;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// TODO Create Task: Cleanup the ToolWindow code (Rename correctly, etc..)
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
        private final ArrayList<LeakageTypeGUI> leakageTypeCounters = new ArrayList<>();

        public LeakageToolWindowContent(ToolWindow toolWindow, MessageBusConnection busConnection) {
            busConnection.subscribe(AnalyzeTaskListener.TOPIC, new AnalyzeTaskListener() {
//                public void updateResults(AnalysisResult result) {
//                    LeakageToolWindowContent.this.updateResults(result);
//                }

                @Override
                public void updateResults(List<AnalysisResult> results) {
                    LeakageToolWindowContent.this.updateResults(results);
                }
            });
            leakageTypeCounters.add(new LeakageTypeGUI("Overlap Leakage", LeakageType.OVERLAP));
            leakageTypeCounters.add(new LeakageTypeGUI("Multi-Test Leakage", LeakageType.MULTITEST));
            leakageTypeCounters.add(new LeakageTypeGUI("Preprocessing Leakage", LeakageType.PREPROCESSING));

            contentPanel.setLayout(new BorderLayout(0, 20));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
            contentPanel.add(createLeakagePanel(), BorderLayout.PAGE_START);
            contentPanel.add(createControlsPanel(toolWindow), BorderLayout.PAGE_END);
        }

        private void updateResults(List<AnalysisResult> results) {
            // TODO: FILL WINDOW TOOL WITH RESULTS (WILL NEED APPROPRIATE LAYOUT)
            // first result
            AnalysisResult result = results.get(0);
            if (result.isSuccessful()) {
                // errors need to displayed
                for (LeakageTypeGUI counter : leakageTypeCounters) {
                    LeakageType leakageType = counter.getType();
                    Optional<Leakage> optLeakage = result.getLeakages().stream()
                            .filter(l -> l.getType().equals(leakageType)).findFirst();
                    optLeakage.ifPresent(leakage -> counter.setCount(String.valueOf(leakage.getDetected())));
                }
            }
        }

        @NotNull
        private JPanel createLeakagePanel() {
            JPanel leakagePanel = new JPanel();
            leakagePanel.setLayout(new VerticalFlowLayout());

            for (LeakageTypeGUI leakageType : leakageTypeCounters) {
                leakagePanel.add(leakageType.getMainPanel());
            }

            return leakagePanel;
        }

        public VirtualFile selectFile() {
            Project project = ProjectManager.getInstance().getOpenProjects()[0];
            assert project != null;
            VirtualFile chooseFile = project.getBaseDir();
            FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor();
            return FileChooser.chooseFile(descriptor, project, chooseFile);
        }

        private void analyzeSelectedFile() {
            Project project = ProjectManager.getInstance().getOpenProjects()[0];
            VirtualFile file = selectFile();

            if (file == null) {
                return;
            }

            String filePath = file.getPath();
            new AnalyzeTask(project, List.of(filePath)).queue();
        }

        private void analyzeCurrentFile() {
            // TODO FINISH AND CLEANUP
            // NOT WORKING CURRENTLY
            Project project = ProjectManager.getInstance().getOpenProjects()[0];
            ProjectRootManager.getInstance(project).getFileIndex().iterateContent(file -> {
                System.out.println("File: " + file.getPath());
                return true;
            });
            AbstractProjectViewPane view = ProjectView.getInstance(project).getCurrentProjectViewPane();

            if (view == null) {
                return;
            }

            StringBuilder sb = new StringBuilder();
            Object[] nodes = view.getSelectedPath().getPath();

            for (int i = 0; i < nodes.length; i++) {
                sb.append(File.separatorChar).append(nodes[i].toString());
            }

            try {
                // TODO: USE TASK INSTEAD (see analyzeSelectedFile(...))
                LeakageService leakageApiService = project.getService(LeakageService.class);
                AnalysisResult result = leakageApiService.analyze(sb.toString());
                updateResults(List.of(result));
            } catch (RuntimeException e) {
                Notifier.notifyError(e.getMessage(), e.getCause().getMessage());
            }
        }

        private void reset() {
            for (LeakageTypeGUI leakageType : leakageTypeCounters) {
                leakageType.reset();
            }
        }

        @NotNull
        private JPanel createControlsPanel(ToolWindow toolWindow) {
            JPanel controlsPanel = new JPanel();

            JButton analyzeSelectedFileButton = new JButton("Select File");
            analyzeSelectedFileButton.addActionListener(e ->
            {
                analyzeSelectedFileButton.setEnabled(false);
                analyzeSelectedFile();
                analyzeSelectedFileButton.setEnabled(true);
            });
            controlsPanel.add(analyzeSelectedFileButton);

            JButton analyzeCurrentFileButton = new JButton("Current File");
            analyzeCurrentFileButton.addActionListener(e ->
            {
                controlsPanel.setEnabled(false);
                analyzeCurrentFile();
                controlsPanel.setEnabled(true);
            });
            controlsPanel.add(analyzeCurrentFileButton);

            JButton resetButton = new JButton("Reset");
            resetButton.addActionListener(e ->
            {
                controlsPanel.setEnabled(false);
                reset();
                controlsPanel.setEnabled(true);
            });
            controlsPanel.add(resetButton);

            return controlsPanel;
        }

        public JPanel getContentPanel() {
            return contentPanel;
        }
    }
}
