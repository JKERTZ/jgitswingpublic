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
package com.jkertz.jgitswing.toolbars.log;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JComboBox;
import com.jkertz.jgitswing.logger.JGSlogger;
import com.jkertz.jgitswing.toolbars.common.JGScommonToolbar;

/**
 *
 * @author jkertz
 */
public class JGSlogToolbar extends JGScommonToolbar {

    private final IJGSlogToolbar receiver;
    private JComboBox dropDown;

    public JGSlogToolbar(IJGSlogToolbar receiver) {
        super();
        this.receiver = receiver;
        dropDown = new JComboBox();
        List<Level> availableLevels = JGSlogger.getINSTANCE().getAvailableLevels();
        for (Level level : availableLevels) {
            dropDown.addItem(level);
        }
        dropDown.addActionListener(getActionListenerLevelDowpdown());
        //TODO: set loglevel matching to logger
        this.add(dropDown);
    }

    private ActionListener getActionListenerLevelDowpdown() {
        ActionListener actionListener = (ActionEvent e) -> {
            Level selectedItem = (Level) dropDown.getSelectedItem();
            receiver.onLogToolbarLevelChanged(selectedItem);
        };
        return actionListener;
    }

    public void setLoglevel(Level level) {
        dropDown.setSelectedItem(level);
    }
}
