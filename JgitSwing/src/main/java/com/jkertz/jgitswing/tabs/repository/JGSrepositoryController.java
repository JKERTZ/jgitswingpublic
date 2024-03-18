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
package com.jkertz.jgitswing.tabs.repository;

import com.jkertz.jgitswing.callback.IJGScallbackRefresh;
import com.jkertz.jgitswing.model.JGSrecent;
import com.jkertz.jgitswing.model.JGSrepositoryModel;
import com.jkertz.jgitswing.settings.JGSsettings;
import com.jkertz.jgitswing.tabs.branches.JGSbranchesController;
import com.jkertz.jgitswing.tabs.common.IJGScommonController;
import com.jkertz.jgitswing.tabs.common.IJGSsubTabController;
import com.jkertz.jgitswing.tabs.common.JGScommonController;
import com.jkertz.jgitswing.tabs.common.JGShtmlUtils;
import com.jkertz.jgitswing.tabs.config.JGSconfigController;
import com.jkertz.jgitswing.tabs.currentdiff.JGScurrentDiffController;
import com.jkertz.jgitswing.tabs.graph.JGSgraphController;
import com.jkertz.jgitswing.tabs.history.JGShistoryController;
import com.jkertz.jgitswing.tabs.ignored.JGSignoredController;
import com.jkertz.jgitswing.tabs.staging.JGSstagingController;
import com.jkertz.jgitswing.tabs.stagingtree.JGSstagingTreeController;
import com.jkertz.jgitswing.tabs.tags.JGStagsController;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.lib.BranchTrackingStatus;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.PushResult;

/**
 *
 * @author jkertz
 */
public final class JGSrepositoryController extends JGScommonController implements IJGSrepositoryPanel, IJGScommonController {

    private JGSrepositoryPanel panel;
    private Set<IJGSsubTabController> activeSubControllers;
    private final Set<IJGSsubTabController> availableSubControllers;
    private JGSbranchesController jGSbranchesController;
    private JGStagsController jGStagsController;
    private JGSstagingController jGSstagingController;
    private JGSstagingTreeController jGSstagingTreeController;
    private JGScurrentDiffController jGScurrentDiffController;
    private JGSignoredController jGSignoredController;
    private JGShistoryController jGShistoryController;
    private JGSgraphController jGSgraphController;
    private JGSconfigController jGSconfigController;
    private JGShtmlUtils htmlUtils = JGShtmlUtils.getINSTANCE();

    public JGSrepositoryController(JGSrepositoryModel jGSrepositoryModel) {
        super(jGSrepositoryModel.getDirectoryFromRepositoryName(), jGSrepositoryModel);

        activeSubControllers = new HashSet<>();
        availableSubControllers = new HashSet<>();
        panel = new JGSrepositoryPanel(this);
        setPanel(panel);
//        bc.addReceiver(this);
        addSubTabs();
        refresh();
        refreshSubTabs();
    }

//    @Override
//    public void onIJGSbcRefsChanged() {
//        logger.getLogger().fine("onIJGSbcRefsChanged");
//        refresh();
//    }
    @Override
    public void onRepositoryPanelClickedFetch() {
        //validate remote configuration
        if (!autoFixRemoteEditConfigInfo(true)) {
            return;
        }

        showProgressBar("FetchRemote");
        Map<String, String> parameters = getUserPasswordParameters();
        Map<String, Boolean> options = getFetchOptions();

        boolean showParameterMapDialog = jGSdialogFactory.showParameterMapDialog("Fetch", parameters, options, false);
        if (showParameterMapDialog) {
            String usernameInput = parameters.get("Username");
            String passwordInput = parameters.get("Password");
            boolean dryRun = options.get("dryrun");
            boolean checkFetchedObjects = options.get("CheckFetchedObjects");
            boolean removeDeletedRefs = options.get("RemoveDeletedRefs");
            fetchRemote(usernameInput, passwordInput, dryRun, checkFetchedObjects, removeDeletedRefs);
            saveRemoteCredentials(usernameInput, passwordInput);

        }
        hideProgressBar();
    }

