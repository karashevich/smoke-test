package automation.commands;

import automation.actions.IdleAction;
import com.intellij.util.containers.Queue;

/**
 * Created by karashevich on 21/04/16.
 */
public class IdleCommand extends Command{

    @Override
    public void process(Queue<Command> script) throws Exception {
        (new IdleAction()).actionPerformed(null);
    }
}
