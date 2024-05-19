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
import com.jkertz.jgitswing.businesslogic.JGSworker;
import com.jkertz.jgitswing.dialogs.JGScloneRepositoryDialog;
import com.jkertz.jgitswing.dialogs.JGSdialogFactory;
import com.jkertz.jgitswing.dialogs.JGSeditSettingsDialog;
import com.jkertz.jgitswing.dialogs.JGSprogressCollector;
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
    //FIXME: currentDiff takes a long time on huge repositories, progress is not shown correctly

    //incomplete features
    //TODO: fix tags feature
    //TODO: make config like pull-result
    //TODO: remove finalize from all classes after checking memory release on close tabs
    //TODO: running threads should be merged with global progress bar
    //TODO: merge Graph and History Panel
    //TODO: make Graph Tags reactive
    //TODO: remove hardcoded column number access
    //TODO: user swingworker for threading
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
    private final JGSprogressCollector progressCollector;

    private JGSmainController() {

        logger = JGSlogger.getINSTANCE();
        utils = JGSutils.getINSTANCE();
        subControllers = new ArrayList<>();

        panel = new JGSmainView(this);
        panel.getjFrame().setTitle("JGS v0.20240519");

        jGSdialogFactory = new JGSdialogFactory(panel.getjFrame());
        progressCollector = JGSprogressCollector.getINSTANCE();
        progressCollector.setParentFrame(panel.getjFrame());

        settings = JGSsettings.getINSTANCE();
        settings.addReceiver(this);
        settings.loadRecentRepositories();

        addSubTabs();
        showInfoToast("JGSmainController completed");
        logger.getLogger().fine("JGSmainController completed");
        progressCollector.addProgress("Startup completed", 100, this.getClass().getName());
        startDemoProgress();
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

    @Override
    public void onOpenRepositoryClicked() {
        String chooseDirectory = chooseOpenRepository();
        JGSworker.runOnWorkerThread(() -> {
            openRepository(chooseDirectory);
        });
    }

    @Override
    public void onOpenRepositoryClicked(String chooseDirectory) {
        JGSworker.runOnWorkerThread(() -> {
            openRepository(chooseDirectory);
        });
    }

    private void openRepository(String chooseDirectory) {
        if (chooseDirectory != null) {
            Git git;
            try {
                showProgressBar("openRepository", 0);
                git = utils.openRepository(chooseDirectory);
                showProgressBar("openRepository", 25);
                JGSrepositoryModel jGSrepositoryModel = new JGSrepositoryModel(git);
                showProgressBar("openRepository", 50);
                addRepositoryTab(jGSrepositoryModel);
                showProgressBar("openRepository", 75);
                saveRepositoryPath(jGSrepositoryModel);
                showProgressBar("openRepository", 100);
            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, "onOpenRepositoryClicked", ex);
            }
        } else {
            showProgressBar("openRepository", 100);
        }
    }

    @Override
    public void onInitRepositoryClicked() {
        JGSworker.runOnWorkerThread(() -> {
            initRepository(false);
        });
    }

    @Override
    public void onInitBareRepositoryClicked() {
        JGSworker.runOnWorkerThread(() -> {
            initRepository(true);
        });
    }

    private void initRepository(boolean isBare) {

        String chooseDirectory = jGSdialogFactory.chooseDirectory("Init: choose Target Directory");
        if (chooseDirectory != null) {

            try {
                showProgressBar("initRepository", 0);
                Git git = utils.initRepository(chooseDirectory, isBare);
                showProgressBar("initRepository", 25);
                JGSrepositoryModel jGSrepositoryModel = new JGSrepositoryModel(git);
                showProgressBar("initRepository", 50);
                addRepositoryTab(jGSrepositoryModel);
                showProgressBar("initRepository", 75);
                saveRepositoryPath(jGSrepositoryModel);
                showProgressBar("initRepository", 100);
            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, null, ex);
            }

        } else {
            showProgressBar("initRepository", 100);
        }
    }

    @Override
    public void onCloneRepositoryClicked() {
        //TODO: replace this code with DialogFactory
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
            JGSworker.runOnWorkerThread(() -> {
                try {
                    showProgressBar("CloneRepository", -1);
                    Git git = utils.cloneRepository(targetDirectory, parameters);
                    showProgressBar("CloneRepository", 25);
                    JGSrepositoryModel jGSrepositoryModel = new JGSrepositoryModel(git);
                    showProgressBar("CloneRepository", 50);
                    addRepositoryTab(jGSrepositoryModel);
                    showProgressBar("CloneRepository", 75);
                    String path = jGSrepositoryModel.getDirectoryFromRepositoryName();
                    settings.setUserAndPassword(path, username, password, uri);
                    showProgressBar("CloneRepository", 100);
                } catch (Exception ex) {
                    logger.getLogger().log(Level.SEVERE, null, ex);
                }
            });
        } else {
            hideProgressBar("CloneRepository");
        }

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
        showProgressBar("addSubTab: " + subtab.getName(), 0);
        subControllers.add(subtab);
        panel.addTab(subtab.getName(), subtab.getPanel(), autoselect);
        showProgressBar("addSubTab: " + subtab.getName(), 100);
    }

    private void saveRepositoryPath(JGSrepositoryModel jGSrepositoryModel) {
        String path = jGSrepositoryModel.getDirectoryFromRepositoryName();
        settings.setPath(path);
    }

    private String chooseOpenRepository() {
        return panel.chooseOpenRepository();
    }

    private void hideProgressBar(String title) {
        progressCollector.removeProgress(title);
    }

    private void showProgressBar(String title, int progress) {
        progressCollector.addProgress(title, progress, this.getClass().getName());
    }

    private void startDemoProgress() {
        JGSworker.runOnWorkerThread(() -> {
            for (int prog = 0; prog <= 100; prog++) {
                showProgressBar("DemoProgress", prog);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });

//        new Thread(() -> {
//            for (int prog = 0; prog <= 100; prog++) {
//                progress.addProgress("DemoProgress", prog, this.getClass().getName());
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException ex) {
//                    ex.printStackTrace();
//                }
//            }
////            jGSprogressCollector.removeProgress("DemoProgress");
//        }).start();
    }

}
