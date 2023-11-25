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
package com.jkertz.jgitswing.tabs.history;

import java.util.List;
import java.util.logging.Level;
import com.jkertz.jgitswing.callback.IJGScallbackListDiffEntry;
import com.jkertz.jgitswing.callback.IJGScallbackListRefCommit;
import com.jkertz.jgitswing.callback.IJGScallbackRefresh;
import com.jkertz.jgitswing.callback.IJGScallbackString;
import com.jkertz.jgitswing.model.JGSrepositoryModel;
import com.jkertz.jgitswing.tabs.common.IJGScommonController;
import com.jkertz.jgitswing.tabs.common.JGScommonController;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 *
 * @author jkertz
 */
public final class JGShistoryController extends JGScommonController implements IJGShistoryPanel, IJGScommonController {

    private JGShistoryPanel panel;
    private String _commitId = null;

    public JGShistoryController(JGSrepositoryModel jGSrepositoryModel) {
        super("History", jGSrepositoryModel);
        panel = new JGShistoryPanel(this);
        setPanel(panel);
    }

    @Override
    public void updateWidgets(IJGScallbackRefresh refresh) {
        //chain only independent methods here
        updateHistoryTable(5, refresh);
    }

    @Override
    public void onHistoryPanelClickedShow100(String text) {
        if (text == null || text.isEmpty()) {
            updateHistoryTable(100, refreshCallback());
        } else {
            filterHistory(100, text);
        }
    }

    @Override
    public void onHistoryPanelClickedShowAll(String text) {
        if (text == null || text.isEmpty()) {
            updateHistoryTableAll(refreshCallback());
        } else {
            filterHistory(Integer.MAX_VALUE, text);
        }
    }

    @Override
    public void onCommonToolbarClickedRefresh() {
        refresh();
    }

    @Override
    public void onGitRefChanged() {
        //caused by commit
        logger.getLogger().fine("onGitRefChanged");
        refresh();
    }

//    @Override
//    public void onIJGSbcRefsChanged() {
//        logger.getLogger().fine("onIJGSbcRefsChanged");
//        refresh();
//    }
    @Override
    public void onFileStatusWidgetListSelectionChanged(List<String> selectionList) {
        boolean invalidSelection = (selectionList == null || selectionList.isEmpty());
        String path = invalidSelection ? null : selectionList.get(0);

        if (path != null) {
            getDiffFile(path, () -> {
                hideProgressBar();
            });
        }
    }

    @Override
    public void onHistoryTableWidgetSelectionChanged(String _commitId) {
        this._commitId = _commitId;
        if (_commitId != null) {
            String commitId = _commitId + "^{tree}";
            String previousCommitId = _commitId + "~1^{tree}";
            getDiff(previousCommitId, commitId, () -> {
                hideProgressBar();
            });
        }
    }

    @Override
    public void onHistoryToolbarClickedCheckout() {
        if (_commitId != null) {
            String commitId = _commitId + "^{tree}";
            checkout(_commitId);
        }
    }

