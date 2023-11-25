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

import java.awt.Color;
import java.awt.Frame;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

/**
 *
 * @author jkertz
 */
public class JgitSwingToast {

    private int toastcount = 0;

    private static JgitSwingToast INSTANCE = null;

    private final int waitBeforeFade = 2000;
    private final int sleeptime = 50;
    private final float startAlpha = 0.9f;
    private final int fadeDuration = 1000;
    private final int fadeSteps = fadeDuration / sleeptime;
    private final float alphaStep = startAlpha / fadeSteps;

    private JgitSwingToast() {

    }

    public static JgitSwingToast getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new JgitSwingToast();
        }
        return INSTANCE;
    }

    public void show(Frame parentFrame, String message, Color color) {
        //calculate top right position
        final int x = parentFrame.getX();
        final int y = parentFrame.getY();
        final int popWidth = 300;
        final int popHeight = 70;
        final int parentWidth = parentFrame.getWidth();
        final int parentHeight = parentFrame.getHeight();
        final int locationX = x + parentWidth - popWidth - 5;
        final int locationY = y + 25 + toastcount * (popHeight + 2);
        final String lafName = UIManager.getLookAndFeel().getName();
        boolean isNimbus = ("Nimbus".equals(lafName));
        isNimbus = true;
        final JDialog newdialog = new JDialog(parentFrame, "Dialog ", false);
        //set content
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setCursor(null);
        textArea.setOpaque(false);
        textArea.setFocusable(false);
        textArea.setLineWrap(true);
        textArea.setText(message);
        textArea.setBorder(null);
        textArea.setSize(popWidth - 10, popHeight - 10);
        if (color != null) {
            if (isNimbus) {
                textArea.setBackground(color);
            } else {
                textArea.setForeground(color);
            }
        }

        //JProgressBar
        JProgressBar jProgressBar = new JProgressBar();
        jProgressBar.setVisible(true);
        jProgressBar.setMinimum(0);
        jProgressBar.setMaximum(100);

        //JScrollPane
        JScrollPane jsp = new JScrollPane(textArea);
        jsp.setBorder(null);
        jsp.setOpaque(false);
        if (color != null && isNimbus) {
            jsp.setBackground(color);
        }

        //JPanel
        JPanel myPanel = new JPanel();
        myPanel.add(jProgressBar);
        myPanel.add(jsp);

        newdialog.setContentPane(myPanel);

        //setup size and location
        newdialog.setLocationRelativeTo(parentFrame);
        newdialog.setAlwaysOnTop(true);
        newdialog.setLocation(locationX, locationY);
        newdialog.setSize(popWidth, popHeight);
        newdialog.setUndecorated(true);
        newdialog.setShape(new RoundRectangle2D.Double(0, 0, newdialog.getWidth(), newdialog.getHeight(), 20, 20));
        newdialog.getRootPane().setOpaque(false);
        if (color != null && isNimbus) {
            newdialog.setBackground(color);
        }
        newdialog.setVisible(true);

        toastcount += 1;

        //start fading animation
        new Thread(() -> { // Lambda Expression
            int progress;
            int progresstime = 0;

            newdialog.setOpacity(startAlpha);

            //wait before fading
            float alpha = startAlpha;
            while (progresstime < waitBeforeFade) {
                progress = (progresstime * 100) / (waitBeforeFade + fadeDuration);
                jProgressBar.setValue(100-progress);
                try {
                    Thread.sleep(sleeptime);
                } catch (InterruptedException ex) {
                }
                progresstime += sleeptime;
            }

            //fade
            while (progresstime < (waitBeforeFade + fadeDuration)) {
                progress = (progresstime * 100) / (waitBeforeFade + fadeDuration);
                jProgressBar.setValue(100-progress);
                newdialog.setOpacity(alpha);
                alpha -= alphaStep;

                try {
                    Thread.sleep(sleeptime);
                } catch (InterruptedException ex) {
                }
                progresstime += sleeptime;
            }

            newdialog.setVisible(false);
            toastcount = 0;
        }
        ).start();

    }
}
