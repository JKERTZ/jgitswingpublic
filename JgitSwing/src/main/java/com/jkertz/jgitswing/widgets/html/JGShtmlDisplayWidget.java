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
package com.jkertz.jgitswing.widgets.html;

import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import com.jkertz.jgitswing.widgets.common.JGScommonScrollwidget;

/**
 *
 * @author jkertz
 */
public class JGShtmlDisplayWidget extends JGScommonScrollwidget {

    private final JEditorPane jEditorPanel;
    private final Desktop desk;

    public JGShtmlDisplayWidget(String filename) {
        super(filename);
        jEditorPanel = getHtmlDisplayPane(filename);
        desk = Desktop.getDesktop();
        this.setViewportView(jEditorPanel);
    }

    private JEditorPane getHtmlDisplayPane(String filename) {
        JEditorPane jEditorPane = new JEditorPane();

        jEditorPane.addHyperlinkListener(getLinkListener());
        jEditorPane.setEditable(false);
        URL url = null;
        if (filename.startsWith("http")) {
            try {
                url = new URL(filename);
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            }
        } else {
            url = getClass().getResource(filename);
        }

        try {
            jEditorPane.setPage(url);
        } catch (Exception e) {
            e.printStackTrace();
            jEditorPane.setContentType("text/html");
            jEditorPane.setText("<html>Page not found. " + filename + "</html>");
        }
        return jEditorPane;
    }

    private HyperlinkListener getLinkListener() {
        return (HyperlinkEvent e) -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    URL url = e.getURL();
                    System.out.println(e.getEventType().toString() + " url: " + url);
                    desk.browse(url.toURI());
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                    Logger.getLogger(JGShtmlDisplayWidget.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                System.out.println(e.getEventType().toString());
            }
        };
    }

}
