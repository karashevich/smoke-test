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
import com.intellij.ide.projectView.impl.ProjectTreeBuilder;
import com.intellij.ide.projectView.impl.ProjectViewTree;
import com.intellij.ide.projectView.impl.nodes.BasePsiNode;
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.ide.util.treeView.AbstractTreeBuilder;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.ide.util.treeView.AbstractTreeStructure;
import com.intellij.ide.util.treeView.AbstractTreeUi;
import com.intellij.openapi.util.ActionCallback;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.SpeedSearchBase;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.speedSearch.SpeedSearchSupply;
import com.intellij.util.containers.Queue;
import com.intellij.util.ui.tree.TreeUtil;
import org.jdom.Attribute;
import org.jdom.Element;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

import static automation.GuiUtil.getProjectViewTree;

/**
 * Created by Sergey Karashevich on 29/01/16.
 */
public class CheckProjectViewModelCommand extends Command {

    public final static String PATH = "path";
//    public final static String ACTION = "action";
//    public final static String ACTION_LEFT_CLICK = "left-click";
//    public final static String ACTION_DOUBLE_CLICK = "double-click";
//    public final static String ACTION_RIGHT_CLICK = "right-click";
    private static final String SPLIT_SYMBOL = "/";


    Element myRawElement;
    String path;
//    String action;

    public CheckProjectViewModelCommand(Element rawElement) {
        myRawElement = rawElement;
        try {
            extractInfoFromRawElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void extractInfoFromRawElement() throws Exception {
        final Attribute attributeElement = myRawElement.getAttribute(PATH);
        if (attributeElement == null)
            throw new Exception("SMOKE_TEST: Unable to find attribute 'path' for <project-model-check> command. Please fix your script");

        path = attributeElement.getValue();
//        action = attributeAction.getValue();
    }

    @Override
    public void process(final Queue<Command> script) throws Exception {

        //Project View item name
        final Component focusOwner = IdeFocusManager.getGlobalInstance().getFocusOwner();
        final ProjectViewTree projectViewTree = getProjectViewTree(focusOwner);
        final TreeModel model = projectViewTree.getModel();
        final TreePath projTreePath = TreeUtil.getFirstLeafNodePath(projectViewTree);

        final DefaultMutableTreeNode child = (DefaultMutableTreeNode) projectViewTree.getModel().getChild(model.getRoot(), 0);
        final DefaultMutableTreeNode extLibs = (DefaultMutableTreeNode) projectViewTree.getModel().getChild(model.getRoot(), 1);
        DefaultMutableTreeNode loadingChild = (DefaultMutableTreeNode)child.getFirstChild();
        final AbstractTreeBuilder treeBuilder = ProjectTreeBuilder.getBuilderFor(projectViewTree);

        final SpeedSearchSupply speedSearchSupply = SpeedSearchBase.getSupply(projectViewTree);
        final AbstractTreeStructure treeStructure = treeBuilder.getTreeStructure();
        final Object rootElement = treeStructure.getRootElement();
        final Object[] childElements = treeStructure.getChildElements(rootElement);

        AbstractTreeNode prj = (AbstractTreeNode) childElements[0];

        treeBuilder.expand(getNodeFromPath(path, prj), runNext(script));
//        treeBuilder.getTreeStructure().

    }

    private AbstractTreeNode getNodeFromPath(String path, AbstractTreeNode startNode) throws Exception {
        final String[] splitted = path.split(SPLIT_SYMBOL);
        if (splitted.length == 0) throw new Exception("Unable to split path: " + path);
        if (splitted.length == 1) {
//            final AbstractTreeNode[] nodes = (AbstractTreeNode[]) startNode.getChildren().toArray();
//            for (AbstractTreeNode node : nodes) {
            final Object[] nodes = startNode.getChildren().toArray();
            for (Object xnode : nodes) {
                if (!(xnode instanceof AbstractTreeNode))
                    throw new Exception("Object cannot be cast to AbstractTreeNode");
                AbstractTreeNode node = (AbstractTreeNode) xnode;
                if (node.getElement() instanceof BasePsiNode) {
                    final VirtualFile virtualFile = ((BasePsiNode) node.getElement()).getVirtualFile();
                    assert virtualFile != null;
                    if (virtualFile.getName().equals(path)) {
                        node.setParent(startNode);
                        return node;
                    }
                } else {
                    throw new Exception("Unable to cast node to BasePsiNode");
                }
            }
            throw new Exception("Unable to find such AbstractTreeNode by name: " + path );
        } else {
            String startname = splitted[0];
            String newpath = path.substring(startname.length() + SPLIT_SYMBOL.length());
            final Object[] nodes = startNode.getChildren().toArray();
            for (Object xnode : nodes) {
                if (!(xnode instanceof AbstractTreeNode))
                    throw new Exception("Object cannot be cast to AbstractTreeNode");
                AbstractTreeNode node = (AbstractTreeNode) xnode;
                if (node.getElement() instanceof BasePsiNode) {
                    final VirtualFile virtualFile = ((BasePsiNode) node.getElement()).getVirtualFile();
                    assert virtualFile != null;
                    if (virtualFile.getName().equals(startname)) {
                        node.setParent(startNode);
                        return getNodeFromPath(newpath, node);
                    }
                } else {
                    throw new Exception("Unable to cast node to BasePsiNode");
                }
            }
        }
        throw new Exception("Unable to find such AbstractTreeNode by name:" + path);
    }

}
