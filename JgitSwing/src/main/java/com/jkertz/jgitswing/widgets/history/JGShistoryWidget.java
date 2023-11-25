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
package com.jkertz.jgitswing.widgets.history;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import com.jkertz.jgitswing.widgets.common.JGScommonPanelwidget;

/**
 *
 * @author jkertz
 */
public class JGShistoryWidget extends JGScommonPanelwidget {

    private final IJGShistoryWidget receiver;
    private JTable jTableHistory;
    private JTable jTableFiles;
    private JTextPane jTextPane1;

    public JGShistoryWidget(IJGShistoryWidget receiver) {
        super(new GridLayout(2, 1));
        this.receiver = receiver;

        TitledBorder titledBorder = new TitledBorder("");
        this.setBorder(titledBorder);
        this.add(getHistoryPanel());
        this.add(getFilesPanel());
    }

    public JTable getjTableHistory() {
        return jTableHistory;
    }

    public JTable getjTableFiles() {
        return jTableFiles;
    }

    public JTextPane getjTextPane1() {
        return jTextPane1;
    }

    private JPanel getHistoryPanel() {
        JPanel jPanel = new JPanel(new BorderLayout());
        jPanel.add(getHistoryScrollpanel(), BorderLayout.CENTER);
        return jPanel;
    }

    private JPanel getFilesPanel() {
        JPanel jPanel = new JPanel(new GridLayout(1, 2));
        jPanel.add(getFilesScrollpanel());
        jPanel.add(getChangesScrollpanel());
        return jPanel;
    }

    private JScrollPane getHistoryScrollpanel() {
        JScrollPane jScrollPane = new JScrollPane();
        jTableHistory = new JTable();
        jTableHistory.getSelectionModel().addListSelectionListener(getListSelectionListenerHistory());

        jScrollPane.setViewportView(jTableHistory);
        return jScrollPane;
    }

    private JScrollPane getFilesScrollpanel() {
        JScrollPane jScrollPane = new JScrollPane();
        jTableFiles = new JTable();
        jTableFiles.getSelectionModel().addListSelectionListener(getListSelectionListenerFile());

        jScrollPane.setViewportView(jTableFiles);
        return jScrollPane;
    }

    private JScrollPane getChangesScrollpanel() {
        JScrollPane jScrollPane = new JScrollPane();
        jTextPane1 = new JTextPane();
        jScrollPane.setViewportView(jTextPane1);
        return jScrollPane;
    }

    private ListSelectionListener getListSelectionListenerHistory() {
        ListSelectionListener listSelectionListener = (ListSelectionEvent lse) -> {
            receiver.onHistoryWidgetListSelectionChangedHistory();
        };
        return listSelectionListener;
    }

    private ListSelectionListener getListSelectionListenerFile() {
        ListSelectionListener listSelectionListener = (ListSelectionEvent lse) -> {
            receiver.onHistoryWidgetListSelectionChangedFile();
        };
        return listSelectionListener;
    }

}
