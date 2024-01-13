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

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 *
 * @author jkertz
 */
public class JGScloneRepositoryDialog {

    private JFrame parentFrame;
    private JDialog newdialog;
    private boolean dialogResultOK = false;

    private String uri = null;
    private String username = null;
    private String password = null;
    private String targetDirectory = null;

    private JTextField inputTarget;
    private JTextField inputURI;
    private JTextField inputUsername;
    private JTextField inputPassword;

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

        newdialog = new JDialog(parentFrame, "Clone Repository ", true);
        newdialog.setContentPane(getPanel());
        newdialog.setLocation(locationX, locationY);
        newdialog.pack();//adapt dialog size to content
        newdialog.setVisible(true);//waits here until closed

        return dialogResultOK;
    }

    public String getUri() {
        return uri;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getTargetDirectory() {
        return targetDirectory;
    }

    private JPanel getPanel() {
        JPanel myPanel = new JPanel();
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.PAGE_AXIS));
        myPanel.add(getSourcePanel());
        myPanel.add(getTargetPanel());
        myPanel.add(getValidationButtonPanel());

        return myPanel;
    }

    private JPanel getValidationButtonPanel() {
        JPanel sectionPanel = new JPanel(new GridLayout(0, 1));
        sectionPanel.add(getButtonPanel());
        return sectionPanel;
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
            if (validateForm()) {
                dialogResultOK = true;
                targetDirectory = inputTarget.getText();
                uri = inputURI.getText();
                username = inputUsername.getText();
                password = inputPassword.getText();

                newdialog.setVisible(false);
            }
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

    private JPanel getSourcePanel() {
        JPanel sectionPanel = new JPanel(new GridLayout(0, 1));
        TitledBorder sectionBorder = new TitledBorder("Clone Source");
        sectionPanel.setBorder(sectionBorder);
        sectionPanel.add(getSourceURIPanel());
        sectionPanel.add(getSourceUsernamePanel());
        sectionPanel.add(getSourcePasswordPanel());

        return sectionPanel;
    }

    private JPanel getSourceUsernamePanel() {
        JPanel nameValuePanel = new JPanel();
        nameValuePanel.add(new JLabel("Username"));
        inputUsername = new JTextField();
        inputUsername.setColumns(20);
        inputUsername.addActionListener(getActionListenerValidate());
        nameValuePanel.add(inputUsername);
        return nameValuePanel;
    }

    private ActionListener getActionListenerValidate() {
        ActionListener actionListener = (ActionEvent e) -> {
            validateForm();
        };
        return actionListener;
    }

    private JPanel getSourcePasswordPanel() {
        JPanel nameValuePanel = new JPanel();
        nameValuePanel.add(new JLabel("Password"));
        inputPassword = new JTextField();
        inputPassword.setColumns(20);
        inputPassword.addActionListener(getActionListenerValidate());
        nameValuePanel.add(inputPassword);
        return nameValuePanel;
    }

    private JPanel getSourceURIPanel() {
        JPanel nameValuePanel = new JPanel();
        nameValuePanel.add(new JLabel("URI"));
        inputURI = new JTextField();
        inputURI.setColumns(40);
        inputURI.addActionListener(getActionListenerInputURI());
        nameValuePanel.add(inputURI);

        JButton chooseDirectoryButton = new JButton("choose local URI");
        chooseDirectoryButton.addActionListener(getActionListenerChooseLocalURI());
        nameValuePanel.add(chooseDirectoryButton);

        return nameValuePanel;
    }

    private ActionListener getActionListenerInputURI() {
        ActionListener actionListener = (ActionEvent e) -> {
            String text = inputURI.getText();
            if (text != null && !text.isEmpty()) {

            }
        };
        return actionListener;
    }

    private ActionListener getActionListenerChooseLocalURI() {
        ActionListener actionListener = (ActionEvent e) -> {
//            System.out.println("getActionListenerChooseLocalURI");
            JGSopenRepositoryFileChooser fileChooser = new JGSopenRepositoryFileChooser();
//            fileChooser.setDialogTitle("Source directory");
//            fileChooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fileChooser.showOpenDialog(parentFrame);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                String directory = file.getAbsolutePath();
//                System.out.println(directory);
                inputURI.setText(directory);
            } else {
//                System.out.println("File access cancelled by user.");
            }
        };
        return actionListener;
    }

    private JPanel getTargetPanel() {
        JPanel sectionPanel = new JPanel(new GridLayout(0, 1));
        TitledBorder sectionBorder = new TitledBorder("Target directory");
        sectionPanel.setBorder(sectionBorder);
        sectionPanel.add(getTargetSelectionPanel());
        return sectionPanel;
    }

    private JPanel getTargetSelectionPanel() {
        JPanel nameValuePanel = new JPanel();
        nameValuePanel.add(new JLabel("Path"));

        inputTarget = new JTextField();
        inputTarget.setColumns(40);
        nameValuePanel.add(inputTarget);

        JButton chooseDirectoryButton = new JButton("choose Directory");
        chooseDirectoryButton.addActionListener(getActionListenerChooseDirectory());
        nameValuePanel.add(chooseDirectoryButton);

        return nameValuePanel;
    }

    private ActionListener getActionListenerChooseDirectory() {
        ActionListener actionListener = (ActionEvent e) -> {
            System.out.println("ActionListenerChooseDirectory");
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Target directory");
            fileChooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fileChooser.showOpenDialog(parentFrame);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                String directory = file.getAbsolutePath();
                System.out.println(directory);
                inputTarget.setText(directory);
            } else {
                System.out.println("File access cancelled by user.");
            }
        };
        return actionListener;
    }

    private boolean validateForm() {
        boolean isRemoteRepo = JGSvalidationUtils.getINSTANCE().isRemoteRepo(inputURI.getText());

        boolean inputUsernameValid = !isRemoteRepo || JGSvalidationUtils.getINSTANCE().isValidTextInput(inputUsername);
        boolean inputPasswordValid = !isRemoteRepo || JGSvalidationUtils.getINSTANCE().isValidTextInput(inputPassword);
        boolean inputURIValid = JGSvalidationUtils.getINSTANCE().isValidCloneURI(inputURI);
        boolean inputTargetValid = JGSvalidationUtils.getINSTANCE().isValidCloneTarget(inputTarget);

        boolean isValid = (inputUsernameValid && inputPasswordValid && inputURIValid && inputTargetValid);
        return isValid;
    }

}
