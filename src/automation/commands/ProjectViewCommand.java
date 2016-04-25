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
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.util.containers.Queue;
import com.intellij.util.ui.EdtInvocationManager;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Created by Sergey Karashevich on 29/01/16.
 */
public class ProjectViewCommand extends Command {

    public final static String ELEMENT = "element";
    public final static String ACTION = "action";
    public final static String ACTION_EXPAND_ALL = "expand-all";
    public final static String ACTION_LEFT_CLICK = "left-click";
    public final static String ACTION_DOUBLE_CLICK = "double-click";
    public final static String ACTION_RIGHT_CLICK = "right-click";


    Element myRawElement;
    String elementText;
    String action;

    public ProjectViewCommand(Element rawElement) {
        myRawElement = rawElement;
        try {
            extractInfoFromRawElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void extractInfoFromRawElement() throws Exception {
        final Attribute attributeElement = myRawElement.getAttribute(ELEMENT);
        if (attributeElement == null)
            throw new Exception("SMOKE_TEST: Unable to find attribute 'elementText' for <project-view> command. Please fix your script");

        final Attribute attributeAction = myRawElement.getAttribute(ACTION);
        if (attributeAction == null)
            throw new Exception("SMOKE_TEST: Unable to find attribute 'action' for <project-view> command. Please fix your script");

        elementText = attributeElement.getValue();
        action = attributeAction.getValue();
    }

    @Override
    public void process(final Queue<Command> script) throws Exception {

        //Project View item name
        final Component focusOwner = IdeFocusManager.getGlobalInstance().getFocusOwner();
        final Component componentByText = GuiUtil.findComponentByTextAndType(elementText, HierarchyTree.ProjectViewElement.class, focusOwner);
        final RobotControl rc = RobotControlManager.getInstance().getRobotControl();

        switch (action) {
            case ACTION_LEFT_CLICK:
                rc.navigateAndClick(componentByText, runNext(script));
                break;
            case ACTION_DOUBLE_CLICK:
                rc.navigateAndDoubleClick(componentByText, runNext(script));
                break;
            case ACTION_RIGHT_CLICK:
                rc.navigateAndRightClick(componentByText, runNext(script));
                break;
            case ACTION_EXPAND_ALL:


        }

    }



}
