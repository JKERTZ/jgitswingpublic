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
public class JGSNimbusTheme {

    public static void install(LookAndFeel laf) {

        //primary colors
        UIManager.put("control", new Color(0, 255, 0));//Rahmenhintergrund
        UIManager.put("info", new Color(0, 0, 0));//tooltip backgroud

        UIManager.put("nimbusBase", new Color(1, 1, 1));//Basis für Buttons, Tabs, alle shaded elements
        UIManager.put("nimbusLightBackground", new Color(0, 0, 255));//textfeldhintergrund und selected list text
        UIManager.put("text", new Color(255, 255, 0));//alle Texte ausser Menu
        //secondary colors
        UIManager.put("nimbusBlueGrey", new Color(255, 0, 0));//Menü und Scrollhintergrund
        UIManager.put("nimbusFocus", new Color(255, 255, 255));//Rahmen um Selected Item

        UIManager.put("nimbusSelectedText", new Color(255, 0, 0));//Seleted Text NICHT Tabellenzeile
        UIManager.put("nimbusSelectionBackground", new Color(0, 0, 0));//Hintergrund von Seleted Text und Tabellenzeile
        //Menu
//        UIManager.put("Menu.background", new Color(0, 255, 255));//nix
//        UIManager.getLookAndFeelDefaults().put("Menu[Enabled].textForeground", Color.GREEN);//nix
//        UIManager.getLookAndFeelDefaults().put("Menu[Selected].textForeground", Color.MAGENTA);//nix
        //MenuBar
//        UIManager.put("MenuBar.background", new Color(0, 255, 255));//nix
//        UIManager.put("MenuBar.foreground", new Color(0, 255, 0));//nix
//        UIManager.put("menu", new Color(255, 0, 0));//nicht das Hauptmenü
//        UIManager.put("menuText", new Color(0, 255, 255));//nicht das Hauptmenü
//        Color caretForeground = new Color(230, 230, 230);
//        Color selectionBackground = new Color(104, 93, 156);
//        Color selectedText = new Color(255, 255, 255);
//
//        UIManager.put("control", new Color(128, 128, 128));
//        UIManager.put("info", new Color(128, 128, 128));
//        UIManager.put("nimbusBase", new Color(18, 30, 49));
//        UIManager.put("nimbusAlertYellow", new Color(248, 187, 0));
//        UIManager.put("nimbusDisabledText", new Color(196, 196, 196));
//        UIManager.put("nimbusFocus", new Color(115, 164, 209));
//        UIManager.put("nimbusGreen", new Color(176, 179, 50));
//        UIManager.put("nimbusInfoBlue", new Color(66, 139, 221));
//        UIManager.put("nimbusOrange", new Color(191, 98, 4));
//        UIManager.put("nimbusRed", new Color(169, 46, 34));
//
//        UIManager.put("text", new Color(230, 230, 230));
//
//        UIManager.put("PropSheet.setBackground", new Color(112, 112, 112)); //NOI18N
//        UIManager.put("PropSheet.selectedSetBackground", new Color(100, 100, 100)); //NOI18N
//
//        UIManager.put("TextField.selectionForeground", selectedText); //NOI18N
//        UIManager.put("TextField.selectionBackground", selectionBackground); //NOI18N
//        UIManager.put("TextField.caretForeground", caretForeground); //NOI18N
//
//        UIManager.put("selection.highlight", new Color(202, 152, 0));
//        UIManager.put("textArea.background", new Color(128, 128, 128));
//
//        //#233622
//        UIManager.put("List[Selected].textForeground", UIManager.getColor("nimbusSelectedText"));
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
        UIManager.put("text", null);

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

        //primary colors default values
//        UIManager.put("control", new Color(214, 217, 223));//Rahmenhintergrund
//        UIManager.put("info", new Color(242, 242, 189));
//        UIManager.put("nimbusAlertYellow", new Color(255, 220, 35));
//        UIManager.put("nimbusBase", new Color(51, 98, 140));
//        UIManager.put("nimbusDisabledText", new Color(142, 143, 145));
//        UIManager.put("nimbusFocus", new Color(115, 164, 209));
//        UIManager.put("nimbusGreen", new Color(176, 179, 50));
//        UIManager.put("nimbusInfoBlue", new Color(47, 92, 180));
//        UIManager.put("nimbusLightBackground", new Color(255, 255, 255));
//        UIManager.put("nimbusOrange", new Color(191, 98, 4));
//        UIManager.put("nimbusRed", new Color(169, 46, 34));
//        UIManager.put("nimbusSelectedText", new Color(255, 255, 255));
//        UIManager.put("nimbusSelectionBackground", new Color(57, 105, 138));
//        UIManager.put("text", new Color(0, 0, 0));
//
//        //secondary colors default values
//        UIManager.put("nimbusBlueGrey", new Color(169, 176, 190));//Menü und Scrollhintergrund
//
//        UIManager.put("activeCaption", new Color(186, 190, 198));
//        UIManager.put("background", new Color(214, 217, 223));
//        UIManager.put("controlDkShadow", new Color(164, 171, 184));
//        UIManager.put("controlHighlight", new Color(233, 236, 242));
//        UIManager.put("controlLHighlight", new Color(247, 248, 250));
//        UIManager.put("controlShadow", new Color(204, 211, 224));
//        UIManager.put("controlText", new Color(0, 0, 0));
//        UIManager.put("desktop", new Color(61, 96, 121));
//        UIManager.put("inactiveCaption", new Color(189, 193, 200));
//        UIManager.put("infoText", new Color(0, 0, 0));
//        UIManager.put("menu", new Color(237, 239, 242));
//        UIManager.put("menuText", new Color(0, 0, 0));
//        UIManager.put("nimbusBorder", new Color(146, 151, 161));
//        UIManager.put("nimbusSelection", new Color(57, 105, 138));
//        UIManager.put("scrollbar", new Color(205, 208, 213));
//        UIManager.put("textBackground", new Color(57, 105, 138));
//        UIManager.put("textForeground", new Color(0, 0, 0));
//        UIManager.put("textHighlight", new Color(57, 105, 138));
//        UIManager.put("textHighlightText", new Color(255, 255, 255));
//        UIManager.put("textInactiveText", new Color(142, 143, 145));
    }
}
