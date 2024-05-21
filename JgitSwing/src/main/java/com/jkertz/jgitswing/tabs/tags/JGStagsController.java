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
package com.jkertz.jgitswing.tabs.tags;

import com.jkertz.jgitswing.businesslogic.JGSworker;
import com.jkertz.jgitswing.callback.IJGScallbackDirConfigInfoMap;
import com.jkertz.jgitswing.callback.IJGScallbackRef;
import com.jkertz.jgitswing.callback.IJGScallbackRefresh;
import com.jkertz.jgitswing.model.JGSrepositoryModel;
import com.jkertz.jgitswing.model.JGStag;
import com.jkertz.jgitswing.tablemodels.IterableRevCommitTableModel;
import com.jkertz.jgitswing.tablemodels.ListJGStagsTableModel;
import com.jkertz.jgitswing.tabs.common.IJGScommonController;
import com.jkertz.jgitswing.tabs.common.JGScommonController;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.SwingUtilities;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.PushResult;

/**
 *
 * @author jkertz
 */
public final class JGStagsController extends JGScommonController implements IJGStagsPanel, IJGScommonController {

    private JGStagsPanel panel;
    private String _commitId = null;

    public JGStagsController(JGSrepositoryModel jGSrepositoryModel) {
        super("Tags", jGSrepositoryModel);
        panel = new JGStagsPanel(this);
        setPanel(panel);
//        bc.addReceiver(this);
    }

    @Override
    public void onGitRefChanged() {
        //caused by commit
        logger.getLogger().fine("onGitRefChanged");
        refresh();
    }

    @Override
    public void onTagsToolbarClickedShow5() {
        logger.getLogger().fine("onTagsToolbarClickedShow5");
        getJGStags(5, () -> {
        });
    }

    @Override
    public void onTagsToolbarClickedShow100() {
        logger.getLogger().fine("onTagsToolbarClickedShow100");
        getJGStags(100, () -> {
        });
    }

    @Override
    public void onTagsToolbarClickedShowAll() {
        logger.getLogger().fine("onTagsToolbarClickedShowAll");
        getJGStags(Integer.MAX_VALUE, () -> {
        });
    }

    @Override
    public void onTagsToolbarClickedPushTags() {
        logger.getLogger().fine("onTagsToolbarClickedPushTags");
        //validate remote configuration
        if (!autoFixRemoteEditConfigInfo(true)) {
            return;
        }

        try {
            Git git = jGSrepositoryModel.getGit();
            Map<String, String> parameters = getUserPasswordParameters();
            Map<String, Boolean> options = getPushOptions();
            boolean showParameterMapDialog = jGSdialogFactory.showParameterMapDialog("Push", parameters, options, false);
            if (showParameterMapDialog) {
                String usernameInput = parameters.get("Username");
                String passwordInput = parameters.get("Password");
                boolean dryRun = options.get("dryrun");
                showProgressBar("PushTags", 0);
                Iterable<PushResult> pushTags = utils.pushTags(git, usernameInput, passwordInput, dryRun);
                showProgressBar("PushTags", 100);
                jGSdialogFactory.showPushResults("PushTags", pushTags);
            }
        } catch (Exception ex) {
            logger.getLogger().log(Level.SEVERE, "onTagsToolbarClickedPushTags", ex);
        }
    }

    @Override
    public void onHistoryTableWidgetSelectionChanged(String _commitId) {
        logger.getLogger().fine("onHistoryTableWidgetSelectionChanged");
        this._commitId = _commitId;
//        showErrorDialog("onHistoryTableWidgetSelectionChanged", "Not supported yet.");
    }

    @Override
    public void onTagsWidgetSelectionChanged() {
        logger.getLogger().fine("onTagsWidgetSelectionChanged");
        showErrorDialog("onTagsWidgetSelectionChanged", "Not supported yet.");
    }

