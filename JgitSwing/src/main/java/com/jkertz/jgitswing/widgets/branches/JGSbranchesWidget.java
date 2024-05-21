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

import com.jkertz.jgitswing.widgets.common.JGScommonScrollwidget;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

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

    public void updateBranchTree(DefaultTreeModel dtm) {
//        DefaultTreeModel dtm = uiUtils.buildTreeModelBranches(mapLocalBranches, listRemoteBranches, currentBranch);

//        SwingUtilities.invokeLater(() -> {
        jTree.removeAll();
        jTree.setModel(dtm);

        for (int i = 0; i < jTree.getRowCount(); i++) {
            jTree.expandRow(i);
        }

//        });
    }

    private TreeSelectionListener getTreeSelectionListener() {
        TreeSelectionListener treeSelectionListener = (TreeSelectionEvent e) -> {
            TreePath selectionPath = jTree.getSelectionPath();
            receiver.onBranchesWidgetTreeSelectionChanged(selectionPath);
        };
        return treeSelectionListener;
    }

}
