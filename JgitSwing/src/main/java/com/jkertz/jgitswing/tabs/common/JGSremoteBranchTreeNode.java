/*
 * Copyright (C) 2023 JKERTZ
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

import org.eclipse.jgit.lib.Ref;

/**
 *
 * @author Jürgen Kertz
 */
public class JGSremoteBranchTreeNode {

    private final Ref branch;
    private final String htmlNode;
    private final JGShtmlUtils htmlUtils = JGShtmlUtils.getINSTANCE();

    public JGSremoteBranchTreeNode(Ref branch) {
        this.branch = branch;
        String _pureBranchname = JGSuiUtils.getINSTANCE().getPureBranchname(branch.getName());
        String remoteName = JGSuiUtils.getINSTANCE().getRemoteName(branch.getName());
//        htmlNode = _pureBranchname;
        String orangeremoteName = htmlUtils.toOrange(remoteName);
        htmlNode = htmlUtils.toHtml(_pureBranchname + " (" + orangeremoteName + ")");
    }

    @Override
    public String toString() {
        return htmlNode;
    }

    public Ref getBranch() {
        return branch;
    }

}
