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
package com.jkertz.jgitswing.widgets.branches;

import java.util.List;
import java.util.Map;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import com.jkertz.jgitswing.callback.IJGScallbackChain;
import com.jkertz.jgitswing.widgets.common.JGScommonScrollwidget;
import org.eclipse.jgit.lib.BranchTrackingStatus;
import org.eclipse.jgit.lib.Ref;

/**
 *
 * @author jkertz
 */
public class JGSbranchesWidget extends JGScommonScrollwidget {

    private JTree jTree;
    private final IJGSbranchesWidget receiver;

    public JGSbranchesWidget(IJGSbranchesWidget receiver) {
        super();
        this.receiver = receiver;

        jTree = new JTree();
        jTree.addTreeSelectionListener(getTreeSelectionListener());
        this.setViewportView(jTree);

    }

    public void updateBranchTree(Map<Ref, BranchTrackingStatus> mapLocalBranches, List<Ref> listRemoteBranches, String currentBranch, IJGScallbackChain callback) {
        DefaultTreeModel dtm = uiUtils.buildTreeModelBranches(mapLocalBranches, listRemoteBranches, currentBranch);

        SwingUtilities.invokeLater(() -> {
            jTree.removeAll();
            jTree.setModel(dtm);

            for (int i = 0; i < jTree.getRowCount(); i++) {
                jTree.expandRow(i);
            }
            callback.doNext(null);
//            TreePath rootPath = new TreePath(rootNode.getPath());
//            jTree.expandPath(rootPath);
//            TreePath localPath = new TreePath(localRootNode.getPath());
//            jTree.expandPath(localPath);
//            TreePath remotePath = new TreePath(remoteRootNode.getPath());
//            jTree.expandPath(remotePath);
        });

    }

    private TreeSelectionListener getTreeSelectionListener() {
        TreeSelectionListener treeSelectionListener = (TreeSelectionEvent e) -> {
            TreePath selectionPath = jTree.getSelectionPath();
            receiver.onBranchesWidgetTreeSelectionChanged(selectionPath);
        };
        return treeSelectionListener;
    }

}
