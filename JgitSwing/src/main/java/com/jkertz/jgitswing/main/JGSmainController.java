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
package com.jkertz.jgitswing.main;

import com.jkertz.jgitswing.businesslogic.JGSutils;
import com.jkertz.jgitswing.dialogs.JGScloneRepositoryDialog;
import com.jkertz.jgitswing.dialogs.JGSdialogFactory;
import com.jkertz.jgitswing.dialogs.JGSeditSettingsDialog;
import com.jkertz.jgitswing.logger.JGSlogger;
import com.jkertz.jgitswing.model.JGSrepositoryModel;
import com.jkertz.jgitswing.model.JGSsetting;
import com.jkertz.jgitswing.settings.IJGSsettings;
import com.jkertz.jgitswing.settings.JGSsettings;
import com.jkertz.jgitswing.tabs.common.IJGSsubTabController;
import com.jkertz.jgitswing.tabs.log.JGSlogController;
import com.jkertz.jgitswing.tabs.repository.JGSrepositoryController;
import com.jkertz.jgitswing.tabs.welcome.JGSwelcomeController;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.JFrame;
import org.eclipse.jgit.api.Git;

/**
 *
 * @author jkertz
 */
public class JGSmainController implements IJGSmainView, IJGSsettings {
    //known Bugs, use "Action Items" in Netbeans
    //FIXME: create branch: configure remote to push new branch
    //FIXME: currentDiff takes a long time on huge repositories, progress is not shown correctly

    //incomplete features
    //TODO: refresh config on branch switch
    //TODO: make config like pull-result
    //TODO: remove finalize from all classes after checking memory release on close tabs
    //TODO: running threads should be merged with global progress bar
    //TODO: merge Graph and History Panel
    //TODO: make Graph Tags reactive
    //TODO: remove hardcoded column number access
    //future
    //TODO: implement SSH remote repository
    //FIXME: progress bar in Toast flickering on Linux (only wayland)
    private static JGSmainController INSTANCE = null;
    private final JGSlogger logger;
    private final JGSmainView panel;
    private final JGSsettings settings;
    private final JGSutils utils;
    private final List<IJGSsubTabController> subControllers;
    private final JGSdialogFactory jGSdialogFactory;

    private JGSmainController() {

        logger = JGSlogger.getINSTANCE();
        utils = JGSutils.getINSTANCE();
        subControllers = new ArrayList<>();

        panel = new JGSmainView(this);
        panel.getjFrame().setTitle("JGS v0.20240317");

        jGSdialogFactory = new JGSdialogFactory(panel.getjFrame());

        settings = JGSsettings.getINSTANCE();
        settings.addReceiver(this);
        settings.loadRecentRepositories();

        addSubTabs();
        showInfoToast("JGSmainController completed");
        logger.getLogger().fine("JGSmainController completed");
    }

    public static JGSmainController getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new JGSmainController();
        }
        return INSTANCE;
    }

    public void showToast(String message) {
        panel.showToast(message);
    }

    public final void showInfoToast(String message) {
        panel.showInfoToast(message);
    }

    public void showWarningToast(String message) {
        panel.showWarningToast(message);
    }

    public void showErrorToast(String message) {
        panel.showErrorToast(message);
    }

    private void hideProgressBar() {
        panel.hideProgressBar();
    }

    private void showProgressBar(String text) {
        panel.showProgressBar(text);
    }

    @Override
    public void onOpenRepositoryClicked() {
        showProgressBar("OpenRepository");
//        String chooseDirectory = chooseDirectory("Open: choose Directory");
        String chooseDirectory = chooseOpenRepository();
        new Thread(() -> {
            openRepository(chooseDirectory);
        }).start();
    }

    @Override
    public void onOpenRepositoryClicked(String chooseDirectory) {
        showProgressBar("OpenRepository");
        new Thread(() -> {
            openRepository(chooseDirectory);
        }).start();
    }

    private void openRepository(String chooseDirectory) {
        if (chooseDirectory != null) {
            Git git;
            try {
                git = utils.openRepository(chooseDirectory);
                JGSrepositoryModel jGSrepositoryModel = new JGSrepositoryModel(git);
                addRepositoryTab(jGSrepositoryModel);
                saveRepositoryPath(jGSrepositoryModel);
                hideProgressBar();
            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, "onOpenRepositoryClicked", ex);
            }
        } else {
            hideProgressBar();
        }
    }

    @Override
    public void onInitRepositoryClicked() {
        showProgressBar("InitRepository");
        new Thread(() -> {
            initRepository(false);
        }).start();
    }

    @Override
    public void onInitBareRepositoryClicked() {
        showProgressBar("onInitBareRepositoryClicked");
        new Thread(() -> {
            initRepository(true);
        }).start();
    }

    private void initRepository(boolean isBare) {

        String chooseDirectory = jGSdialogFactory.chooseDirectory("Init: choose Target Directory");
        if (chooseDirectory != null) {

            try {
                Git git = utils.initRepository(chooseDirectory, isBare);
                JGSrepositoryModel jGSrepositoryModel = new JGSrepositoryModel(git);
                addRepositoryTab(jGSrepositoryModel);
                saveRepositoryPath(jGSrepositoryModel);
                hideProgressBar();
            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, null, ex);
            }

        } else {
            hideProgressBar();
        }
    }

    @Override
    public void onCloneRepositoryClicked() {
        showProgressBar("CloneRepository");
        JFrame frame = panel.getjFrame();
        JGScloneRepositoryDialog jgScloneRepositoryDialog = new JGScloneRepositoryDialog();
        boolean dialogResultOK = jgScloneRepositoryDialog.show(frame);
        if (dialogResultOK) {
            Map<String, String> parameters = new LinkedHashMap<>();

            String targetDirectory = jgScloneRepositoryDialog.getTargetDirectory();
            String uri = jgScloneRepositoryDialog.getUri();
            String username = jgScloneRepositoryDialog.getUsername();
            String password = jgScloneRepositoryDialog.getPassword();
            parameters.put("URI", uri);
            parameters.put("Username", username);
            parameters.put("Password", password);

            try {
                Git git = utils.cloneRepository(targetDirectory, parameters);
                JGSrepositoryModel jGSrepositoryModel = new JGSrepositoryModel(git);
                addRepositoryTab(jGSrepositoryModel);
//                saveRepositoryPath(jGSrepositoryModel);
//                saveRemoteCredentials(username, password);
                String path = jGSrepositoryModel.getDirectoryFromRepositoryName();
                settings.setUserAndPassword(path, username, password, uri);
                hideProgressBar();
            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, null, ex);
            }
        } else {
            hideProgressBar();
        }
