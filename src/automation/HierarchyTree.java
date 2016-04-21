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

import a.b.T;
import com.intellij.ide.navigationToolbar.NavBarItem;
import com.intellij.internal.inspector.UiInspectorAction;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.labels.ActionLink;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.TreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

/**
 * Created by Sergey Karashevich on 26/01/16.
 */
public class HierarchyTree extends JTree implements TreeSelectionListener {
  final Component myComponent;

  private HierarchyTree(Component c) {
    myComponent = c;
    setModel(GuiUtil.getTree(c));
    getSelectionModel().addTreeSelectionListener(this);
    new TreeSpeedSearch(this);
  }

  public void expandPath() {
    TreeUtil.expandAll(this);
    int count = getRowCount();
    ComponentNode node = new ComponentNode(myComponent);

    for (int i = 0; i < count; i++) {
      TreePath row = getPathForRow(i);
      if (row.getLastPathComponent().equals(node)) {
        setSelectionPath(row);
        scrollPathToVisible(getSelectionPath());
        break;
      }
    }
  }

  @Nullable
  public static Component findComponentByText(String s, @Nullable ComponentNode startNode){
    if (startNode == null) return null;
    Component component = startNode.getComponent();
    if (component !=null && component.isVisible() && component instanceof ProjectViewElement && ((ProjectViewElement)component).getText().equals(s))
      return component;
    if (component != null && component.isShowing() && component.isVisible()) {
      if ((component instanceof ActionLink) && ((ActionLink)component).getText().equals(s))
        return component;
      if ((component instanceof JButton) && (((JButton)component).getText())!=null && ((JButton)component).getText().equals(s))
        return component;
      if ((component instanceof JLabel) && (((JLabel)component).getText())!=null &&((JLabel)component).getText().equals(s))
        return component;
      if ((component instanceof JBLabel) && (((JBLabel)component).getText())!=null && ((JBLabel)component).getText().equals(s))
        return component;
      if ((component instanceof JBCheckBox) && (((JBCheckBox)component).getText())!=null && ((JBCheckBox)component).getText().equals(s))
        return component;
      if ((component instanceof NavBarItem) && (((NavBarItem)component).getText())!=null && ((NavBarItem)component).getText().equals(s))
        return component;
      else {
        if (startNode.getChildCount() > 0) {
          for (int i = 0; i < startNode.getChildCount(); i++) {
            final Component result = findComponentByText(s, (ComponentNode) startNode.getChildAt(i));
            if (result != null)
              return result;
          }
        }
        else
          return null;
      }
      return null;
    }
    return null;
  }

  public static Component findComponentsByTextAndType(@NotNull String text, Class<? extends Component> clazz, ComponentNode root) {
    if (root == null) return null;
    Component component = root.getComponent();
    if (component != null && component.isVisible() && (component.isShowing() || component instanceof ProjectViewElement)) {
      if (clazz.isInstance(component) && getTextForced(component) != null && getTextForced(component).equals(text))
        return component;
      else {
        if (root.getChildCount() > 0) {
          for (int i = 0; i < root.getChildCount(); i++) {
            final Component componentByTextAndType = findComponentsByTextAndType(text, clazz, (ComponentNode) root.getChildAt(i));
            if (componentByTextAndType != null)
              return componentByTextAndType;
          }
        }
        else
          return null;
      }
      return null;
    }
    return null;
  }

  @Nullable
  public static Component findComponentByTextImproved(String s, @Nullable ComponentNode startNode){
    if (startNode == null) return null;
    Component component = startNode.getComponent();
    if (component != null && component.isShowing() && component.isVisible()) {
      if (getTextForced(component) !=null && getTextForced(component).equals(s))
        return component;
      else {
        if (startNode.getChildCount() > 0) {
          for (int i = 0; i < startNode.getChildCount(); i++) {
            if (findComponentByText(s, (ComponentNode)startNode.getChildAt(i)) != null)
              return (findComponentByText(s, (ComponentNode)startNode.getChildAt(i)));
          }
        }
        else
          return null;
      }
      return null;
    }
    return null;
  }

