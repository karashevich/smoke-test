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

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.keymap.KeymapManager;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.util.containers.Queue;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Created by Sergey Karashevich on 29/01/16.
 */
public class ActionCommand extends Command{

  Parameters myParameters;

  public ActionCommand(Parameters parameters) {
    myParameters = parameters;
  }

  @Override
  public void process(Queue<Command> script) throws Exception {
    String actionId = myParameters.getTextField();
    AnAction action = ActionManager.getInstance().getAction(actionId);
    final InputEvent inputEvent = getInputEvent(actionId);


    ActionManager.getInstance().tryToExecute(action, inputEvent, IdeFocusManager.getGlobalInstance().getFocusOwner(), null, true);
    startNext(script);
  }

  private static InputEvent getInputEvent(String actionName) {
    final Shortcut[] shortcuts = KeymapManager.getInstance().getActiveKeymap().getShortcuts(actionName);
    KeyStroke keyStroke = null;
    for (Shortcut each : shortcuts) {
      if (each instanceof KeyboardShortcut) {
        keyStroke = ((KeyboardShortcut) each).getFirstKeyStroke();
        break;
      }
    }

    if (keyStroke != null) {
      return new KeyEvent(JOptionPane.getRootFrame(),
              KeyEvent.KEY_PRESSED,
              System.currentTimeMillis(),
              keyStroke.getModifiers(),
              keyStroke.getKeyCode(),
              keyStroke.getKeyChar(),
              KeyEvent.KEY_LOCATION_STANDARD);
    } else {
      return new MouseEvent(JOptionPane.getRootFrame(), MouseEvent.MOUSE_PRESSED, 0, 0, 0, 0, 1, false, MouseEvent.BUTTON1);
    }
  }
}
