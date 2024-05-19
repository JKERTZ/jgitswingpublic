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
package com.jkertz.jgitswing.widgets.tags;

import java.util.List;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import com.jkertz.jgitswing.tablemodels.IterableRevCommitTableModel;
import com.jkertz.jgitswing.widgets.common.JGScommonScrollwidget;

/**
 *
 * @author jkertz
 */
public class JGShistoryTableWidget extends JGScommonScrollwidget {

    private final IJGShistoryTableWidget receiver;
    private JTable jTableHistory;

    public JGShistoryTableWidget(IJGShistoryTableWidget receiver) {
        super();
        this.receiver = receiver;

        jTableHistory = new JTable();
        jTableHistory.getSelectionModel().addListSelectionListener(getListSelectionListenerHistory());
        this.setViewportView(jTableHistory);

    }

    public JTable getjTableHistory() {
        return jTableHistory;
    }

    public void updateHistoryTable(IterableRevCommitTableModel tableModel) {
        jTableHistory.removeAll();
        uiUtils.setTableSingleSelect(jTableHistory, tableModel);
    }

    private ListSelectionListener getListSelectionListenerHistory() {
        ListSelectionListener listSelectionListener = (ListSelectionEvent lse) -> {
            List<String> selectionList = uiUtils.getSelectedFromTable(jTableHistory);
            boolean invalidSelection = (selectionList == null || selectionList.isEmpty());
            String _commitId = invalidSelection ? null : selectionList.get(0);
            receiver.onHistoryTableWidgetSelectionChanged(_commitId);
        };
        return listSelectionListener;
    }

}
