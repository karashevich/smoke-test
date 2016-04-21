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

import com.intellij.util.containers.Queue;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sergey Karashevich on 29/01/16.
 */
public abstract class Command {


  public void startNext(Queue<Command> script) throws Exception {
    if (!script.isEmpty()) {
      Command command = script.pullFirst();
      System.out.println("    " +(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS")).format(new Date()) + " START COMMAND: " + command.getClass() + "");
      command.process(script);
    }
  }

  public abstract void process(Queue<Command> script) throws Exception;
}
