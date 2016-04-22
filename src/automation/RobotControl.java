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

import com.intellij.ide.util.newProjectWizard.TemplatesGroup;
import com.intellij.openapi.actionSystem.ex.ActionUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerAdapter;
import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.openapi.wm.impl.welcomeScreen.FlatWelcomeFrame;
import com.intellij.ui.components.JBList;
import com.intellij.ui.popup.PopupFactoryImpl;
import com.intellij.util.ui.EdtInvocationManager;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by Sergey Karashevich on 28/01/16.
 */
public class RobotControl {
    private RobotThread robotThread;
    private Robot myRobot;


    private int threadDelay = 500;

    public RobotControl() {
        try {
            myRobot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        this.robotThread = new RobotThread("RobotThread");
    }

    public void takeScreenshot() throws AWTException, IOException {
        BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        ImageIO.write(image, "png", new File(RobotControlManager.getInstance().getImagePath()));
    }

    public void startRobotActivity() {
        robotThread.start();
    }

    public void runInRoboThreadWhenIdeaIsOk(Runnable runnable) throws InterruptedException {
        robotThread.runInRobotThreadWhenIdeaIsOk(runnable);
    }

    public void runInRobotThreadWhenDialogIsShown(Runnable runnable, String dialogTitle, int timeout) throws InterruptedException {
        robotThread.runInRobotThreadWhenDialogIsShown(runnable, dialogTitle, timeout);
    }

    public void runInRobotThreadWhenWelcomeScreenIsShown(Runnable runnable) throws InterruptedException {
        robotThread.runInRobotThreadWhenWelcomeScreenIsShown(runnable);
    }

  /*
  ROBOT ACTIONS
   */

    //Move mouse on vertical line firstly and on horizontal line secondly.
    private void mouseMoveMenu(Point locationOnScreen, boolean horizontalFirst) throws InterruptedException {
        Point start = MouseInfo.getPointerInfo().getLocation();
        if (horizontalFirst) {
            mouseMove(new Point(locationOnScreen.x, start.y));
            mouseMove(new Point(locationOnScreen.x, locationOnScreen.y));
        } else {
            mouseMove(new Point(start.x, locationOnScreen.y));
            mouseMove(new Point(locationOnScreen.x, locationOnScreen.y));
        }
    }

    //Move mouse on short line.
    public void mouseMove(Point target) throws InterruptedException {
        int n = 100;
        int t = 800;
        Point start = MouseInfo.getPointerInfo().getLocation();
        double dx = (target.x - start.x) / ((double) n);
        double dy = (target.y - start.y) / ((double) n);
        double dt = t / ((double) n);
        for (int step = 1; step <= n; step++) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            myRobot.mouseMove(
                    (int) (start.x + dx * ((Math.log(1.0 * step / n) - Math.log(1.0 / n)) * n / (0 - Math.log(1.0 / n)))),
                    (int) (start.y + dy * ((Math.log(1.0 * step / n) - Math.log(1.0 / n)) * n / (0 - Math.log(1.0 / n)))));
        }
        myRobot.mouseMove(target.x, target.y);

    }

    private void mouseMove(int x1, int y1) throws InterruptedException {
        mouseMove(new Point(x1, y1));
    }

    public void navigateAndClickMenu(Point locationOnScreen, Runnable runnable, boolean horizontalFirst) throws InterruptedException {
        mouseMoveMenu(locationOnScreen, horizontalFirst);
        mouseClick();
        EdtInvocationManager.getInstance().invokeLater(runnable);
    }

    public void navigateAndClickMenu(Component cmp, Runnable runnable, boolean horizontalFirst) throws InterruptedException {
        Point cmpLocationOnScreen = cmp.getLocationOnScreen();
        Rectangle bounds = cmp.getBounds();
        IdeFocusManager.findInstanceByComponent(cmp).requestFocus(cmp, true);
        Point clickPoint = new Point(cmpLocationOnScreen.x + bounds.width / 2, cmpLocationOnScreen.y + bounds.height / 2);

        navigateAndClickMenu(clickPoint, runnable, horizontalFirst);
    }

    public void navigateMenu(Point locationOnScreen, Runnable runnable, boolean horizontalFirst) throws InterruptedException {
        mouseMoveMenu(locationOnScreen, horizontalFirst);
        EdtInvocationManager.getInstance().invokeLater(runnable);
    }

    public void navigateAndClick(Point locationOnScreen, Runnable runnable) throws Exception {
        mouseMove(locationOnScreen);
        mouseClick();
        EdtInvocationManager.getInstance().invokeLater(runnable);
    }

    public void navigateAndDoubleClick(Point locationOnScreen, Runnable runnable) throws Exception {
        mouseMove(locationOnScreen);
        mouseDoubleClick();
        EdtInvocationManager.getInstance().invokeLater(runnable);
    }

