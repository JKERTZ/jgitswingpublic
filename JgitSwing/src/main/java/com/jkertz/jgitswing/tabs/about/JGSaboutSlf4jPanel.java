/*
 * Copyright (C) 2023 jkertz
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
package com.jkertz.jgitswing.tabs.about;

import com.jkertz.jgitswing.tabs.common.JGScommonPanel;
import com.jkertz.jgitswing.widgets.html.JGShtmlDisplayWidget;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;

/**
 *
 * @author jkertz
 */
public class JGSaboutSlf4jPanel extends JGScommonPanel {

    private final JGShtmlDisplayWidget jGStextDisplayWidget3;
    private final JGShtmlDisplayWidget jGShtmlDisplayWidget;

    public JGSaboutSlf4jPanel() {
        super(new BorderLayout());

        jGStextDisplayWidget3 = new JGShtmlDisplayWidget("/slf4j/aboutSLF4J.html");
        jGShtmlDisplayWidget = new JGShtmlDisplayWidget("/slf4j/LICENSE.txt");

        JPanel gridPanel = new JPanel(new GridLayout(2, 1));

        gridPanel.add(jGStextDisplayWidget3);
        gridPanel.add(jGShtmlDisplayWidget);

        this.add(gridPanel, BorderLayout.CENTER);
    }

}
