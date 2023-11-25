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
package com.jkertz.jgitswing.dialogs;

import java.awt.Component;
import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;

/**
 *
 * @author jkertz
 */
public class JGSopenRepositoryFileChooser extends JFileChooser {

    private final JButton approveButton;

    public JGSopenRepositoryFileChooser() {
        setDialogTitle("JGSopenRepositoryFileChooser");
        setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        addPropertyChangeListener(getPropertyChangeListener());
//        setControlButtonsAreShown(false);//does not work with themes
        // Lookup the Button
        approveButton = lookupButton(this, getUI().getApproveButtonText(this));
        approveButton.setEnabled(false);

    }

    /**
     * approveSelection is called on OK click
     */
    @Override
    public void approveSelection() {
        File file = getSelectedFile();
        if (JGSvalidationUtils.getINSTANCE().isValidGitRepo(file)) {
            super.approveSelection();
            return;
        }
    }

    /**
     * disable buttons on invalid selection
     *
     * @return
     */
    private PropertyChangeListener getPropertyChangeListener() {
        PropertyChangeListener listener = (PropertyChangeEvent evt) -> {
            System.out.println("PropertyChangeEvent");
            if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(evt.getPropertyName())) {
                System.out.println("SELECTED_FILE_CHANGED_PROPERTY");
                File file = (File) evt.getNewValue();

                if (JGSvalidationUtils.getINSTANCE().isValidGitRepo(file)) {    // your condition
//                    setControlButtonsAreShown(true);//does not work with themes
//                    System.out.println("setControlButtonsAreShown(true)");
                    approveButton.setEnabled(true);
                } else {
//                    System.out.println(file.getName());
//                    setControlButtonsAreShown(false);//does not work with themes
//                    System.out.println("setControlButtonsAreShown(false)");
                    approveButton.setEnabled(false);

                }
            }
        };
        return listener;
    }

    private JButton lookupButton(Container c, String text) {
        JButton temp = null;
        for (Component comp : c.getComponents()) {
            if (comp == null) {
                continue;
            }
            if (comp instanceof JButton && (temp = (JButton) comp).getText() != null && temp.getText().equals(text)) {
                return temp;
            } else if (comp instanceof Container) {
                if ((temp = lookupButton((Container) comp, text)) != null) {
                    return temp;
                }
            }
        }
        return temp;
    }
}
