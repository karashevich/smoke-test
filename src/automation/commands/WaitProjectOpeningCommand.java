package automation.commands;

import automation.RobotControlManager;
import com.intellij.util.containers.Queue;
import com.intellij.util.ui.EdtInvocationManager;

/**
 * Created by Sergey Karashevich on 09/02/16.
 */
public class WaitProjectOpeningCommand extends Command {

    Parameters myParameters;

    public WaitProjectOpeningCommand(Parameters parameters) {
        myParameters = parameters;
    }

    @Override
    public void process(Queue<Command> script) throws Exception {
        myParameters.log();
        RobotControlManager.getInstance().getRobotControl().waitProjectOpening(() -> EdtInvocationManager.getInstance().invokeLater(() -> {
            try {
                startNext(script);
            }
            catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }), myParameters.getMyTimeout());
    }
}