    @Override
    public void onTagsHistoryToolbarClickedCreateTag() {
        logger.getLogger().fine("onTagsHistoryToolbarClickedCreateTag");
        try {
            if (_commitId != null && !_commitId.isEmpty()) {
                Git git = jGSrepositoryModel.getGit();

                Map<String, String> parameters = getCreateTagParameters();
                boolean showParameterMapDialog = jGSdialogFactory.showParameterMapDialog("CreateTag", parameters, false);
                if (showParameterMapDialog) {
                    String tagName = parameters.get("tagName");
                    String tagMessage = parameters.get("tagMessage");
                    String taggerName = parameters.get("taggerName");
                    String taggerEmail = parameters.get("taggerEmail");
                    showProgressBar("CreateTag", 0);
                    Ref createTag = utils.createTag(git, tagName, tagMessage, taggerName, taggerEmail, _commitId);
                    showProgressBar("CreateTag", 100);
                }
            } else {
                jGSdialogFactory.showErrorDialog("CreateTag", "no commit selected!");
            }
        } catch (Exception ex) {
            logger.getLogger().log(Level.SEVERE, "onTagsHistoryToolbarClickedCreateTag", ex);
        }
    }

    @Override
    public void onTagsHistoryToolbarClickedShow5() {
        logger.getLogger().fine("onTagsHistoryToolbarClickedShow5");
        getCommits(5, () -> {
        });
    }

    @Override
    public void onTagsHistoryToolbarClickedShow100() {
        logger.getLogger().fine("onTagsHistoryToolbarClickedShow100");
        getCommits(100, () -> {
        });
    }

    @Override
    public void onTagsHistoryToolbarClickedShowAll() {
        logger.getLogger().fine("onTagsHistoryToolbarClickedShowAll");
        getAllCommits(() -> {
        });
    }

    @Override
    public void updateWidgets(IJGScallbackRefresh refresh
    ) {
        //chain only independent methods here
        getCommits(5, () -> {
            getJGStags(5, refresh);
        });
    }

    private void getAllCommits(IJGScallbackRefresh refresh) {
        logger.getLogger().fine("getAllCommits");
//        bc.getAllCommits(updateHistoryTableCallback(refresh));
        try {
            showProgressBar("getAllCommits", 0);

            Iterable<RevCommit> allCommits = jGSrepositoryModel.getAllCommits();
            showProgressBar("getAllCommits", 30);
            IterableRevCommitTableModel tableModel = uiUtils.getTableModel(allCommits);
            SwingUtilities.invokeLater(() -> {
                showProgressBar("getAllCommits", 60);
                panel.updateHistoryTable(tableModel);
                showProgressBar("getAllCommits", 100);
            });

        } catch (Exception ex) {
            logger.getLogger().severe(ex.getMessage());
        }

    }

