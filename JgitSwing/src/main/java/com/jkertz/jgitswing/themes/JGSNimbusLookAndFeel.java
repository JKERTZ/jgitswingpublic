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

import javax.swing.plaf.nimbus.NimbusLookAndFeel;

/**
 *
 * @author jkertz
 */
public class JGSNimbusLookAndFeel extends NimbusLookAndFeel {

    @Override
    public String getName() {
        return "JGSNimbusLookAndFeel";
    }

    @Override
    public String getID() {
        return "JGSNimbusLookAndFeel";
    }

    @Override
    public String getDescription() {
        return "JGS Nimbus Look and Feel";
    }

    @Override
    public void initialize() {
        super.initialize();
        JGSNimbusTheme.install(this);
    }

    @Override
    public void uninitialize() {
        super.uninitialize();
        JGSNimbusTheme.uninstall(this);
    }

}
