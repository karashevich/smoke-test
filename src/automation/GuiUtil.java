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


import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Sergey Karashevich on 26/01/16.
 */
public class GuiUtil {


  public TreeModel getFocusedTree(){
    return getTree(IdeFocusManager.getGlobalInstance().getFocusOwner());
  }


  /**
   * Build a TreeModel (hierarchical structure of components) for a pane with component c
   *
   * @param c - any component of a building tree
   * @return
   */
  public static TreeModel getTree(Component c){
    Component parent = c.getParent();
    while (parent != null) {
      c = parent;
      parent = c.getParent();//Find root window
    }
    return new DefaultTreeModel(new HierarchyTree.ComponentNode(c));
  }

  /**
   *
   * Find startComponent of hierarchy tree which have a text field equaled to String s
   *
   * @param s - textField for a searchable startComponent
   * @param startComponent - a component from which will be built hierarchy tree
   * @return
   */
  public static Component findComponentByText(String s, Component startComponent) throws Exception {
    TreeModel tree = getTree(startComponent);
    final Component componentByText = HierarchyTree.findComponentByText(s, (HierarchyTree.ComponentNode) tree.getRoot());
    if (componentByText == null) throw new Exception("Unable to find component by text: \"" + s + "\"");
    return componentByText;
  }

  public static Component findComponentByTextAndType(String text, Class<? extends Component> clazz, Component startComponent){
    TreeModel tree = getTree(startComponent);
    return HierarchyTree.findComponentsByTextAndType(text, clazz, (HierarchyTree.ComponentNode)tree.getRoot());
  }

  public static ArrayList<Component> findComponentsByType(Class<? extends Component> clazz, Component startComponent){
    TreeModel tree = getTree(startComponent);
    return HierarchyTree.findComponentsByType(clazz, (HierarchyTree.ComponentNode)tree.getRoot());
  }

  /**
   * Checks that hierarchy tree contains or not ActionLink, JButton, JBButton, JLabel, JBLabel, JBCheckBox with the finding text
   *
   * @param s - finding text
   * @return true if hierarchy tree built for Component startComponent contains component with textField s
   */
  public static boolean checkTextСomponent(String s, Component startComponent){
    TreeModel tree = getTree(startComponent);
    return HierarchyTree.findComponentByText(s, (HierarchyTree.ComponentNode)tree.getRoot()) != null;
  }

  public static TreeNode traverse(@NotNull final TreeNode node, Acceptor acceptor) {
    final int childCount = node.getChildCount();
    for (int i = 0; i < childCount; i++){
      final TreeNode result = traverse(node.getChildAt(i), acceptor);
      if (result != null) return result;
    }
    if (acceptor.accept(node)) return node;
    else return null;
  }

  interface Acceptor{
    boolean accept(TreeNode treeNode);
  }

  /**
   * Use for debug only
   */
  //public String[] getAllTextFromComponents(){
  //  TreeModel tree = getFocusedTree();
  //  ArrayList<String> result = new ArrayList<String>(10);
  //
  //}
}
