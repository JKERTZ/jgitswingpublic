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
package com.jkertz.jgitswing.tabs.staging;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import com.jkertz.jgitswing.callback.IJGScallbackDirCache;
import com.jkertz.jgitswing.callback.IJGScallbackListDirCache;
import com.jkertz.jgitswing.callback.IJGScallbackListRef;
import com.jkertz.jgitswing.callback.IJGScallbackRef;
import com.jkertz.jgitswing.callback.IJGScallbackRefCommit;
import com.jkertz.jgitswing.callback.IJGScallbackRefresh;
import com.jkertz.jgitswing.callback.IJGScallbackStatus;
import com.jkertz.jgitswing.dialogs.JGSParameterMapDialog;
import com.jkertz.jgitswing.model.JGSrepositoryModel;
import com.jkertz.jgitswing.tabs.common.IJGScommonController;
import com.jkertz.jgitswing.tabs.common.JGScommonController;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 *
 * @author jkertz
 */
public final class JGSstagingController extends JGScommonController implements IJGSstagingPanel, IJGScommonController {

    private JGSstagingPanel panel;
    private List<String> unstagedSelectionList;
    private List<String> stagedSelectionList;

    public JGSstagingController(JGSrepositoryModel jGSrepositoryModel) {
        super("Staging", jGSrepositoryModel);
        panel = new JGSstagingPanel(this);
        setPanel(panel);
    }

    @Override
    public void onGitIndexChanged() {
        System.out.println("JGSstagingController onGitIndexChanged");
        logger.getLogger().fine("onGitIndexChanged");
        updateWidgets(refreshCallback());
    }

    @Override
    public void updateWidgets(IJGScallbackRefresh refresh) {
        new Thread(() -> {
            try {
                //chain only independent methods here
                Status status = jGSrepositoryModel.getStatus();
                panel.updateStagedTable(status, doNothingChainCallback());
                panel.updateUnstagedTable(status, endOfChainCallback(refresh));

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
            showProgressBar("onIJGSstagingPanelStageClicked");
            Git git = jGSrepositoryModel.getGit();

            try {
                List<DirCache> stageSelectedTable = utils.stageSelectedTable(git, unstagedSelectionList);
                updateTables();
            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, "onStagingPanelClickedStage", ex);
            }
            userOperationInProgress = false;
            hideProgressBar();

        } else {
            logger.getLogger().info("onStagingPanelClickedStage ignored, userOperationInProgress");
        }
    }

    @Override
    public void onStagingPanelClickedRemove() {
        logger.getLogger().fine("onStagingPanelClickedRemove");
        if (!userOperationInProgress) {
            userOperationInProgress = true;
            showProgressBar("onIJGSstagingPanelRemoveClicked");
            Git git = jGSrepositoryModel.getGit();

            try {
                List<DirCache> removeSelectedTable = utils.removeSelectedTable(git, unstagedSelectionList);
                updateTables();
            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, "onStagingPanelClickedRemove", ex);
            }
            userOperationInProgress = false;
            hideProgressBar();

        } else {
            logger.getLogger().info("onStagingPanelClickedRemove ignored, userOperationInProgress");
        }
    }

