package automation.actions;

import automation.ScriptProcessor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * Created by Sergey Karashevich on 09/02/16.
 */
public class ProcessScriptAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        try {
            ScriptProcessor.process();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
