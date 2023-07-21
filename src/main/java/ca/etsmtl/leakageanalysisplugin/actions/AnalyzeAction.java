package ca.etsmtl.leakageanalysisplugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class MenuAction extends AnAction{

    // check https://youtrack.jetbrains.com/issue/IDEA-262880/Create-Plugin-like-the-new-on-EditorPopupMenu
    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        System.out.println("ACTION: " + e);
        System.out.println("ACTION input event: " + e.getInputEvent());
    }
}
