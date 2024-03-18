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
package com.jkertz.jgitswing.tabs.common;

import com.jkertz.jgitswing.businesslogic.JGSutils;
import com.jkertz.jgitswing.callback.IJGScallbackChain;
import com.jkertz.jgitswing.callback.IJGScallbackRefresh;
import com.jkertz.jgitswing.dialogs.JGSdialogFactory;
import com.jkertz.jgitswing.logger.JGSlogger;
import com.jkertz.jgitswing.main.JGSmainController;
import com.jkertz.jgitswing.model.IJGSrepositoryModel;
import com.jkertz.jgitswing.model.JGSrecent;
import com.jkertz.jgitswing.model.JGSrepositoryModel;
import com.jkertz.jgitswing.settings.JGSsettings;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

/**
 *
 * @author jkertz
 */
public class JGScommonController implements IJGSsubTabController, IJGSrepositoryModel {

    protected final JGSrepositoryModel jGSrepositoryModel;
    protected JGSlogger logger;
    protected JGSuiUtils uiUtils;
    private JGSmainController mainController;
    protected boolean isRefreshing = false;
    protected boolean userOperationInProgress = false;
    private JGScommonPanel commonPanel;
    private final String name;
    protected final JGSutils utils;
    protected JGSdialogFactory jGSdialogFactory;

    public JGScommonController(String name, JGSrepositoryModel jGSrepositoryModel) {
        this.jGSrepositoryModel = jGSrepositoryModel;
        this.name = name;

        //moved addReceiver to post panel definition
        logger = JGSlogger.getINSTANCE();
        mainController = JGSmainController.getINSTANCE();
        uiUtils = JGSuiUtils.getINSTANCE();
        utils = JGSutils.getINSTANCE();
        jGSrepositoryModel.addReceiver(this);

    }

    protected void setPanel(JGScommonPanel commonPanel) {
        this.commonPanel = commonPanel;
        jGSdialogFactory = new JGSdialogFactory(commonPanel);
    }

    public void showErrorDialog(String title, String message) {
        jGSdialogFactory.showErrorDialog(title, message);
    }

    public void showInfoDialog(String title, String message) {
        jGSdialogFactory.showInfoDialog(title, message);
    }

    public String showInputDialog(String title, String message) {
        return jGSdialogFactory.showInputDialog(title, message);
    }

    public boolean showConfirmDialog(String title, String message) {
        return jGSdialogFactory.showConfirmDialog(title, message);
    }

    protected void showToast(String message) {
        mainController.showToast(message);
    }

    protected final void showInfoToast(String message) {
        mainController.showInfoToast(message);
    }

    protected void showWarningToast(String message) {
        mainController.showWarningToast(message);
    }

    protected void showErrorToast(String message) {
        mainController.showErrorToast(message);
    }

    protected final void hideProgressBar() {
        commonPanel.hidevProgressBar();
    }

    protected final void showProgressBar(String text) {
        commonPanel.showvProgressBar(text);
    }

