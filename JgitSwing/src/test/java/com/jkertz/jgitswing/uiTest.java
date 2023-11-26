/*
 * Copyright (C) 2023 JKERTZ
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
package com.jkertz.jgitswing;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

/**
 *
 * @author jkertz
 */
public class uiTest {

    public void setUp() {

    }

    public void tearDown() {

    }

    private void testRightAlign() {
        JScrollPane jScrollPane = new JScrollPane();
        JPanel contentPanel = new JPanel(new GridLayout(0, 1));

        contentPanel.setBackground(Color.GREEN);

        JLabel jLabel = new JLabel("a", JLabel.TRAILING);
        JTextField jTextField = new JTextField(5);
        jLabel.setLabelFor(jTextField);

        contentPanel.add(jLabel);
        contentPanel.add(jTextField);

        contentPanel.add(getLabeledInputPanel("myInput1"));

        jScrollPane.add(getLabeledInputPanel("scrollInput1"));
        jScrollPane.add(getLabeledInputPanel("scrollInput2"));
        contentPanel.add(jScrollPane);
        contentPanel.add(getLabeledInputPanel("myInput2"));

        int result = JOptionPane.showConfirmDialog(null, contentPanel, "Please enter values.", JOptionPane.OK_CANCEL_OPTION);
        assert result == JOptionPane.OK_OPTION;

    }

    private JPanel getLabeledInputPanel(String text) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panel.setBackground(Color.YELLOW);
//        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        JPanel subpanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        subpanel.setBackground(Color.red);

        JLabel jLabel = new JLabel(text, JLabel.TRAILING);
        JTextField jTextField = new JTextField(5);
        jLabel.setLabelFor(jTextField);

        subpanel.add(jLabel);
        subpanel.add(jTextField);

        subpanel.setAlignmentX(Component.RIGHT_ALIGNMENT);//0.0
//        panel.setAlignmentX(Component.RIGHT_ALIGNMENT);//0.0
        panel.add(subpanel);

        return panel;
    }

    private void testSpringForm() {
        String[] labels = {"Name: ", "Fax: ", "Email: ", "Address: "};
        int numPairs = labels.length;

//Create and populate the panel.
        JPanel p = new JPanel(new SpringLayout());
        for (int i = 0; i < numPairs; i++) {
            JLabel l = new JLabel(labels[i], JLabel.TRAILING);
            p.add(l);
            JTextField textField = new JTextField(10);
            l.setLabelFor(textField);
            p.add(textField);
        }

//Lay out the panel.
//        SpringUtilities.makeCompactGrid(p,
//                numPairs, 2, //rows, cols
//                6, 6, //initX, initY
//                6, 6);       //xPad, yPad
        int result = JOptionPane.showConfirmDialog(null, p, "Please enter values.", JOptionPane.OK_CANCEL_OPTION);
        assert result == JOptionPane.OK_OPTION;

    }

    private void testRightAlignment() {
        JPanel a = new JPanel();
        JPanel asub = new JPanel();
        JPanel b = new JPanel();
        JPanel c = new JPanel();

        a.setBackground(Color.RED);
        asub.setBackground(Color.YELLOW);
        b.setBackground(Color.GREEN);
        c.setBackground(Color.BLUE);

//        a.setMaximumSize(new Dimension(10, 10));
//        b.setMaximumSize(new Dimension(50, 10));
//        a.setAlignmentX(Component.LEFT_ALIGNMENT);//0.0
//        b.setAlignmentX(Component.LEFT_ALIGNMENT);//0.0
//        c.setAlignmentX(Component.LEFT_ALIGNMENT);//0.0
        a.add(new JLabel("a"));
//        JTextField at = new JTextField(10);
//        at.setAlignmentX(Component.RIGHT_ALIGNMENT);
        a.add(new JTextField(5));

        asub.add(new JLabel("asub"));
        asub.add(new JTextField(5));
        asub.setAlignmentX(Component.RIGHT_ALIGNMENT);//0.0

        a.add(asub);

        b.add(new JLabel("bb"));
        b.add(new JTextField(15));

        c.add(new JLabel("ccc"));
        c.add(new JTextField(30));

        a.setAlignmentX(Component.RIGHT_ALIGNMENT);//0.0
        b.setAlignmentX(Component.RIGHT_ALIGNMENT);//0.0
        c.setAlignmentX(Component.RIGHT_ALIGNMENT);//0.0

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(a);
        panel.add(b);
        panel.add(c);

        int result = JOptionPane.showConfirmDialog(null, panel, "Please enter values.", JOptionPane.OK_CANCEL_OPTION);
        assert result == JOptionPane.OK_OPTION;
    }

}
