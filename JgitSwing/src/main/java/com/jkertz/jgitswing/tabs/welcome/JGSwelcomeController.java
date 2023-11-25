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
package com.jkertz.jgitswing.tabs.welcome;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import com.jkertz.jgitswing.tabs.about.JGSaboutFlatlafController;
import com.jkertz.jgitswing.tabs.about.JGSaboutFlatlafintellijController;
import com.jkertz.jgitswing.tabs.about.JGSaboutJgitController;
import com.jkertz.jgitswing.tabs.about.JGSaboutJgituiController;
import com.jkertz.jgitswing.tabs.about.JGSaboutJgsController;
import com.jkertz.jgitswing.tabs.about.JGSaboutSlf4jController;
import com.jkertz.jgitswing.tabs.common.IJGSsubTabController;

/**
 *
 * @author jkertz
 */
public class JGSwelcomeController implements IJGSsubTabController {

    private final String name = "Welcome";
    private final JGSwelcomePanel panel;
    private final List<IJGSsubTabController> subControllers;

    public JGSwelcomeController() {
        subControllers = new ArrayList<>();

        panel = new JGSwelcomePanel();
        addSubTabs();

    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void refresh() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void addSubTabs() {
        addSubTab(new JGSaboutJgsController());
        addSubTab(new JGSaboutJgitController());
        addSubTab(new JGSaboutJgituiController());
        addSubTab(new JGSaboutSlf4jController());
        addSubTab(new JGSaboutFlatlafController());
        addSubTab(new JGSaboutFlatlafintellijController());

    }

    private void addSubTab(IJGSsubTabController subtab) {
        subControllers.add(subtab);
        panel.addTab(subtab.getName(), subtab.getPanel());
    }

    @Override
    public void deconstruct() {

    }

    @Override
    protected void finalize() throws Throwable {
        try {
            // Cleanup operations
            String className = this.getClass().getName();
            System.out.println(className + " finalize");

        } finally {
            super.finalize();
        }
    }

}