    public void navigateAndRightClick(Point locationOnScreen, Runnable runnable) throws Exception {
        mouseMove(locationOnScreen);
        mouseRightClick();
        EdtInvocationManager.getInstance().invokeLater(runnable);
    }

    public void navigate(Point locationOnScreen, Runnable runnable) throws InterruptedException {
        mouseMove(locationOnScreen);
        EdtInvocationManager.getInstance().invokeLater(runnable);
    }

    public void navigateAndClick(Component cmp, Runnable runnable) throws Exception {
        Point cmpLocationOnScreen = cmp.getLocationOnScreen();
        Rectangle bounds = cmp.getBounds();
        IdeFocusManager.findInstanceByComponent(cmp).requestFocus(cmp, true);
        Point clickPoint = new Point(cmpLocationOnScreen.x + bounds.width / 2, cmpLocationOnScreen.y + bounds.height / 2);
        navigateAndClick(clickPoint, runnable);
    }

    public void navigateAndDoubleClick(Component cmp, Runnable runnable) throws Exception {
        Point cmpLocationOnScreen = cmp.getLocationOnScreen();
        Rectangle bounds = cmp.getBounds();
        IdeFocusManager.findInstanceByComponent(cmp).requestFocus(cmp, true);
        Point clickPoint = new Point(cmpLocationOnScreen.x + bounds.width / 2, cmpLocationOnScreen.y + bounds.height / 2);
        navigateAndDoubleClick(clickPoint, runnable);
    }

    public void navigateAndRightClick(Component cmp, Runnable runnable) throws Exception {
        Point cmpLocationOnScreen = cmp.getLocationOnScreen();
        Rectangle bounds = cmp.getBounds();
        IdeFocusManager.findInstanceByComponent(cmp).requestFocus(cmp, true);
        Point clickPoint = new Point(cmpLocationOnScreen.x + bounds.width / 2, cmpLocationOnScreen.y + bounds.height / 2);
        navigateAndRightClick(clickPoint, runnable);
    }

    public void waitUi(String componentText, Runnable runnable, int timeout) throws Exception {
        robotThread.waitUi(runnable, componentText, timeout);
    }

    @Nullable
    public JTextField findJTextField(String textComponent) throws Exception {

        if (textComponent.equals("")) {
            ArrayList<Component> jTextFields = GuiUtil.findComponentsByType(JTextField.class, IdeFocusManager.getGlobalInstance().getFocusOwner());
            return (JTextField) jTextFields.get(0);
        }

        Component cmpWithText = GuiUtil.findComponentByText(textComponent, IdeFocusManager.getGlobalInstance().getFocusOwner());
        Container parent = cmpWithText.getParent();
        Component[] components = parent.getComponents();
        for (Component component : components) {
            if (component instanceof JTextField &&
                    component.getLocationOnScreen().getY() <= cmpWithText.getLocationOnScreen().getY() &&
                    ((component.getLocationOnScreen().getY() + component.getBounds().height) >= (cmpWithText.getLocationOnScreen().getY() + cmpWithText.getBounds().height))) {
                return (JTextField) component;
            }
        }
        return null;
    }

    public void waitSome(int millis, Runnable runnable) throws InterruptedException {
        robotThread.waitSome(millis, runnable);
    }

