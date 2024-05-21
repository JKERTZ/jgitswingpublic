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
package com.jkertz.jgitswing.tabs.config;

import com.jkertz.jgitswing.businesslogic.JGSworker;
import com.jkertz.jgitswing.callback.IJGScallbackRefresh;
import com.jkertz.jgitswing.model.JGSrepositoryModel;
import com.jkertz.jgitswing.tabs.common.IJGScommonController;
import com.jkertz.jgitswing.tabs.common.JGScommonController;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author jkertz
 */
public final class JGSconfigController extends JGScommonController implements IJGSconfigPanel, IJGScommonController {

    private final String name = "Config";
    private JGSconfigPanel panel;

    public JGSconfigController(JGSrepositoryModel jGSrepositoryModel) {
        super("Config", jGSrepositoryModel);
        panel = new JGSconfigPanel(this);
        setPanel(panel);
//        bc.addReceiver(this);
    }

    @Override
    public void onGitConfigChanged() {
        logger.getLogger().fine("onGitConfigChanged");
        refresh();
    }

    @Override
    public void onGitRefChanged() {
        //caused by create commit
        //caused by branch checkout
        //caused by pull
        logger.getLogger().fine("onGitRefChanged");
        refresh();
    }

    @Override
    public void updateWidgets(IJGScallbackRefresh refresh) {
        //chain only independent methods here
        updateConfigTree();
    }

    @Override
    public void onConfigPanelClickedEditConfig() {
        editConfigInfo();
    }

    @Override
    public void onConfigToolbarClickedFixRemote() {
        autoFixRemoteEditConfigInfo(false);
    }

    private void editConfigInfo() {
        try {
            Map<String, Map<String, Map<String, String>>> configInfoMap = jGSrepositoryModel.getConfigInfo();
            boolean showParameterMapDialog = jGSdialogFactory.showSectional("Config", configInfoMap, false);
            if (showParameterMapDialog) {
                saveConfigInfo(configInfoMap);
            } else {
            }

        } catch (Exception ex) {
            logger.getLogger().log(Level.SEVERE, "editConfigInfo", ex);

        }
    }

    private void updateConfigTree() {
        logger.getLogger().fine("updateConfigTree");
        //        bc.getConfigInfo(updateConfigCallback(refresh));
        JGSworker.runOnWorkerThread(() -> {
            try {
                showProgressBar("updateConfigTree", 0);
                Map<String, Map<String, Map<String, String>>> configInfoMap = jGSrepositoryModel.getConfigInfo();
                showProgressBar("updateConfigTree", 25);
                DefaultTreeModel dtm = uiUtils.buildTreeModelConfig(configInfoMap);
                SwingUtilities.invokeLater(() -> {
                    showProgressBar("updateConfigTree", 50);
                    panel.updateConfigTree(dtm);
                    showProgressBar("updateConfigTree", 100);
                });
            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, "updateConfigTree", ex);
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
