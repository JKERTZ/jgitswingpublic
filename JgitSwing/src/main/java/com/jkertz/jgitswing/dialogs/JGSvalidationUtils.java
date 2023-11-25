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

import java.awt.Color;
import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.JTextField;
import com.jkertz.jgitswing.logger.JGSlogger;

/**
 *
 * @author jkertz
 */
public class JGSvalidationUtils {

    private static JGSvalidationUtils INSTANCE = null;
    private JGSlogger logger;

    private JGSvalidationUtils() {
        logger = JGSlogger.getINSTANCE();
    }

    public static JGSvalidationUtils getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new JGSvalidationUtils();
        }
        return INSTANCE;
    }

    public boolean isValidTextInput(JTextField jTextField) {
        String text = jTextField.getText();
        boolean isValid = (text != null && !text.isEmpty());
        if (isValid) {
            jTextField.setBackground(Color.green);
            jTextField.setToolTipText("isValid");
        } else {
            jTextField.setBackground(Color.red);
            jTextField.setToolTipText("input required");
        }
        return isValid;
    }

    public boolean isRemoteRepo(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        boolean isHttp = (text.toLowerCase().startsWith("http"));
        boolean isSsh = (text.toLowerCase().startsWith("ssh"));

        return (isHttp || isSsh);
    }

    public boolean isValidCloneURI(JTextField jTextField) {
        String text = jTextField.getText();

        if (text == null || text.isEmpty()) {
            jTextField.setBackground(Color.red);
            jTextField.setToolTipText("input required");
            return false;
        }
        if (text.toLowerCase().startsWith("http")) {
            jTextField.setBackground(Color.green);
            jTextField.setToolTipText("http supported");
            return true;
        }
        if (text.toLowerCase().startsWith("ssh")) {
            //SSH not supported
            jTextField.setBackground(Color.red);
            jTextField.setToolTipText("SSH not supported");
            return false;
        }
        //check if valid local repo
        try {
            File file = new File(text);
            if (isValidGitRepo(file)) {
                jTextField.setBackground(Color.green);
                jTextField.setToolTipText("valid local repo");
                return true;
            } else {
                jTextField.setBackground(Color.red);
                jTextField.setToolTipText("invalid local repo");
                return false;
            }
        } catch (Exception e) {
            jTextField.setBackground(Color.red);
            jTextField.setToolTipText("invalid local repo: " + e.getMessage());
            return false;
        }
    }

    public boolean isValidCloneTarget(JTextField jTextField) {
        String text = jTextField.getText();
        if (text == null || text.isEmpty()) {
            jTextField.setBackground(Color.red);
            jTextField.setToolTipText("input required");
            return false;
        }
        try {
            File file = new File(text);
            if (isEmptyDir(file)) {
                jTextField.setBackground(Color.green);
                jTextField.setToolTipText("valid Clone Target");
                return true;
            } else {
                jTextField.setBackground(Color.red);
                jTextField.setToolTipText("invalid Clone Target");
                return false;
            }
        } catch (Exception e) {
            jTextField.setBackground(Color.red);
            jTextField.setToolTipText(e.getMessage());
            return false;
        }

    }

    private boolean isEmptyDir(File file) {
        String directory = file.getAbsolutePath();
        Set<String> directoriyContent = listDirectoryContent(directory);
        return directoriyContent.isEmpty();
    }

    public boolean isValidGitRepo(File file) {
        String directory = file.getAbsolutePath();
        Set<String> subdirectories = listSubdirectories(directory);
        return (subdirectories != null && subdirectories.contains(".git"));
    }

    private Set<String> listSubdirectories(String dir) {
        return Stream.of(new File(dir).listFiles())
                .filter(file -> file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }

    private Set<String> listDirectoryContent(String dir) {
        return Stream.of(new File(dir).listFiles())
                .map(File::getName)
                .collect(Collectors.toSet());
    }

}
