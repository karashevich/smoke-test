package automation.commands;

import automation.RobotControl;
import automation.RobotControlManager;
import com.intellij.util.containers.Queue;

/**
 * Created by Sergey Karashevich on 06/02/16.
 */
public class SelectInListCommand extends Command {

    Parameters myParameters;

    public SelectInListCommand(Parameters parameters) {
        myParameters = parameters;
    }

    @Override
    public void process(Queue<Command> script) throws Exception {
        RobotControl rc = RobotControlManager.getInstance().getRobotControl();
        rc.selectItemFromProjectWizardList(myParameters.getTextField(), new Runnable() {
            @Override
            public void run() {
                try {
                    startNext(script);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
