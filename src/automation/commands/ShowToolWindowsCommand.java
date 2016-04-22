package automation.commands;

import automation.GuiUtil;
import automation.RobotControl;
import automation.RobotControlManager;
import com.intellij.ide.ui.UISettings;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.impl.status.StatusBarUI;
import com.intellij.openapi.wm.impl.status.StatusBarUtil;
import com.intellij.util.containers.Queue;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Sergey Karashevich on 09/02/16.
 */
public class ShowToolWindowsCommand extends Command {

    Parameters myParameters;

    public ShowToolWindowsCommand() {
    }


    @Override
    public void process(Queue<Command> script) throws Exception {
        final RobotControl rc = RobotControlManager.getInstance().getRobotControl();
        if (UISettings.getInstance().HIDE_TOOL_STRIPES) {
            final ArrayList<Component> componentsByType = GuiUtil.findComponentsByType(JLabel.class, IdeFocusManager.getGlobalInstance().getFocusOwner());
            for (Component component : componentsByType) {
                if (component instanceof StatusBarWidget) {
                    StatusBarWidget statusBarWidget = (StatusBarWidget) component;
                    if (statusBarWidget.ID().equals("ToolWindows Widget"))
                        rc.navigateAndClick(component, runnext(script));
                    return;
                }
            }
            throw new Exception("Unable to find ToolWindow Widget");
        } else {
            startNext(script);
        }
    }

    private Runnable runnext(Queue<Command> script){
        return new Runnable() {
            @Override
            public void run() {
                try {
                    startNext(script);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
