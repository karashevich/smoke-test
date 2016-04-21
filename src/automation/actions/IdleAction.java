package automation.actions;

import automation.RobotControlManager;
import automation.Script;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jdom.JDOMException;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by jetbrains on 29/03/16.
 */
public class IdleAction extends AnAction{

    @Override
    public void actionPerformed(AnActionEvent e) {
        System.out.println("Idle action");
    }
}
