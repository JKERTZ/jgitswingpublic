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
package com.jkertz.jgitswing.tabs.graph;

import java.awt.BorderLayout;
import com.jkertz.jgitswing.tabs.common.JGScommonPanel;
import com.jkertz.jgitswing.toolbars.graph.IJGSgraphToolbar;
import com.jkertz.jgitswing.toolbars.graph.JGSgraphToolbar;
import com.jkertz.jgitswing.widgets.graph.IJGSgraphWidget;
import com.jkertz.jgitswing.widgets.graph.JGSgraphPane;
import com.jkertz.jgitswing.widgets.graph.JGSgraphWidget;

/**
 *
 * @author jkertz
 */
public class JGSgraphPanel extends JGScommonPanel implements IJGSgraphWidget, IJGSgraphToolbar {

    private final IJGSgraphPanel receiver;
    private final JGSgraphWidget jGSgraphWidget;
    private final JGSgraphToolbar jGSgraphToolbar;

    protected JGSgraphPanel(IJGSgraphPanel receiver) {
        super(new BorderLayout());
        this.receiver = receiver;

        jGSgraphToolbar = new JGSgraphToolbar(this);
        jGSgraphWidget = new JGSgraphWidget(this);

        this.add(jGSgraphToolbar, BorderLayout.NORTH);
        this.add(jGSgraphWidget, BorderLayout.CENTER);
    }

    public JGSgraphPane getGraphPane() {
        return jGSgraphWidget.getGraphPane();
    }

    @Override
    public void onGraphToolbarClickedShow100() {
        receiver.onGraphPanelClickedShow100();
    }

    @Override
    public void onGraphToolbarClickedShowAll() {
        receiver.onGraphPanelClickedShowAll();
    }

}