    public void deconstruct() {
        String className = this.getClass().getName();
        System.out.println(className + " deconstruct");
        jGSrepositoryModel.removeReceiver(this);

        logger = null;
        mainController = null;
        uiUtils = null;
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            // Cleanup operations
            System.out.println("JGScommonController finalize");

        } finally {
            super.finalize();
        }
    }

    @Override
    public final JGScommonPanel getPanel() {
        return commonPanel;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final void refresh() {
        if (!isRefreshing) {
            isRefreshing = true;
            updateWidgets(() -> {
                isRefreshing = false;
                hideProgressBar();
            });

        } else {
            logger.getLogger().info("refresh in progress...");
        }
    }

    public void updateWidgets(IJGScallbackRefresh refresh) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    protected final IJGScallbackChain endOfChainCallback(IJGScallbackRefresh refresh) {
        IJGScallbackChain callback = (Object result) -> {
            refresh.finish();
        };
        return callback;
    }

    protected final IJGScallbackRefresh refreshCallback() {
        IJGScallbackRefresh callback = () -> {
            hideProgressBar();
        };
        return callback;
    }

    protected final IJGScallbackChain doNothingChainCallback() {
        IJGScallbackChain callback = (Object result) -> {
        };
        return callback;
    }

    @Override
    public void onGitConfigChanged() {
        logger.getLogger().fine("onGitConfigChanged");
    }

    @Override
    public void onGitIndexChanged() {
        //caused by staging
        logger.getLogger().fine("onGitIndexChanged");
    }

    @Override
    public void onGitRefChanged() {
        //caused by create commit
        //caused by branch checkout
        //caused by pull
        logger.getLogger().fine("onGitRefChanged");
    }

    @Override
    public void onGitWorkingTreeModified() {
        logger.getLogger().fine("onGitWorkingTreeModified");
    }

    protected boolean autoFixRemoteEditConfigInfo(boolean silentIfValid) {
        boolean result = false;
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

            logger.getLogger().fine("autoFixRemoteEditConfigInfo branchRemote: " + branchRemote);
            logger.getLogger().fine("autoFixRemoteEditConfigInfo branchMerge: " + branchMerge);

            //check if current config is set
            boolean isValid = (branchRemote != null && !branchRemote.isEmpty() && branchMerge != null && !branchMerge.isEmpty());
            if (isValid && silentIfValid) {
                //no further checks needed
                logger.getLogger().info("autoFixRemoteEditConfigInfo isValid: " + isValid);
                return true;
            }

            //get list of remotes
            List<RemoteConfig> remoteList = jGSrepositoryModel.getRemoteList();
            for (RemoteConfig remoteConfig : remoteList) {
                String remoteConfigName = remoteConfig.getName();
                System.out.println(remoteConfigName);
            }

            //choose remote
            int selectionindex = 0;
            if (remoteList.size() > 1 || !silentIfValid) {
                //let user choose remote
                List<String> options = new ArrayList<>();
                for (RemoteConfig remoteConfig : remoteList) {
                    String remoteConfigName = remoteConfig.getName();
                    options.add(remoteConfigName);
                    System.out.println(remoteConfigName);
                    List<URIish> urIs = remoteConfig.getURIs();
                }

                Object[] optionsArray = options.toArray();
                selectionindex = jGSdialogFactory.showOptionDialog("Remote", "Choose remote", optionsArray);
            } else {
                //autoselect first remote
                selectionindex = 0;
            }
            RemoteConfig remoteConfig = remoteList.get(selectionindex);
            String remoteConfigName = remoteConfig.getName();

            //prefill missing remote info
            URIish uri = remoteConfig.getURIs().get(0);
            String newUrl = uri.toString();
            remoteMap.put(ConfigConstants.CONFIG_KEY_URL, newUrl);

            branchMap.put(ConfigConstants.CONFIG_KEY_REMOTE, remoteConfigName);//origin

            String newBranchMerge = "refs/heads/" + branchSubSection;
            branchMap.put(ConfigConstants.CONFIG_KEY_MERGE, newBranchMerge);//refs/heads/branchname

            //show editor with fixed values for user confirmation
            boolean showParameterMapDialog = jGSdialogFactory.showSectional("Confirm remote config for branch", configInfoMap, false);
            if (showParameterMapDialog) {
                saveConfigInfo(configInfoMap);
                result = true;
            } else {
                result = false;
            }

        } catch (Exception ex) {
            logger.getLogger().log(Level.SEVERE, "autoFixRemoteEditConfigInfo", ex);
        }
        hideProgressBar();
        return result;
    }

    protected void saveConfigInfo(Map<String, Map<String, Map<String, String>>> configInfoMap) {
        showProgressBar("saveConfigInfo");
        try {
            jGSrepositoryModel.saveConfigInfo(configInfoMap);
        } catch (Exception ex) {
            logger.getLogger().log(Level.SEVERE, "saveConfigInfo", ex);
        }
    }

    protected Map<String, String> getUserPasswordParameters() {
        String path = jGSrepositoryModel.getDirectoryFromRepositoryName();

        String username = JGSsettings.getINSTANCE().getUsername(path);
        String password = JGSsettings.getINSTANCE().getPassword(path);
        JGSrecent remoteSettings = JGSsettings.getINSTANCE().getRemoteSettings(path);
        Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("Username", username);
        parameters.put("Password", password);
        return parameters;
    }

    protected Map<String, Boolean> getPushOptions() {
        Map<String, Boolean> options = new LinkedHashMap<>();
        options.put("dryrun", false);
        return options;
    }

}