    @Override
    public void onStagingPanelClickedStageAll() {
        logger.getLogger().fine("onStagingPanelClickedStageAll");
        if (!userOperationInProgress) {
            userOperationInProgress = true;
            showProgressBar("onIJGSstagingPanelStageAllClicked");
            Git git = jGSrepositoryModel.getGit();

            try {
                DirCache stageAll = utils.stageAll(git);
                updateTables();

            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, "onStagingPanelClickedStageAll", ex);
            }
            userOperationInProgress = false;
            hideProgressBar();

        } else {
            logger.getLogger().info("onStagingPanelClickedStageAll ignored, userOperationInProgress");
        }
    }

    @Override
    public void onStagingPanelClickedHardReset() {
        logger.getLogger().fine("onStagingPanelClickedHardReset");
        if (!userOperationInProgress) {
            userOperationInProgress = true;
            showProgressBar("onIJGSstagingPanelHardResetClicked");
            boolean showConfirmDialog = showConfirmDialog("onStagingPanelClickedHardReset", "All changes will be reverted. Do hard reset?");
            if (showConfirmDialog) {
                Git git = jGSrepositoryModel.getGit();

                try {
                    Ref resetHard = utils.resetHard(git);
                    updateTables();
                } catch (Exception ex) {
                    logger.getLogger().log(Level.SEVERE, "onStagingPanelClickedHardReset", ex);
                }
            }
            userOperationInProgress = false;
            hideProgressBar();
        } else {
            logger.getLogger().info("onStagingPanelClickedStageAll ignored, userOperationInProgress");
        }
    }

    @Override
    public void onStagingPanelClickedUnstage() {
        logger.getLogger().fine("onStagingPanelClickedUnstage");
        if (!userOperationInProgress) {
            userOperationInProgress = true;
            showProgressBar("onStagingPanelClickedUnstage");
            Git git = jGSrepositoryModel.getGit();

            try {
                List<Ref> unstageSelectedTable = utils.unstageSelectedTable(git, stagedSelectionList);
                updateTables();
            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, "onStagingPanelClickedUnstage", ex);
            }
            userOperationInProgress = false;
            hideProgressBar();

        } else {
            logger.getLogger().info("onStagingPanelClickedUnstage ignored, userOperationInProgress");
        }
    }

    @Override
    public void onStagingPanelClickedResetFile() {
        logger.getLogger().fine("onStagingPanelClickedResetFile");
        if (!userOperationInProgress) {
            userOperationInProgress = true;
            showProgressBar("onStagingPanelClickedResetFile");
            boolean showConfirmDialog = showConfirmDialog("onStagingPanelClickedResetFile", "All changes of: " + unstagedSelectionList + " will be reverted. Do file reset?");
            if (showConfirmDialog) {
                Git git = jGSrepositoryModel.getGit();
                try {
                    List<Ref> resetFile = utils.resetFile(git, unstagedSelectionList);
                    updateTables();
                } catch (Exception ex) {
                    logger.getLogger().log(Level.SEVERE, "onStagingPanelClickedResetFile", ex);
                }
            }
            userOperationInProgress = false;
            hideProgressBar();
        } else {
            logger.getLogger().info("onStagingPanelClickedResetFile ignored, userOperationInProgress");
        }
    }

    @Override
    public void onStagingPanelClickedUnstageAll() {
        logger.getLogger().fine("onStagingPanelClickedUnstageAll");
        if (!userOperationInProgress) {
            userOperationInProgress = true;
            showProgressBar("onStagingPanelClickedUnstageAll");
            Git git = jGSrepositoryModel.getGit();

            try {
                Ref unstageAll = utils.unstageAll(git);
                updateTables();
            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, "onStagingPanelClickedUnstageAll", ex);
            }
            userOperationInProgress = false;
            hideProgressBar();

        } else {
            logger.getLogger().info("onStagingPanelClickedUnstageAll ignored, userOperationInProgress");
        }
    }

    @Override
    public void onStagingPanelClickedCommit() {
        logger.getLogger().fine("onStagingPanelClickedCommit");
        if (!userOperationInProgress) {
            userOperationInProgress = true;
            showProgressBar("onStagingPanelClickedCommit");

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
                boolean showParameterMapDialog = new JGSParameterMapDialog().show("Commit", parameters, false);
                if (showParameterMapDialog) {
                    commit(parameters);
                } else {
                }
                updateTables();

            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, "onStagingPanelClickedCommit", ex);
            }
            userOperationInProgress = false;
            hideProgressBar();

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
        utils.commit(git, userInput, emailInput, messageInput);
    }

    private IJGScallbackRefCommit commitCallback(IJGScallbackRefresh refresh) {
        IJGScallbackRefCommit callback = new IJGScallbackRefCommit() {
            @Override
            public void onSuccess(RevCommit result) {
                refresh.finish();
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
                showErrorDialog("onStagingPanelClickedCommit", "commit ERROR:\n" + ex.getMessage());
                refresh.finish();
            }
        };
        return callback;
    }

    private IJGScallbackStatus updateTablesCallback(IJGScallbackRefresh refresh) {
        IJGScallbackStatus callback = new IJGScallbackStatus() {
            @Override
            public void onSuccess(Status result) {
                panel.updateStagedTable(result, endOfChainCallback(refresh));
                panel.updateUnstagedTable(result, endOfChainCallback(refresh));
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
                showErrorDialog("updateTables", "getStatus ERROR:\n" + ex.getMessage());
                refresh.finish();
            }
        };
        return callback;
    }

    private IJGScallbackListDirCache stagingChangedCallback(IJGScallbackRefresh refresh) {
        IJGScallbackListDirCache callback = new IJGScallbackListDirCache() {
            @Override
            public void onSuccess(List<DirCache> result) {
                refresh.finish();
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
                showErrorDialog("stagingChangedCallback", "stagingChangedCallback ERROR:\n" + ex.getMessage());
                refresh.finish();
            }
        };
        return callback;
    }

    private IJGScallbackDirCache stageAllCallback(IJGScallbackRefresh refresh) {
        IJGScallbackDirCache callback = new IJGScallbackDirCache() {
            @Override
            public void onSuccess(DirCache result) {
                refresh.finish();
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
                showErrorDialog("onStagingPanelClickedStageAll", "stageAll ERROR:\n" + ex.getMessage());
                refresh.finish();
            }

        };
        return callback;
    }

    private IJGScallbackRef resetHardCallback(IJGScallbackRefresh refresh) {
        IJGScallbackRef callback = new IJGScallbackRef() {
            @Override
            public void onSuccess(Ref result) {
                refresh.finish();
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
                showErrorDialog("onStagingPanelClickedHardReset", "resetHard ERROR:\n" + ex.getMessage());
                refresh.finish();
            }
        };
        return callback;
    }

    private IJGScallbackListRef resetFileCallback(IJGScallbackRefresh refresh) {
        IJGScallbackListRef callback = new IJGScallbackListRef() {
            @Override
            public void onSuccess(List<Ref> result) {
                refresh.finish();
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
                showErrorDialog("resetFileCallback", "resetFileCallback ERROR:\n" + ex.getMessage());
                refresh.finish();
            }
        };
        return callback;
    }

    private IJGScallbackRef unstageAllCallback(IJGScallbackRefresh refresh) {
        IJGScallbackRef callback = new IJGScallbackRef() {
            @Override
            public void onSuccess(Ref result) {
                refresh.finish();
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
                showErrorDialog("onStagingPanelClickedUnstageAll", "unstageAll ERROR:\n" + ex.getMessage());
                refresh.finish();
            }
        };
        return callback;
    }

    private void updateTables() {
        try {
            Status status = jGSrepositoryModel.getStatus();
            panel.updateStagedTable(status, doNothingChainCallback());
            panel.updateUnstagedTable(status, doNothingChainCallback());
        } catch (Exception ex) {
            logger.getLogger().log(Level.SEVERE, "updateTables", ex);
        }

    }

    @Override
    public void onUnstagedListSelectionChanged(List<String> selectionList) {
        this.unstagedSelectionList = selectionList;
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
