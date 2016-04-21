package automation.commands;

import automation.RobotControlManager;
import com.intellij.util.containers.Queue;
import com.intellij.util.ui.EdtInvocationManager;

/**
 * Created by Sergey Karashevich on 09/02/16.
 */
public class RunnableCommand extends Command {

    Parameters myParameters;
    Runnable myRunnable;

    public RunnableCommand(Parameters parameters) {
        myParameters = parameters;
    }

    public RunnableCommand(Runnable runnable) {
        myRunnable = runnable;
    }

    @Override
    public void process(Queue<Command> script) throws Exception {
//        myParameters.log();
        myRunnable.run();
        startNext(script);
    }
}
