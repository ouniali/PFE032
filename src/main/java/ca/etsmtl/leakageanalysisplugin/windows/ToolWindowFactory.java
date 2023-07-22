// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package ca.etsmtl.leakageanalysisplugin.windows;

import ca.etsmtl.leakageanalysisplugin.notifications.Notifier;
import ca.etsmtl.leakageanalysisplugin.services.LeakageApiServiceImpl;
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
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

// TODO Create Task: Cleanup the ToolWindow code (Rename correctly, etc..)
public class ToolWindowFactory implements com.intellij.openapi.wm.ToolWindowFactory, DumbAware {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        LeakageToolWindowContent toolWindowContent = new LeakageToolWindowContent(toolWindow);
        Content content = ContentFactory.getInstance().createContent(toolWindowContent.getContentPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

    private static class LeakageToolWindowContent {
        private final JPanel contentPanel = new JPanel();
        private final ArrayList<LeakageTypeGUI> leakageTypes = new ArrayList<>();

        private LeakageApiServiceImpl leakageAnalysisServiceImpl = new LeakageApiServiceImpl();

        public LeakageToolWindowContent(ToolWindow toolWindow) {
            leakageTypes.add(new LeakageTypeGUI("Overlap Leakage", "overlap leakage"));
            leakageTypes.add(new LeakageTypeGUI("Multi-Test Leakage", "no independence test data"));
            leakageTypes.add(new LeakageTypeGUI("Preprocessing Leakage", "pre-processing leakage"));

            contentPanel.setLayout(new BorderLayout(0, 20));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
            contentPanel.add(createLeakagePanel(), BorderLayout.PAGE_START);
            contentPanel.add(createControlsPanel(toolWindow), BorderLayout.PAGE_END);
        }

        @NotNull
        private JPanel createLeakagePanel() {
            JPanel leakagePanel = new JPanel();
            leakagePanel.setLayout(new VerticalFlowLayout());

            for (LeakageTypeGUI leakageType : leakageTypes) {
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
            VirtualFile file = selectFile();

            if (file == null) {
                return;
            }

            String filePath = file.getPath();

            try {
                JSONObject data = leakageAnalysisServiceImpl.analyze(filePath);

                for (LeakageTypeGUI leakageType : leakageTypes) {
                    leakageType.parseData(data.getJSONObject(leakageType.jsonKey));
                }
            } catch (RuntimeException e) {
                Notifier.notifyError(e.getMessage(), e.getCause().getMessage());
            }
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
                JSONObject data = leakageAnalysisServiceImpl.analyze(sb.toString());

                for (LeakageTypeGUI leakageType : leakageTypes) {
                    leakageType.parseData(data.getJSONObject(leakageType.jsonKey));
                }
            } catch (RuntimeException e) {
                Notifier.notifyError(e.getMessage(), e.getCause().getMessage());
            }
        }

        private void reset() {
            for (LeakageTypeGUI leakageType : leakageTypes) {
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