    @Override
    public void onRepositoryPanelClickedPull() {
        //validate remote configuration
        if (!autoFixRemoteEditConfigInfo(true)) {
            return;
        }
        showProgressBar("PullRemote");
        Map<String, String> parameters = getUserPasswordParameters();
        boolean showParameterMapDialog = jGSdialogFactory.showParameterMapDialog("Pull", parameters, false);
        if (showParameterMapDialog) {
            String usernameInput = parameters.get("Username");
            String passwordInput = parameters.get("Password");
            Git git = jGSrepositoryModel.getGit();
            try {
                PullResult result = utils.pullRemote(git, usernameInput, passwordInput);
                saveRemoteCredentials(usernameInput, passwordInput);
                jGSdialogFactory.showPullResult("Pull result", result);
            } catch (CheckoutConflictException ccex) {
                String message = ccex.getMessage();
                logger.getLogger().log(Level.INFO, message);
                showInfoDialog("Checkout conflicts", message);
            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, null, ex);
            }
        }
        hideProgressBar();
    }

    @Override
    public void onRepositoryPanelClickedPush() {
        //validate remote configuration
        if (!autoFixRemoteEditConfigInfo(true)) {
            return;
        }

        showProgressBar("PushRemote");
        Map<String, String> parameters = getUserPasswordParameters();
        Map<String, Boolean> options = getPushOptions();
        boolean showParameterMapDialog = jGSdialogFactory.showParameterMapDialog("Push", parameters, options, false);
        if (showParameterMapDialog) {
            String usernameInput = parameters.get("Username");
            String passwordInput = parameters.get("Password");
            boolean dryRun = options.get("dryrun");

            pushRemote(usernameInput, passwordInput, dryRun);
            saveRemoteCredentials(usernameInput, passwordInput);
        }
        hideProgressBar();
    }

    @Override
    public void onRepositoryPanelClickedPushAndFetch() {
        //validate remote configuration
        if (!autoFixRemoteEditConfigInfo(true)) {
            return;
        }

        showProgressBar("PushAndFetchRemote");
        Map<String, String> parameters = getUserPasswordParameters();
        Map<String, Boolean> options = getFetchOptions();
        boolean showParameterMapDialog = jGSdialogFactory.showParameterMapDialog("Push", parameters, options, false);
        if (showParameterMapDialog) {
            String usernameInput = parameters.get("Username");
            String passwordInput = parameters.get("Password");
            boolean dryRun = options.get("dryrun");
            boolean checkFetchedObjects = options.get("CheckFetchedObjects");
            boolean removeDeletedRefs = options.get("RemoveDeletedRefs");
            boolean pushRemotePreview = pushRemote(usernameInput, passwordInput, dryRun);
            if (dryRun && !pushRemotePreview) {
                hideProgressBar();
                return;
            }
            saveRemoteCredentials(usernameInput, passwordInput);
            if (dryRun) {
                pushRemote(usernameInput, passwordInput, false);
            }
            boolean fetchRemotePreview = fetchRemote(usernameInput, passwordInput, dryRun, checkFetchedObjects, removeDeletedRefs);
            if (dryRun && !fetchRemotePreview) {
                hideProgressBar();
                return;
            }
            if (dryRun) {
                fetchRemote(usernameInput, passwordInput, dryRun, checkFetchedObjects, removeDeletedRefs);
            }
        }
        hideProgressBar();
    }

    @Override
    public void onRepositoryPanelClickedRefresh() {
        logger.getLogger().fine("onRepositoryPanelClickedRefresh");
        refresh();
        refreshSubTabs();
    }

    @Override
    public void onRepositoryTabRightClick(JTabbedPane tabbedPane, int x, int y) {
        logger.getLogger().fine("onRepositoryTabRightClick");
        String selectedTabTitle = getSelectedTabTitle(tabbedPane);
        JPopupMenu menu = getTabRightClickMenu(selectedTabTitle);
        menu.show(tabbedPane, x, y);
    }

    @Override
    public void onRepositoryPanelClickedOpenFileManager() {
        logger.getLogger().fine("onRepositoryPanelClickedOpenFileManager");
        Git git = jGSrepositoryModel.getGit();
        String path = jGSrepositoryModel.getDirectoryFromRepositoryName();
        File directory = new File(path);
        try {
            Desktop.getDesktop().open(directory);
        } catch (Exception ex) {
            logger.getLogger().log(Level.SEVERE, "onRepositoryPanelClickedOpenFileManager", ex);
        }
    }

    @Override
    public void updateWidgets(IJGScallbackRefresh refresh) {
        //chain only independent methods here
        updateBranchName();
//        updateAheadBehind();
        refresh.finish();
    }

    @Override
    public void onGitRefChanged() {
        //caused by commit
        logger.getLogger().fine("onGitRefChanged");
//        updateAheadBehind();
//        updateBranchName();
//        hideProgressBar();
        refresh();
    }

    private void refreshSubTabs() {
        for (IJGSsubTabController subController : activeSubControllers) {
            System.out.println("refreshSubTab: " + subController.getName());
            subController.refresh();
        }
    }

    private void addSubTabs() {
        jGSbranchesController = new JGSbranchesController(jGSrepositoryModel);
        availableSubControllers.add(jGSbranchesController);

        jGStagsController = new JGStagsController(jGSrepositoryModel);
        availableSubControllers.add(jGStagsController);

        jGSstagingController = new JGSstagingController(jGSrepositoryModel);
        availableSubControllers.add(jGSstagingController);

        jGSstagingTreeController = new JGSstagingTreeController(jGSrepositoryModel);
        availableSubControllers.add(jGSstagingTreeController);

        jGScurrentDiffController = new JGScurrentDiffController(jGSrepositoryModel);
        availableSubControllers.add(jGScurrentDiffController);

        jGSignoredController = new JGSignoredController(jGSrepositoryModel);
        availableSubControllers.add(jGSignoredController);

        jGShistoryController = new JGShistoryController(jGSrepositoryModel);
        availableSubControllers.add(jGShistoryController);

        jGSgraphController = new JGSgraphController(jGSrepositoryModel);
        availableSubControllers.add(jGSgraphController);

        jGSconfigController = new JGSconfigController(jGSrepositoryModel);
        availableSubControllers.add(jGSconfigController);

        addSubTab(jGSbranchesController);
//        addSubTab(jGStagsController);
//        addSubTab(jGSstagingController);
        addSubTab(jGSstagingTreeController);

//        addSubTab(jGScurrentDiffController);
//        addSubTab(jGSignoredController);
        addSubTab(jGShistoryController);
        addSubTab(jGSgraphController);
        addSubTab(jGSconfigController);
    }

    private void addSubTab(String tabTitle) {
        for (IJGSsubTabController subController : availableSubControllers) {
            String name = subController.getName();
            if (name.equals(tabTitle)) {
                addSubTab(subController);
                subController.refresh();
                break;
            }
        }
    }

    private void addSubTab(IJGSsubTabController subtab) {
        activeSubControllers.add(subtab);
        panel.addTab(subtab.getName(), subtab.getPanel());
    }

    private void removeSubTab(String tabTitle) {
        IJGSsubTabController subtabToRemove = null;
        for (IJGSsubTabController subController : activeSubControllers) {
            String name = subController.getName();
            if (name.equals(tabTitle)) {
                removeSubTab(subController);
                subtabToRemove = subController;
                break;
            }
        }
        if (subtabToRemove != null) {
            activeSubControllers.remove(subtabToRemove);
//            subtabToRemove.deconstruct();
//            subtabToRemove = null;
//            System.gc();
        }
    }

    private void removeSubTab(IJGSsubTabController subtab) {
        panel.removeTab(subtab.getName());
    }

    private void updateBranchName() {
        logger.getLogger().fine("updateBranchName");
        String labelText = "Branch: ";
        showProgressBar("updateBranchName");
        try {
            String branchName = jGSrepositoryModel.getBranchName();
            labelText += branchName;

            BranchTrackingStatus branchTrackingStatus = jGSrepositoryModel.getBranchTrackingStatus(branchName);
            if (branchTrackingStatus != null) {
                int aheadCount = branchTrackingStatus.getAheadCount();
                int behindCount = branchTrackingStatus.getBehindCount();
                String remoteTrackingBranch = branchTrackingStatus.getRemoteTrackingBranch();
                String aheadBehind = htmlUtils.toAheadBehind(aheadCount, behindCount);
                labelText += aheadBehind;
                String htmlToolTip = "<html>";
                htmlToolTip += "<b>" + branchName + "</b>";//bold
                htmlToolTip += "<br>";//new line
                if (aheadCount == 0) {
                    htmlToolTip += "↑ Ahead: " + aheadCount;
                } else {
//                    htmlToolTip += "<div style='background:red;'> ↑ Ahead: " + aheadCount + "</div>";
                    htmlToolTip += "<font color=orange>" + "↑ Ahead: " + aheadCount + "</font>";
                }
                htmlToolTip += "<br>";//new line
                if (behindCount == 0) {
                    htmlToolTip += " ↓ Behind: " + behindCount;
                } else {
//                    htmlToolTip += "<div style='background:red;'> ↓ Behind: " + behindCount + "</div>";
                    htmlToolTip += "<font color=orange>" + " ↓ Behind: " + behindCount + "</font>";
                }
                htmlToolTip += "<br>";//new line
                htmlToolTip += "remote: " + remoteTrackingBranch;
                htmlToolTip += "<br>";//new line
                htmlToolTip += "</html>";
                panel.getLabelBranch().setToolTipText(htmlToolTip);
            } else {
                String aheadBehind = " (↑- ↓-) ";
                labelText += aheadBehind;
                String htmlToolTip = "<html>";
                htmlToolTip += "<b>" + branchName + "</b>";//bold
                htmlToolTip += "<br>";//new line
                htmlToolTip += "no Ahead/Behind information";
                htmlToolTip += "<br>";//new line
                htmlToolTip += "no remote branch";
                htmlToolTip += "<br>";//new line
                panel.getLabelBranch().setToolTipText(htmlToolTip);
            }

        } catch (Exception ex) {
            logger.getLogger().log(Level.SEVERE, "updateBranchName", ex);
            panel.getLabelBranch().setToolTipText(ex.getMessage());
        }
        panel.getLabelBranch().setText(htmlUtils.toHtml(labelText));
    }

