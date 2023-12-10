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
package com.jkertz.jgitswing.main;

import java.util.HashMap;
import java.util.Map;
import javax.swing.UIManager;

/**
 *
 * @author jkertz
 */
public class JGSlookAndFeels {

    private static JGSlookAndFeels INSTANCE = null;

    private final Map<String, String> systemLookAndFeels;
    private final Map<String, String> jgsLookAndFeels;
    private final Map<String, String> flatLafLookAndFeels;
    private final Map<String, String> flatLafThemes;
    private final Map<String, String> flatLafMaterialThemes;

    private JGSlookAndFeels() {
        systemLookAndFeels = initSystemLookAndFeels();
        jgsLookAndFeels = initJgsLookAndFeels();
        flatLafLookAndFeels = initFlatLafLookAndFeels();
        flatLafThemes = initFlatLafThemes();
        flatLafMaterialThemes = initFlatLafMaterialThemes();

    }

    public static JGSlookAndFeels getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new JGSlookAndFeels();
        }
        return INSTANCE;
    }

    public Map<String, String> getSystemLookAndFeels() {
        return systemLookAndFeels;
    }

    public Map<String, String> getJgsLookAndFeels() {
        return jgsLookAndFeels;
    }

    public Map<String, String> getFlatLafLookAndFeels() {
        return flatLafLookAndFeels;
    }

    public Map<String, String> getFlatLafThemes() {
        return flatLafThemes;
    }

    public Map<String, String> getFlatLafMaterialThemes() {
        return flatLafMaterialThemes;
    }

    private void installThemes(Map<String, String> lookAndFeels) {
        for (String theme : lookAndFeels.keySet()) {
            UIManager.installLookAndFeel(theme, lookAndFeels.get(theme));
        }
    }

    private Map<String, String> initSystemLookAndFeels() {
        Map<String, String> result = new HashMap<>();
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            System.out.println("initSystemLookAndFeels Found LookAndFeel: " + info.getName());
            result.put(info.getName(), info.getClassName());
        }
        return result;
    }

    private Map<String, String> initJgsLookAndFeels() {
        Map<String, String> result = new HashMap<>();
        //Nimus themes destroy behaviour of other themes!
//            result.put("JGSNimbusLookAndFeel", "jgitswing.themes.JGSNimbusLookAndFeel");
//            result.put("DarkNimbusLookAndFeel", "jgitswing.themes.DarkNimbusLookAndFeel");
        result.put("DarkSteelLookAndFeel", "com.jkertz.jgitswing.themes.DarkSteelLookAndFeel");
        result.put("DarkOceanLookAndFeel", "com.jkertz.jgitswing.themes.DarkOceanLookAndFeel");
        result.put("LightSteelLookAndFeel", "com.jkertz.jgitswing.themes.LightSteelLookAndFeel");
        result.put("LightOceanLookAndFeel", "com.jkertz.jgitswing.themes.LightOceanLookAndFeel");

        installThemes(result);
        return result;
    }

    private Map<String, String> initFlatLafLookAndFeels() {
        Map<String, String> result = new HashMap<>();
        //additional themes from flatlaf jar
        result.put("FlatDarculaLaf", "com.formdev.flatlaf.FlatDarculaLaf");
        result.put("FlatLightLaf", "com.formdev.flatlaf.FlatLightLaf");
        result.put("FlatDarkLaf", "com.formdev.flatlaf.FlatDarkLaf");
        result.put("FlatIntelliJLaf", "com.formdev.flatlaf.FlatIntelliJLaf");

        installThemes(result);
        return result;
    }

    private Map<String, String> initFlatLafThemes() {
        Map<String, String> result = new HashMap<>();
        //additional themes from flatlaf intellij jar
        result.put("FlatArcIJTheme", "com.formdev.flatlaf.intellijthemes.FlatArcIJTheme");
        result.put("FlatArcOrangeIJTheme", "com.formdev.flatlaf.intellijthemes.FlatArcOrangeIJTheme");
        result.put("FlatArcDarkIJTheme", "com.formdev.flatlaf.intellijthemes.FlatArcDarkIJTheme");
        result.put("FlatArcDarkOrangeIJTheme", "com.formdev.flatlaf.intellijthemes.FlatArcDarkOrangeIJTheme");
        result.put("FlatCarbonIJTheme", "com.formdev.flatlaf.intellijthemes.FlatCarbonIJTheme");
        result.put("FlatCobalt2IJTheme", "com.formdev.flatlaf.intellijthemes.FlatCobalt2IJTheme");
        result.put("FlatCyanLightIJTheme", "com.formdev.flatlaf.intellijthemes.FlatCyanLightIJTheme");
        result.put("FlatDarkFlatIJTheme", "com.formdev.flatlaf.intellijthemes.FlatDarkFlatIJTheme");
        result.put("FlatDarkPurpleIJTheme", "com.formdev.flatlaf.intellijthemes.FlatDarkPurpleIJTheme");
        result.put("FlatDraculaIJTheme", "com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme");
        result.put("FlatGradiantoDarkFuchsiaIJTheme", "com.formdev.flatlaf.intellijthemes.FlatGradiantoDarkFuchsiaIJTheme");
        result.put("FlatGradiantoDeepOceanIJTheme", "com.formdev.flatlaf.intellijthemes.FlatGradiantoDeepOceanIJTheme");
        result.put("FlatGradiantoMidnightBlueIJTheme", "com.formdev.flatlaf.intellijthemes.FlatGradiantoMidnightBlueIJTheme");
        result.put("FlatGradiantoNatureGreenIJTheme", "com.formdev.flatlaf.intellijthemes.FlatGradiantoNatureGreenIJTheme");
        result.put("FlatGrayIJTheme", "com.formdev.flatlaf.intellijthemes.FlatGrayIJTheme");
        result.put("FlatGruvboxDarkHardIJTheme", "com.formdev.flatlaf.intellijthemes.FlatGruvboxDarkHardIJTheme");
        result.put("FlatGruvboxDarkMediumIJTheme", "com.formdev.flatlaf.intellijthemes.FlatGruvboxDarkMediumIJTheme");
        result.put("FlatGruvboxDarkSoftIJTheme", "com.formdev.flatlaf.intellijthemes.FlatGruvboxDarkSoftIJTheme");
        result.put("FlatHiberbeeDarkIJTheme", "com.formdev.flatlaf.intellijthemes.FlatHiberbeeDarkIJTheme");
        result.put("FlatHighContrastIJTheme", "com.formdev.flatlaf.intellijthemes.FlatHighContrastIJTheme");
        result.put("FlatLightFlatIJTheme", "com.formdev.flatlaf.intellijthemes.FlatLightFlatIJTheme");
        result.put("FlatMaterialDesignDarkIJTheme", "com.formdev.flatlaf.intellijthemes.FlatMaterialDesignDarkIJTheme");
        result.put("FlatMonocaiIJTheme", "com.formdev.flatlaf.intellijthemes.FlatMonocaiIJTheme");
        result.put("FlatMonokaiProIJTheme", "com.formdev.flatlaf.intellijthemes.FlatMonokaiProIJTheme");
        result.put("FlatNordIJTheme", "com.formdev.flatlaf.intellijthemes.FlatNordIJTheme");
        result.put("FlatOneDarkIJTheme", "com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme");
        result.put("FlatSolarizedDarkIJTheme", "com.formdev.flatlaf.intellijthemes.FlatSolarizedDarkIJTheme");
        result.put("FlatSolarizedLightIJTheme", "com.formdev.flatlaf.intellijthemes.FlatSolarizedLightIJTheme");
        result.put("FlatSpacegrayIJTheme", "com.formdev.flatlaf.intellijthemes.FlatSpacegrayIJTheme");
        result.put("FlatVuesionIJTheme", "com.formdev.flatlaf.intellijthemes.FlatVuesionIJTheme");
        result.put("FlatXcodeDarkIJTheme", "com.formdev.flatlaf.intellijthemes.FlatXcodeDarkIJTheme");

//        result.put("FlatArcOrangeIJTheme", "com.formdev.flatlaf.intellijthemes.FlatArcDarkOrangeIJTheme");
//        result.put("FlatCarbonIJTheme", "com.formdev.flatlaf.intellijthemes.FlatCarbonIJTheme");
//        result.put("FlatGradiantoNatureGreenIJTheme", "com.formdev.flatlaf.intellijthemes.FlatGradiantoNatureGreenIJTheme");
//        result.put("FlatSolarizedDarkIJTheme", "com.formdev.flatlaf.intellijthemes.FlatSolarizedDarkIJTheme");
//        result.put("FlatSpacegrayIJTheme", "com.formdev.flatlaf.intellijthemes.FlatSpacegrayIJTheme");
//        result.put("FlatVuesionIJTheme", "com.formdev.flatlaf.intellijthemes.FlatVuesionIJTheme");
//        result.put("FlatXcodeDarkIJTheme", "com.formdev.flatlaf.intellijthemes.FlatXcodeDarkIJTheme");
//        result.put("FlatCobalt2IJTheme", "com.formdev.flatlaf.intellijthemes.FlatCobalt2IJTheme");
//        result.put("FlatDarkPurpleIJTheme", "com.formdev.flatlaf.intellijthemes.FlatDarkPurpleIJTheme");
//        result.put("FlatHiberbeeDarkIJTheme", "com.formdev.flatlaf.intellijthemes.FlatHiberbeeDarkIJTheme");
        installThemes(result);
        return result;
    }

    private Map<String, String> initFlatLafMaterialThemes() {
        Map<String, String> result = new HashMap<>();
        //additional themes from flatlaf intellij jar
        result.put("FlatArcDarkIJTheme", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatArcDarkIJTheme");
        result.put("FlatArcDarkContrastIJTheme", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatArcDarkContrastIJTheme");
        result.put("FlatAtomOneDarkIJTheme", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneDarkIJTheme");
        result.put("FlatAtomOneDarkContrastIJTheme", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneDarkContrastIJTheme");
        result.put("FlatAtomOneLightIJTheme", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneLightIJTheme");
        result.put("FlatAtomOneLightContrastIJTheme", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneLightContrastIJTheme");
        result.put("FlatDraculaContrastIJTheme", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatDraculaContrastIJTheme");
        result.put("FlatGitHubIJTheme", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubIJTheme");
        result.put("FlatGitHubContrastIJTheme", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubContrastIJTheme");
        result.put("FlatGitHubDarkIJTheme", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkIJTheme");
        result.put("FlatGitHubDarkContrastIJTheme", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkContrastIJTheme");
        result.put("FlatLightOwlIJTheme", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatLightOwlIJTheme");
        result.put("FlatLightOwlContrastIJTheme", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatLightOwlContrastIJTheme");
        result.put("FlatMaterialDarkerIJTheme", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDarkerIJTheme");
        result.put("FlatMaterialDarkerContrastIJTheme", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDarkerContrastIJTheme");
        result.put("FlatMaterialDeepOceanIJTheme", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDeepOceanIJTheme");
        result.put("FlatMaterialDeepOceanContrastIJTheme", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDeepOceanContrastIJTheme");
        result.put("FlatMaterialLighterIJTheme", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialLighterIJTheme");
        result.put("FlatMaterialLighterContrastIJTheme", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialLighterContrastIJTheme");
        result.put("FlatMaterialOceanicIJTheme", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialOceanicIJTheme");
        result.put("FlatMaterialOceanicContrastIJTheme", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialOceanicContrastIJTheme");
        result.put("FlatMaterialPalenightIJTheme", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialPalenightIJTheme");
        result.put("FlatMaterialPalenightContrastIJTheme", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialPalenightContrastIJTheme");
        result.put("FlatMonokaiProIJTheme", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMonokaiProIJTheme");
        result.put("FlatMonokaiProContrastIJTheme", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMonokaiProContrastIJTheme");
        result.put("FlatMoonlightIJTheme", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMoonlightIJTheme");
        result.put("FlatMoonlightContrastIJTheme", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMoonlightContrastIJTheme");
        result.put("FlatNightOwlIJTheme", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatNightOwlIJTheme");
        result.put("FlatNightOwlContrastIJTheme", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatNightOwlContrastIJTheme");
        result.put("FlatSolarizedDarkIJTheme", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatSolarizedDarkIJTheme");
        result.put("FlatSolarizedDarkContrastIJTheme", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatSolarizedDarkContrastIJTheme");
        result.put("FlatSolarizedLightIJTheme", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatSolarizedLightIJTheme");
        result.put("FlatSolarizedLightContrastIJTheme", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatSolarizedLightContrastIJTheme");

//        result.put("Dracula (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatDraculaIJTheme");
//        result.put("GitHub Dark (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkIJTheme");
//        result.put("Material Deep Ocean (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDeepOceanIJTheme");
//        result.put("Material Deep Ocean Contrast (Material)", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDeepOceanContrastIJTheme");
        installThemes(result);
        return result;

    }

}
