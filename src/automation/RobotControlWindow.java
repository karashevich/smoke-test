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

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.roots.ui.configuration.actions.IconWithTextAction;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by Sergey Karashevich on 28/01/16.
 */
public class RobotControlWindow extends JDialog {
  private final JPanel myWrapperPanel;
  private HierarchyTree myHierarchyTree;
  public JLabel statusLabel;

  private RobotControl myRobotControl;


  public RobotControlWindow(@NotNull final Component component) throws HeadlessException {
    super(findWindow(component));

    myRobotControl = RobotControlManager.getInstance().getRobotControl();

    Window window = findWindow(component);
    setModal(window instanceof JDialog && ((JDialog)window).isModal());
    getRootPane().setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    setLayout(new BorderLayout());
    setTitle(component.getClass().getName());

    DefaultActionGroup actions = new DefaultActionGroup();
    actions.addAction(new IconWithTextAction("Push the ActionLink") {
      @Override
      public void actionPerformed(AnActionEvent e) {
        //  Component actionLink = GuiUtil.findComponentByText("Create New Project", component);
        //  if(actionLink != null) {
        //    try {
        //      RobotControlManager.getInstance().getRobotControl().navigateAndClickScript(actionLink);
        //    }
        //    catch (InterruptedException e1) {
        //      e1.printStackTrace();
        //    }
        //    catch (Exception e1) {
        //      e1.printStackTrace();
        //    }
        //  }
        //}
        try {
          ScriptProcessor.process();
        }
        catch (Exception e1) {
          e1.printStackTrace();
        }
      }

      @Override
      public void update(AnActionEvent e) {
        //do nothing
      }
    });

    //actions.addSeparator();

     //actions.add(new IconWithTextAction("Refresh") {
     //
     // @Override
     // public void actionPerformed(AnActionEvent e) {
     //   getCurrentTable().refresh();
     // }
     //
     // @Override
     // public void update(AnActionEvent e) {
     //   e.getPresentation().setEnabled(myComponent != null && myComponent.isVisible());
     // }
    //});

    ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.CONTEXT_TOOLBAR, actions, true);
    add(toolbar.getComponent(), BorderLayout.NORTH);
    statusLabel = new JLabel("Ok");
    add(statusLabel);

    myWrapperPanel = new JPanel(new BorderLayout());

    //myHierarchyTree = new HierarchyTree(component) {
    //  @Override
    //  public void onComponentChanged(Component c) {
    //    boolean wasHighlighted = myHighlightComponent != null;
    //    setHighlightingEnabled(false);
    //    switchInfo(c);
    //    setHighlightingEnabled(wasHighlighted);
    //  }
    //};

    //myWrapperPanel.add(myInspectorTable, BorderLayout.CENTER);

    //JSplitPane splitPane = new JSplitPane();
    //splitPane.setDividerLocation(0.5);
    //splitPane.setRightComponent(myWrapperPanel);

    JScrollPane pane = new JBScrollPane(myHierarchyTree);
    //splitPane.setLeftComponent(pane);
    //add(splitPane, BorderLayout.CENTER);

    //myHierarchyTree.expandPath();

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        close();
      }
    });

    getRootPane().getActionMap().put("CLOSE", new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        close();
      }
    });
    //setHighlightingEnabled(true);
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "CLOSE");
  }

  private static Window findWindow(Component component) {
    DialogWrapper dialogWrapper = DialogWrapper.findInstance(component);
    if (dialogWrapper != null) {
      return dialogWrapper.getPeer().getWindow();
    }
    return null;
  }


  public void close() {
    setVisible(false);
    dispose();
  }

  public RobotControl getRobotControl(){
    return myRobotControl;
  }

}
