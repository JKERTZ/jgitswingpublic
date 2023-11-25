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

import java.awt.Color;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

/**
 *
 * @author jkertz
 */
public class DarkNimbusTheme {

    public static void install(LookAndFeel laf) {

        //primary colors
        UIManager.put("control", new Color(40, 40, 40));//Rahmenhintergrund
        UIManager.put("info", new Color(0, 0, 0));//tooltip backgroud

        UIManager.put("nimbusBase", new Color(1, 1, 1));//Basis für Buttons, Tabs, alle shaded elements
        UIManager.put("nimbusLightBackground", new Color(40, 40, 40));//textfeldhintergrund und selected list text
        UIManager.put("text", new Color(255, 255, 255));//alle Texte ausser Menu
        //secondary colors
        UIManager.put("nimbusBlueGrey", new Color(0, 0, 0));//Menü und Scrollhintergrund
        UIManager.put("nimbusFocus", new Color(255, 255, 255));//Rahmen um Selected Item

        UIManager.put("nimbusSelectedText", new Color(255, 0, 0));//Seleted Text NICHT Tabellenzeile
        UIManager.put("nimbusSelectionBackground", new Color(255, 255, 0));//Hintergrund von Seleted Text und Tabellenzeile

    }

    static void uninstall(LookAndFeel laf) {

        //primary colors default values
        UIManager.put("control", null);//Rahmenhintergrund
        UIManager.put("info", null);
        UIManager.put("nimbusAlertYellow", null);
        UIManager.put("nimbusBase", null);
        UIManager.put("nimbusDisabledText", null);
        UIManager.put("nimbusFocus", null);
        UIManager.put("nimbusGreen", null);
        UIManager.put("nimbusInfoBlue", null);
        UIManager.put("nimbusLightBackground", null);
        UIManager.put("nimbusOrange", null);
        UIManager.put("nimbusRed", null);
        UIManager.put("nimbusSelectedText", null);
        UIManager.put("nimbusSelectionBackground", null);
//        UIManager.put("text", null);
        UIManager.put("text", new Color(0, 0, 0));

        //secondary colors default values
        UIManager.put("nimbusBlueGrey", null);//Menü und Scrollhintergrund

        UIManager.put("activeCaption", null);
        UIManager.put("background", null);
        UIManager.put("controlDkShadow", null);
        UIManager.put("controlHighlight", null);
        UIManager.put("controlLHighlight", null);
        UIManager.put("controlShadow", null);
        UIManager.put("controlText", null);
        UIManager.put("desktop", null);
        UIManager.put("inactiveCaption", null);
        UIManager.put("infoText", null);
        UIManager.put("menu", null);
        UIManager.put("menuText", null);
        UIManager.put("nimbusBorder", null);
        UIManager.put("nimbusSelection", null);
        UIManager.put("scrollbar", null);
        UIManager.put("textBackground", null);
        UIManager.put("textForeground", null);
        UIManager.put("textHighlight", null);
        UIManager.put("textHighlightText", null);
        UIManager.put("textInactiveText", null);

    }
}
