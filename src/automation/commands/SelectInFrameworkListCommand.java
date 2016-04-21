package automation.commands;

import automation.RobotControl;
import automation.RobotControlManager;
import com.intellij.util.containers.Queue;
import org.jetbrains.annotations.NotNull;

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
        rc.navigateAndClick(rc.findFrameworkCheckbox(myParameters.getTextField()), runnext(script));
    }

    @NotNull
    private Runnable runnext(final Queue<Command> script) {
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
