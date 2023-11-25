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
package com.jkertz.jgitswing.toolbars.tags;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import com.jkertz.jgitswing.toolbars.common.JGScommonToolbar;

/**
 *
 * @author jkertz
 */
public class JGStagsToolbar extends JGScommonToolbar {

    private final IJGStagsToolbar receiver;

    public JGStagsToolbar(IJGStagsToolbar receiver) {
        super();
        this.receiver = receiver;
        JButton buttonShow5 = new JButton("show 5");
        JButton buttonShow100 = new JButton("show 100");
        JButton buttonShowAll = new JButton("show all");
        JButton buttonPushTags = new JButton("push tags");

        buttonShow5.addActionListener(getActionListenerShow5());
        buttonShow100.addActionListener(getActionListenerShow100());
        buttonShowAll.addActionListener(getActionListenerShowAll());
        buttonPushTags.addActionListener(getActionListenerPushTags());

        this.add(buttonShow5);
        this.add(buttonShow100);
        this.add(buttonShowAll);
        this.add(buttonPushTags);
    }

    private ActionListener getActionListenerPushTags() {
        ActionListener actionListener = (ActionEvent e) -> {
            receiver.onTagsToolbarClickedPushTags();
        };
        return actionListener;
    }

    private ActionListener getActionListenerShow5() {
        ActionListener actionListener = (ActionEvent e) -> {
            receiver.onTagsToolbarClickedShow5();
        };
        return actionListener;
    }

    private ActionListener getActionListenerShow100() {
        ActionListener actionListener = (ActionEvent e) -> {
            receiver.onTagsToolbarClickedShow100();
        };
        return actionListener;
    }

    private ActionListener getActionListenerShowAll() {
        ActionListener actionListener = (ActionEvent e) -> {
            receiver.onTagsToolbarClickedShowAll();
        };
        return actionListener;
    }

}
