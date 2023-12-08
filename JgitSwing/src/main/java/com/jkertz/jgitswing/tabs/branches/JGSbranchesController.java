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

import com.jkertz.jgitswing.callback.IJGScallbackRefresh;
import com.jkertz.jgitswing.dialogs.JGScheckoutDialog;
import com.jkertz.jgitswing.model.JGSrepositoryModel;
import com.jkertz.jgitswing.tabs.common.IJGScommonController;
import com.jkertz.jgitswing.tabs.common.JGSbranchTreeNode;
import com.jkertz.jgitswing.tabs.common.JGScommonController;
import com.jkertz.jgitswing.tabs.common.JGSuiUtils;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.lib.BranchTrackingStatus;
import org.eclipse.jgit.lib.Ref;

/**
 *
 * @author jkertz
 */
public final class JGSbranchesController extends JGScommonController implements IJGSbranchesPanel, IJGScommonController {

    private JGSbranchesPanel panel;
    private TreePath selectionPath;
    private final String LOCALSTRING = "local";
    private final String REMOTESTRING = "remote";

    public JGSbranchesController(JGSrepositoryModel jGSrepositoryModel) {
        super("Branches", jGSrepositoryModel);
        panel = new JGSbranchesPanel(this);
        setPanel(panel);
    }

    @Override
    public void onGitRefChanged() {
        //caused by commit
        logger.getLogger().fine("onGitRefChanged");
        refresh();
    }

    @Override
    public void onBranchesPanelClickedCreate() {
        showProgressBar("onBranchesPanelClickedCreate");
        String branchName = showInputDialog("Create Branch", "Enter new branch name");
        Git git = jGSrepositoryModel.getGit();

        try {
            Ref result = utils.createBranch(git, branchName);
            hideProgressBar();
            showInfoDialog("onBranchesPanelClickedCreate", result.getName());
        } catch (Exception ex) {
            logger.getLogger().log(Level.SEVERE, "onBranchesPanelClickedCreate", ex);
        }
    }

    private String getSelectedTreeNode() {
        String result = null;
        if (selectionPath == null) {
            return result;
        }
        Object lastPathComponent = selectionPath.getLastPathComponent();
        if (lastPathComponent instanceof DefaultMutableTreeNode defaultMutableTreeNode) {
            System.out.println("is DefaultMutableTreeNode");
            if (defaultMutableTreeNode.isLeaf()) {
                System.out.println("isLeaf");
                Object userObject = defaultMutableTreeNode.getUserObject();
                if (userObject instanceof JGSbranchTreeNode jGSbranchTreeNode) {
                    System.out.println("is JGSbranchTreeNode");
                    //rich html node
                    Ref branch = jGSbranchTreeNode.getBranch();
                    result = JGSuiUtils.getINSTANCE().removeRefsHeads(branch.getName());
                } else {
                    result = defaultMutableTreeNode.toString();
                    System.out.println("plain leaf: " + result);
                }
            }
        }
        return result;
    }

