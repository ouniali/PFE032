// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package etsmtl.ca.leakanalysis.toolWindow;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import etsmtl.ca.leakanalysis.leakageAnalysis.DummyLeakageType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

// TODO Create Task: Cleanup the ToolWindow code (Rename correctly, etc..)
public class LeakageToolWindowFactory implements ToolWindowFactory, DumbAware
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

        public LeakageToolWindowContent(ToolWindow toolWindow)
        {
            leakageTypes.add(new LeakageTypeGUI("Overlap Leakage", new DummyLeakageType()));
            leakageTypes.add(new LeakageTypeGUI("Multi-Test Leakage", new DummyLeakageType()));
            leakageTypes.add(new LeakageTypeGUI("Preprocessing Leakage", new DummyLeakageType()));

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
                leakagePanel.add(leakageType.getPanel());
            }

            return leakagePanel;
        }

        private void analyzeRepos()
        {
            for (LeakageTypeGUI leakageType: leakageTypes)
            {
                // TODO Get a list of all selected files
                leakageType.analyze("lol");
            }
        }

        private void analyzeFile()
        {
            for (LeakageTypeGUI leakageType: leakageTypes)
            {
                // TODO Get the current file
                leakageType.analyze("lol");
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
            analyzeReposButton.addActionListener(e -> analyzeRepos());
            controlsPanel.add(analyzeReposButton);

            JButton analyzeFileButton = new JButton("Analyze selected file");
            analyzeFileButton.addActionListener(e -> analyzeFile());
            controlsPanel.add(analyzeFileButton);

            JButton resetButton = new JButton("Reset");
            resetButton.addActionListener(e -> reset());
            controlsPanel.add(resetButton);

            return controlsPanel;
        }

        public JPanel getContentPanel() {
            return contentPanel;
        }
    }
}
