/*
 * Copyright (C) 2024 JKERTZ
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

import java.awt.Frame;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author jkertz
 */
public class JGSprogressCollector {

    private static JGSprogressCollector INSTANCE = null;
    private Frame parentFrame;
    private JDialog progressDialog = null;
    private JPanel panel = null;
    private final Map<String, JProgressBar> progressMap = new HashMap<>();
    private int dialogHeight = 50;
    private int progressBarHeight = 20;
    private final int popWidth = 300;

    public static JGSprogressCollector getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new JGSprogressCollector();
        }
        return INSTANCE;
    }

    public void setParentFrame(Frame parentFrame) {
        this.parentFrame = parentFrame;
    }

    public void addProgress(String title, int progress) {
        System.out.println("addProgress title: " + title + " progress: " + progress);
        SwingUtilities.invokeLater(() -> {

            if (progressDialog == null) {
                progressDialog = new JDialog(parentFrame, "JGSprogressCollector", false);

                // create panel
                panel = new JPanel(new GridLayout(0, 1));

                //add panel to scrollpane
                JScrollPane jScrollPane = new JScrollPane();
                jScrollPane.setViewportView(panel);

                // add scrollpane to dialog
                progressDialog.setContentPane(jScrollPane);
                dialogHeight = progressDialog.getPreferredSize().height;
                System.out.println("dialogHeight: " + dialogHeight);

                //setup size and location
                progressDialog.setLocationRelativeTo(parentFrame);
                progressDialog.setAlwaysOnTop(true);
//                progressDialog.setLocation(locationX, locationY);
//                progressDialog.setSize(popWidth, popHeight);
                recalculateLocationAndSize();

            }

            if (!progressMap.keySet().contains(title)) {
                //add new progress
                JProgressBar newProgressBar = new JProgressBar();
                newProgressBar.setVisible(true);
                newProgressBar.setMinimum(0);
                newProgressBar.setMaximum(100);
                newProgressBar.setString(title);
                newProgressBar.setStringPainted(true);
                progressBarHeight = newProgressBar.getPreferredSize().height;

                if (progress < 0) {
                    //indetermine
                    newProgressBar.setIndeterminate(true);
                } else {
                    newProgressBar.setValue(progress);
                    if (progress >= 100) {
                        markForRemove(title);
                    }
                }
                progressMap.put(title, newProgressBar);
                panel.add(newProgressBar);

                recalculateLocationAndSize();

            } else {
                //update progress
                JProgressBar existingProgressBar = progressMap.get(title);
                if (progress < 0) {
                    //indetermine
                    existingProgressBar.setIndeterminate(true);
                } else {
                    existingProgressBar.setValue(progress);
                    if (progress >= 100) {
                        markForRemove(title);
                    }
                }
            }
            progressDialog.setVisible(true);
        });
    }

    public void removeProgress(String title) {
        SwingUtilities.invokeLater(() -> {
            if (progressMap.keySet().contains(title)) {
                System.out.println("removeProgress: " + title);
                JProgressBar existingProgressBar = progressMap.get(title);
                panel.remove(existingProgressBar);
//                LayoutManager layout = panel.getLayout();
//                layout.removeLayoutComponent(existingProgressBar);
                progressMap.remove(title);
                existingProgressBar = null;
                recalculateLocationAndSize();
                panel.revalidate();
                panel.repaint();
            } else {
                //error, progress does not exist
                System.out.println("removeProgress: " + title + "NOT FOUND!");
            }

            if (progressMap.isEmpty()) {
                progressDialog.dispose();
            }
        });
    }

    private void recalculateLocationAndSize() {
        //calculate bottom right position
        final int x = parentFrame.getX();
        final int y = parentFrame.getY();
        final int parentWidth = parentFrame.getWidth();
        final int parentHeight = parentFrame.getHeight();
        final int locationX = x + parentWidth - popWidth - 5;

        //calculate height of window
        int amount = progressMap.keySet().size();
        int popHeight = dialogHeight + progressBarHeight * amount;
        int locationY = y + parentHeight - popHeight - 5;

        //limit size to parent window
        if (locationY < y) {
            locationY = y;
        }
        if (popHeight > parentHeight) {
            popHeight = parentHeight;
        }
        System.out.println("popHeight: " + popHeight);
        System.out.println("locationY: " + locationY);
        progressDialog.setLocation(locationX, locationY);
        progressDialog.setSize(popWidth, popHeight);
    }

    private void markForRemove(final String title) {
        new Thread(() -> {
            System.out.println("markForRemove: " + title + " in one second...");
            for (int remProg = 99; remProg > 0; remProg--) {
                addProgress(title, remProg);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            removeProgress(title);
        }).start();
    }
}
