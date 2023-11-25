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
package com.jkertz.jgitswing.tabs.branches;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Map;
import javax.swing.tree.TreePath;
import com.jkertz.jgitswing.callback.IJGScallbackChain;
import com.jkertz.jgitswing.tabs.common.JGScommonPanel;
import com.jkertz.jgitswing.toolbars.branches.IJGSbranchesToolbar;
import com.jkertz.jgitswing.toolbars.branches.JGSbranchesToolbar;
import com.jkertz.jgitswing.widgets.branches.IJGSbranchesWidget;
import com.jkertz.jgitswing.widgets.branches.JGSbranchesWidget;
import org.eclipse.jgit.lib.BranchTrackingStatus;
import org.eclipse.jgit.lib.Ref;

/**
 *
 * @author jkertz
 */
public class JGSbranchesPanel extends JGScommonPanel implements IJGSbranchesToolbar, IJGSbranchesWidget {

    private final IJGSbranchesPanel receiver;
    private final JGSbranchesToolbar jGSbranchesToolbar;
    private final JGSbranchesWidget jGSbranchesWidget;

    protected JGSbranchesPanel(IJGSbranchesPanel receiver) {
        super(new BorderLayout());
        this.receiver = receiver;

        jGSbranchesToolbar = new JGSbranchesToolbar(this);
        jGSbranchesWidget = new JGSbranchesWidget(this);

//        JPanel gridPanel = new JPanel(new GridLayout(2, 1));
//
//        gridPanel.add(jGSbranchesWidget);
//        this.add(gridPanel, BorderLayout.CENTER);
        this.add(jGSbranchesWidget, BorderLayout.CENTER);

        this.add(jGSbranchesToolbar, BorderLayout.NORTH);

    }

    public void updateBranchTree(Map<Ref, BranchTrackingStatus> mapLocalBranches, List<Ref> listRemoteBranches, String currentBranch, IJGScallbackChain callback) {
        jGSbranchesWidget.updateBranchTree(mapLocalBranches, listRemoteBranches, currentBranch, callback);
    }

    protected void enableBranchButtos() {
        jGSbranchesToolbar.enableBranchButtos();
    }

    protected void disableBranchButtos() {
        jGSbranchesToolbar.disableBranchButtos();
    }

    @Override
    public void onBranchesToolbarClickedCreate() {
        receiver.onBranchesPanelClickedCreate();
    }

    @Override
    public void onBranchesToolbarClickedCheckout() {
        receiver.onBranchesPanelClickedCheckout();
    }

    @Override
    public void onBranchesToolbarClickedMerge() {
        receiver.onBranchesPanelClickedMerge();
    }

    @Override
    public void onBranchesToolbarClickedDelete() {
        receiver.onBranchesPanelClickedDelete();
    }

    @Override
    public void onBranchesWidgetTreeSelectionChanged(TreePath selectionPath) {
        receiver.onBranchesPanelTreeSelectionChanged(selectionPath);
    }

}
