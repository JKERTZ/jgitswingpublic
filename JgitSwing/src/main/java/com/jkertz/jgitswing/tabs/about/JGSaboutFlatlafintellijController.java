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

import com.jkertz.jgitswing.tabs.common.IJGSsubTabController;

/**
 *
 * @author jkertz
 */
public class JGSaboutFlatlafintellijController implements IJGSsubTabController {

    private final String name = "about FlatLaf Intellij";
    private final JGSaboutFlatlafintellijPanel panel;

    public JGSaboutFlatlafintellijController() {
        this.panel = new JGSaboutFlatlafintellijPanel();
    }

    @Override
    public JGSaboutFlatlafintellijPanel getPanel() {
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