//    private void updateAheadBehind() {
//        logger.getLogger().fine("updateAheadBehind");
//        showProgressBar("updateAheadBehind");
//
//        List<Integer> commitsAhead;
//        try {
//            String aheadBehind = "Ahead:-" + " Behind:-";
//            panel.getLabelAheadBehind().setText(aheadBehind);
//
//            commitsAhead = jGSrepositoryModel.getCommitsAhead();
//            if (commitsAhead != null && commitsAhead.size() > 1) {
//                aheadBehind = "↑ Ahead:" + commitsAhead.get(0) + " ↓ Behind:" + commitsAhead.get(1);
//            }
//            panel.getLabelAheadBehind().setText(aheadBehind);
//
//        } catch (Exception ex) {
//            logger.getLogger().log(Level.SEVERE, "updateAheadBehind", ex);
////            showErrorDialog("updateAheadBehind", ex.getMessage());
//        }
//        try {
//            String branchName = jGSrepositoryModel.getBranchName();
//            BranchTrackingStatus branchTrackingStatus = jGSrepositoryModel.getBranchTrackingStatus(branchName);
//            int aheadCount = branchTrackingStatus.getAheadCount();
//            int behindCount = branchTrackingStatus.getBehindCount();
//            String remoteTrackingBranch = branchTrackingStatus.getRemoteTrackingBranch();
//            String htmlToolTip = "<html>";
//            htmlToolTip += "<b>" + "bold text" + "</b>";//bold
//            htmlToolTip += "<br>";//new line
//            htmlToolTip += "<i>" + "italic text" + "</i>"; //italic
//            htmlToolTip += "<br>";//new line
//            htmlToolTip += "<div style='background:green;'> green background </div>";
//            htmlToolTip += "<div style='background:red;'> red background </div>";
//            htmlToolTip += "</html>";
//            panel.getLabelAheadBehind().setToolTipText(htmlToolTip);
//
//        } catch (Exception ex) {
//            panel.getLabelAheadBehind().setToolTipText(ex.getMessage());
//        }
//
//    }
    /**
     *
     * @param usernameInput
     * @param passwordInput
     * @param dryRun
     * @param checkFetchedObjects
     * @param removeDeletedRefs
     */
    private boolean fetchRemote(String usernameInput, String passwordInput, boolean dryRun, boolean checkFetchedObjects, boolean removeDeletedRefs) {
        Git git = jGSrepositoryModel.getGit();
        try {
            FetchResult fetchRemote = utils.fetchRemote(git, dryRun, checkFetchedObjects, removeDeletedRefs, usernameInput, passwordInput);
            String resultTitle = "Fetch result";
            if (dryRun) {
                resultTitle = "Fetch preview";
            }
            boolean showFetchResult = jGSdialogFactory.showFetchResult(resultTitle, fetchRemote);
            return showFetchResult;
        } catch (Exception ex) {
            logger.getLogger().log(Level.SEVERE, "fetchRemote", ex);
            showErrorDialog("fetchRemote", ex.getMessage());
        }
        return false;
    }

    /**
     *
     * @param usernameInput
     * @param passwordInput
     * @return
     */
    private boolean pushRemote(String usernameInput, String passwordInput, boolean dryRun) {
        Git git = jGSrepositoryModel.getGit();

        try {
            Iterable<PushResult> pushResults = utils.pushRemote(git, dryRun, usernameInput, passwordInput);
            String resultTitle = "Push result";
            if (dryRun) {
                resultTitle = "Push preview";
            }
            boolean showPushResult = jGSdialogFactory.showPushResults(resultTitle, pushResults);
            return showPushResult;

        } catch (Exception ex) {
            logger.getLogger().log(Level.SEVERE, "pushRemote", ex);
            showErrorDialog("fetchRemote", ex.getMessage());
        }
        return false;
    }

