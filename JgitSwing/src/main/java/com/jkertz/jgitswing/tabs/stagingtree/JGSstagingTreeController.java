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
package com.jkertz.jgitswing.tabs.stagingtree;

import com.jkertz.jgitswing.callback.IJGScallbackRefresh;
import com.jkertz.jgitswing.model.JGSrepositoryModel;
import com.jkertz.jgitswing.tabs.common.IJGScommonController;
import com.jkertz.jgitswing.tabs.common.JGScommonController;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.tree.DefaultTreeModel;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Ref;

/**
 *
 * @author jkertz
 */
public final class JGSstagingTreeController extends JGScommonController implements IJGSstagingTreePanel, IJGScommonController, IJGSstagingTreeModel {

    private JGSstagingTreePanel panel;
    private List<String> unstagedSelectionList;
    private List<String> stagedSelectionList;

    public JGSstagingTreeController(JGSrepositoryModel jGSrepositoryModel) {
        super("StagingTree", jGSrepositoryModel);
        panel = new JGSstagingTreePanel(this);
        setPanel(panel);
    }

    @Override
    public void onGitIndexChanged() {
        System.out.println("JGSstagingController onGitIndexChanged");
        logger.getLogger().fine("onGitIndexChanged");
        updateWidgets(refreshCallback());
    }

    @Override
    public void onGitRefChanged() {
        //caused by commit, pull
        logger.getLogger().fine("onGitRefChanged");
        refresh();
    }

    @Override
    public void updateWidgets(IJGScallbackRefresh refresh) {
        new Thread(() -> {
            try {
                //chain only independent methods here
                Status status = jGSrepositoryModel.getStatus();
                DefaultTreeModel dtmStaged = uiUtils.buildTreeModelStaged(status);
                DefaultTreeModel dtmUnstaged = uiUtils.buildTreeModelUnstaged(status);
                panel.updateStagingTrees(dtmStaged, dtmUnstaged);

            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, "updateWidgets", ex);
            }
            refresh.finish();

        }).start();
    }

    @Override
    public void onStagingPanelClickedStage() {
        logger.getLogger().fine("onStagingPanelClickedStage");
        if (!userOperationInProgress) {
            userOperationInProgress = true;
            Git git = jGSrepositoryModel.getGit();

            try {
                showProgressBar("stageSelectedTable", 0);
                List<DirCache> stageSelectedTable = utils.stageSelectedTable(git, unstagedSelectionList);
                showProgressBar("stageSelectedTable", 100);
                updateTables();
            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, "onStagingPanelClickedStage", ex);
            }
            userOperationInProgress = false;

        } else {
            logger.getLogger().info("onStagingPanelClickedStage ignored, userOperationInProgress");
        }
    }

    @Override
    public void onStagingPanelClickedRemove() {
        logger.getLogger().fine("onStagingPanelClickedRemove");
        if (!userOperationInProgress) {
            userOperationInProgress = true;
            Git git = jGSrepositoryModel.getGit();

            try {
                showProgressBar("removeSelectedTable", 0);
                List<DirCache> removeSelectedTable = utils.removeSelectedTable(git, unstagedSelectionList);
                showProgressBar("removeSelectedTable", 100);
                updateTables();
            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, "onStagingPanelClickedRemove", ex);
            }
            userOperationInProgress = false;

        } else {
            logger.getLogger().info("onStagingPanelClickedRemove ignored, userOperationInProgress");
        }
    }

    @Override
    public void onStagingPanelClickedStageAll() {
        logger.getLogger().fine("onStagingPanelClickedStageAll");
        if (!userOperationInProgress) {
            userOperationInProgress = true;
            Git git = jGSrepositoryModel.getGit();

            try {
                showProgressBar("stageAll", 0);
                DirCache stageAll = utils.stageAll(git);
                showProgressBar("stageAll", 100);
                updateTables();
            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, "onStagingPanelClickedStageAll", ex);
            }
            userOperationInProgress = false;
        } else {
            logger.getLogger().info("onStagingPanelClickedStageAll ignored, userOperationInProgress");
        }
    }

    @Override
    public void onStagingPanelClickedHardReset() {
        logger.getLogger().fine("onStagingPanelClickedHardReset");
        if (!userOperationInProgress) {
            userOperationInProgress = true;
            boolean showConfirmDialog = showConfirmDialog("onStagingPanelClickedHardReset", "All changes will be reverted. Do hard reset?");
            if (showConfirmDialog) {
                Git git = jGSrepositoryModel.getGit();

                try {
                    showProgressBar("resetHard", 0);
                    Ref resetHard = utils.resetHard(git);
                    showProgressBar("resetHard", 100);
                    updateTables();
                } catch (Exception ex) {
                    logger.getLogger().log(Level.SEVERE, "onStagingPanelClickedHardReset", ex);
                }
            }
            userOperationInProgress = false;
        } else {
            logger.getLogger().info("onStagingPanelClickedStageAll ignored, userOperationInProgress");
        }
    }

    @Override
    public void onStagingPanelClickedUnstage() {
        logger.getLogger().fine("onStagingPanelClickedUnstage");
        if (!userOperationInProgress) {
            userOperationInProgress = true;
            Git git = jGSrepositoryModel.getGit();

            try {
                showProgressBar("unstageSelectedTable", 0);
                List<Ref> unstageSelectedTable = utils.unstageSelectedTable(git, stagedSelectionList);
                showProgressBar("unstageSelectedTable", 100);
                updateTables();
            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, "onStagingPanelClickedUnstage", ex);
            }
            userOperationInProgress = false;

        } else {
            logger.getLogger().info("onStagingPanelClickedUnstage ignored, userOperationInProgress");
        }
    }

    @Override
    public void onStagingPanelClickedResetFile() {
        logger.getLogger().fine("onStagingPanelClickedResetFile");
        if (!userOperationInProgress) {
            userOperationInProgress = true;
            boolean showConfirmDialog = showConfirmDialog("onStagingPanelClickedResetFile", "All changes of: " + unstagedSelectionList + " will be reverted. Do file reset?");
            if (showConfirmDialog) {
                Git git = jGSrepositoryModel.getGit();
                try {
                    showProgressBar("resetFile", 0);
                    List<Ref> resetFile = utils.resetFile(git, unstagedSelectionList);
                    showProgressBar("resetFile", 100);
                    updateTables();
                } catch (Exception ex) {
                    logger.getLogger().log(Level.SEVERE, "onStagingPanelClickedResetFile", ex);
                }
            }
            userOperationInProgress = false;
        } else {
            logger.getLogger().info("onStagingPanelClickedResetFile ignored, userOperationInProgress");
        }
    }

    @Override
    public void onStagingPanelClickedUnstageAll() {
        logger.getLogger().fine("onStagingPanelClickedUnstageAll");
        if (!userOperationInProgress) {
            userOperationInProgress = true;
            Git git = jGSrepositoryModel.getGit();

            try {
                showProgressBar("unstageAll", 0);
                Ref unstageAll = utils.unstageAll(git);
                showProgressBar("unstageAll", 100);
                updateTables();
            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, "onStagingPanelClickedUnstageAll", ex);
            }
            userOperationInProgress = false;

        } else {
            logger.getLogger().info("onStagingPanelClickedUnstageAll ignored, userOperationInProgress");
        }
    }

    @Override
    public void onStagingPanelClickedCommit() {
        logger.getLogger().fine("onStagingPanelClickedCommit");
        if (!userOperationInProgress) {
            userOperationInProgress = true;

            try {
                Map<String, Map<String, Map<String, String>>> configInfoMap = jGSrepositoryModel.getConfigInfo();

                Map<String, Map<String, String>> sectionUser = configInfoMap.get(ConfigConstants.CONFIG_USER_SECTION);
                Map<String, String> userMap = sectionUser.get(null);
                Map<String, String> parameters = new LinkedHashMap<>();
                String username = userMap.get(ConfigConstants.CONFIG_KEY_NAME);
                String email = userMap.get(ConfigConstants.CONFIG_KEY_EMAIL);
                parameters.put("User", username);
                parameters.put("Email", email);
                parameters.put("CommitMessage", null);
                boolean showParameterMapDialog = jGSdialogFactory.showParameterMapDialog("Commit", parameters, false);
                if (showParameterMapDialog) {
                    commit(parameters);
                } else {
                }
                updateTables();

            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, "onStagingPanelClickedCommit", ex);
            }
            userOperationInProgress = false;

        } else {
            logger.getLogger().info("onStagingPanelClickedUnstageAll ignored, userOperationInProgress");
        }
    }

