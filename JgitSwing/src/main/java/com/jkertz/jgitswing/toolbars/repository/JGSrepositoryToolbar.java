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
package com.jkertz.jgitswing.toolbars.repository;

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
public final class JGSrepositoryToolbar extends JGScommonToolbar {

    private final IJGSrepositoryToolbar receiver;
    private final JLabel labelBranch;

    public JGSrepositoryToolbar(IJGSrepositoryToolbar receiver) {
        super();
        this.receiver = receiver;
        labelBranch = new JLabel("branch");
        labelBranch.setBorder(new EtchedBorder());

        JButton buttonRefresh = new JButton("↺ Refresh");
        JButton buttonFetch = new JButton("⇣ Fetch");
        JButton buttonPull = new JButton("⇓ Pull");
        JButton buttonPush = new JButton("⇑ Push");
        JButton buttonPushAndFetch = new JButton("⇑⇣ Push and Fetch");

        buttonRefresh.addActionListener(getActionListenerRefresh());
        buttonFetch.addActionListener(getActionListenerFetch());
        buttonPull.addActionListener(getActionListenerPull());
        buttonPush.addActionListener(getActionListenerPush());
        buttonPushAndFetch.addActionListener(getActionListenerPushAndFetch());

        this.add(buttonRefresh);
        this.add(buttonFetch);
        this.add(buttonPull);
        this.add(buttonPush);
        this.add(buttonPushAndFetch);
        this.add(labelBranch);
    }

    public JLabel getLabelBranch() {
        return labelBranch;
    }

    private ActionListener getActionListenerRefresh() {
        ActionListener actionListener = (ActionEvent e) -> {
            receiver.onRepositoryToolbarClickedRefresh();
        };
        return actionListener;
    }

    private ActionListener getActionListenerFetch() {
        ActionListener actionListener = (ActionEvent e) -> {
            receiver.onRepositoryToolbarClickedFetch();
        };
        return actionListener;
    }

    private ActionListener getActionListenerPull() {
        ActionListener actionListener = (ActionEvent e) -> {
            receiver.onRepositoryToolbarClickedPull();
        };
        return actionListener;
    }

    private ActionListener getActionListenerPush() {
        ActionListener actionListener = (ActionEvent e) -> {
            receiver.onRepositoryToolbarClickedPush();
        };
        return actionListener;
    }

    private ActionListener getActionListenerPushAndFetch() {
        ActionListener actionListener = (ActionEvent e) -> {
            receiver.onRepositoryToolbarClickedPushAndFetch();
        };
        return actionListener;
    }

}
