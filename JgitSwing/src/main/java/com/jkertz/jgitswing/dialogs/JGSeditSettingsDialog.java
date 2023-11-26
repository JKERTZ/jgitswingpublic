/*
 * Copyright (C) 2023 JKERTZ
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

import com.jkertz.jgitswing.model.JGSrecent;
import com.jkertz.jgitswing.model.JGSsetting;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Jürgen Kertz
 */
public class JGSeditSettingsDialog {

    private JGSsetting setting;
    private JFrame parentFrame;
    private JTextField inputTheme;
    private JDialog newdialog;
    private boolean dialogResultOK = false;

    public JGSeditSettingsDialog(JGSsetting oldSetting) {
        this.setting = oldSetting;
    }

    public boolean show(JFrame parentFrame) {
        this.parentFrame = parentFrame;
//        Frame rootFrame = JOptionPane.getRootFrame();
        final int x = parentFrame.getX();
        final int y = parentFrame.getY();
        final int parentWidth = parentFrame.getWidth();
        final int parentHeight = parentFrame.getHeight();
//        final int locationX = (x + parentWidth) / 2;
//        final int locationY = (y + parentHeight) / 2;
        final int locationX = (x + 10);
        final int locationY = (y + 10);

        newdialog = new JDialog(parentFrame, "Dialog ", true);
        newdialog.setContentPane(getPanel());
        newdialog.setLocation(locationX, locationY);
        newdialog.pack();//adapt dialog size to content
        newdialog.setVisible(true);//waits here until closed

        return dialogResultOK;
    }

    public JGSsetting getSetting() {
        return setting;
    }

    private JPanel getPanel() {
        JPanel myPanel = new JPanel();
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));

        myPanel.add(getThemePanel());
        myPanel.add(getRecentsPanel());
        myPanel.add(getButtonPanel());

//        myPanel.add(jScrollPane);
        return myPanel;
    }

    private JPanel getButtonPanel() {
        JPanel nameValuePanel = new JPanel();

        JButton jButtonOK = new JButton("OK");
        jButtonOK.addActionListener(getActionListenerButtonOK());
        nameValuePanel.add(jButtonOK);

        JButton jButtonCancel = new JButton("Cancel");
        jButtonCancel.addActionListener(getActionListenerButtonCancel());
        nameValuePanel.add(jButtonCancel);

        return nameValuePanel;
    }

    private ActionListener getActionListenerButtonOK() {
        ActionListener actionListener = (ActionEvent e) -> {
//            if (validateForm()) {
//                dialogResultOK = true;
//                targetDirectory = inputTarget.getText();
//                uri = inputURI.getText();
//                username = inputUsername.getText();
//                password = inputPassword.getText();
//
//            }
            dialogResultOK = true;
            newdialog.setVisible(false);
        };
        return actionListener;
    }

    private ActionListener getActionListenerButtonCancel() {
        ActionListener actionListener = (ActionEvent e) -> {
            dialogResultOK = false;
            newdialog.setVisible(false);
        };
        return actionListener;
    }

    private JPanel getThemePanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        TitledBorder sectionBorder = new TitledBorder("Theme");
        panel.setBorder(sectionBorder);
        panel.add(getThemeEditPanel());

        return panel;
    }

    private JPanel getRecentsPanel() {
//        JScrollPane jScrollPane = new JScrollPane();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        TitledBorder sectionBorder = new TitledBorder("Recents");
        panel.setBorder(sectionBorder);
//        panel.setAlignmentX(JPanel.RIGHT_ALIGNMENT);
        Set<JGSrecent> recents = setting.getRecents();
        for (JGSrecent recent : recents) {
            panel.add(getRecentEditPanel(recent));
        }
//        jScrollPane.add(panel);
        return panel;
    }

    private JPanel getThemeEditPanel() {

        return JGSdialogUtils.getINSTANCE().getLabeledInput("Theme", inputTheme, setting.getTheme(), true);

//        JPanel nameValuePanel = new JPanel();
//        nameValuePanel.add(new JLabel("Theme"));
//        inputTheme = new JTextField();
//        inputTheme.setColumns(20);
//        nameValuePanel.add(inputTheme);
//        return nameValuePanel;
    }

    private JPanel getRecentEditPanel(JGSrecent recent) {
        String localPath = recent.getLocalPath();
        String remoteUsername = recent.getRemoteUsername();
        String remotePassword = recent.getRemotePassword();
        String uri = recent.getUri();

//        JPanel panel = new JPanel(new GridLayout(0, 1));
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        TitledBorder sectionBorder = new TitledBorder(localPath);
        panel.setBorder(sectionBorder);

        panel.add(JGSdialogUtils.getINSTANCE().getLabeledInput("localPath", new JTextField(), localPath, false));
        panel.add(JGSdialogUtils.getINSTANCE().getLabeledInput("remoteUsername", new JTextField(), remoteUsername, false));
        panel.add(JGSdialogUtils.getINSTANCE().getLabeledInput("remotePassword", new JTextField(), remotePassword, false));
        panel.add(JGSdialogUtils.getINSTANCE().getLabeledInput("uri", new JTextField(), uri, false));

//        JPanel panel = new JPanel();
//        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
//
//        TitledBorder sectionBorder = new TitledBorder(localPath);
//        panel.setBorder(sectionBorder);
////        panel.setAlignmentX(JPanel.RIGHT_ALIGNMENT);
//        JPanel localPathPanel = getNameValuePanel("localPath", localPath, new JTextField());
//        localPathPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
//        panel.add(localPathPanel);
//        panel.add(getNameValuePanel("localPath", localPath, new JTextField()));
//        panel.add(getNameValuePanel("remoteUsername", remoteUsername, new JTextField()));
//        panel.add(getNameValuePanel("remotePassword", remotePassword, new JTextField()));
//        panel.add(getNameValuePanel("uri", uri, new JTextField()));
        return panel;

    }

    private JPanel getNameValuePanel(String name, String value, JTextField input) {
        JPanel nameValuePanel = new JPanel();
//        nameValuePanel.setLayout(new BoxLayout(nameValuePanel, BoxLayout.LINE_AXIS));
        nameValuePanel.add(new JLabel(name));
//        nameValuePanel.add(Box.createHorizontalGlue());
        input = new JTextField();
        input.setColumns(20);
        input.setText(value);
        nameValuePanel.add(input);
//        nameValuePanel.setAlignmentX(JPanel.RIGHT_ALIGNMENT);
        return nameValuePanel;
    }

    private Box getNameValueBox(String name, String value, JTextField input) {
        Box nameValueBox = Box.createHorizontalBox();
        nameValueBox.add(new JLabel(name));
        input = new JTextField();
        input.setColumns(20);
        input.setText(value);
        nameValueBox.add(input);

        return nameValueBox;
    }
}