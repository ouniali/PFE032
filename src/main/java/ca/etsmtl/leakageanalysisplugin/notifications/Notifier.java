//        List<String> filePaths = Arrays.stream(directory.getFiles()).map(f -> f.getVirtualFile().getPath()).toList();
//        MessageBus bus = ProjectManager.getInstance().getDefaultProject().getMessageBus();
//        UpdateLeakagesListener listener = bus.syncPublisher(UpdateLeakagesListener.TOPIC);
//        LeakageService service = project.getService(LeakageService.class);
//        JSONObject data = service.analyze(filePaths);
//        System.out.println("data: " + data);
//        listener.updateLeakages(data);
package ca.etsmtl.leakageanalysisplugin.notifications;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;

public class Notifier
{

    public static void notifyInformation(String title, String content)
    {
        notify("TestNotification", title, content, NotificationType.INFORMATION);
    }

    public static void notifyError(String title, String content)
    {
        notify("TestNotification", title, content, NotificationType.ERROR);
    }

    private static void notify(String group, String title, String content, NotificationType type)
    {
        Project project = ProjectManager.getInstance().getOpenProjects()[0];

        NotificationGroupManager.getInstance()
                .getNotificationGroup(group)
                .createNotification(title, content, type)
                .notify(project);
    }

}
