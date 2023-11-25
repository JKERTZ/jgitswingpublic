/*
 * Copyright (C) 2022 jkertz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jkertz.jgitswing.widgets.staging;

import java.util.HashSet;
import java.util.Set;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import com.jkertz.jgitswing.callback.IJGScallbackChain;
import com.jkertz.jgitswing.tabs.common.JGSstagingTreeNode;
import com.jkertz.jgitswing.widgets.common.JGScommonScrollwidget;
import org.eclipse.jgit.api.Status;

/**
 *
 * @author jkertz
 */
public class JGSstagingTreeWidget extends JGScommonScrollwidget {

    private final JTree jTree;
    private final IJGSstagingTreeWidget receiver;

    public JGSstagingTreeWidget(IJGSstagingTreeWidget receiver) {
        super();
        this.receiver = receiver;

        jTree = new JTree();
        jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        jTree.addTreeSelectionListener(getTreeFilesSelectionListener());
        this.setViewportView(jTree);

    }

    public void updateStagedTable(Status status, IJGScallbackChain callback) {
        DefaultTreeModel dtm = uiUtils.buildTreeModelStaged(status);

        SwingUtilities.invokeLater(() -> {
            jTree.removeAll();
            jTree.setModel(dtm);

            for (int i = 0; i < jTree.getRowCount(); i++) {
                jTree.expandRow(i);
            }
            callback.doNext(null);
        });
    }

    public void updateUnstagedTable(Status status, IJGScallbackChain callback) {
        DefaultTreeModel dtm = uiUtils.buildTreeModelUnstaged(status);

        SwingUtilities.invokeLater(() -> {
            jTree.removeAll();
            jTree.setModel(dtm);

            for (int i = 0; i < jTree.getRowCount(); i++) {
                jTree.expandRow(i);
            }
            callback.doNext(null);
        });
    }

//    private TreeSelectionListener getTreeSelectionListener() {
//        TreeSelectionListener treeSelectionListener = (TreeSelectionEvent e) -> {
//            TreePath selectionPath = jTree.getSelectionPath();
//            receiver.onBranchesWidgetTreeSelectionChanged(selectionPath);
//        };
//        return treeSelectionListener;
//    }
    private TreeSelectionListener getTreeFilesSelectionListener() {
        TreeSelectionListener treeSelectionListener = (TreeSelectionEvent e) -> {
//            TreePath selectionPath = jTree.getSelectionPath();
            Set<String> selectionList = new HashSet<>();
            TreePath[] selectionPaths = jTree.getSelectionPaths();
            if (selectionPaths != null) {
                for (TreePath selectionPath : selectionPaths) {
                    Set<String> selectedTreeNodes = getSelectedTreeNode(selectionPath);
                    selectionList.addAll(selectedTreeNodes);
                }
            }
            receiver.onFileStatusWidgetListSelectionChanged(selectionList);
        };
        return treeSelectionListener;
    }

    private Set<String> getSelectedTreeNode(TreePath selectionPath) {
        Set<String> result = new HashSet<>();
        if (selectionPath == null) {
            return result;
        }
        Object lastPathComponent = selectionPath.getLastPathComponent();
        if (lastPathComponent instanceof DefaultMutableTreeNode defaultMutableTreeNode) {
            System.out.println("is DefaultMutableTreeNode");
            if (defaultMutableTreeNode.isLeaf()) {
                System.out.println("isLeaf");
                Object userObject = defaultMutableTreeNode.getUserObject();
                if (userObject instanceof JGSstagingTreeNode jGSTreeNode) {
                    System.out.println("is JGSstagingTreeNode");
                    //rich html node
                    String file = jGSTreeNode.getFile();
                    result.add(file);
                }
            } else {
                System.out.println("isNode");
                Set<String> recursiveResults = getChildLeaves(defaultMutableTreeNode);
                result.addAll(recursiveResults);
            }
        }
        return result;
    }

    private Set<String> getChildLeaves(DefaultMutableTreeNode defaultMutableTreeNode) {
        Set<String> result = new HashSet<>();
        int childCount = defaultMutableTreeNode.getChildCount();
        for (int i = 0; i < childCount; i++) {
            TreeNode childAt = defaultMutableTreeNode.getChildAt(i);
            if (childAt instanceof DefaultMutableTreeNode defaultMutablechildAt) {
                if (defaultMutablechildAt.isLeaf()) {
                    System.out.println("isLeaf");
                    Object userObject = defaultMutablechildAt.getUserObject();
                    if (userObject instanceof JGSstagingTreeNode jGSTreeNode) {
                        System.out.println("is JGSstagingTreeNode");
                        //rich html node
                        String file = jGSTreeNode.getFile();
                        result.add(file);
                    }
                } else {
                    System.out.println("isNode");
                    Set<String> recursiveResults = getChildLeaves(defaultMutablechildAt);
                    result.addAll(recursiveResults);
                }
            }
        }
        return result;
    }

}
