/*
 * Copyright (C) 2023 jkertz
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
package com.jkertz.jgitswing.dialogs;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.PushResult;

/**
 *
 * @author jkertz
 */
public class JGSresultDialog {

    JGSdialogUtils dialogUtils;

    public JGSresultDialog() {
        this.dialogUtils = JGSdialogUtils.getINSTANCE();
    }

    public boolean show(String title, PullResult pullResult) {
        JPanel dialogPanel = dialogUtils.getDialogPanel(pullResult);
        int result = JOptionPane.showConfirmDialog(null, dialogPanel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.OK_OPTION;
    }

    public boolean show(String title, FetchResult fetchResult) {
        JPanel dialogPanel = dialogUtils.getDialogPanel(fetchResult);
        int result = JOptionPane.showConfirmDialog(null, dialogPanel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.OK_OPTION;
    }

    public boolean showPushResults(String title, Iterable<PushResult> pushResults) {
        JPanel dialogPanel = dialogUtils.getDialogPanelPushResults(pushResults);
        int result = JOptionPane.showConfirmDialog(null, dialogPanel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.OK_OPTION;
    }

    public boolean showMergeResult(String title, MergeResult mergeResult) {
        JPanel dialogPanel = dialogUtils.getDialogPanel(mergeResult);
        int result = JOptionPane.showConfirmDialog(null, dialogPanel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.OK_OPTION;
    }

}