    private void getCommits(Integer amount, IJGScallbackRefresh refresh) {
        logger.getLogger().fine("getCommits: " + amount);
//        bc.getCommits(amount, limitedCommitsCallback(refresh));

        JGSworker.runOnWorkerThread(() -> {
            try {
                showProgressBar("getCommits " + amount, 0);
                Iterable<RevCommit> commits = jGSrepositoryModel.getCommits(amount);
                showProgressBar("getCommits " + amount, 30);
                IterableRevCommitTableModel tableModel = uiUtils.getTableModel(commits);
                SwingUtilities.invokeLater(() -> {
                    showProgressBar("getCommits " + amount, 60);
                    panel.updateHistoryTable(tableModel);
                    showProgressBar("getCommits " + amount, 100);
                });

            } catch (NoHeadException nhw) {
                logger.getLogger().info(nhw.getMessage());
                showErrorDialog("NoHeadException", "This repository has no commits yet, please create an initial commit");
            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, "getCommits", ex);
            }
        });

    }

    private void getJGStags(int amount, IJGScallbackRefresh refresh) {
        logger.getLogger().fine("getJGStags: " + amount);
//        bc.getJGStags(amount, limitedJGStagsCallback(refresh));
        JGSworker.runOnWorkerThread(() -> {
            try {
                showProgressBar("getJGStags " + amount, 0);
                List<JGStag> jgStags = jGSrepositoryModel.getJGStags(amount);
                showProgressBar("getJGStags " + amount, 25);
                ListJGStagsTableModel tableModelJGStags = uiUtils.getTableModelJGStags(jgStags);
                showProgressBar("getJGStags " + amount, 50);
                SwingUtilities.invokeLater(() -> {
                    showProgressBar("getJGStags " + amount, 75);
                    panel.updateTagTable(tableModelJGStags);
                    showProgressBar("getJGStags " + amount, 100);
                });
            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, "getJGStags", ex);
            }
        });
    }

    private void createTag(IJGScallbackRefresh refresh) {
        Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("tagName", null);
        parameters.put("tagMessage", null);
        parameters.put("taggerName", null);
        parameters.put("taggerEmail", null);
        boolean showParameterMapDialog = jGSdialogFactory.showParameterMapDialog("Commit", parameters, false);
        if (showParameterMapDialog) {
        }

    }

    private void createTag(Map<String, String> parameters, IJGScallbackRefresh refresh) {
        String tagName = parameters.get("tagName");
        String tagMessage = parameters.get("tagMessage");
        String taggerName = parameters.get("taggerName");
        String taggerEmail = parameters.get("taggerEmail");
        String commit = _commitId;
        //        bc.tagCommit(tagName, tagMessage, taggerName, taggerEmail, commit, createTagCallback(refresh));
        JGSworker.runOnWorkerThread(() -> {
            try {
                Ref tagCommit = jGSrepositoryModel.tagCommit(tagName, tagMessage, taggerName, taggerEmail, commit);
                refresh.finish();
            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, "createTag", ex);
            }
        });

    }

    private IJGScallbackRef createTagCallback(IJGScallbackRefresh refresh) {
        IJGScallbackRef callback = new IJGScallbackRef() {
            @Override
            public void onSuccess(Ref result) {
                getJGStags(5, refresh);
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
                showErrorDialog("createTagCallback", "tagCommit ERROR:\n" + ex.getMessage());
                refresh.finish();
            }
        };
        return callback;

    }

    private IJGScallbackDirConfigInfoMap getConfigInfoCallback(IJGScallbackRefresh refresh) {
        IJGScallbackDirConfigInfoMap callback = new IJGScallbackDirConfigInfoMap() {
            @Override
            public void onSuccess(Map<String, Map<String, Map<String, String>>> result) {
                Map<String, Map<String, Map<String, String>>> configInfoMap = result;
                Map<String, Map<String, String>> sectionUser = configInfoMap.get(ConfigConstants.CONFIG_USER_SECTION);
                Map<String, String> userMap = sectionUser.get(null);
                Map<String, String> parameters = new LinkedHashMap<>();
                String username = userMap.get(ConfigConstants.CONFIG_KEY_NAME);
                String email = userMap.get(ConfigConstants.CONFIG_KEY_EMAIL);
                parameters.put("tagName", null);
                parameters.put("tagMessage", null);
                parameters.put("taggerName", username);
                parameters.put("taggerEmail", email);
//                parameters.put("taggerName", null);
//                parameters.put("taggerEmail", null);
                boolean showParameterMapDialog = jGSdialogFactory.showParameterMapDialog("Tag", parameters, false);
                if (showParameterMapDialog) {
                    createTag(parameters, refresh);
                } else {
                    refresh.finish();
                }
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
                showErrorDialog("onTagsHistoryToolbarClickedCreateTag", "getConfigInfo ERROR:\n" + ex.getMessage());
                refresh.finish();
            }
        };
        return callback;
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

    private Map<String, String> getCreateTagParameters() throws Exception {

        try {
            Map<String, Map<String, Map<String, String>>> configInfoMap = jGSrepositoryModel.getConfigInfo();

            Map<String, Map<String, String>> sectionUser = configInfoMap.get(ConfigConstants.CONFIG_USER_SECTION);
            Map<String, String> userMap = sectionUser.get(null);

            String username = userMap.get(ConfigConstants.CONFIG_KEY_NAME);
            String email = userMap.get(ConfigConstants.CONFIG_KEY_EMAIL);

            Map<String, String> parameters = new LinkedHashMap<>();

            String tagName = null;
            String tagMessage = null;
            String taggerName = username;
            String taggerEmail = email;

            parameters.put("tagName", tagName);
            parameters.put("tagMessage", tagMessage);
            parameters.put("taggerName", taggerName);
            parameters.put("taggerEmail", taggerEmail);
            return parameters;
        } catch (Exception ex) {
            logger.getLogger().log(Level.SEVERE, "getCreateTagParameters", ex);
            throw ex;
        }

    }

}
