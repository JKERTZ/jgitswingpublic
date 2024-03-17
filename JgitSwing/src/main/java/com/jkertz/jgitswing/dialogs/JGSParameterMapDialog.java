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
package com.jkertz.jgitswing.dialogs;

import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 *
 * @author jkertz
 */
public class JGSParameterMapDialog {

    public boolean show(String title, Map<String, String> parameters, boolean isReadonly) {
        Map<String, JTextField> inputMap = new HashMap<>();
        JPanel myPanel = new JPanel(new GridLayout(0, 1));
        for (String key : parameters.keySet()) {
            String value = parameters.get(key);
            myPanel.add(new JLabel(key));
            JTextField input = new JTextField();
            input.setEditable(!isReadonly);
            if (value != null) {
                input.setText(value);
            }
            input.setColumns(30);
            myPanel.add(input);
            inputMap.put(key, input);
        }

        int result = JOptionPane.showConfirmDialog(null, myPanel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            for (String key : parameters.keySet()) {
                JTextField input = inputMap.get(key);
                String value = input.getText();
                parameters.put(key, value);
                System.out.println(key + " : " + value);
            }

            return true;
        }
        return false;
    }

    public boolean show(String title, Map<String, String> parameters, Map<String, Boolean> options, boolean isReadonly) {
        Map<String, JTextField> inputMap = new HashMap<>();
        Map<String, JCheckBox> optionMap = new HashMap<>();
        JPanel myPanel = new JPanel(new GridLayout(0, 1));
        for (String key : parameters.keySet()) {
            String value = parameters.get(key);
            myPanel.add(new JLabel(key));
            JTextField input = new JTextField("input123");
            input.setEditable(!isReadonly);
            if (value != null) {
                input.setText(value);
            }
            input.setColumns(30);
            myPanel.add(input);
            inputMap.put(key, input);
        }

        for (String key : options.keySet()) {
            Boolean value = options.get(key);
//            myPanel.add(new JLabel(key));
            JCheckBox check = new JCheckBox(key);
            check.setEnabled(!isReadonly);
            if (value != null) {
                check.setSelected(value);
            }
            myPanel.add(check);
            optionMap.put(key, check);
        }

        int result = JOptionPane.showConfirmDialog(null, myPanel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            for (String key : parameters.keySet()) {
                JTextField input = inputMap.get(key);
                String value = input.getText();
                parameters.put(key, value);
                System.out.println(key + " : " + value);
            }
            for (String key : options.keySet()) {
                JCheckBox check = optionMap.get(key);
                Boolean value = check.isSelected();
                options.put(key, value);
                System.out.println(key + " : " + value);
            }

            return true;
        }
        return false;
    }

    public boolean showSectional(String title, Map<String, Map<String, Map<String, String>>> parameters, boolean isReadonly) {
        Map<String, JTextField> inputMap = new HashMap<>();
        JPanel myPanel = new JPanel();
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.PAGE_AXIS));

        for (String section : parameters.keySet()) {
            System.out.println("Section: " + section);
            JPanel sectionPanel = new JPanel(new GridLayout(0, 1));
            TitledBorder sectionBorder = new TitledBorder(section);
            sectionPanel.setBorder(sectionBorder);
            myPanel.add(sectionPanel);

            Map<String, Map<String, String>> subSectionMap = parameters.get(section);
            for (String subSection : subSectionMap.keySet()) {
                System.out.println("-SubSection: " + subSection);
                JPanel subSectionPanel = new JPanel(new GridLayout(0, 1));

                TitledBorder subSectionBorder = new TitledBorder(subSection);
                subSectionPanel.setBorder(subSectionBorder);
                sectionPanel.add(subSectionPanel);

                Map<String, String> nameMap = subSectionMap.get(subSection);
                for (String name : nameMap.keySet()) {
                    String value = nameMap.get(name);
                    System.out.println("--Name: " + name + " Value: " + value);

                    JTextField input = new JTextField();
                    JPanel nameValuePanel = JGSdialogUtils.getINSTANCE().getLabeledInput(name, input, value, isReadonly);

                    subSectionPanel.add(nameValuePanel);
                    String key = section + subSection + name;
                    inputMap.put(key, input);
                }
            }
        }

        int result = JOptionPane.showConfirmDialog(null, myPanel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            for (String section : parameters.keySet()) {

                Map<String, Map<String, String>> subSectionMap = parameters.get(section);
                Set<String> subSections = subSectionMap.keySet();
                for (String subSection : subSections) {

                    Map<String, String> nameMap = subSectionMap.get(subSection);
                    Set<String> names = nameMap.keySet();
                    for (String name : names) {
                        String key = section + subSection + name;
                        JTextField input = inputMap.get(key);
                        String value = input.getText();
                        nameMap.put(name, value);
                    }
                }
            }

            return true;
        }
        return false;

    }

}
