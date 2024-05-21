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

import com.jkertz.jgitswing.businesslogic.JGSworker;
import com.jkertz.jgitswing.callback.IJGScallbackRefresh;
import com.jkertz.jgitswing.callback.IJGScallbackString;
import com.jkertz.jgitswing.model.JGSrepositoryModel;
import com.jkertz.jgitswing.tablemodels.IterableRevCommitTableModel;
import com.jkertz.jgitswing.tablemodels.ListDiffEntryTableModel;
import com.jkertz.jgitswing.tabs.common.IJGScommonController;
import com.jkertz.jgitswing.tabs.common.JGScommonController;
import java.util.List;
import java.util.logging.Level;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultStyledDocument;
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
        updateHistoryTable(5);
    }

    @Override
    public void onHistoryPanelClickedShow100(String text) {
        if (text == null || text.isEmpty()) {
            updateHistoryTable(100);
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
            getDiffFile(path);
        }
    }

    @Override
    public void onHistoryTableWidgetSelectionChanged(String _commitId) {
        this._commitId = _commitId;
        if (_commitId != null) {
            String commitId = _commitId + "^{tree}";
            String previousCommitId = _commitId + "~1^{tree}";
            getDiff(previousCommitId, commitId);
        }
    }

    @Override
    public void onHistoryToolbarClickedCheckout() {
        if (_commitId != null) {
            String commitId = _commitId + "^{tree}";
            checkout(_commitId);
        }
    }

    private void getDiffFile(String path) {
        String commitId = _commitId + "^{tree}";
        String previousCommitId = _commitId + "~1^{tree}";
//        bc.getDiffFile(previousCommitId, commitId, path, updateDiffFileCallback(refresh));
        JGSworker.runOnWorkerThread(() -> {
            try {
                showProgressBar("getDiffFile", 0);
                String diffFile = jGSrepositoryModel.getDiffFile(previousCommitId, commitId, path);
                DefaultStyledDocument doc = uiUtils.buildStyledDocumentFromFileDiff(diffFile);
                SwingUtilities.invokeLater(() -> {
                    showProgressBar("getDiffFile", 50);
                    panel.updateCurrentfile(doc);
                    showProgressBar("getDiffFile", 100);
                });
            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, "getDiffFile", ex);
            }
        });

    }

    private IJGScallbackString updateDiffFileCallback(IJGScallbackRefresh refresh) {
        IJGScallbackString callback = new IJGScallbackString() {
            @Override
            public void onSuccess(String result) {
                String diffFile = result;
                DefaultStyledDocument doc = uiUtils.buildStyledDocumentFromFileDiff(diffFile);
                SwingUtilities.invokeLater(() -> {
                    showProgressBar("getDiff", 50);

                    panel.updateCurrentfile(doc);
                    showProgressBar("getDiff", 100);
                });

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

    private void getDiff(String previousCommitId, String commitId) {
        //        bc.getDiff(previousCommitId, commitId, updateFileTableCallback(refresh));
        JGSworker.runOnWorkerThread(() -> {
            try {
                showProgressBar("getDiff", 0);
                List<DiffEntry> diff = jGSrepositoryModel.getDiff(previousCommitId, commitId);
                ListDiffEntryTableModel tableModel = uiUtils.getTableModel(diff);
                showProgressBar("getDiff", 25);
                DefaultStyledDocument doc = uiUtils.buildStyledDocumentFromFileDiff("");
                SwingUtilities.invokeLater(() -> {
                    showProgressBar("getDiff", 50);
                    panel.updateFileTables(tableModel);
                    showProgressBar("getDiff", 75);
                    panel.updateCurrentfile(doc);
                    showProgressBar("getDiff", 100);
                });
            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, null, ex);
            }
        });
    }

    private void updateHistoryTable(Integer amount) {
        logger.getLogger().fine("updateHistoryTable: " + amount);

        if (amount == null) {
            showErrorDialog("updateHistoryTable", "getAllCommits ERROR:\n");
        } else {
            JGSworker.runOnWorkerThread(() -> {
                try {
                    showProgressBar("updateHistoryTable " + amount, 0);
                    Iterable<RevCommit> commits = jGSrepositoryModel.getCommits(amount);
                    showProgressBar("updateHistoryTable " + amount, 25);
                    IterableRevCommitTableModel tableModel = uiUtils.getTableModel(commits);
                    DefaultStyledDocument doc = uiUtils.buildStyledDocumentFromFileDiff("");
                    SwingUtilities.invokeLater(() -> {
                        showProgressBar("updateHistoryTable " + amount, 50);
                        panel.updateHistoryTable(tableModel);
                        showProgressBar("updateHistoryTable " + amount, 75);
                        panel.updateCurrentfile(doc);
                        showProgressBar("updateHistoryTable " + amount, 100);
                    });

                } catch (NoHeadException nhw) {
                    logger.getLogger().info(nhw.getMessage());
                    showErrorDialog("NoHeadException", "This repository has no commits yet, please create an initial commit");
                } catch (Exception ex) {
                    logger.getLogger().severe(ex.getMessage());
                }
            });
        }
    }

    private void updateHistoryTableAll(IJGScallbackRefresh refresh) {
        logger.getLogger().fine("updateHistoryTableAll");
        //        bc.getAllCommits(updateHistoryTableCallback(refresh));
        JGSworker.runOnWorkerThread(() -> {
            try {
                showProgressBar("updateHistoryTableAll", 0);
                Iterable<RevCommit> allCommits = jGSrepositoryModel.getAllCommits();
                showProgressBar("updateHistoryTableAll", 25);
                IterableRevCommitTableModel tableModel = uiUtils.getTableModel(allCommits);
                DefaultStyledDocument doc = uiUtils.buildStyledDocumentFromFileDiff("");
                SwingUtilities.invokeLater(() -> {
                    showProgressBar("updateHistoryTableAll", 50);
                    panel.updateHistoryTable(tableModel);
                    showProgressBar("updateHistoryTableAll", 75);
                    panel.updateCurrentfile(doc);
                    showProgressBar("updateHistoryTableAll", 100);
                });

            } catch (Exception ex) {
                logger.getLogger().severe(ex.getMessage());
            }
        });
    }

    private void filterHistory(Integer amount, String text) {
        logger.getLogger().fine("filterHistory: " + text);

        JGSworker.runOnWorkerThread(() -> {
            try {
                showProgressBar("filterHistory " + text, 0);
                Iterable<RevCommit> commits = jGSrepositoryModel.getCommits(amount, text);
                IterableRevCommitTableModel tableModel = uiUtils.getTableModel(commits);
                showProgressBar("filterHistory " + text, 25);
                DefaultStyledDocument doc = uiUtils.buildStyledDocumentFromFileDiff("");
                showProgressBar("filterHistory " + text, 50);
                SwingUtilities.invokeLater(() -> {
                    panel.updateHistoryTable(tableModel);
                    showProgressBar("filterHistory " + text, 75);
                    panel.updateCurrentfile(doc);
                    showProgressBar("filterHistory " + text, 100);
                });

            } catch (NoHeadException nhe) {
                logger.getLogger().severe(nhe.getMessage());
            } catch (Exception ex) {
                logger.getLogger().severe(ex.getMessage());
            }

        });
    }

    private void checkout(String _commitId) {
        logger.getLogger().fine("checkout: " + _commitId);

        JGSworker.runOnWorkerThread(() -> {
            try {
                Git git = jGSrepositoryModel.getGit();
                showProgressBar("checkout " + _commitId, 0);
                Ref checkoutLocalBranch = utils.checkoutLocalBranch(git, _commitId);
                showProgressBar("checkout " + _commitId, 100);
            } catch (Exception ex) {
                logger.getLogger().severe(ex.getMessage());
            }
        });
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
