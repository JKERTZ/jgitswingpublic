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
package com.jkertz.jgitswing.themes;

import javax.swing.UIDefaults;
import javax.swing.plaf.metal.MetalLookAndFeel;
import static javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme;
import javax.swing.plaf.metal.OceanTheme;

/**
 *
 * @author jkertz
 */
public class LightOceanLookAndFeel extends MetalLookAndFeel {

    @Override
    public String getName() {
        return "DarkOceanLookAndFeel";
    }

    @Override
    protected void createDefaultTheme() {
        super.createDefaultTheme();
        setCurrentTheme(new OceanTheme());
    }

    @Override
    public UIDefaults getDefaults() {
        UIDefaults defaults = super.getDefaults(); //To change body of generated methods, choose Tools | Templates.
        return defaults;
    }

    @Override
    public void initialize() {
        super.initialize(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void uninitialize() {
        super.uninitialize();
        //reset default theme
        setCurrentTheme(new OceanTheme());
    }

}
