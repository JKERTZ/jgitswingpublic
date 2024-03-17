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

import java.awt.Component;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.PushResult;

/**
 *
 * @author jkertz
 */
public class JGSdialogFactory {

    private final JGSdialogPanelFactory dialogUtils;
    private final Component parent;

    public JGSdialogFactory(Component parent) {
        this.dialogUtils = JGSdialogPanelFactory.getINSTANCE();
        this.parent = parent;
    }

    public boolean showPullResult(String title, PullResult pullResult) {
        JPanel dialogPanel = dialogUtils.getDialogPanel(pullResult);
        int result = JOptionPane.showConfirmDialog(parent, dialogPanel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.OK_OPTION;
    }

    public boolean showFetchResult(String title, FetchResult fetchResult) {
        JPanel dialogPanel = dialogUtils.getDialogPanel(fetchResult);
        int result = JOptionPane.showConfirmDialog(parent, dialogPanel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.OK_OPTION;
    }

    public boolean showPushResults(String title, Iterable<PushResult> pushResults) {
        JPanel dialogPanel = dialogUtils.getDialogPanelPushResults(pushResults);
        int result = JOptionPane.showConfirmDialog(parent, dialogPanel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.OK_OPTION;
    }

    public boolean showMergeResult(String title, MergeResult mergeResult) {
        JPanel dialogPanel = dialogUtils.getDialogPanel(mergeResult);
        int result = JOptionPane.showConfirmDialog(parent, dialogPanel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.OK_OPTION;
    }

    public boolean showParameterMapDialog(String title, Map<String, String> parameters, boolean isReadonly) {
        Map<String, JTextField> inputMap = new HashMap<>();
        JPanel myPanel = dialogUtils.getParameterMapPanel(inputMap, parameters, isReadonly);

        int result = JOptionPane.showConfirmDialog(parent, myPanel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            for (String key : parameters.keySet()) {
                JTextField input = inputMap.get(key);
                String value = input.getText();
                parameters.put(key, value);
                System.out.println(key + " : " + value);
            }
            return true;
        }
        return false;
    }

    public boolean showParameterMapDialog(String title, Map<String, String> parameters, Map<String, Boolean> options, boolean isReadonly) {
        Map<String, JTextField> inputMap = new HashMap<>();
        Map<String, JCheckBox> optionMap = new HashMap<>();

        JPanel myPanel = dialogUtils.getParameterMapPanel(inputMap, optionMap, parameters, options, isReadonly);

        int result = JOptionPane.showConfirmDialog(parent, myPanel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            for (String key : parameters.keySet()) {
                JTextField input = inputMap.get(key);
                String value = input.getText();
                parameters.put(key, value);
                System.out.println(key + " : " + value);
            }
            for (String key : options.keySet()) {
                JCheckBox check = optionMap.get(key);
                Boolean value = check.isSelected();
                options.put(key, value);
                System.out.println(key + " : " + value);
            }

            return true;
        }
        return false;
    }

    public boolean showSectional(String title, Map<String, Map<String, Map<String, String>>> parameters, boolean isReadonly) {
        Map<String, JTextField> inputMap = new HashMap<>();

        JPanel myPanel = JGSdialogPanelFactory.getINSTANCE().getParameterMapPanelSectional(inputMap, parameters, isReadonly);

        int result = JOptionPane.showConfirmDialog(parent, myPanel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            for (String section : parameters.keySet()) {

                Map<String, Map<String, String>> subSectionMap = parameters.get(section);
                Set<String> subSections = subSectionMap.keySet();
                for (String subSection : subSections) {

                    Map<String, String> nameMap = subSectionMap.get(subSection);
                    Set<String> names = nameMap.keySet();
                    for (String name : names) {
                        String key = section + subSection + name;
                        JTextField input = inputMap.get(key);
                        String value = input.getText();
                        nameMap.put(name, value);
                    }
                }
            }

            return true;
        }
        return false;

    }

    /**
     * shows a directorychooser returns null if action is canceled by user
     *
     * @param parent
     * @param title
     * @return
     */
    public String chooseDirectory(String title) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(title);
        fileChooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fileChooser.showOpenDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String directory = file.getAbsolutePath();
            System.out.println(directory);
            return directory;

        } else {
            System.out.println("File access cancelled by user.");
            return null;
        }
    }

    public void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public void showInfoDialog(String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public String showInputDialog(String title, String message) {
        String input = JOptionPane.showInputDialog(parent, message, title, JOptionPane.QUESTION_MESSAGE);
        return input;
    }

    public boolean showConfirmDialog(String title, String message) {
        int showConfirmDialog = JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        // 0=ok, 2=cancel
        return (showConfirmDialog == 0);
    }

}