    @Override
    public void onBranchesPanelClickedCheckout() {
        showProgressBar("onIJGSbranchesPanelCheckoutClicked");

        JGScheckoutDialog JGScheckoutDialog = new JGScheckoutDialog();
        boolean result = JGScheckoutDialog.show(panel);
        if (!result) {
            return;
        }

        String pathComponent0 = selectionPath.getPathComponent(0).toString();//Branches
        String pathComponent1 = selectionPath.getPathComponent(1).toString();//local
        Object lastPathComponent = selectionPath.getLastPathComponent();
//        String pathComponent2 = null;
//
//        if (lastPathComponent instanceof DefaultMutableTreeNode defaultMutableTreeNode) {
//            Object userObject = defaultMutableTreeNode.getUserObject();
//            if (userObject instanceof JGSbranchTreeNode jGSbranchTreeNode) {
//                Ref branch = jGSbranchTreeNode.getBranch();
//                pathComponent2 = JGSuiUtils.getINSTANCE().removeRefsHeads(branch.getName());
//            }
//        }
        String pathComponent2 = getSelectedTreeNode();
        Git git = jGSrepositoryModel.getGit();
        try {
            if (pathComponent2 == null) {
                showInfoDialog("Checkout not possible", "Checkout not possible");
                hideProgressBar();
                return;
            }

            //Userabfrage
            boolean userconfirmed = showConfirmDialog("Confirm checkout", "Checkout Branch " + pathComponent2 + " ?");
            if (!userconfirmed) {
                hideProgressBar();
                return;
            }
            switch (pathComponent1) {
                case LOCALSTRING -> {
                    logger.getLogger().info("checkoutLocalBranch: " + pathComponent2);
                    Ref checkoutLocalBranch = utils.checkoutLocalBranch(git, pathComponent2);
//                    showInfoDialog("onBranchesPanelClickedCheckout", checkoutLocalBranch.getName());
                    showInfoToast("Checkout success " + pathComponent2);
                    hideProgressBar();
                }

                case REMOTESTRING -> {
                    logger.getLogger().info("checkoutRemoteBranch: " + pathComponent2);
                    String remoteAndBranchName = null;
                    Ref checkoutRemoteBranch = utils.checkoutRemoteBranch(git, pathComponent2, remoteAndBranchName);
//                    showInfoDialog("onBranchesPanelClickedCheckout", checkoutRemoteBranch.getName());
                    showInfoToast("Checkout success " + pathComponent2);
                    hideProgressBar();
                }

                default -> {
                    logger.getLogger().severe("Selected path not valid!");
                    hideProgressBar();
                }
            }

        } catch (Exception ex) {
            logger.getLogger().log(Level.SEVERE, "onBranchesPanelClickedCheckout", ex);
            showErrorDialog("onBranchesPanelClickedCheckout", "checkoutBranch ERROR:\n" + ex.getMessage());
            hideProgressBar();
        }
    }

    @Override
    public void onBranchesPanelClickedMerge() {
        showProgressBar("onIJGSbranchesPanelMergeClicked");
//        String fileName = selectionPath.getLastPathComponent().toString();
        String pathComponent2 = getSelectedTreeNode();
        Git git = jGSrepositoryModel.getGit();
        try {
            String branchName = jGSrepositoryModel.getBranchName();
            if (pathComponent2 == null) {
                showInfoDialog("Merge not possible", "Merge not possible");
                hideProgressBar();
                return;
            }

            //Userabfrage
            boolean userconfirmed = showConfirmDialog("Confirm merge", "Merge Branch " + pathComponent2 + " into " + branchName + " ?");
            if (!userconfirmed) {
                hideProgressBar();
                return;
            }
            logger.getLogger().info("mergeIntoCurrentBranch: " + pathComponent2);
            MergeResult result = utils.mergeIntoCurrentBranch(git, pathComponent2);
//            showInfoDialog("onBranchesPanelClickedMerge", result.getMergeStatus().toString());
            showInfoToast("Merge success " + pathComponent2);
            //show merge result details
            showMergeResult("Merge Branch " + pathComponent2 + " into " + branchName, result);
            hideProgressBar();
        } catch (Exception ex) {
            logger.getLogger().log(Level.SEVERE, "onBranchesPanelClickedMerge", ex);
            showErrorDialog("onIJGSbranchesPanelMergeClicked", "mergeIntoCurrentBranch ERROR:\n" + ex.getMessage());
            hideProgressBar();
        }
    }

