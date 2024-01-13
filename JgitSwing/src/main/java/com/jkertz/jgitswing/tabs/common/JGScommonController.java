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
import com.jkertz.jgitswing.dialogs.JGSresultDialog;
import com.jkertz.jgitswing.logger.JGSlogger;
import com.jkertz.jgitswing.main.JGSmainController;
import com.jkertz.jgitswing.model.IJGSrepositoryModel;
import com.jkertz.jgitswing.model.JGSrepositoryModel;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.PushResult;

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
    }

    protected void showErrorDialog(String title, String message) {
        mainController.showErrorDialog(title, message);
    }

    protected void showInfoDialog(String title, String message) {
        mainController.showInfoDialog(title, message);
    }

    protected String showInputDialog(String title, String message) {
        return mainController.showInputDialog(title, message);
    }

    protected boolean showConfirmDialog(String title, String message) {
        return mainController.showConfirmDialog(title, message);
    }

    protected String chooseDirectory(String title) {
        return mainController.chooseDirectory(title);
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

    protected boolean showMergeResult(String title, MergeResult result) {
        boolean showJGSresultDialog = new JGSresultDialog().showMergeResult(title, result);
        return showJGSresultDialog;
    }

    protected boolean showPushResult(String title, Iterable<PushResult> pushResults) {
        boolean showJGSresultDialog = new JGSresultDialog().showPushResults(title, pushResults);
        return showJGSresultDialog;
    }

    protected boolean showPullResult(String title, PullResult pullResult) {
        boolean showJGSresultDialog = new JGSresultDialog().showPullResult(title, pullResult);
        return showJGSresultDialog;
    }

    protected boolean showFetchResult(String title, FetchResult fetchResult) {
        boolean showJGSresultDialog = new JGSresultDialog().showFetchResult(title, fetchResult);
        return showJGSresultDialog;
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
        logger.getLogger().fine("onGitIndexChanged");
    }

    @Override
    public void onGitRefChanged() {
        logger.getLogger().fine("onGitRefChanged");
    }

    @Override
    public void onGitWorkingTreeModified() {
        logger.getLogger().fine("onGitWorkingTreeModified");
    }

}
