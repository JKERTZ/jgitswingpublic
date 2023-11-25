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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import com.jkertz.jgitswing.tabs.common.JGScommonPanel;
import com.jkertz.jgitswing.widgets.html.JGShtmlDisplayWidget;

/**
 *
 * @author jkertz
 */
public class JGSaboutFlatlafintellijPanel extends JGScommonPanel {

    private final JGShtmlDisplayWidget jGStextDisplayWidget2;
    private final JGShtmlDisplayWidget jGShtmlDisplayWidget;

    public JGSaboutFlatlafintellijPanel() {
        super(new BorderLayout());

        String text2 = "FlatLaf is open source licensed under the Apache 2.0 License\n"
                + "FlatLaf documentation on this website is Copyright © 2019-2023 FormDev Software GmbH. All rights\n"
                + "reserved. Any other use or reproduction requires written consent of FormDev Software GmbH.\n";

        jGStextDisplayWidget2 = new JGShtmlDisplayWidget("flatlafintellij/aboutFlatlafIntellij.html");
        jGShtmlDisplayWidget = new JGShtmlDisplayWidget("flatlafintellij/LICENSE");

        JPanel gridPanel = new JPanel(new GridLayout(2, 1));

        gridPanel.add(jGStextDisplayWidget2);
        gridPanel.add(jGShtmlDisplayWidget);

        this.add(gridPanel, BorderLayout.CENTER);

    }

}