    private void getDiffFile(String path, IJGScallbackRefresh refresh) {
        String commitId = _commitId + "^{tree}";
        String previousCommitId = _commitId + "~1^{tree}";
//        bc.getDiffFile(previousCommitId, commitId, path, updateDiffFileCallback(refresh));
        new Thread(() -> {
            try {
                String diffFile = jGSrepositoryModel.getDiffFile(previousCommitId, commitId, path);
                panel.updateCurrentfile(diffFile, endOfChainCallback(refresh));
            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, "getDiffFile", ex);
            }
        }).start();

    }

    private IJGScallbackString updateDiffFileCallback(IJGScallbackRefresh refresh) {
        IJGScallbackString callback = new IJGScallbackString() {
            @Override
            public void onSuccess(String result) {
                String diffFile = result;
                panel.updateCurrentfile(diffFile, endOfChainCallback(refresh));
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
                showErrorDialog("onHistoryPanelListSelectionChangedFile", "getDiff ERROR:\n" + ex.getMessage());
                refresh.finish();
            }
        };
        return callback;
    }

    private void getDiff(String previousCommitId, String commitId, IJGScallbackRefresh refresh) {
        showProgressBar("getDiff");
        //        bc.getDiff(previousCommitId, commitId, updateFileTableCallback(refresh));
        new Thread(() -> {
            try {
                List<DiffEntry> diff = jGSrepositoryModel.getDiff(previousCommitId, commitId);
                panel.updateFileTables(diff, doNothingChainCallback());
                panel.updateCurrentfile("", endOfChainCallback(refresh));
            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, null, ex);
            }
            refresh.finish();
        }).start();

    }

    private IJGScallbackListDiffEntry updateFileTableCallback(IJGScallbackRefresh refresh) {
        IJGScallbackListDiffEntry callback = new IJGScallbackListDiffEntry() {
            @Override
            public void onSuccess(List<DiffEntry> result) {
                List<DiffEntry> diff = result;
                panel.updateFileTables(diff, endOfChainCallback(refresh));
                panel.updateCurrentfile("", endOfChainCallback(refresh));
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
                showErrorDialog("onHistoryPanelListSelectionChangedHistory", "getDiff ERROR:\n" + ex.getMessage());
                refresh.finish();
            }

        };
        return callback;
    }

    private void updateHistoryTable(Integer amount, IJGScallbackRefresh refresh) {
        logger.getLogger().fine("updateHistoryTable: " + amount);
        showProgressBar("updateHistoryTable " + amount);

        if (amount == null) {
            showErrorDialog("updateHistoryTable", "getAllCommits ERROR:\n");
            refresh.finish();
        } else {
            new Thread(() -> {
                try {
                    Iterable<RevCommit> commits = jGSrepositoryModel.getCommits(amount);
                    panel.updateHistoryTable(commits, doNothingChainCallback());
                    panel.updateCurrentfile("", doNothingChainCallback());
                } catch (NoHeadException nhw) {
                    logger.getLogger().info(nhw.getMessage());
                    showErrorDialog("NoHeadException", "This repository has no commits yet, please create an initial commit");
                } catch (Exception ex) {
                    logger.getLogger().severe(ex.getMessage());
                }
                refresh.finish();
            }).start();
        }
    }

    private void updateHistoryTableAll(IJGScallbackRefresh refresh) {
        logger.getLogger().fine("updateHistoryTableAll");
        showProgressBar("updateHistoryTableAll");
        //        bc.getAllCommits(updateHistoryTableCallback(refresh));
        new Thread(() -> {
            try {
                Iterable<RevCommit> allCommits = jGSrepositoryModel.getAllCommits();
                panel.updateHistoryTable(allCommits, doNothingChainCallback());
                panel.updateCurrentfile("", doNothingChainCallback());
            } catch (Exception ex) {
                logger.getLogger().severe(ex.getMessage());
            }
            refresh.finish();
        }).start();

    }

    private void filterHistory(Integer amount, String text) {
        logger.getLogger().fine("filterHistory: " + text);
        showProgressBar("filterHistory " + text);

        new Thread(() -> {
            try {
                Iterable<RevCommit> commits = jGSrepositoryModel.getCommits(amount, text);
                panel.updateHistoryTable(commits, doNothingChainCallback());
                panel.updateCurrentfile("", doNothingChainCallback());
            } catch (NoHeadException nhe) {
                logger.getLogger().severe(nhe.getMessage());
            } catch (Exception ex) {
                logger.getLogger().severe(ex.getMessage());
            }
            hideProgressBar();

        }).start();
    }

    private void checkout(String _commitId) {
        logger.getLogger().fine("checkout: " + _commitId);
        showProgressBar("checkout " + _commitId);

        new Thread(() -> {
            try {
                Git git = jGSrepositoryModel.getGit();
                Ref checkoutLocalBranch = utils.checkoutLocalBranch(git, _commitId);
            } catch (Exception ex) {
                logger.getLogger().severe(ex.getMessage());
            }
            hideProgressBar();
        }).start();
    }

    private IJGScallbackListRefCommit updateHistoryTableCallback(IJGScallbackRefresh refresh) {
        IJGScallbackListRefCommit callback = new IJGScallbackListRefCommit() {
            @Override
            public void onSuccess(Iterable<RevCommit> result) {
                Iterable<RevCommit> commits = result;
                panel.updateHistoryTable(commits, endOfChainCallback(refresh));
                panel.updateCurrentfile("", endOfChainCallback(refresh));
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
                showErrorDialog("updateHistoryTable", "getAllCommits ERROR:\n" + ex.getMessage());
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
