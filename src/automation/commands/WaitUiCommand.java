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
package automation.commands;

import automation.RobotControlManager;
import com.intellij.util.containers.Queue;
import com.intellij.util.ui.EdtInvocationManager;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sergey Karashevich on 29/01/16.
 */
public class WaitUiCommand extends Command {

  Parameters myParameters;

  public WaitUiCommand(Parameters parameters) {
    myParameters = parameters;
  }

  @Override
  public void process(final Queue<Command> script) throws Exception {
    myParameters.log();
    RobotControlManager.getInstance().getRobotControl().waitUi(myParameters.getTextField(), new Runnable() {
      @Override
      public void run() {
        EdtInvocationManager.getInstance().invokeLater(new Runnable() {
          @Override
          public void run() {
            try {
              startNext(script);
            }
            catch (Exception e) {
              e.printStackTrace();
              System.exit(1);
            }
          }
        });
      }
    }, myParameters.getMyTimeout());
  }
}
