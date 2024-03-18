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

import com.jkertz.jgitswing.callback.IJGScallbackDirConfigInfoMap;
import com.jkertz.jgitswing.callback.IJGScallbackRefresh;
import com.jkertz.jgitswing.callback.IJGScallbackString;
import com.jkertz.jgitswing.model.JGSrepositoryModel;
import com.jkertz.jgitswing.tabs.common.IJGScommonController;
import com.jkertz.jgitswing.tabs.common.JGScommonController;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

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
        updateConfigTree(refresh);
    }

    @Override
    public void onConfigPanelClickedEditConfig() {
        showProgressBar("onConfigPanelClickedEditConfig");
        editConfigInfo();
    }

    @Override
    public void onConfigToolbarClickedFixRemote() {
        showProgressBar("onConfigToolbarClickedFixRemote");
        autoFixRemoteEditConfigInfo();
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
            Logger.getLogger(JGSconfigController.class.getName()).log(Level.SEVERE, "editConfigInfo", ex);
        }
        hideProgressBar();
    }

    private void autoFixRemoteEditConfigInfo() {
        try {
            Map<String, Map<String, Map<String, String>>> configInfoMap = jGSrepositoryModel.getConfigInfo();

            //check if remote config is valid
            Map<String, Map<String, String>> remoteSectionMap = configInfoMap.get(ConfigConstants.CONFIG_REMOTE_SECTION);
            Set<String> remoteSubSections = remoteSectionMap.keySet();
            String remoteSubSection = remoteSubSections.iterator().next();
            Map<String, String> remoteMap = remoteSectionMap.get(remoteSubSection);
            String remoteUrl = remoteMap.get(ConfigConstants.CONFIG_KEY_URL);

            Map<String, Map<String, String>> branchSectionMap = configInfoMap.get(ConfigConstants.CONFIG_BRANCH_SECTION);
            Set<String> branchSubSections = branchSectionMap.keySet();
            String branchSubSection = branchSubSections.iterator().next();
            Map<String, String> branchMap = branchSectionMap.get(branchSubSection);
            String branchRemote = branchMap.get(ConfigConstants.CONFIG_KEY_REMOTE);
            String branchMerge = branchMap.get(ConfigConstants.CONFIG_KEY_MERGE);

            //get list of remotes
            List<RemoteConfig> remoteList = jGSrepositoryModel.getRemoteList();
            for (RemoteConfig remoteConfig : remoteList) {
                String remoteConfigName = remoteConfig.getName();
                System.out.println(remoteConfigName);
            }

            //let user choose remote
            RemoteConfig remoteConfig = remoteList.get(0);
            String remoteConfigName = remoteConfig.getName();

            //prefill missing remote info
            URIish uri = remoteConfig.getURIs().get(0);
            String newUrl = uri.toString();
            remoteMap.put(ConfigConstants.CONFIG_KEY_URL, newUrl);

            branchMap.put(ConfigConstants.CONFIG_KEY_REMOTE, remoteConfigName);//origin

            String newBranchMerge = "refs/heads/" + branchSubSection;
            branchMap.put(ConfigConstants.CONFIG_KEY_MERGE, newBranchMerge);//refs/heads/branchname

            //show editor with fixed values for user confirmation
            boolean showParameterMapDialog = jGSdialogFactory.showSectional("Config", configInfoMap, false);
            if (showParameterMapDialog) {
                saveConfigInfo(configInfoMap);
            } else {
            }

        } catch (Exception ex) {
            Logger.getLogger(JGSconfigController.class.getName()).log(Level.SEVERE, "autoFixRemoteEditConfigInfo", ex);
        }
        hideProgressBar();

    }

//    private IJGScallbackDirConfigInfoMap editConfigCallback(IJGScallbackRefresh refresh) {
//        IJGScallbackDirConfigInfoMap callback = new IJGScallbackDirConfigInfoMap() {
//            @Override
//            public void onSuccess(Map<String, Map<String, Map<String, String>>> result) {
//                Map<String, Map<String, Map<String, String>>> configInfoMap = result;
//                boolean showParameterMapDialog = new JGSParameterMapDialog().showSectional("Config", configInfoMap, false);
//                if (showParameterMapDialog) {
//                    saveConfigInfo(configInfoMap, refresh);
//                } else {
//                    refresh.finish();
//                }
//            }
//
//            @Override
//            public void onError(Exception ex) {
//                ex.printStackTrace();
//                showErrorDialog("updateConfigCallback", "getConfigInfo ERROR:\n" + ex.getMessage());
//                refresh.finish();
//            }
//        };
//        return callback;
//    }
//    private void saveConfigInfo(Map<String, Map<String, Map<String, String>>> configInfoMap, IJGScallbackRefresh refresh) {
//        showProgressBar("saveConfigInfo");
//        bc.saveConfigInfo(configInfoMap, configInfoSavedCallback(refresh));
//    }
    private void saveConfigInfo(Map<String, Map<String, Map<String, String>>> configInfoMap) {
        showProgressBar("saveConfigInfo");
        try {
            jGSrepositoryModel.saveConfigInfo(configInfoMap);
        } catch (Exception ex) {
            Logger.getLogger(JGSconfigController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private IJGScallbackString configInfoSavedCallback(IJGScallbackRefresh refresh) {
        IJGScallbackString callback = new IJGScallbackString() {
            @Override
            public void onSuccess(String result) {
                showInfoDialog("onConfigPanelClickedEditConfig", "saveConfigInfo SUCCESS");
                updateConfigTree(refresh);
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
                showErrorDialog("onConfigPanelClickedEditConfig", "saveConfigInfo ERROR:\n" + ex.getMessage());
                refresh.finish();
            }
        };
        return callback;

    }

    private void updateConfigTree(IJGScallbackRefresh refresh) {
        logger.getLogger().fine("updateConfigTree");
        //        bc.getConfigInfo(updateConfigCallback(refresh));
        showProgressBar("updateConfigTree");
        new Thread(() -> {
            try {
                Map<String, Map<String, Map<String, String>>> configInfoMap = jGSrepositoryModel.getConfigInfo();
                panel.updateConfigTree(configInfoMap, endOfChainCallback(refresh));
            } catch (Exception ex) {
                Logger.getLogger(JGSconfigController.class.getName()).log(Level.SEVERE, null, ex);
                refresh.finish();
            }
        }).start();
    }

    private IJGScallbackDirConfigInfoMap updateConfigCallback(IJGScallbackRefresh refresh) {
        IJGScallbackDirConfigInfoMap callback = new IJGScallbackDirConfigInfoMap() {
            @Override
            public void onSuccess(Map<String, Map<String, Map<String, String>>> result) {
                Map<String, Map<String, Map<String, String>>> configInfoMap = result;
                panel.updateConfigTree(configInfoMap, endOfChainCallback(refresh));
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
                showErrorDialog("updateConfigCallback", "getConfigInfo ERROR:\n" + ex.getMessage());
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