    public void selectItemFromJdkList(String jdkListName, String itemName, Runnable runnable, int timeout) throws Exception {
        waitUi(jdkListName, new Runnable() {
            @Override
            public void run() {
                try {
                    selectItemFromPopupList(itemName, runnable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, timeout);
    }

    @Nullable
    public void selectItemFromProjectWizardList(String itemName, Runnable runnable) throws Exception {
        ArrayList<Component> componentArrayList = GuiUtil.findComponentsByType(JBList.class, IdeFocusManager.getGlobalInstance().getFocusOwner());
        TemplatesGroup myGroup = null;
        int i0 = -1;
        int j0 = -1;
        for (int j = 0; j < componentArrayList.size(); j++) {
            ListModel model = ((JBList) componentArrayList.get(j)).getModel();
            for (int i = 0; i < model.getSize(); i++) {
                if (!(model.getElementAt(0) instanceof TemplatesGroup)) break;
                TemplatesGroup tGroup = (TemplatesGroup) model.getElementAt(i);
                if (tGroup.getName().equals(itemName)) {
                    myGroup = tGroup;
                    i0 = i;
                    j0 = j;
                }
            }
        }
        if (j0 >= 0 && i0 >= 0 && myGroup != null) {
            JBList jbList = (JBList) componentArrayList.get(j0);
            Point relativePoint = jbList.indexToLocation(i0);
            Rectangle cellBounds = jbList.getCellBounds(i0, 0);
            Point pointOnScreen = new Point(jbList.getLocationOnScreen().x + relativePoint.x + cellBounds.width / 2,
                    jbList.getLocationOnScreen().y + relativePoint.y + cellBounds.height / 2);
            clickItemInListVerifyAndRun(pointOnScreen, jbList, i0, runnable);

        } else {
            throw new Exception("Unable to find item \"" + itemName + "\" or Project Wizard's JBList");
        }
    }

    @Nullable
    public void selectItemFromPopupList(String itemName, Runnable runnable) throws Exception {
        ArrayList<Component> componentArrayList = GuiUtil.findComponentsByType(JBList.class, IdeFocusManager.getGlobalInstance().getFocusOwner());
        PopupFactoryImpl.ActionItem myActionItem = null;
        int i0 = -1;
        int j0 = -1;
        for (int j = 0; j < componentArrayList.size(); j++) {
            ListModel model = ((JBList) componentArrayList.get(j)).getModel();
            for (int i = 0; i < model.getSize(); i++) {
                if (!(model.getElementAt(0) instanceof PopupFactoryImpl.ActionItem)) break;
                PopupFactoryImpl.ActionItem actionItem = (PopupFactoryImpl.ActionItem) model.getElementAt(i);
                if (actionItem.getText().equals(itemName)) {
                    myActionItem = actionItem;
                    i0 = i;
                    j0 = j;
                }
            }
        }
        if (j0 >= 0 && i0 >= 0 && myActionItem != null) {
            JBList jbList = (JBList) componentArrayList.get(j0);
            Point relativePoint = jbList.indexToLocation(i0);
            Rectangle cellBounds = jbList.getCellBounds(i0, 0);
            Point pointOnScreen = new Point(jbList.getLocationOnScreen().x + relativePoint.x + cellBounds.width / 2,
                    jbList.getLocationOnScreen().y + relativePoint.y + cellBounds.height / 2);
            clickItemInListVerifyAndRun(pointOnScreen, jbList, i0, runnable);

        } else {
            throw new Exception("Unable to find item \"" + itemName + "\" or popup list's JBList");
        }
    }

    private void clickItemInListVerifyAndRun(Point pointOnScreen, JBList jbList, int index, Runnable runnable) throws InterruptedException {
        mouseMove(pointOnScreen.x, pointOnScreen.y);
        mouseClick();
        EdtInvocationManager.getInstance().invokeLater(new Runnable() {
            @Override
            public void run() {
                if (jbList.getSelectedIndex() == index) {
                    runnable.run();
                } else {
                    try {
                        clickItemInListVerifyAndRun(pointOnScreen, jbList, index, runnable);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void checkUiAndRunOnRobotThread(String searchableText, Runnable runnable) {

    }

  /*
  SCRIPT DEMO ACTIONS
   */

    public void navigateAndClickScript(Component cmp) throws Exception {
        Point cmpLocationOnScreen = cmp.getLocationOnScreen();
        Rectangle bounds = cmp.getBounds();
        IdeFocusManager.findInstanceByComponent(cmp).requestFocus(cmp, true);
        myRobot.mouseMove(cmpLocationOnScreen.x + bounds.width / 2, cmpLocationOnScreen.y + bounds.height / 2);
//    MyLog.log("mouse moved");
        mouseClick();
//    MyLog.log("mouse clicked");

    }


    public void mouseClick() {
        myRobot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        myRobot.delay(100);
        myRobot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    public void mouseDoubleClick() {
        mouseClick();
        myRobot.delay(100);
        mouseClick();
    }

    public void mouseRightClick() {
        myRobot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
        myRobot.delay(100);
        myRobot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
    }

    public void waitProjectOpening(Runnable runnable, int myTimeout) throws InterruptedException {
        ProjectManagerAdapter projectManagerListener = new ProjectManagerAdapter() {
            public void projectOpened(Project project) {
                ProjectManagerEx.getInstanceEx().removeProjectManagerListener(this);
                StartupManager.getInstance(project).registerPostStartupActivity(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            waitIdeaOpening(runnable, project);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        ProjectManagerEx.getInstanceEx().addProjectManagerListener(projectManagerListener);

    }

    public void waitIdeaOpening(Runnable runnable, Project project) throws InterruptedException {
        robotThread.queueOfTasks.put((Computable) () -> {
            EdtInvocationManager.getInstance().invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (ActionUtil.isDumbMode(project)) {
                        try {
                            robotThread.waitSome(2000, new Runnable() {
                                @Override
                                public void run() {
                                    EdtInvocationManager.getInstance().invokeLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                waitIdeaOpening(runnable, project);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        runnable.run();
                    }
                }
            });
            return null;
        });
    }

    private class RobotThread extends Thread {

        private SynchronousQueue<Computable> queueOfTasks;
        private final Object monitor = new Object();


        public RobotThread(String name) {
            super(name);
            queueOfTasks = new SynchronousQueue<Computable>();
        }

        public void runInEdtWhenIdeaIsOk(final Runnable run) throws InterruptedException {
            queueOfTasks.put(new Computable<Runnable>() {
                @Override
                public Runnable compute() {
                    EdtInvocationManager.getInstance().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            run.run();
                        }
                    });
                    return run;
                }
            });
        }


        /**
         * Based on focus tracking. When focus is settled on a new component we try to check is it instance of JDialog. If true we compare the title of the dialog with <b>dialogTitle</b>.
         *
         * @param run         - the Runnable should begin when dialog is shown. The Runnable works on EDT by default.
         * @param dialogTitle - the aim title. All appeared dialogs compare their title with dialogTitle. If they are equaled the runnable start.
         * @param timeout
         * @throws InterruptedException
         */
        public void runInRobotThreadWhenDialogIsShown(final Runnable run, final String dialogTitle, int timeout) throws InterruptedException {
            AWTEventListener listener = new AWTEventListener() {
                @Override
                public void eventDispatched(AWTEvent event) {
                    Component owner = IdeFocusManager.findInstance().getFocusOwner();
                    JRootPane pane = UIUtil.getRootPane(owner);
                    if (pane != null) {
                        Container parent = pane.getParent();
                        if (parent != null) {
                            if (parent instanceof JDialog) {
                                JDialog jd = (JDialog) parent;
                                if (jd.getTitle().equals(dialogTitle)) {
                                    run.run();
                                    Toolkit.getDefaultToolkit().removeAWTEventListener(this);
                                }
                            }
                        }
                    }
                }
            };
            Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.FOCUS_EVENT_MASK);
        }

        /**
         * Based on focus tracking. When Welcom screen dialog appears method starts runnable
         *
         * @param runnable - the Runnable should begin when dialog is shown. The Runnable works on EDT by default.
         * @throws InterruptedException
         */
        public void runInRobotThreadWhenWelcomeScreenIsShown(final Runnable runnable) throws InterruptedException {
            AWTEventListener listener = new AWTEventListener() {
                @Override
                public void eventDispatched(AWTEvent event) {
                    Component owner = IdeFocusManager.findInstance().getFocusOwner();
                    JRootPane pane = UIUtil.getRootPane(owner);
                    if (pane != null) {
                        Container parent = pane.getParent();
                        if (parent != null) {
                            if (parent instanceof FlatWelcomeFrame) {
                                RobotControlManager.getInstance().setMotherComponent(owner);
                                runnable.run();
                                Toolkit.getDefaultToolkit().removeAWTEventListener(this);
                            }
                        }
                    }
                }
            };
            Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.FOCUS_EVENT_MASK);
        }

        public void runInRobotThreadWhenIdeaIsOk(final Runnable run) throws InterruptedException {
            MyLog.log("pushed to Edt");
            queueOfTasks.put(new Computable<Runnable>() {
                @Override
                public Runnable compute() {
                    EdtInvocationManager.getInstance().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (monitor) {
                                monitor.notifyAll();
                                MyLog.log("EDT finshed");
                            }
                        }
                    });
                    try {
                        synchronized (monitor) {
                            MyLog.log("Monitor is locked. Wait for EDT");
                            monitor.wait();
                            MyLog.log("Monitor unlocked");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return run;
                }
            });
        }

        public void waitUi(final Runnable runnable, final String componentText, int timeout) throws Exception {
            EdtInvocationManager.getInstance().invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (componentText.equals("")) {
                        EdtInvocationManager.getInstance().invokeLater(runnable);
                    }
                    try {
                        Component component = null;
                        try {
                            component = GuiUtil.findComponentByText(componentText, IdeFocusManager.getGlobalInstance().getFocusOwner());
                        } catch (Exception ignored) {
                        }
                        if (component != null) {
                            runnable.run();
                        } else {
                            try {
                                queueOfTasks.put(new Computable<Runnable>() {
                                    @Override
                                    public Runnable compute() {
                                        try {
                                            sleep(100);
                                            return new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        waitUi(runnable, componentText, timeout);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            };
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        return null;
                                    }
                                });
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        public void waitSome(int millis, Runnable runnable) throws InterruptedException {
            queueOfTasks.put(new Computable<Runnable>() {
                @Override
                public Runnable compute() {
                    try {
                        sleep(millis);
                        return runnable;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(100);
                    Object result = queueOfTasks.take().compute();
                    if (result != null && result instanceof Runnable) {
                        ((Runnable) result).run();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public Robot getMyRobot() {
        return myRobot;
    }


    public void setThreadDelay(int threadDelay) {
        this.threadDelay = threadDelay;
    }


}
