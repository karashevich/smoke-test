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

import automation.GuiUtil;
import automation.HierarchyTree;
import automation.RobotControl;
import automation.RobotControlManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.openapi.wm.IdeFrame;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.util.containers.Queue;
import com.intellij.util.ui.EdtInvocationManager;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Sergey Karashevich on 29/01/16.
 */
public class ParkMouseCommand extends Command {

    private final static String POSITION = "position";
    private final static String CENTER = "center";


    private Element myRawElement;
    private String myPosition;

    public ParkMouseCommand(Element rawElement) {
        myRawElement = rawElement;
        try {
            extractInfoFromRawElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void extractInfoFromRawElement() throws Exception {
        final Attribute attributePosition = myRawElement.getAttribute(POSITION);
        if (attributePosition != null) myPosition = attributePosition.getValue();
    }

    @Override
    public void process(final Queue<Command> script) throws Exception {

        //Project View item name
        final IdeFrame ideFrame = IdeFocusManager.findInstance().getLastFocusedFrame();
        final RobotControl rc = RobotControlManager.getInstance().getRobotControl();

        switch (myPosition) {
            case CENTER:
                rc.navigateAndClick(ideFrame.getComponent(), runNext(script));
                break;
        }

    }

    @NotNull
    private Runnable runNext(final Queue<Command> script) {
        return new Runnable() {
            @Override
            public void run() {
                EdtInvocationManager.getInstance().invokeLater(new Runnable() {
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
        };
    }

}
