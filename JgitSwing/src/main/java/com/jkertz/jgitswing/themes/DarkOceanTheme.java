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

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.OceanTheme;

/**
 *
 * @author jkertz
 */
public class DarkOceanTheme extends OceanTheme {

    private final ColorUIResource primary1 = new ColorUIResource(121, 121, 125);
    private final ColorUIResource primary2 = new ColorUIResource(71, 71, 75);
    private final ColorUIResource primary3 = new ColorUIResource(99, 99, 99);
    private final ColorUIResource secondary1 = new ColorUIResource(113, 113, 113);
    private final ColorUIResource secondary2 = new ColorUIResource(91, 91, 95);
    private final ColorUIResource secondary3 = new ColorUIResource(51, 51, 55);
    private final ColorUIResource black = new ColorUIResource(222, 222, 222);
    private final ColorUIResource white = new ColorUIResource(18, 30, 49);

    @Override
    public String getName() {
        return "Dark Ocean Theme";
    }

//    @Override
//    public void addCustomEntriesToTable(UIDefaults table) {
//        super.addCustomEntriesToTable(table);
//        table.put("PropSheet.setBackground", new Color(primary2.getRGB())); //NOI18N
//        table.put("PropSheet.selectedSetBackground", new Color(primary1.getRGB())); //NOI18N
//        table.put("PropSheet.selectedSetForeground", black); //NOI18N
//        table.put("PropSheet.setForeground", black); //NOI18N
//        table.put("PropSheet.selectionBackground", primary3); //NOI18N
//        table.put("PropSheet.selectionForeground", black); //NOI18N
//
//        UIManager.put("selection.highlight", new Color(202, 152, 0));
//        UIManager.put("textArea.background", new Color(51, 51, 55));
//
//        //#232854 - menu item accelerators are too dark
//        UIManager.put("MenuItem.acceleratorForeground", new Color(198, 198, 198));
//        UIManager.put("CheckBoxMenuItem.acceleratorForeground", new Color(198, 198, 198));
//        UIManager.put("RadioButtonMenuItem.acceleratorForeground", new Color(198, 198, 198));
//
//    }
    @Override
    protected ColorUIResource getPrimary1() {
        return primary1;
    }

    @Override
    protected ColorUIResource getPrimary2() {
        return primary2;
    }

    @Override
    protected ColorUIResource getPrimary3() {
        return primary3;
    }

    @Override
    protected ColorUIResource getSecondary1() {
        return secondary1;
    }

    @Override
    protected ColorUIResource getSecondary2() {
        return secondary2;
    }

    @Override
    protected ColorUIResource getSecondary3() {
        return secondary3;
    }

    @Override
    protected ColorUIResource getWhite() {
        return white;
    }

    @Override
    protected ColorUIResource getBlack() {
        return black;
    }

//    @Override
//    public ColorUIResource getDesktopColor() {
//        return new ColorUIResource(0, 255, 0);
//    }
    @Override
    public ColorUIResource getInactiveControlTextColor() {
        return new ColorUIResource(0, 100, 100);
    }

    @Override
    public ColorUIResource getControlTextColor() {
        return new ColorUIResource(0, 150, 150);
//        return black;
    }

//    @Override
//    public ColorUIResource getMenuDisabledForeground() {
//        return new ColorUIResource(0, 255, 255);
//    }
//
//    @Override
//    public FontUIResource getControlTextFont() {
//        return DEFAULT_FONT;
//    }
//
//    @Override
//    public FontUIResource getSystemTextFont() {
//        return DEFAULT_FONT;
//    }
//
//    @Override
//    public FontUIResource getUserTextFont() {
//        return DEFAULT_FONT;
//    }
//
//    @Override
//    public FontUIResource getMenuTextFont() {
//        return DEFAULT_FONT;
//    }
//
//    @Override
//    public FontUIResource getWindowTitleFont() {
//        return DEFAULT_FONT;
//    }
//
//    @Override
//    public FontUIResource getSubTextFont() {
//        return DEFAULT_FONT;
//    }
//    private final static FontUIResource DEFAULT_FONT = new FontUIResource("Dialog", Font.PLAIN, 11); //NOI18N
}
