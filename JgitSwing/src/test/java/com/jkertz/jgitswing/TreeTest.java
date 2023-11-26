/*
 * Copyright (C) 2023 Jürgen Kertz
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
package com.jkertz.jgitswing;

import com.jkertz.jgitswing.tabs.common.JGSuiUtils;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author Jürgen Kertz
 */
public class TreeTest {

    public void hello() {
        System.out.println("hello");
    }

    public void addToTreeTest() {
        String test1 = "/folder1/subfolder1/node1";
        String test2 = "/folder1/subfolder2/node2";

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Paths1");
        DefaultTreeModel defaultTreeModel = new DefaultTreeModel(rootNode);

        addStringToNode(rootNode, test1);
        addStringToNode(rootNode, test2);
    }

    public void splitTest() {
        String string1 = "hello";
        String string2 = "ufo/depp";
        String pureBranchname11 = JGSuiUtils.getINSTANCE().getPureBranchname(string1);
        String pureBranchname12 = JGSuiUtils.getINSTANCE().getPureBranchname(string2);
//        String pureBranchname21 = JGSuiUtils.getINSTANCE().getPureBranchname2(string1);
//        String pureBranchname22 = JGSuiUtils.getINSTANCE().getPureBranchname2(string2);
        System.out.println("pureBranchname11: " + pureBranchname11);
        System.out.println("pureBranchname12: " + pureBranchname12);
//        System.out.println("pureBranchname21: " + pureBranchname21);
//        System.out.println("pureBranchname22: " + pureBranchname22);
    }

    private void addStringToNode(DefaultMutableTreeNode rootNode, String text) {
        String[] split = text.split("/");
        int length = split.length;
        addRecusiveToNode(rootNode, split, 0, length);
    }

    private void addRecusiveToNode(DefaultMutableTreeNode parent, String[] split, int depth, int length) {
        String element = split[depth];
        DefaultMutableTreeNode child = findMatchingChild(parent, element);
        if (child == null) {
            child = new DefaultMutableTreeNode(element);
            parent.add(child);
        }
        if ((depth + 1) < length) {
            addRecusiveToNode(child, split, depth + 1, length);
        }
    }

    private DefaultMutableTreeNode findMatchingChild(DefaultMutableTreeNode parent, String element) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            DefaultMutableTreeNode childAt = (DefaultMutableTreeNode) parent.getChildAt(i);
            String childAttoString = childAt.toString();
            if (childAttoString.equals(element)) {
                return childAt;
            }
        }
        return null;
    }
}
