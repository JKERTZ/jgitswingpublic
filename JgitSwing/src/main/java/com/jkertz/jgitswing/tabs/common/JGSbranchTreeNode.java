/*
 * Copyright (C) 2023 jkertz
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
package com.jkertz.jgitswing.tabs.common;

import org.eclipse.jgit.lib.BranchTrackingStatus;
import org.eclipse.jgit.lib.Ref;

/**
 *
 * @author jkertz
 */
public class JGSbranchTreeNode {

    private final Ref branch;
    private final BranchTrackingStatus branchTrackingStatus;
    private final String htmlNode;

    public JGSbranchTreeNode(Ref branch, BranchTrackingStatus branchTrackingStatus, String currentBranch) {
        this.branch = branch;
        this.branchTrackingStatus = branchTrackingStatus;

        int aheadCount = branchTrackingStatus != null ? branchTrackingStatus.getAheadCount() : 0;
        int behindCount = branchTrackingStatus != null ? branchTrackingStatus.getBehindCount() : 0;
        String remoteTrackingBranch = branchTrackingStatus != null ? branchTrackingStatus.getRemoteTrackingBranch() : "<font color=orange>" + "no remote branch" + "</font>";

        String branchname = JGSuiUtils.getINSTANCE().removeRefsHeads(branch.getName());
        String _pureBranchname = JGSuiUtils.getINSTANCE().getPureBranchname(branch.getName());
        boolean isCurrentBranch = branchname.equals(currentBranch);
        if (isCurrentBranch) {
            _pureBranchname = "<b>" + _pureBranchname + "</b>";
        }
        String aheadBehind = " (↑" + aheadCount + " ↓" + behindCount + ") ";

        remoteTrackingBranch = "<i>" + remoteTrackingBranch + "</i>";
        htmlNode = "<html>" + _pureBranchname + aheadBehind + remoteTrackingBranch + "</html>";
    }

    @Override
    public String toString() {
//        return "JGSbranchTreeNode{" + "pureBranchname=" + pureBranchname + ", currentBranch=" + currentBranch + '}';
        return htmlNode;
    }

    public Ref getBranch() {
        return branch;
    }

    public BranchTrackingStatus getBranchTrackingStatus() {
        return branchTrackingStatus;
    }

}