//        showProgressBar("CloneRepository");
//        String chooseDirectory = chooseDirectory("Clone: choose Target Directory");
//        if (chooseDirectory != null) {
//            new Thread(() -> {
//                Map<String, String> parameters = new LinkedHashMap<>();
//                String uri = "";
//                String username = "";
//                String password = "";
//                parameters.put("URI", uri);
//                parameters.put("Username", username);
//                parameters.put("Password", password);
//
//                if (new JGSParameterMapDialog().show("Enter remote location", parameters, false)) {
//                    try {
//                        Git git = utils.cloneRepository(chooseDirectory, parameters);
//                        JGSrepositoryModel jGSrepositoryModel = new JGSrepositoryModel(git);
//                        addRepositoryTab(jGSrepositoryModel);
//                        saveRepositoryPath(jGSrepositoryModel);
//                        hideProgressBar();
//
//                    } catch (Exception ex) {
//                        logger.getLogger().log(Level.SEVERE, null, ex);
//                    }
//                } else {
//                    hideProgressBar();
//                }
//            }).start();
//        } else {
//            hideProgressBar();
//        }
    }

    @Override
    public void onIJGSsettingsRecentRepositoryChanged(List<String> recentRepositoryPaths) {
        logger.getLogger().fine("onIJGSsettingsRecentRepositoryChanged");
        java.awt.EventQueue.invokeLater(() -> {
            panel.updateRepositoryMenu(recentRepositoryPaths);
        });
    }

    @Override
    public void onCloseTab(String tabTitle) {
        logger.getLogger().fine("onCloseTab " + tabTitle);
        removeSubTab(tabTitle);
    }

    @Override
    public void onEditSettingsClicked() {
        logger.getLogger().fine("onEditSettingsClicked");
        JGSsetting oldSetting = JGSsettings.getINSTANCE().getSetting();

        JFrame frame = panel.getjFrame();
        JGSeditSettingsDialog jGSeditSettingsDialog = new JGSeditSettingsDialog(frame, oldSetting);
        boolean dialogResultOK = jGSeditSettingsDialog.show();
        if (dialogResultOK) {
            //save settings
            JGSsetting newSetting = jGSeditSettingsDialog.getSetting();
            JGSsettings.getINSTANCE().setSetting(newSetting);
        }
    }

    @Override
    public void onCloseWindow() {
        if (jGSdialogFactory.showConfirmDialog("Quit", "really quit?")) {
            System.exit(0);
        }
    }

    private void removeSubTab(String tabTitle) {
        IJGSsubTabController subtabToRemove = null;
        for (IJGSsubTabController subController : subControllers) {
            String name = subController.getName();
            if (name.equals(tabTitle)) {
                removeSubTab(subController);
                subtabToRemove = subController;
                break;
            }
        }
        if (subtabToRemove != null) {
            subControllers.remove(subtabToRemove);
            subtabToRemove.deconstruct();
            subtabToRemove = null;
            System.gc();
        }
    }

    private void removeSubTab(IJGSsubTabController subtab) {
        logger.getLogger().fine("removeSubTab");
        panel.removeTab(subtab.getName());
    }

    private void addRepositoryTab(JGSrepositoryModel jGSrepositoryModel) {
        logger.getLogger().fine("addRepositoryTab");
        addSubTab(new JGSrepositoryController(jGSrepositoryModel), true);
    }

    private void addSubTabs() {
        addSubTab(new JGSwelcomeController(), false);
        addSubTab(new JGSlogController(), false);
    }

    private void addSubTab(IJGSsubTabController subtab, boolean autoselect) {
        subControllers.add(subtab);
        panel.addTab(subtab.getName(), subtab.getPanel(), autoselect);
    }

    private void saveRepositoryPath(JGSrepositoryModel jGSrepositoryModel) {
        String path = jGSrepositoryModel.getDirectoryFromRepositoryName();
        settings.setPath(path);
    }

    private String chooseOpenRepository() {
        return panel.chooseOpenRepository();
    }

}
