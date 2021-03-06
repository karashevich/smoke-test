package automation.commands.select;

import automation.GuiUtil;
import automation.HierarchyTree;
import automation.RobotControl;
import automation.RobotControlManager;
import automation.commands.Command;
import automation.commands.Parameters;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.util.containers.Queue;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Sergey Karashevich on 06/02/16.
 */
public class SelectInFrameworkListCommand extends Command {

    Parameters myParameters;

    public SelectInFrameworkListCommand(Parameters parameters) {
        myParameters = parameters;
    }

    @Override
    public void process(Queue<Command> script) throws Exception {
        RobotControl rc = RobotControlManager.getInstance().getRobotControl();
        final Component focusOwner = IdeFocusManager.getGlobalInstance().getFocusOwner();
//        rc.navigateAndClick(rc.findFrameworkCheckbox(myParameters.getTextField()), runnext(script));
        final HierarchyTree.FrameworkSupportElement componentByText = (HierarchyTree.FrameworkSupportElement) GuiUtil.findComponentByTextAndType(myParameters.getTextField(), HierarchyTree.FrameworkSupportElement.class, focusOwner);
        final JCheckBox checkbox = componentByText.getCheckbox();
        rc.navigateAndClick(checkbox, runNext(script));
    }

}
