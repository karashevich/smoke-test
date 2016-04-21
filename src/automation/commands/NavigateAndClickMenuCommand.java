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
import automation.util.PngUtils;
import com.intellij.ide.DataManager;
import com.intellij.ide.ui.customization.CustomActionsSchema;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.impl.ActionMenu;
import com.intellij.openapi.actionSystem.impl.MenuItemPresentationFactory;
import com.intellij.openapi.actionSystem.impl.Utils;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.openapi.wm.impl.IdeFrameImpl;
import com.intellij.ui.mac.foundation.Foundation;
import com.intellij.ui.mac.foundation.ID;
import com.intellij.util.containers.Queue;
import com.intellij.util.ui.EdtInvocationManager;
import org.jdesktop.swingx.util.WindowUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static com.intellij.ui.mac.foundation.Foundation.invoke;

/**
 * Created by Sergey Karashevich on 29/01/16.
 */
public class NavigateAndClickMenuCommand extends Command {

    Parameters myParameters;

    public NavigateAndClickMenuCommand(Parameters parameters) {
        myParameters = parameters;
    }

    @Override
    public void process(final Queue<Command> script) throws Exception {
        myParameters.log();
        String menusParam = myParameters.getTextField();
        final String[] splittedMenu = menusParam.split("->");

        if (SystemInfo.isMacSystemMenu) {
            if (splittedMenu.length == 1) {
                Point macMainMenuLocation = getMacMainMenuLocation(menusParam);
                navNclickMainMenu(macMainMenuLocation, runNext(script));
            } else {
                MenuChain menuChain = new MenuChain(menusParam);
                processMenuChain(menuChain, script);
            }

        } else {

            Component mainMenuComponent = getMainMenuComponent(menusParam);
            navNclick(mainMenuComponent, runNext(script), false);
        }
    }

    private void processMenuChain(MenuChain menuChain, Queue<Command> script) throws Exception {
        if (menuChain.isFirstMenu()) {
            Point macMainMenuLocation = getMacMainMenuLocation(menuChain.getMenu());
            navNclickNwaitMainMenu(macMainMenuLocation, processNextMenuChain(menuChain, script), 1500);
        } else {
            boolean horizontalFirst = (menuChain.getSplittedMenu().length > 2 && menuChain.getSplittedMenu().length - menuChain.getSize() >= 2);
            if (menuChain.isLastMenu()) {
                final String menu = menuChain.getMenu();
                final int lastMenuIndex = menuChain.getLastMenuIndex();
                if (lastMenuIndex == -1)
                    throw new Exception("Unable to detect menuIndex at jMenu for menu item:" + menu);
                Point p = PngUtils.getSubmenuItemPoint(menuChain.getBefore(), menuChain.getAfter(), lastMenuIndex);
                //probably patch monitor location
//                navNclick(p, runNext(script));
                navNclickNwait(p, runNext(script), 1500, horizontalFirst);
            } else {
                final String menu = menuChain.getMenu();
                final int lastMenuIndex = menuChain.getLastMenuIndex();
                if (lastMenuIndex == -1)
                    throw new Exception("Unable to detect menuIndex at jMenu for menu item:" + menu);
                Point p = PngUtils.getSubmenuItemPoint(menuChain.getBefore(), menuChain.getAfter(), lastMenuIndex);
                //probably patch monitor location
                navNwait(p, processNextMenuChain(menuChain, script), 1500, horizontalFirst);
            }
        }
    }

    //move mouse by L path, wait delay and run on EDT
    private void navNclickNwait(Component c, Runnable r, int delay, boolean horizontalFirst) throws Exception {
        navNclick(c, waitNrun(r, delay), horizontalFirst);
    }

    //move mouse by L path, wait delay and run on EDT
    private void navNclickNwait(Point p, Runnable r, int delay, boolean horizontalFirst) throws Exception {
        navNclick(p, waitNrun(r, delay), horizontalFirst);
    }

    //we don't need to move mouse by L trajectory if it is a main menu. Just go the straight path
    private void navNclickNwaitMainMenu(Point p, Runnable r, int delay) throws Exception {
        navNclickMainMenu(p, waitNrun(r, delay));
    }

    private void navNwait(Point p, Runnable r, int delay, boolean horizontalFirst) throws Exception {
        nav(p, waitNrun(r, delay), horizontalFirst);
    }

