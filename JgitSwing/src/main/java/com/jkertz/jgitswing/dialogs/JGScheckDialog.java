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

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 *
 * @author jkertz
 */
public class JGScheckDialog {

    private JFrame parentFrame;
    private JDialog newdialog;
    private boolean dialogResultOK = false;

    private JTextField sourceInput;
    private JTextField targetInput;

    private String sourceBranch;
    private String targetBranch;

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

    private JPanel getPanel() {
        JPanel myPanel = new JPanel();
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.PAGE_AXIS));
        myPanel.add(getSourcePanel());
        myPanel.add(getTargetPanel());
        myPanel.add(getButtonPanel());

        return myPanel;
    }

    private JPanel getSourcePanel() {
        JPanel sectionPanel = new JPanel(new GridLayout(0, 1));
        TitledBorder sectionBorder = new TitledBorder("Checkout Source");
        sectionPanel.setBorder(sectionBorder);
        sectionPanel.add(JGSdialogUtils.getINSTANCE().getLabeledInput("Source", sourceInput, sourceBranch, true));
        return sectionPanel;
    }

    private JPanel getTargetPanel() {
        JPanel sectionPanel = new JPanel(new GridLayout(0, 1));
        TitledBorder sectionBorder = new TitledBorder("Target Branch");
        sectionPanel.setBorder(sectionBorder);
        sectionPanel.add(JGSdialogUtils.getINSTANCE().getLabeledInput("Target", targetInput, targetBranch, true));
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

}
