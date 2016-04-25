package automation.commands.select;

import automation.RobotControl;
import automation.RobotControlManager;
import automation.commands.Command;
import automation.commands.Parameters;
import com.intellij.util.containers.Queue;

/**
 * Created by Sergey Karashevich on 06/02/16.
 */
public class SelectInJdkListCommand extends Command {

    Parameters myParameters;

    public SelectInJdkListCommand(Parameters parameters) {
        myParameters = parameters;
    }

    @Override
    public void process(Queue<Command> script) throws Exception {
        RobotControl rc = RobotControlManager.getInstance().getRobotControl();
        rc.selectItemFromJdkList("Set up Project SDK", myParameters.getTextField(), runNext(script), myParameters.getMyTimeout());

    }
}
