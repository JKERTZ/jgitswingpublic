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
package com.jkertz.jgitswing.tabs.currentdiff;

import java.util.List;
import java.util.logging.Level;
import com.jkertz.jgitswing.callback.IJGScallbackRefresh;
import com.jkertz.jgitswing.callback.IJGScallbackString;
import com.jkertz.jgitswing.model.JGSrepositoryModel;
import com.jkertz.jgitswing.tabs.common.IJGScommonController;
import com.jkertz.jgitswing.tabs.common.JGScommonController;
import org.eclipse.jgit.diff.DiffEntry;

/**
 *
 * @author jkertz
 */
public final class JGScurrentDiffController extends JGScommonController implements IJGScurrentDiffPanel, IJGScommonController {

    private JGScurrentDiffPanel panel;

    public JGScurrentDiffController(JGSrepositoryModel jGSrepositoryModel) {
        super("CurrentDiff", jGSrepositoryModel);
        panel = new JGScurrentDiffPanel(this);
        setPanel(panel);
    }

    @Override
    public void onGitIndexChanged() {
        //caused by staging
        logger.getLogger().fine("onGitIndexChanged");
        refresh();
    }

    @Override
    public void updateWidgets(IJGScallbackRefresh refresh) {
        updateCurrentDiff(refresh);
//        updateCurrentDiffFile(path, refresh);
    }

    private void updateCurrentDiff(IJGScallbackRefresh refresh) {
        showProgressBar("updateWidgets");
        // jGSrepositoryModel async thread
        new Thread(() -> {
            try {
                List<DiffEntry> currentDiff = jGSrepositoryModel.getCurrentDiff();
                panel.updateFileTables(currentDiff, endOfChainCallback(refresh));
            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, "updateCurrentDiff", ex);
                refresh.finish();
            }
        }).start();
    }

    private void updateCurrentDiffFile(String path, IJGScallbackRefresh refresh) {
        showProgressBar("updateWidgets");
        // jGSrepositoryModel async thread
        new Thread(() -> {
            try {
                String currentDiffFile = jGSrepositoryModel.getCurrentDiffFile(path);
                panel.updateCurrentfile(currentDiffFile, endOfChainCallback(refresh));
            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, "updateCurrentDiffFile", ex);
            }
        }).start();
    }

    @Override
    public void onFileStatusWidgetListSelectionChanged(List<String> selectionList) {
        showProgressBar("onFileStatusWidgetListSelectionChanged");
        boolean invalidSelection = (selectionList == null || selectionList.isEmpty());
        String path = invalidSelection ? null : selectionList.get(0);
        updateCurrentDiffFile(path, refreshCallback());
//        getCurrentDiffFile(path, () -> {
//            hideProgressBar();
//        });
    }

//    private void getCurrentDiffFile(String path, IJGScallbackRefresh refresh) {
//        if (path == null) {
//            panel.updateCurrentfile("", endOfChainCallback(refresh));
//        } else {
//            bc.getCurrentDiffFile(path, updateCurrentfileCallback(refresh));
//        }
//    }
    private IJGScallbackString updateCurrentfileCallback(IJGScallbackRefresh refresh) {
        IJGScallbackString callback = new IJGScallbackString() {
            @Override
            public void onSuccess(String result) {
                String currentDiffFile = result;
                panel.updateCurrentfile(currentDiffFile, endOfChainCallback(refresh));
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
                showErrorDialog("onIJGScurrentDiffPanelFileSelected", "getCurrentDiffFile ERROR:\n" + ex.getMessage());
                refresh.finish();
            }
        };
        return callback;
    }

//    @Override
//    public void onIJGSbcRefsChanged() {
//        logger.getLogger().fine("onIJGSbcRefsChanged");
//        refresh();
//    }
//
//    @Override
//    public void onIJGSbcIndexChanged() {
//        logger.getLogger().fine("onIJGSbcIndexChanged");
//        refresh();
//    }
//    private void getCurrentDiff(IJGScallbackRefresh refresh) {
//        bc.getCurrentDiff(updateCurrentDiffCallback(refresh));
//    }
//
//    private IJGScallbackListDiffEntry updateCurrentDiffCallback(IJGScallbackRefresh refresh) {
//        IJGScallbackListDiffEntry callback = new IJGScallbackListDiffEntry() {
//            @Override
//            public void onSuccess(List<DiffEntry> result) {
//                panel.updateFileTables(result, endOfChainCallback(refresh));
//            }
//
//            @Override
//            public void onError(Exception ex) {
//                ex.printStackTrace();
//                showErrorDialog("updateCurrentDiff", "getCurrentDiff ERROR:\n" + ex.getMessage());
//                refresh.finish();
//            }
//        };
//        return callback;
//    }
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
