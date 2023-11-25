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
package com.jkertz.jgitswing.widgets;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.StringReader;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import com.jkertz.jgitswing.widgets.common.JGScommonScrollwidget;

/**
 *
 * @author jkertz
 */
public class JGStextDisplayWidget extends JGScommonScrollwidget {

    JTextPane jTextPane1;

    public JGStextDisplayWidget(String title, String content) {
        this(title);
        jTextPane1.setText(content);
    }

    public JGStextDisplayWidget(String title) {
        super(title);
        jTextPane1 = new JTextPane();
        jTextPane1.setEditable(false);
        this.setViewportView(jTextPane1);
    }

    public void addFormattedLog(String logmessage, Color color) {
        SwingUtilities.invokeLater(() -> {
            StyledDocument doc = (StyledDocument) jTextPane1.getDocument();
            final Style colorStyle = doc.addStyle("ColorStyle", null);

            StyleContext styleContext = new StyleContext();
            final Style defaultStyle = styleContext.getStyle(StyleContext.DEFAULT_STYLE);

            if (color != null) {
                StyleConstants.setForeground(colorStyle, color);
            }

            new BufferedReader(new StringReader(logmessage)).lines().forEach(
                    (line) -> {
                        try {
                            line += "\n";
                            if (color != null) {
                                doc.insertString(doc.getLength(), line, colorStyle);
                            } else {
                                doc.insertString(doc.getLength(), line, defaultStyle);

                            }
                        } catch (BadLocationException ex) {
                            ex.printStackTrace();
                        }
                    }
            );
        });

    }

}
