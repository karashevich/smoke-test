package automation;

import com.intellij.internal.inspector.UiInspectorAction;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.util.ui.EdtInvocationManager;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Sergey Karashevich on 04/02/16.
 */
public class RobotControlComponent implements ApplicationComponent {
    @Override
    public void initComponent() {
//        (new UiInspectorAction()).setSelected(null, true);
        RobotControlManager.getInstance().getRobotControl().startRobotActivity();
        try {
            RobotControlManager.getInstance().getRobotControl().runInRobotThreadWhenWelcomeScreenIsShown(() ->
                    EdtInvocationManager.getInstance().invokeLater(() ->
            {
                try {
                    ScriptProcessor.process();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disposeComponent() {
        //TODO: add dipose procedure here
        //do nothing
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "smoke-test-plugin.RobotControlComponent";
    }
}