  @Nullable
  public static String getTextForced(Component component){
    Class<? extends Component> aClass = component.getClass();
    for (Method method : aClass.getMethods()) {
      if (method.getName().equals("getText")) {
        try {
          Object result = method.invoke(component);
          if (result != null && result instanceof String) return (String) result;
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
      }
    }
    return null;
  }

  public static ArrayList<Component> findComponentsByType(Class<? extends Component> clazz, ComponentNode startNode){
    if (startNode == null) return null;
    Component component = startNode.getComponent();
    if (component != null && component.isShowing() && component.isVisible()) {
      if (clazz.isInstance(component)){
        ArrayList<Component> result = new ArrayList();
        if (startNode.getChildCount() > 0) {
          for (int i = 0; i < startNode.getChildCount(); i++) {
            final ArrayList<? extends Component> resultArray = findComponentsByType(clazz, (ComponentNode) startNode.getChildAt(i));
            if (resultArray != null) {
              result.addAll(resultArray);
            }
          }

          result.add(component);
          return result;
        } else {
          result.add(component);
          return result;
        }
      } else {
        ArrayList<Component> result = new ArrayList<>();
        if (startNode.getChildCount() > 0) {
          for (int i = 0; i < startNode.getChildCount(); i++) {
            final ArrayList<Component> resultArray = findComponentsByType(clazz, (ComponentNode) startNode.getChildAt(i));
            if (resultArray != null) {
              result.addAll(resultArray);
            }
          }
          return result;
        }
      }
    } else {
      return null;
    }
    return null;
  }

  @Override
  public void valueChanged(TreeSelectionEvent e) {
    TreePath path = e.getNewLeadSelectionPath();
    if (path == null) {
      onComponentChanged(null);
      return;
    }
    Object component = path.getLastPathComponent();
    if (component instanceof ComponentNode) {
      Component c = ((ComponentNode)component).getComponent();
      onComponentChanged(c);
    }
  }

  public void onComponentChanged(Component c){
    //do nothing yet
  }

  public static class ComponentNode extends DefaultMutableTreeNode {
    private final Component myComponent;
    String myText;

    public ComponentNode(@NotNull Component component) {
      super(component);
      myComponent = component;
      children = prepareChildren(myComponent);
    }

    Component getComponent() {
      return myComponent;
    }

    @Override
    public String toString() {
      return myText != null ? myText : myComponent.getClass().getName();
    }

    public void setText(String value) {
      myText = value;
    }

    public String getText(){
      return myText;
    }

    @Override
    public boolean equals(Object obj) {
      return obj instanceof ComponentNode && ((ComponentNode)obj).getComponent() == getComponent();
    }

    @SuppressWarnings("UseOfObsoleteCollectionType")
    private static Vector prepareChildren(Component parent) {
      Vector<ComponentNode> result = new Vector<ComponentNode>();
      if (parent instanceof Container) {
        for (Component component : ((Container)parent).getComponents()) {
          result.add(new ComponentNode(component));
          if (component instanceof Tree) {
            final Tree tree = (Tree)component;
            final TreeUI treeUi = tree.getUI();
            final int rowCount = treeUi.getRowCount(tree);
            for (int i = 0; i < rowCount; i++) {
              final TreePath treePath = treeUi.getPathForRow(tree, i);
              final Rectangle rowBounds = treeUi.getPathBounds(tree, treePath);
              final DefaultMutableTreeNode node = (DefaultMutableTreeNode)treePath.getPathComponent(1);
              final String cmpText = treePath.getLastPathComponent().toString();
              ProjectViewElement projectViewElement = new ProjectViewElement(tree, node.getClass(), cmpText);
              projectViewElement.setBounds(rowBounds);
              projectViewElement.setVisible(true);
              result.add(new ComponentNode(projectViewElement));
            }
          }
        }
      }
      if (parent instanceof Window) {
        Window[] children = ((Window)parent).getOwnedWindows();
        for (Window child : children) {
          result.add(new ComponentNode(child));
        }
      }

      return result;
    }
  }

  public static class ProjectViewElement extends JLabel{

    Tree myTree;
    Class myOriginalClass;
    String myText;

    public ProjectViewElement(Tree tree, Class originalClass, String text){
      super(text);
      myText = text;
      myTree = tree;
      myOriginalClass = originalClass;
    }

    @Override
    public Point getLocationOnScreen() {
      final Point parentLocationOnscreen = getParent().getLocationOnScreen();
      final Point result = new Point(parentLocationOnscreen.x + getBounds().x, parentLocationOnscreen.y + getBounds().y);
      return result;
    }

    @Override
    public Container getParent() {
      return myTree;
    }
  }
}