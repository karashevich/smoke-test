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

import com.intellij.openapi.util.ActionCallback;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.util.containers.Queue;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sergey Karashevich on 29/01/16.
 */
public class ChangeFocusCommand extends Command {

  Parameters myParameters;

  public ChangeFocusCommand(Parameters parameters){
    myParameters = parameters;
  }

  @Override
  public void process(final Queue<Command> script) throws Exception {
    myParameters.log();
    ActionCallback callback = IdeFocusManager.getGlobalInstance().requestFocus(myParameters.getComponent(), true);
    callback.doWhenDone(new Runnable() {
      @Override
      public void run() {
        try {
          startNext(script);
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
}