//    @Override
//    public void onIJGSbcIndexChanged() {
//        logger.getLogger().fine("onIJGSbcIndexChanged");
//        refresh();
//    }
//    @Override
//    public void onIJGSbcRefsChanged() {
//        logger.getLogger().fine("onIJGSbcRefsChanged");
//        refresh();
//    }
    private void commit(Map<String, String> parameters) throws Exception {
        String userInput = parameters.get("User");
        String emailInput = parameters.get("Email");
        String messageInput = parameters.get("CommitMessage");
        Git git = jGSrepositoryModel.getGit();
        showProgressBar("commit", 0);
        utils.commit(git, userInput, emailInput, messageInput);
        showProgressBar("commit", 100);
    }

    private void updateTables() {
        try {
            Status status = jGSrepositoryModel.getStatus();
            DefaultTreeModel dtmStaged = uiUtils.buildTreeModelStaged(status);
            DefaultTreeModel dtmUnstaged = uiUtils.buildTreeModelUnstaged(status);
            SwingUtilities.invokeLater(() -> {
                panel.updateStagingTrees(dtmStaged, dtmUnstaged);
            });
        } catch (Exception ex) {
            logger.getLogger().log(Level.SEVERE, "updateTables", ex);
        }
    }

    private void updateCurrentDiffFile(String path, IJGScallbackRefresh refresh) {
        try {
            String currentDiffFile = "";
            if (path != null && !path.isEmpty()) {
                currentDiffFile = jGSrepositoryModel.getCurrentDiffFile(path);
            }
            showProgressBar("commit", 0);

            DefaultStyledDocument doc = uiUtils.buildStyledDocumentFromFileDiff(currentDiffFile);
            showProgressBar("commit", 50);
            SwingUtilities.invokeLater(() -> {
                panel.updateCurrentfile(doc);
            });

            showProgressBar("commit", 100);

        } catch (Exception ex) {
            logger.getLogger().log(Level.SEVERE, "updateCurrentDiffFile", ex);
        }
    }

    @Override
    public void onUnstagedListSelectionChanged(List<String> selectionList) {
        this.unstagedSelectionList = selectionList;
        System.out.println("onUnstagedListSelectionChanged");
        for (String selection : selectionList) {
            System.out.println(selection);
        }
        if (selectionList != null && !selectionList.isEmpty()) {
            String path = selectionList.get(0);
            updateCurrentDiffFile(path, refreshCallback());
        } else {
            updateCurrentDiffFile(null, refreshCallback());
        }

    }

    @Override
    public void onStagedListSelectionChanged(List<String> selectionList) {
        this.stagedSelectionList = selectionList;
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
