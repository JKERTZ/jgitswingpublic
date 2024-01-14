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
package com.jkertz.jgitswing.toolbars.config;

import com.jkertz.jgitswing.toolbars.common.JGScommonToolbar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

/**
 *
 * @author jkertz
 */
public class JGSconfigToolbar extends JGScommonToolbar {

    private final IJGSconfigToolbar receiver;

    public JGSconfigToolbar(IJGSconfigToolbar receiver) {
        super();
        this.receiver = receiver;
        JButton buttonEditConfig = new JButton("edit config");
        JButton buttonFixRemote = new JButton("fix remote");

        buttonEditConfig.addActionListener(getActionListenerEditConfig());
        buttonFixRemote.addActionListener(getActionListenerFixRemote());

        this.add(buttonEditConfig);
        this.add(buttonFixRemote);
    }

    private ActionListener getActionListenerEditConfig() {
        ActionListener actionListener = (ActionEvent e) -> {
            receiver.onConfigToolbarClickedEditConfig();
        };
        return actionListener;
    }

    private ActionListener getActionListenerFixRemote() {
        ActionListener actionListener = (ActionEvent e) -> {
            receiver.onConfigToolbarClickedFixRemote();
        };
        return actionListener;
    }

}