    private Runnable processNextMenuChain(MenuChain menuChain, Queue<Command> script) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    processMenuChain(menuChain, script);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @NotNull
    private Runnable waitNrun(final Runnable r, final int delay) {
        return new Runnable() {
            @Override
            public void run() {
                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            RobotControlManager.getInstance().getRobotControl().waitSome(delay, new Runnable() {
                                @Override
                                public void run() {
                                    EdtInvocationManager.getInstance().invokeLater(r);
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
    }


    private void navNclick(Component c, Runnable r, boolean horizontalFirst) throws Exception {
        RobotControlManager.getInstance().getRobotControl().navigateAndClickMenu(c, r, horizontalFirst);
    }

    private void navNclickMainMenu(Point p, Runnable r) throws Exception {
        RobotControlManager.getInstance().getRobotControl().navigateAndClick(p, r);
    }

    private void navNclick(Point p, Runnable r, boolean horizontalFirst) throws Exception {
        RobotControlManager.getInstance().getRobotControl().navigateAndClickMenu(p, r, horizontalFirst);
    }

    private void nav(Point p, Runnable r, boolean horizontalFirst) throws InterruptedException {
        RobotControlManager.getInstance().getRobotControl().navigateMenu(p, r, horizontalFirst);
    }

    private Runnable runNext(Queue<Command> script) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    startNext(script);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private Point getMacMainMenuLocation(String menuName) throws Exception {
        int mainMenuIndex = patchMacMainMenuIndex(getMainMenuIndex(menuName));
        BufferedImage regionCapture = captureMacMainMenu();
        Rectangle monitorBounds = getMonitorBounds();
        Point menuCenter = PngUtils.getMenuCenter(regionCapture, mainMenuIndex);
        //if multimonitor
        return new Point(monitorBounds.x + menuCenter.x,
                monitorBounds.y + menuCenter.y);
    }

    private int patchMacMainMenuIndex(int mainMenuIndex) {
        return mainMenuIndex + 2;
    }


    private void getNSMenuItem(String menuName) throws Exception {
        Component focusOwner = IdeFocusManager.getGlobalInstance().getFocusOwner();
        JRootPane rootPane = SwingUtilities.getRootPane(focusOwner);
        final JMenuBar jMenuBar = rootPane.getJMenuBar();

        Window mainWindow = SwingUtilities.getWindowAncestor(jMenuBar);
        if (mainWindow instanceof IdeFrameImpl) {

            final ID sharedApplication = invoke("NSApplication", "sharedApplication");
            final ID mainMenu = invoke(sharedApplication, "mainMenu");
            final int itemCount = invoke(mainMenu, "numberOfItems").intValue();
            ID resultMenuItem = null;

            for (int i = 0; i < itemCount; i++) {
                ID menuItem = invoke(mainMenu, "itemAtIndex:", i);
                ID mt = invoke(menuItem, "title");
                String menuItemName = Foundation.toStringViaUTF8(mt);
                if (menuItemName.equals(menuName)) {
                    resultMenuItem = menuItem;
                    break;
                }
            }
            if (resultMenuItem == null) throw new Exception("Unable to find Mac main menu: " + menuName);

            int index = invoke(mainMenu, "indexOfItem:", resultMenuItem).intValue();

        }
    }

    public Component getMainMenuComponent(String menuName) throws Exception {
        Component focusOwner = IdeFocusManager.getGlobalInstance().getFocusOwner();
        JMenuBar jMenuBar = SwingUtilities.getRootPane(focusOwner).getJMenuBar();
        int n = jMenuBar.getComponentCount();
        for (int i = 0; i < n; i++) {
            ActionMenu actionMenu = (ActionMenu) jMenuBar.getComponent(i);
            if (actionMenu.getText().equals(menuName)) {
                return actionMenu.getComponent();
            }
        }
        throw new Exception("Unable to find \"" + menuName + "\" in main menu");
    }

    public int getMainMenuIndex(String menuName) throws Exception {
        Component focusOwner = IdeFocusManager.getGlobalInstance().getFocusOwner();
        JMenuBar jMenuBar = SwingUtilities.getRootPane(focusOwner).getJMenuBar();
        int n = jMenuBar.getComponentCount();
        for (int i = 0; i < n; i++) {
            ActionMenu actionMenu = (ActionMenu) jMenuBar.getComponent(i);
            if (actionMenu.getText().equals(menuName)) {
                return i;
            }
        }
        throw new Exception("Unable to find \"" + menuName + "\" in main menu");
    }


    public BufferedImage captureMacMainMenu() throws Exception {
        Rectangle bounds = getMonitorBounds();
        Rectangle rectangle = new Rectangle(bounds.x, bounds.y, bounds.width, 20);
        Robot myRobot = RobotControlManager.getInstance().getRobotControl().getMyRobot();
        BufferedImage regionCapture = myRobot.createScreenCapture(rectangle);
//        File file = new File("regionCapture.png");
//        ImageIO.write(regionCapture, "png", file);

        return regionCapture;
    }

    public BufferedImage captureIdeaScreen() throws Exception {
        Rectangle bounds = getMonitorBounds();
        Robot myRobot = RobotControlManager.getInstance().getRobotControl().getMyRobot();
        BufferedImage regionCapture = myRobot.createScreenCapture(bounds);
        return regionCapture;
    }

    private Rectangle getMonitorBounds() throws Exception {
        Component focusOwner = IdeFocusManager.getGlobalInstance().getFocusOwner();
        JMenuBar jMenuBar = SwingUtilities.getRootPane(focusOwner).getJMenuBar();
        Window window = WindowUtils.findWindow(jMenuBar);
        if (window == null) throw new Exception("Unable to find window");


        GraphicsConfiguration config = window.getGraphicsConfiguration();
        GraphicsDevice myScreen = config.getDevice();
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();

        GraphicsDevice[] allScreens = env.getScreenDevices();
        int myScreenIndex = -1;
        for (int i = 0; i < allScreens.length; i++) {
            if (allScreens[i].equals(myScreen)) {
                myScreenIndex = i;
                break;
            }
        }
        return allScreens[myScreenIndex].getDefaultConfiguration().getBounds();
    }


    private class MenuChain {
        BufferedImage before;
        BufferedImage after;
        boolean firstMenu;
        String[] splittedMenu;
        JMenuBar jMenuBar;
        int menuIndex = -1;
        ActionGroup lastActionGroup;

        Queue<String> menus;
        int level;

        MenuChain(String menuStr) {
            splittedMenu = menuStr.split("->");
            menus = new Queue<>(splittedMenu.length);
            for (String menu : splittedMenu) {
                menus.addLast(menu);
            }
            firstMenu = true;
            level = 0;

            Component focusOwner = IdeFocusManager.getGlobalInstance().getFocusOwner();
            jMenuBar = SwingUtilities.getRootPane(focusOwner).getJMenuBar();
        }

        boolean isFirstMenu() {
            return firstMenu;
        }

        boolean isLastMenu() {
            return !menus.isEmpty() && menus.size() == 1;
        }

        public int getSize() {
            return menus.size();
        }

        @Nullable
        String getMenu() {
            if (firstMenu) {
                firstMenu = false;
            }
            if (menus.isEmpty()) return null;
            level++;
            takeSnapshot();
            menuIndex = calcMenuIndex(menus.peekFirst());
            return menus.pullFirst();
        }

        private int calcMenuIndex(String menuName) {
            if (lastActionGroup == null) {
                lastActionGroup = (ActionGroup) CustomActionsSchema.getInstance().getCorrectedAction(IdeActions.GROUP_MAIN_MENU);
//                    Component focusOwner = IdeFocusManager.getGlobalInstance().getFocusOwner();
//                    JRootPane rootPane = SwingUtilities.getRootPane(focusOwner);
//                    final JMenuBar jMenuBar = rootPane.getJMenuBar();
//
//                    jMenuBar.getSubElements()
                ArrayList<AnAction> list = new ArrayList<>();
                Utils.expandActionGroup(lastActionGroup,
                        list,
                        new MenuItemPresentationFactory(),
                        DataManager.getInstance().getDataContext(IdeFocusManager.getGlobalInstance().getFocusOwner()),
                        ActionPlaces.MAIN_MENU,
                        ActionManager.getInstance(),
                        true,
                        false);
                //remove separators
                for (int i = 0; i < list.size(); i++) {
                    final Presentation templatePresentation = list.get(i).getTemplatePresentation();

                    if (templatePresentation != null && templatePresentation.getText().equals(menuName)) {
                        if (list.get(i) instanceof ActionGroup) lastActionGroup = (ActionGroup) list.get(i);
                        return i;
                    }
                }
            } else {

                ArrayList<AnAction> list = new ArrayList<>();
                Utils.expandActionGroup(lastActionGroup,
                        list,
                        new MenuItemPresentationFactory(),
                        DataManager.getInstance().getDataContext(IdeFocusManager.getGlobalInstance().getFocusOwner()),
                        ActionPlaces.MAIN_MENU,
                        ActionManager.getInstance(),
                        false,
                        true);
                //remove separators
                int sep = 0;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) instanceof Separator) sep++;
                    else {
                        final Presentation templatePresentation = list.get(i).getTemplatePresentation();

                        if (templatePresentation != null && templatePresentation.getText().equals(menuName)) {
                            if (list.get(i) instanceof ActionGroup) lastActionGroup = (ActionGroup) list.get(i);
                            return i - sep;
                        }
                    }
                }
            }

            return -1;
        }

        int getLevel() {
            return level;
        }

        public BufferedImage getBefore() {
            return before;
        }

        public BufferedImage getAfter() {
            return after;
        }

        public int getLastMenuIndex() {
            return menuIndex;
        }

        private void takeSnapshot() {
            try {
                if (before == null && after == null) {
                    before = captureIdeaScreen();
                } else if (before != null && after == null) {
                    after = captureIdeaScreen();
                } else if (before != null && after != null) {
                    before = after;
                    after = captureIdeaScreen();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private String[] getSplittedMenu() {
            return splittedMenu;
        }
    }
}
