/*
 * Copyright (C) 2022 Jürgen Kertz
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

import com.jkertz.jgitswing.businesslogic.JGSstagingStatus;
import com.jkertz.jgitswing.logger.JGSlogger;
import com.jkertz.jgitswing.model.JGStag;
import com.jkertz.jgitswing.tablemodels.IterableRevCommitTableModel;
import com.jkertz.jgitswing.tablemodels.ListDiffEntryTableModel;
import com.jkertz.jgitswing.tablemodels.ListJGStagsTableModel;
import com.jkertz.jgitswing.tablemodels.ListRefTagsTableModel;
import com.jkertz.jgitswing.tablemodels.StatusIgnoredTableModel;
import com.jkertz.jgitswing.tablemodels.StatusStagedTableModel;
import com.jkertz.jgitswing.tablemodels.StatusUnstagedTableModel;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.BranchTrackingStatus;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 *
 * @author Jürgen Kertz
 */
public class JGSuiUtils {

    private static JGSuiUtils INSTANCE = null;
    private final JGSlogger logger;

    private JGSuiUtils() {
        logger = JGSlogger.getINSTANCE();
    }

    public static JGSuiUtils getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new JGSuiUtils();
        }
        return INSTANCE;
    }

    //TODO: replace this method with real data thing to remove hardcoded column access
    public List<String> getSelectedFromTable(JTable jTable) {
        int[] selectedRows = jTable.getSelectedRows();
        List<String> selectionList = new ArrayList<>();
        for (int sel : selectedRows) {
            int modelSel = jTable.convertRowIndexToModel(sel);
            String fileName = jTable.getModel().getValueAt(modelSel, 2).toString();
            selectionList.add(fileName);
        }
        return selectionList;
    }

    private void setTableFromDiff(JTable jTable, List<DiffEntry> currentDiff) {
//        DefaultTableModel dtm = buildTableModel(currentDiff);
        ListDiffEntryTableModel tableModel = getTableModel(currentDiff);
        jTable.setModel(tableModel);
        jTable.setAutoCreateRowSorter(true);
        jTable.getTableHeader().setReorderingAllowed(false);
        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableModel.fireTableDataChanged();
    }

    public String extractFilenameFromPath(String path) {
        String fileSeparator = File.separator;
        if (!path.contains(fileSeparator)) {
            fileSeparator = "/";
        }
        String result = path.substring(path.lastIndexOf(fileSeparator) + 1);
        return result;
    }

    private void setFormattedDiff(JTextPane jTextPaneCurrentDiff, String currentDiffFile) {
        jTextPaneCurrentDiff.setText("");
        StyledDocument doc = (StyledDocument) jTextPaneCurrentDiff.getDocument();
        Style redStyle = doc.addStyle("RedStyle", null);
        StyleConstants.setForeground(redStyle, Color.red);
        Style greenStyle = doc.addStyle("GreenStyle", null);
        StyleConstants.setForeground(greenStyle, Color.green);
        StyleContext styleContext = new StyleContext();
        Style defaultStyle = styleContext.getStyle(StyleContext.DEFAULT_STYLE);
//        StyleConstants.setBackground(style, Color.blue);
        new BufferedReader(new StringReader(currentDiffFile)).lines().forEach(
                (line) -> {
                    try {
                        line += "\n";
                        if (line.startsWith("+")) {
                            doc.insertString(doc.getLength(), line, greenStyle);
                        } else if (line.startsWith("-")) {
                            doc.insertString(doc.getLength(), line, redStyle);
                        } else {
                            doc.insertString(doc.getLength(), line, defaultStyle);
                        }

                    } catch (BadLocationException ex) {
                        logger.getLogger().severe(ex.getMessage());
                    }
                }
        );
    }

    public DefaultStyledDocument buildStyledDocumentFromFileDiff(String currentDiffFile) {
        DefaultStyledDocument doc = new DefaultStyledDocument();
        Style redStyle = doc.addStyle("RedStyle", null);
        StyleConstants.setForeground(redStyle, Color.red);
        Style greenStyle = doc.addStyle("GreenStyle", null);
        StyleConstants.setForeground(greenStyle, Color.green);
        StyleContext styleContext = new StyleContext();
        Style defaultStyle = styleContext.getStyle(StyleContext.DEFAULT_STYLE);
//        StyleConstants.setBackground(style, Color.blue);
        new BufferedReader(new StringReader(currentDiffFile)).lines().forEach(
                (line) -> {
                    try {
                        line += "\n";
                        if (line.startsWith("+")) {
                            doc.insertString(doc.getLength(), line, greenStyle);
                        } else if (line.startsWith("-")) {
                            doc.insertString(doc.getLength(), line, redStyle);
                        } else {
                            doc.insertString(doc.getLength(), line, defaultStyle);
                        }

                    } catch (BadLocationException ex) {
                        logger.getLogger().severe(ex.getMessage());
                    }
                }
        );
        return doc;
    }

    private void setTableFromCommitsMulitselect(JTable jTable, Iterable<RevCommit> allCommits) {
//        DefaultTableModel dtm = buildTableModel(allCommits);
        IterableRevCommitTableModel tableModel = getTableModel(allCommits);
        jTable.setModel(tableModel);
        jTable.setAutoCreateRowSorter(true);
        jTable.getTableHeader().setReorderingAllowed(false);
        jTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tableModel.fireTableDataChanged();
    }

    private void setTableFromCommits(JTable jTable, Iterable<RevCommit> allCommits) {
//        DefaultTableModel dtm = buildTableModel(allCommits);
        IterableRevCommitTableModel tableModel = getTableModel(allCommits);
        jTable.setModel(tableModel);
        jTable.setAutoCreateRowSorter(true);
        jTable.getTableHeader().setReorderingAllowed(false);
        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableModel.fireTableDataChanged();
    }

    public IterableRevCommitTableModel getTableModel(Iterable<RevCommit> allCommits) {
        return new IterableRevCommitTableModel(allCommits);
    }

    public ListDiffEntryTableModel getTableModel(List<DiffEntry> currentDiff) {
        return new ListDiffEntryTableModel(currentDiff);
    }

    public ListJGStagsTableModel getTableModelJGStags(List<JGStag> jgsTags) {
        return new ListJGStagsTableModel(jgsTags);
    }

    public ListRefTagsTableModel getTableModelTags(List<Ref> tags) {
        return new ListRefTagsTableModel(tags);
    }

    public StatusIgnoredTableModel getTableModelIgnored(Status status) {
        return new StatusIgnoredTableModel(status);
    }

    public StatusStagedTableModel getTableModelStaged(Status status) {
        return new StatusStagedTableModel(status);
    }

    public StatusUnstagedTableModel getTableModelUnstaged(Status status) {
        return new StatusUnstagedTableModel(status);
    }

    public List<Object[]> getStatusTableData(String status, Set<String> paths) {
        List<Object[]> data = new ArrayList<>();
        for (String path : paths) {
            String fileName = extractFilenameFromPath(path);
            Object[] row = {fileName, status, path};
            data.add(row);
        }
        return data;
    }

    public void setTableSingleSelect(JTable jTable, AbstractTableModel dtm) {
        jTable.setModel(dtm);
        jTable.setAutoCreateRowSorter(true);
        jTable.getTableHeader().setReorderingAllowed(false);
        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public void setTableMultiSelect(JTable jTable, AbstractTableModel dtm) {
        jTable.setModel(dtm);
        jTable.setAutoCreateRowSorter(true);
        jTable.getTableHeader().setReorderingAllowed(false);
        jTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    public DefaultTreeModel buildTreeModelBranches(Map<Ref, BranchTrackingStatus> mapLocalBranches, List<Ref> listRemoteBranches, String currentBranch) {
        final String LOCALSTRING = "local";
        final String REMOTESTRING = "remote";
//        final String REMOTESTRING = "<html><b>Bold Text</b></html>";

        Set<Ref> listLocalBranches = mapLocalBranches.keySet();

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Branches");
        DefaultTreeModel defaultTreeModel = new DefaultTreeModel(rootNode);

        int parentIndex = 0;

        DefaultMutableTreeNode localRootNode = new DefaultMutableTreeNode(LOCALSTRING);

        defaultTreeModel.insertNodeInto(localRootNode, rootNode, parentIndex);
        parentIndex++;

        int localIndex = 0;

//        for (Ref branch : listLocalBranches) {
//            BranchTrackingStatus branchTrackingStatus = mapLocalBranches.get(branch);
//            JGSbranchTreeNode treeNode = new JGSbranchTreeNode(branch, branchTrackingStatus, currentBranch);
//            DefaultMutableTreeNode localNode = new DefaultMutableTreeNode(treeNode);
//            defaultTreeModel.insertNodeInto(localNode, localRootNode, localIndex);
//
//            localIndex++;
//        }
        for (Ref branch : listLocalBranches) {
            BranchTrackingStatus branchTrackingStatus = mapLocalBranches.get(branch);
            JGSlocalBranchTreeNode treeNode = new JGSlocalBranchTreeNode(branch, branchTrackingStatus, currentBranch);
            addStringToNode(localRootNode, treeNode);
        }

        DefaultMutableTreeNode remoteRootNode = new DefaultMutableTreeNode(REMOTESTRING);

        defaultTreeModel.insertNodeInto(remoteRootNode, rootNode, parentIndex);
        parentIndex++;

        int remoteIndex = 0;

//        for (Ref branch : listRemoteBranches) {
//            String pureBranchname = removeRefsRemotes(branch.getName());
//            DefaultMutableTreeNode remoteNode = new DefaultMutableTreeNode(pureBranchname);
//            defaultTreeModel.insertNodeInto(remoteNode, remoteRootNode, remoteIndex);
//
//            remoteIndex++;
//        }
        for (Ref branch : listRemoteBranches) {
//            String name = branch.getName();
//            addStringToNode(remoteRootNode, name);
            JGSremoteBranchTreeNode treeNode = new JGSremoteBranchTreeNode(branch);
            addStringToNode(remoteRootNode, treeNode);
        }
        return defaultTreeModel;
    }

    public DefaultTreeModel buildTreeModelStaged(Status status) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Staged");
        DefaultTreeModel defaultTreeModel = new DefaultTreeModel(rootNode);

        Map<String, String> stagedMap = getStagedFiles(status);
        Set<String> files = stagedMap.keySet();
        for (String file : files) {
            String statusname = stagedMap.get(file);
            JGSstagingTreeNode treeNode = new JGSstagingTreeNode(file, statusname);
            addStringToNode(rootNode, treeNode);
        }
        return defaultTreeModel;
    }

    public DefaultTreeModel buildTreeModelUnstaged(Status status) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Unstaged");
        DefaultTreeModel defaultTreeModel = new DefaultTreeModel(rootNode);

        Map<String, String> unstagedMap = getUnstagedFiles(status);
        Set<String> files = unstagedMap.keySet();
        for (String file : files) {
            String statusname = unstagedMap.get(file);
            JGSstagingTreeNode treeNode = new JGSstagingTreeNode(file, statusname);
            addStringToNode(rootNode, treeNode);
        }
        return defaultTreeModel;
    }

    private void addStringToNode(DefaultMutableTreeNode rootNode, JGSstagingTreeNode treeNode) {
        String name = treeNode.getFile();
        String[] split = name.split("/");
        int length = split.length;
        DefaultMutableTreeNode addRecusiveToNode = addRecusiveToNode(rootNode, split, 0, length - 1);
        DefaultMutableTreeNode localNode = new DefaultMutableTreeNode(treeNode);
        addRecusiveToNode.add(localNode);
    }

    private void addStringToNode(DefaultMutableTreeNode rootNode, JGSlocalBranchTreeNode treeNode) {
        String name = treeNode.getBranch().getName();
        String[] split = name.split("/");
        int length = split.length;
        DefaultMutableTreeNode addRecusiveToNode = addRecusiveToNode(rootNode, split, 0, length - 1);
        DefaultMutableTreeNode localNode = new DefaultMutableTreeNode(treeNode);
        addRecusiveToNode.add(localNode);
    }

    private void addStringToNode(DefaultMutableTreeNode rootNode, JGSremoteBranchTreeNode treeNode) {
        String name = treeNode.getBranch().getName();
        String[] split = name.split("/");
        int length = split.length;
        DefaultMutableTreeNode addRecusiveToNode = addRecusiveToNode(rootNode, split, 0, length - 1);
        DefaultMutableTreeNode localNode = new DefaultMutableTreeNode(treeNode);
        addRecusiveToNode.add(localNode);
    }

    private void addStringToNode(DefaultMutableTreeNode rootNode, String text) {
        String[] split = text.split("/");
        int length = split.length;
        addRecusiveToNode(rootNode, split, 0, length);
    }

    private DefaultMutableTreeNode addRecusiveToNode(DefaultMutableTreeNode parent, String[] split, int depth, int length) {
        String element = split[depth];
        DefaultMutableTreeNode child = findMatchingChild(parent, element);
        if (child == null) {
            child = new DefaultMutableTreeNode(element);
            parent.add(child);
        }
        if ((depth + 1) < length) {
            return addRecusiveToNode(child, split, depth + 1, length);
        }
        return child;
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

    public String removeRefsHeads(String name) {
        String result = name.replace("refs/heads/", "");
        return result;
    }

    /**
     * retreives last element of string separated by /
     *
     * @param text
     * @return
     */
    public String getPureBranchname(String text) {
        String[] split = text.split("/");
        int length = split.length;
        return split[length - 1];
    }

    /**
     * retrieves remote name, usually origin, from /refs/remotes/origin/...
     *
     * @param text
     * @return
     */
    public String getRemoteName(String text) {
        String[] split = text.split("/");
        return split[2];
    }

    public String removeRefsRemotes(String name) {
        String toReplace = "refs/remotes/";
        String result = name.replace(toReplace, "");
        return result;
    }

    public String getRemotePureBranchName(String text) {
        String toReplace = "refs/remotes/" + getRemoteName(text) + "/";
        String result = text.replace(toReplace, "");
        return result;
    }

    public DefaultTreeModel buildTreeModelConfig(Map<String, Map<String, Map<String, String>>> configInfoMap) {
        Set<String> sections = configInfoMap.keySet();
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Config");
        DefaultTreeModel defaultTreeModel = new DefaultTreeModel(rootNode);

        int sectionIndex = 0;

        for (String section : sections) {
            DefaultMutableTreeNode sectionNode = new DefaultMutableTreeNode(section);
            defaultTreeModel.insertNodeInto(sectionNode, rootNode, sectionIndex);
            sectionIndex++;

            int subSectionIndex = 0;

            Map<String, Map<String, String>> subSectionMap = configInfoMap.get(section);
            Set<String> subSections = subSectionMap.keySet();
            for (String subSection : subSections) {
                DefaultMutableTreeNode subSectionNode = new DefaultMutableTreeNode(subSection);
                defaultTreeModel.insertNodeInto(subSectionNode, sectionNode, subSectionIndex);
                subSectionIndex++;

                int nameIndex = 0;

                Map<String, String> nameMap = subSectionMap.get(subSection);
                Set<String> names = nameMap.keySet();
                for (String paramName : names) {
                    String paramValue = nameMap.get(paramName);
                    DefaultMutableTreeNode nameNode = new DefaultMutableTreeNode(paramName + " : " + paramValue);
                    defaultTreeModel.insertNodeInto(nameNode, subSectionNode, nameIndex);
                    nameIndex++;
                }

            }
        }
        return defaultTreeModel;

    }

    private Map<String, String> getStagedFiles(Status status) {
        Map<String, String> result = new HashMap<>();
        addStatusToMap(result, status.getChanged(), JGSstagingStatus.Changed.toString());
        addStatusToMap(result, status.getAdded(), JGSstagingStatus.Added.toString());
        addStatusToMap(result, status.getRemoved(), JGSstagingStatus.Removed.toString());
        return result;
    }

    private Map<String, String> getUnstagedFiles(Status status) {
        Map<String, String> result = new HashMap<>();
        addStatusToMap(result, status.getModified(), JGSstagingStatus.Modified.toString());
        addStatusToMap(result, status.getUntracked(), JGSstagingStatus.Untracked.toString());
        addStatusToMap(result, status.getConflicting(), JGSstagingStatus.Conflicting.toString());
        addStatusToMap(result, status.getMissing(), JGSstagingStatus.Missing.toString());
        return result;
    }

    private void addStatusToMap(Map<String, String> result, Set<String> files, String statusname) {
        for (String file : files) {
            result.put(file, statusname);
        }
    }

}
