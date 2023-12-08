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

import com.jkertz.jgitswing.tabs.common.JGSuiUtils;
import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 *
 * @author jkertz
 */
public class JGScheckoutDialog {

    private final JTextField sourceInput;
    private final JTextField targetInput;

    private final String sourceBranch;
    private String targetBranch;

    private final Component parent;

    public JGScheckoutDialog(Component parent, String sourceBranch) {
        this.sourceBranch = sourceBranch;
        this.parent = parent;
        this.targetBranch = JGSuiUtils.getINSTANCE().getRemotePureBranchName(sourceBranch);
        this.sourceInput = new JTextField();
        this.targetInput = new JTextField();
    }

    public boolean show() {
        JPanel myPanel = getPanel();
        String title = "Checkout";
        int result = JOptionPane.showConfirmDialog(parent, myPanel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        targetBranch = targetInput.getText();
        return result == JOptionPane.OK_OPTION;
    }

    public String getTargetBranch() {
        return targetBranch;
    }

    private JPanel getPanel() {
        JPanel myPanel = new JPanel();
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.PAGE_AXIS));
        myPanel.add(getSourcePanel());
        myPanel.add(getTargetPanel());

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
        sectionPanel.add(JGSdialogUtils.getINSTANCE().getLabeledInput("Target", targetInput, targetBranch, false));
        return sectionPanel;
    }

}