//    private void getWorkingTree() {
//        logger.getLogger().fine("getWorkingTree");
//        showProgressBar("getWorkingTree");
//        bc.getWorkingTree(new IJGScallbackListString() {
//            @Override
//            public void onSuccess(List<String> result) {
//                List<String> paths = result;
//                for (String path : paths) {
//                    System.out.println(path);
//                }
//                hideProgressBar();
//            }
//
//            @Override
//            public void onError(Exception ex) {
//                ex.printStackTrace();
//                showErrorDialog("getWorkingTree", "getWorkingTree ERROR:\n" + ex.getMessage());
//                hideProgressBar();
//            }
//        });
//
//    }
    private Map<String, String> getUserPasswordParameters() {
        String path = jGSrepositoryModel.getDirectoryFromRepositoryName();

        String username = JGSsettings.getINSTANCE().getUsername(path);
        String password = JGSsettings.getINSTANCE().getPassword(path);
        JGSrecent remoteSettings = JGSsettings.getINSTANCE().getRemoteSettings(path);
        Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("Username", username);
        parameters.put("Password", password);
        return parameters;
    }

    private Map<String, Boolean> getFetchOptions() {
        Map<String, Boolean> options = new LinkedHashMap<>();
        options.put("dryrun", false);
        options.put("CheckFetchedObjects", false);
        options.put("RemoveDeletedRefs", false);
        return options;
    }

    private Map<String, Boolean> getPushOptions() {
        Map<String, Boolean> options = new LinkedHashMap<>();
        options.put("dryrun", false);
        return options;
    }