    @Override
    public void onBranchesPanelClickedDelete() {
        showProgressBar("onIJGSbranchesPanelDeleteClicked");
//        String fileName = selectionPath.getLastPathComponent().toString();
        String pathComponent2 = getSelectedTreeNode();

        Git git = jGSrepositoryModel.getGit();

        try {
            if (pathComponent2 == null) {
                showInfoDialog("Delete not possible", "Delete not possible");
                hideProgressBar();
                return;
            }

            //Userabfrage
            boolean userconfirmed = showConfirmDialog("Confirm delete", "Delete Branch " + pathComponent2 + " ?");
            if (!userconfirmed) {
                hideProgressBar();
                return;
            }
            logger.getLogger().info("deleteBranch: " + pathComponent2);
            List<String> result = utils.deleteBranch(git, pathComponent2);
//            showInfoDialog("onIJGSbranchesPanelDeleteClicked", result.toString());
            showInfoToast("Delete success " + pathComponent2);
        } catch (Exception ex) {
            logger.getLogger().log(Level.SEVERE, "onBranchesPanelClickedDelete", ex);
            showErrorDialog("onIJGSbranchesPanelDeleteClicked", "deleteBranch ERROR:\n" + ex.getMessage());
        }
        hideProgressBar();
    }

    @Override
    public void onBranchesPanelTreeSelectionChanged(TreePath selectionPath) {
        this.selectionPath = selectionPath;
        if (selectionPath != null) {
            if (((DefaultMutableTreeNode) selectionPath.getLastPathComponent()).isLeaf()) {
                panel.enableBranchButtos();
            } else {
                panel.disableBranchButtos();
            }
            String fileName = selectionPath.getLastPathComponent().toString();
            logger.getLogger().log(Level.FINE, "Tree selected: {0}", fileName);
            System.out.println("Tree selected: " + fileName);
        } else {
            logger.getLogger().log(Level.FINE, "selectionPath is null");
        }
    }

    @Override
    public void updateWidgets(IJGScallbackRefresh refresh) {
        //chain only independent methods here
        new Thread(() -> {
            try {
                List<Ref> localBranches = getLocalBranches();
                List<Ref> remoteBranches = getRemoteBranches();
                String branchName = getBranchName();

                Map<Ref, BranchTrackingStatus> mapLocalBranches = getMapLocalBranches(localBranches);
                panel.updateBranchTree(mapLocalBranches, remoteBranches, branchName, doNothingChainCallback());
            } catch (Exception e) {
                logger.getLogger().log(Level.SEVERE, "updateWidgets", e);
            }
            refresh.finish();
        }).start();
    }

    private Map<Ref, BranchTrackingStatus> getMapLocalBranches(List<Ref> localBranches) throws Exception {
        Map<Ref, BranchTrackingStatus> mapLocalBranches = new LinkedHashMap<>();
        for (Ref localBranch : localBranches) {
            String branchname = localBranch.getName();
            BranchTrackingStatus branchTrackingStatus = jGSrepositoryModel.getBranchTrackingStatus(branchname);
            mapLocalBranches.put(localBranch, branchTrackingStatus);
        }
        return mapLocalBranches;
    }

    private String getBranchName() {
        String branchName = null;
        try {
            branchName = jGSrepositoryModel.getBranchName();
        } catch (Exception ex) {
            logger.getLogger().log(Level.SEVERE, "getBranchName", ex);
        }
        return branchName;
    }

    private List<Ref> getLocalBranches() {
        showProgressBar("getLocalBranches");

        try {
            List<Ref> localBranches = jGSrepositoryModel.getLocalBranches();
            return localBranches;
        } catch (Exception ex) {
            logger.getLogger().log(Level.SEVERE, "getLocalBranches", ex);
            showErrorDialog("updateBranchTree", "getLocalBranches ERROR:\n" + ex.getMessage());
        }
        return null;
    }

    private List<Ref> getRemoteBranches() {
        showProgressBar("getRemoteBranches");

        try {
            List<Ref> remoteBranches = jGSrepositoryModel.getRemoteBranches();
            return remoteBranches;
        } catch (Exception ex) {
            logger.getLogger().log(Level.SEVERE, "getRemoteBranches", ex);
        }
        return null;

    }

    @Override
    public void deconstruct() {
        String className = this.getClass().getName();
        System.out.println(className + " deconstruct");
        panel = null;
//        bc.removeReceiver(this);
        super.deconstruct();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            // Cleanup operations
            String className = this.getClass().getName();
            System.out.println(className + " finalize");

        } finally {
            super.finalize();
        }
    }

}
