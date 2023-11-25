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
package com.jkertz.jgitswing.toolbars.staged;

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
public class JGSstagedToolbar extends JGScommonToolbar {

    private final IJGSstagedToolbar receiver;

    public JGSstagedToolbar(IJGSstagedToolbar receiver) {
        super();
        this.receiver = receiver;
        JLabel label = new JLabel("staged");
        label.setBorder(new EtchedBorder());
        JButton buttonUnstage = new JButton("↑ unstage");
        JButton buttonUnstageAll = new JButton("⇑ unstage all");
        JButton buttonCommit = new JButton("⇛ commit");

        buttonUnstage.addActionListener(getActionListenerUnstage());
        buttonUnstageAll.addActionListener(getActionListenerUnstageAll());
        buttonCommit.addActionListener(getActionListenerCommit());

        this.add(label);
        this.add(buttonUnstage);
        this.add(buttonUnstageAll);
        this.add(buttonCommit);

    }

    private ActionListener getActionListenerUnstage() {
        ActionListener actionListener = (ActionEvent e) -> {
            receiver.onStagedToolbarClickedUnstage();
        };
        return actionListener;
    }

    private ActionListener getActionListenerUnstageAll() {
        ActionListener actionListener = (ActionEvent e) -> {
            receiver.onStagedToolbarClickedUnstageAll();
        };
        return actionListener;
    }

    private ActionListener getActionListenerCommit() {
        ActionListener actionListener = (ActionEvent e) -> {
            receiver.onStagedToolbarClickedCommit();
        };
        return actionListener;
    }

}
