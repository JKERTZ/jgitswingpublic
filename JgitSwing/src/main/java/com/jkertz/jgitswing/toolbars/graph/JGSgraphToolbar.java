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
package com.jkertz.jgitswing.toolbars.graph;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import com.jkertz.jgitswing.toolbars.common.JGScommonToolbar;

/**
 *
 * @author jkertz
 */
public class JGSgraphToolbar extends JGScommonToolbar {

    private final IJGSgraphToolbar receiver;

    public JGSgraphToolbar(IJGSgraphToolbar receiver) {
        super();
        this.receiver = receiver;
        JButton buttonShow100 = new JButton("show 100");
        JButton buttonShowAll = new JButton("show all");

        buttonShow100.addActionListener(getActionListenerShow100());
        buttonShowAll.addActionListener(getActionListenerShowAll());

        this.add(buttonShow100);
        this.add(buttonShowAll);
    }

    private ActionListener getActionListenerShow100() {
        ActionListener actionListener = (ActionEvent e) -> {
            receiver.onGraphToolbarClickedShow100();
        };
        return actionListener;
    }

    private ActionListener getActionListenerShowAll() {
        ActionListener actionListener = (ActionEvent e) -> {
            receiver.onGraphToolbarClickedShowAll();
        };
        return actionListener;
    }

}
