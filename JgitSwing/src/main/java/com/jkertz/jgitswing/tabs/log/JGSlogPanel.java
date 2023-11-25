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
package com.jkertz.jgitswing.tabs.log;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.logging.Level;
import com.jkertz.jgitswing.tabs.common.JGScommonPanel;
import com.jkertz.jgitswing.toolbars.log.IJGSlogToolbar;
import com.jkertz.jgitswing.toolbars.log.JGSlogToolbar;
import com.jkertz.jgitswing.widgets.JGStextDisplayWidget;

/**
 *
 * @author jkertz
 */
public class JGSlogPanel extends JGScommonPanel implements IJGSlogToolbar {

    private final IJGSlogPanel receiver;
    private final JGStextDisplayWidget jGStextDisplayWidget;
    private final JGSlogToolbar jGSlogToolbar;

    protected JGSlogPanel(IJGSlogPanel receiver) {
        super(new BorderLayout());
        this.receiver = receiver;

        jGSlogToolbar = new JGSlogToolbar(this);
        jGStextDisplayWidget = new JGStextDisplayWidget("JGSlogPanel");

        this.add(jGSlogToolbar, BorderLayout.NORTH);
        this.add(jGStextDisplayWidget, BorderLayout.CENTER);

    }

    public void addFormattedLog(String logmessage, Color color) {
        jGStextDisplayWidget.addFormattedLog(logmessage, color);
    }

    public void setLoglevel(Level level) {
        jGSlogToolbar.setLoglevel(level);
    }

    @Override
    public void onLogToolbarLevelChanged(Level selectedItem) {
        receiver.onLogPanelLevelChanged(selectedItem);
    }

}
