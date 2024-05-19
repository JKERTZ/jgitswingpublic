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
package com.jkertz.jgitswing.tabs.graph;

import com.jkertz.jgitswing.callback.IJGScallbackRefresh;
import com.jkertz.jgitswing.model.JGSrepositoryModel;
import com.jkertz.jgitswing.tabs.common.IJGScommonController;
import com.jkertz.jgitswing.tabs.common.JGScommonController;
import com.jkertz.jgitswing.widgets.graph.JGSgraphPane;
import java.io.IOException;
import java.util.logging.Level;
import javax.swing.SwingUtilities;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.revplot.PlotWalk;

/**
 *
 * @author jkertz
 */
public final class JGSgraphController extends JGScommonController implements IJGSgraphPanel, IJGScommonController {

    private JGSgraphPanel panel;

    public JGSgraphController(JGSrepositoryModel jGSrepositoryModel) {
        super("Graph", jGSrepositoryModel);
        panel = new JGSgraphPanel(this);
        setPanel(panel);
    }

    @Override
    public void updateWidgets(IJGScallbackRefresh refresh) {
        updateGraphTable(10);
    }

    @Override
    public void onGitRefChanged() {
        //caused by commit
        logger.getLogger().fine("onGitRefChanged");
        refresh();
    }

    private void updateGraphTable(Integer amount) {
        // jGSrepositoryModel async thread
        new Thread(() -> {
            try {
                String branchName = jGSrepositoryModel.getBranchName();
                if (branchName != null && !branchName.isEmpty()) {
                    showProgressBar("updateGraphTable", 0);
                    PlotWalk plotWalk = jGSrepositoryModel.getPlotWalk(branchName);
                    showProgressBar("updateGraphTable", 100);
                    fillGraphPane(plotWalk, amount);
                } else {
                    logger.getLogger().log(Level.SEVERE, "updateGraphTable", "no branch");
                    showErrorDialog("updateGraphTable", "no branch");
                }
            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, "updateGraphTable", ex);
//                showErrorDialog("updateWidgets", "updateWidgets ERROR:\n" + ex.getMessage());
            }
        }).start();
    }

    private void fillGraphPane(PlotWalk plotWalk, Integer amount) throws Exception {
        SwingUtilities.invokeLater(() -> {
            showProgressBar("fillGraphPane", 0);
            JGSgraphPane graphPane = panel.getGraphPane();
            graphPane.getCommitList().clear();
            graphPane.getCommitList().source(plotWalk);
            try {
                graphPane.getCommitList().fillTo(amount);
            } catch (IncorrectObjectTypeException ex) {
                logger.getLogger().log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                logger.getLogger().log(Level.SEVERE, null, ex);
            }
            showProgressBar("fillGraphPane", 100);
        });
    }

//    @Override
//    public void onIJGSbcRefsChanged() {
//        logger.getLogger().fine("onIJGSbcRefsChanged");
//        refresh();
//    }
    @Override
    public void onGraphPanelClickedShowAll() {
        updateGraphTable(Integer.MAX_VALUE);
    }

    @Override
    public void onGraphPanelClickedShow100() {
        updateGraphTable(100);
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
