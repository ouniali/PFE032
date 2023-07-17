// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package ca.etsmtl.leakageanalysisplugin.windows;

import ca.etsmtl.leakageanalysisplugin.services.LeakageApiService;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

// TODO Create Task: Cleanup the ToolWindow code (Rename correctly, etc..)
public class ToolWindowFactory implements com.intellij.openapi.wm.ToolWindowFactory, DumbAware
{

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow)
    {
        LeakageToolWindowContent toolWindowContent = new LeakageToolWindowContent(toolWindow);
        Content content = ContentFactory.getInstance().createContent(toolWindowContent.getContentPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

    private static class LeakageToolWindowContent
    {
        private final JPanel contentPanel = new JPanel();
        private final ArrayList<LeakageTypeGUI> leakageTypes = new ArrayList<>();

        private LeakageApiService leakageApiService = new LeakageApiService();

        public LeakageToolWindowContent(ToolWindow toolWindow)
        {
            leakageTypes.add(new LeakageTypeGUI("Overlap Leakage", "overlap leakage"));
            leakageTypes.add(new LeakageTypeGUI("Multi-Test Leakage", "no independence test data"));
            leakageTypes.add(new LeakageTypeGUI("Preprocessing Leakage", "pre-processing leakage"));

            contentPanel.setLayout(new BorderLayout(0, 20));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
            contentPanel.add(createLeakagePanel(), BorderLayout.PAGE_START);
            contentPanel.add(createControlsPanel(toolWindow), BorderLayout.PAGE_END);
        }

        @NotNull
        private JPanel createLeakagePanel()
        {
            JPanel leakagePanel = new JPanel();
            leakagePanel.setLayout(new VerticalFlowLayout());

            for (LeakageTypeGUI leakageType: leakageTypes)
            {
                leakagePanel.add(leakageType.getMainPanel());
            }

            return leakagePanel;
        }

        private void analyzeRepos()
        {
            // TODO

            for (LeakageTypeGUI leakageType: leakageTypes)
            {
                // TODO Get a list of all selected files
            }
        }

        private void analyzeFile()
        {
            JSONObject data = leakageApiService.analyzeFile("week02_extra_data_preprocessing_example_full.ipynb");

            for (LeakageTypeGUI leakageType: leakageTypes)
            {
                leakageType.parseData(data.getJSONObject(leakageType.jsonKey));
            }
        }

        private void reset()
        {
            for (LeakageTypeGUI leakageType: leakageTypes)
            {
                leakageType.reset();
            }
        }

        @NotNull
        private JPanel createControlsPanel(ToolWindow toolWindow) {
            JPanel controlsPanel = new JPanel();

            JButton analyzeReposButton = new JButton("Analyze repository");
            analyzeReposButton.addActionListener(e ->
            {
                analyzeReposButton.setEnabled(false);
                analyzeRepos();
                analyzeReposButton.setEnabled(true);
            });
            controlsPanel.add(analyzeReposButton);

            JButton analyzeFileButton = new JButton("Analyze selected file");
            analyzeFileButton.addActionListener(e ->
            {
                controlsPanel.setEnabled(false);
                analyzeFile();
                controlsPanel.setEnabled(true);
            });
            controlsPanel.add(analyzeFileButton);

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
