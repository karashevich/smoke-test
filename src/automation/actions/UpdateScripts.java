package automation.actions;

import automation.RobotControlManager;
import automation.Script;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.util.ClassLoaderUtil;
import org.jdom.JDOMException;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by jetbrains on 29/03/16.
 */
public class UpdateScripts extends AnAction{

    @Override
    public void actionPerformed(AnActionEvent e) {
        URL resource = this.getClass().getClassLoader().getResource("/automation/scripts");
        File file = new File(resource.getFile());
        for (String name : file.list()) {
            if (name.substring(name.length() - 4, name.length()).equals(".xml")) {
                try {
                    String nameWithoutExtension = name.substring(0, name.length() - 4);
                    String genId = "automate." + nameWithoutExtension;
                    AnAction script = new Script(nameWithoutExtension, RobotControlManager.getInstance().getMapping());
                    if (ActionManager.getInstance().getAction(genId) != null)
                        ActionManager.getInstance().unregisterAction(genId);
                    ActionManager.getInstance().registerAction(genId, script);
                } catch (JDOMException | IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }
}
