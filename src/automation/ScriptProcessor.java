/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package automation;

import automation.commands.*;
import com.intellij.util.containers.Queue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sergey Karashevich on 29/01/16.
 */
public class ScriptProcessor {

  public static void process() throws Exception {
    final String SMOKE_TEST_CONFIG_PROJ= "plugin.smoke-test.project";
    String projName = System.getProperty(SMOKE_TEST_CONFIG_PROJ);
    System.out.println(SMOKE_TEST_CONFIG_PROJ + "=" + projName);
    projName = "smoke-test-" + (new SimpleDateFormat("dd-MMM-yy-HH-mm")).format(new Date());

    Map<String, String> mapping = new HashMap<>();
    mapping.put("$PROJECT_NAME", projName);

//    Queue<Command> script = new Queue<>(8);
//    script.addLast(new StartCommand());
//    script.addLast(new NavigateAndClickCommand(new Parameters("Create New Project")));
//    script.addLast(new WaitDialogCommand(new Parameters("New Project")));
//    script.addLast(new SelectInListCommand(new Parameters("Java")));
//    script.addLast(new NavigateAndClickCommand(new Parameters("New...")));
//    script.addLast(new SelectInJdkListCommand(new Parameters("JDK")));
//    script.addLast(new WaitDialogCommand(new Parameters("Select Home Directory for JDK")));
//    script.addLast(new TypeInTextFieldCommand(new Parameters("", null, "/Library/Java/JavaVirtualMachines/jdk1.8.0_71.jdk/Contents/Home")));
//    //the tree is sticking here
//    script.addLast(new WaitCommand(new Parameters(1000)));
//    script.addLast(new NavigateAndClickCommand(new Parameters("OK")));
//    script.addLast(new WaitCommand(new Parameters(500)));
//    script.addLast(new NavigateAndClickCommand(new Parameters("Next")));
//    script.addLast(new WaitUiCommand(new Parameters("Create project from template")));
//    script.addLast(new NavigateAndClickCommand(new Parameters("Next")));
//    script.addLast(new WaitUiCommand(new Parameters("Project name:")));
//    script.addLast(new TypeInTextFieldCommand(new Parameters("Project name:", null, projName)));
//    script.addLast(new WaitCommand(new Parameters(1000)));
//    script.addLast(new NavigateAndClickCommand(new Parameters("Finish")));
//    script.addLast(new WaitProjectOpeningCommand(new Parameters()));
//    script.addLast(new RunnableCommand(() -> {
//      System.out.println("Idea is ready");
//    }));
//    script.pullFirst().process(script);

//    Script script = new Script("start", mapping);
//    script.process();

//    Component target = IdeFocusManager.getGlobalInstance().getFocusOwner();
//    JRootPane rootPane = target == null ? null : SwingUtilities.getRootPane(target);
//    JComponent glassPane = rootPane == null ? null : (JComponent)rootPane.getGlassPane();
//    rootPane.addMouseListener(new MouseAdapter() {
//      @Override
//      public void mouseClicked(MouseEvent e) {
//        System.out.println("mouseClicked();");
//        RobotControlManager.getInstance().getRobotControl().mouseClick();
//        e.consume();
//      }
//    });
  }



}
