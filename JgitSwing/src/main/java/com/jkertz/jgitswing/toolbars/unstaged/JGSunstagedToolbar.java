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
package com.jkertz.jgitswing.toolbars.unstaged;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;
import com.jkertz.jgitswing.toolbars.common.JGScommonToolbar;

/**
 *
 * @author jkertz
 */
public class JGSunstagedToolbar extends JGScommonToolbar {

    private final IJGSunstagedToolbar receiver;

    public JGSunstagedToolbar(IJGSunstagedToolbar receiver) {
        super();
        this.receiver = receiver;
        JLabel label = new JLabel("unstaged");
        label.setBorder(new EtchedBorder());
        JButton buttonStage = new JButton("↓ stage");
        JButton buttonRemove = new JButton("↓ remove");
        JButton buttonStageAll = new JButton("⇓ stage all");
        JButton buttonHardReset = new JButton("⇚ hard reset");
        JButton buttonResetFile = new JButton("↺ reset file");

        buttonStage.addActionListener(getActionListenerStage());
        buttonRemove.addActionListener(getActionListenerRemove());
        buttonStageAll.addActionListener(getActionListenerStageAll());
        buttonHardReset.addActionListener(getActionListenerHardReset());
        buttonResetFile.addActionListener(getActionListenerResetFile());

        this.add(label);
        this.add(buttonStage);
        this.add(buttonRemove);
        this.add(buttonStageAll);
        this.add(buttonHardReset);
        this.add(buttonResetFile);

    }

    private ActionListener getActionListenerStage() {
        ActionListener actionListener = (ActionEvent e) -> {
            receiver.onUnstagedToolbarClickedStage();
        };
        return actionListener;
    }

    private ActionListener getActionListenerRemove() {
        ActionListener actionListener = (ActionEvent e) -> {
            receiver.onUnstagedToolbarClickedRemove();
        };
        return actionListener;
    }

    private ActionListener getActionListenerStageAll() {
        ActionListener actionListener = (ActionEvent e) -> {
            receiver.onUnstagedToolbarClickedStageAll();
        };
        return actionListener;
    }

    private ActionListener getActionListenerHardReset() {
        ActionListener actionListener = (ActionEvent e) -> {
            receiver.onUnstagedToolbarClickedHardReset();
        };
        return actionListener;
    }

    private ActionListener getActionListenerResetFile() {
        ActionListener actionListener = (ActionEvent e) -> {
            receiver.onUnstagedToolbarClickedResetFile();
        };
        return actionListener;
    }

}
