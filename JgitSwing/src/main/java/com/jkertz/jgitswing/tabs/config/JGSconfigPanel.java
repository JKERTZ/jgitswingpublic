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
package com.jkertz.jgitswing.tabs.config;

import com.jkertz.jgitswing.callback.IJGScallbackChain;
import com.jkertz.jgitswing.tabs.common.JGScommonPanel;
import com.jkertz.jgitswing.toolbars.config.IJGSconfigToolbar;
import com.jkertz.jgitswing.toolbars.config.JGSconfigToolbar;
import com.jkertz.jgitswing.widgets.config.IJGSconfigWidget;
import com.jkertz.jgitswing.widgets.config.JGSconfigWidget;
import java.awt.BorderLayout;
import java.util.Map;

/**
 *
 * @author jkertz
 */
public class JGSconfigPanel extends JGScommonPanel implements IJGSconfigWidget, IJGSconfigToolbar {

    private final IJGSconfigPanel receiver;
    private final JGSconfigWidget jGSconfigWidget;
    private final JGSconfigToolbar jGSconfigToolbar;

    protected JGSconfigPanel(IJGSconfigPanel receiver) {
        super(new BorderLayout());
        this.receiver = receiver;

        jGSconfigWidget = new JGSconfigWidget(this);
        jGSconfigToolbar = new JGSconfigToolbar(this);

        this.add(jGSconfigToolbar, BorderLayout.NORTH);
        this.add(jGSconfigWidget, BorderLayout.CENTER);
    }

    public void updateConfigTree(Map<String, Map<String, Map<String, String>>> configInfoMap, IJGScallbackChain callback) {
        jGSconfigWidget.updateConfigTree(configInfoMap, callback);
    }

    @Override
    public void onConfigToolbarClickedEditConfig() {
        receiver.onConfigPanelClickedEditConfig();
    }

    @Override
    public void onConfigToolbarClickedFixRemote() {
        receiver.onConfigToolbarClickedFixRemote();
    }

}
