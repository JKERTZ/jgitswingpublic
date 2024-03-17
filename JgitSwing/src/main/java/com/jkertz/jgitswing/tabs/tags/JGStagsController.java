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

import com.jkertz.jgitswing.callback.IJGScallbackDirConfigInfoMap;
import com.jkertz.jgitswing.callback.IJGScallbackListJGStags;
import com.jkertz.jgitswing.callback.IJGScallbackListRefCommit;
import com.jkertz.jgitswing.callback.IJGScallbackRef;
import com.jkertz.jgitswing.callback.IJGScallbackRefresh;
import com.jkertz.jgitswing.model.JGSrepositoryModel;
import com.jkertz.jgitswing.model.JGStag;
import com.jkertz.jgitswing.tabs.common.IJGScommonController;
import com.jkertz.jgitswing.tabs.common.JGScommonController;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

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

    public void onIJGSbcRefsChanged() {
        logger.getLogger().fine("onIJGSbcRefsChanged");
        refresh();
    }

    @Override
    public void onTagsToolbarClickedShow5() {
        logger.getLogger().fine("onTagsToolbarClickedShow5");
        getJGStags(5, () -> {
            hideProgressBar();
        });
    }

    @Override
    public void onTagsToolbarClickedShow100() {
        logger.getLogger().fine("onTagsToolbarClickedShow100");
        getJGStags(100, () -> {
            hideProgressBar();
        });
    }

    @Override
    public void onTagsToolbarClickedShowAll() {
        logger.getLogger().fine("onTagsToolbarClickedShowAll");
        getJGStags(Integer.MAX_VALUE, () -> {
            hideProgressBar();
        });
    }

    @Override
    public void onTagsToolbarClickedPushTags() {
        logger.getLogger().fine("onTagsToolbarClickedPushTags");
        showErrorDialog("onTagsToolbarClickedPushTags", "Not supported yet.");
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
        if (!userOperationInProgress) {
            userOperationInProgress = true;
            showProgressBar("onStagingPanelClickedCommit");
//            bc.getConfigInfo(getConfigInfoCallback(() -> {
//                userOperationInProgress = false;
//                hideProgressBar();
//                refresh();
//            }));
            try {
                Map<String, Map<String, Map<String, String>>> configInfo = jGSrepositoryModel.getConfigInfo();
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.getLogger().severe("onTagsHistoryToolbarClickedCreateTag");
            }
        } else {
            logger.getLogger().info("onTagsHistoryToolbarClickedCreateTag ignored, userOperationInProgress");
        }
    }

    @Override
    public void onTagsHistoryToolbarClickedShow5() {
        logger.getLogger().fine("onTagsHistoryToolbarClickedShow5");
        getCommits(5, () -> {
            hideProgressBar();
        });
    }

    @Override
    public void onTagsHistoryToolbarClickedShow100() {
        logger.getLogger().fine("onTagsHistoryToolbarClickedShow100");
        getCommits(100, () -> {
            hideProgressBar();
        });
    }

    @Override
    public void onTagsHistoryToolbarClickedShowAll() {
        logger.getLogger().fine("onTagsHistoryToolbarClickedShowAll");
        showProgressBar("getAllCommits");
        getAllCommits(() -> {
            hideProgressBar();
        });
    }

    @Override
    public void updateWidgets(IJGScallbackRefresh refresh) {
        //chain only independent methods here
        getCommits(5, () -> {
            getJGStags(5, refresh);
        });
    }

    private void getAllCommits(IJGScallbackRefresh refresh) {
        logger.getLogger().fine("getAllCommits");
        showProgressBar("getAllCommits");
//        bc.getAllCommits(updateHistoryTableCallback(refresh));
        try {
            Iterable<RevCommit> allCommits = jGSrepositoryModel.getAllCommits();
            panel.updateHistoryTable(allCommits, endOfChainCallback(refresh));
        } catch (Exception ex) {
            logger.getLogger().severe(ex.getMessage());
        }

    }

    private IJGScallbackListRefCommit updateHistoryTableCallback(IJGScallbackRefresh refresh) {
        IJGScallbackListRefCommit callback = new IJGScallbackListRefCommit() {
            @Override
            public void onSuccess(Iterable<RevCommit> result) {
                panel.updateHistoryTable(result, endOfChainCallback(refresh));
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
                showErrorDialog("getAllCommits", "getAllCommits ERROR:\n" + ex.getMessage());
                refresh.finish();
            }
        };
        return callback;
    }

    private void getCommits(Integer amount, IJGScallbackRefresh refresh) {
        logger.getLogger().fine("getCommits: " + amount);
        showProgressBar("getCommits " + amount);
//        bc.getCommits(amount, limitedCommitsCallback(refresh));

        new Thread(() -> {
            try {
                Iterable<RevCommit> commits = jGSrepositoryModel.getCommits(amount);
                panel.updateHistoryTable(commits, endOfChainCallback(refresh));
            } catch (NoHeadException nhw) {
                logger.getLogger().info(nhw.getMessage());
                showErrorDialog("NoHeadException", "This repository has no commits yet, please create an initial commit");
            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, "getCommits", ex);
            }
        }).start();

    }

    private IJGScallbackListRefCommit limitedCommitsCallback(IJGScallbackRefresh refresh) {
        IJGScallbackListRefCommit callback = new IJGScallbackListRefCommit() {
            @Override
            public void onSuccess(Iterable<RevCommit> result) {
                panel.updateHistoryTable(result, endOfChainCallback(refresh));
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
                showErrorDialog("limitedCommitsCallback", "getCommits ERROR:\n" + ex.getMessage());
                refresh.finish();
            }
        };
        return callback;
    }

    private void getJGStags(int amount, IJGScallbackRefresh refresh) {
        logger.getLogger().fine("getJGStags: " + amount);
        showProgressBar("getJGStags " + amount);
//        bc.getJGStags(amount, limitedJGStagsCallback(refresh));
        new Thread(() -> {
            try {
                List<JGStag> jgStags = jGSrepositoryModel.getJGStags(amount);
                panel.updateTagTable(jgStags, endOfChainCallback(refresh));
            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, "getJGStags", ex);
            }
        }).start();
    }

    private IJGScallbackListJGStags limitedJGStagsCallback(IJGScallbackRefresh refresh) {
        IJGScallbackListJGStags callback = new IJGScallbackListJGStags() {
            @Override
            public void onSuccess(List<JGStag> result) {
                panel.updateTagTable(result, endOfChainCallback(refresh));
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
                showErrorDialog("limitedJGStagsCallback", "getJGStags ERROR:\n" + ex.getMessage());
                refresh.finish();
            }
        };

        return callback;

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
        new Thread(() -> {
            try {
                Ref tagCommit = jGSrepositoryModel.tagCommit(tagName, tagMessage, taggerName, taggerEmail, commit);
                refresh.finish();
            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, "createTag", ex);
            }
        }).start();

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

}