//    private void updateThreads(List<String> threads) {
//        int size = threads.size();
//        panel.getLabelThreads().setText("Threads: " + size);
//        String namesToolTips = "<html><div style='background:green;'>no threads running</div></html>";
//        if (size > 0) {
//            namesToolTips = "<html>";
//            for (String name : threads) {
//                namesToolTips += name + "<br>";
//            }
//            namesToolTips += "</html>";
//        }
//        panel.getLabelThreads().setToolTipText(namesToolTips);
//    }
    /**
     *
     * @param usernameInput
     * @param passwordInput
     * @param uriInput
     */
    private void saveRemoteCredentials(String usernameInput, String passwordInput, String uriInput) {
        String path = jGSrepositoryModel.getDirectoryFromRepositoryName();
        JGSsettings.getINSTANCE().setUserAndPassword(path, usernameInput, passwordInput, uriInput);
    }

    /**
     *
     * @param usernameInput
     * @param passwordInput
     */
    private void saveRemoteCredentials(String usernameInput, String passwordInput) {
        String path = jGSrepositoryModel.getDirectoryFromRepositoryName();
        JGSsettings.getINSTANCE().setUserAndPassword(path, usernameInput, passwordInput);
    }

    private String getSelectedTabTitle(JTabbedPane tabbedPane) {
        String tabTitle = "";
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex >= 0) {
            tabTitle = tabbedPane.getTitleAt(selectedIndex);
        }
        return tabTitle;
    }

    private void closeTab(String tabTitle) {
        logger.getLogger().fine("closeTab " + tabTitle);
        removeSubTab(tabTitle);
    }

    private void openTab(String tabTitle) {
        logger.getLogger().fine("openTab " + tabTitle);
        addSubTab(tabTitle);
    }

    private JPopupMenu getTabRightClickMenu(String selectedTabTitle) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem closer = getCloseMenuItem(selectedTabTitle);
        menu.add(closer);

        menu.add(new JSeparator());

        List<String> openTabTitles = getOpenTabTitles();
        for (String openTabTitle : openTabTitles) {
            if (!openTabTitle.equals(selectedTabTitle)) {
                JMenuItem closeitem = getCloseMenuItem(openTabTitle);
                menu.add(closeitem);
            }
        }

        menu.add(new JSeparator());

        List<String> closedTabTitles = getClosedTabTitles();
        for (String closedTabTitle : closedTabTitles) {
            JMenuItem openitem = getOpenMenuItem(closedTabTitle);
            menu.add(openitem);
        }
        return menu;
    }

    private JMenuItem getCloseMenuItem(String selectedTabTitle) {
        JMenuItem closer = new JMenuItem(new AbstractAction("Close " + selectedTabTitle) {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeTab(selectedTabTitle);
            }
        });
        return closer;
    }

    private JMenuItem getOpenMenuItem(String selectedTabTitle) {
        JMenuItem opener = new JMenuItem(new AbstractAction("Open " + selectedTabTitle) {
            @Override
            public void actionPerformed(ActionEvent e) {
                openTab(selectedTabTitle);
            }
        });
        return opener;
    }

    private List<String> getOpenTabTitles() {
        List<String> openTabTitles = new ArrayList<>();
        for (IJGSsubTabController subController : activeSubControllers) {
            String name = subController.getName();
            openTabTitles.add(name);
        }
        return openTabTitles;
    }

    private List<String> getClosedTabTitles() {
        List<String> closedTabTitles = new ArrayList<>();
        for (IJGSsubTabController subController : availableSubControllers) {
            if (!activeSubControllers.contains(subController)) {
                String name = subController.getName();
                closedTabTitles.add(name);
            }
        }
        return closedTabTitles;
    }

    @Override
    public void deconstruct() {
        String className = this.getClass().getName();
        System.out.println(className + " deconstruct");
        for (IJGSsubTabController subController : activeSubControllers) {
            System.out.println("deconstruct SubTab: " + subController.getName());
            subController.deconstruct();
        }
        activeSubControllers = null;
        jGSbranchesController = null;
        jGStagsController = null;
        jGSstagingController = null;
        jGScurrentDiffController = null;
        jGSignoredController = null;
        jGShistoryController = null;
        jGSgraphController = null;
        jGSconfigController = null;

        panel = null;
        //destroy bc only here
//        bc.removeReceiver(this);
//        bc.deconstruct();
//        bc = null;
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
