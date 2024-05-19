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
package com.jkertz.jgitswing.widgets.currentdiff;

import com.jkertz.jgitswing.tablemodels.ListDiffEntryTableModel;
import com.jkertz.jgitswing.tablemodels.StatusIgnoredTableModel;
import com.jkertz.jgitswing.tablemodels.StatusStagedTableModel;
import com.jkertz.jgitswing.tablemodels.StatusUnstagedTableModel;
import com.jkertz.jgitswing.widgets.common.JGScommonScrollwidget;
import java.util.List;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author jkertz
 */
public class JGSfileStatusWidget extends JGScommonScrollwidget {

    private final IJGSfileStatusWidget receiver;
    private JTable jTableFiles;

    public JGSfileStatusWidget(IJGSfileStatusWidget receiver) {
        super();
        this.receiver = receiver;

        jTableFiles = new JTable();
        jTableFiles.getSelectionModel().addListSelectionListener(getListSelectionListenerFile());

        this.setViewportView(jTableFiles);

    }

    public void updateFileTables(ListDiffEntryTableModel tableModel) {
        jTableFiles.removeAll();
        uiUtils.setTableSingleSelect(jTableFiles, tableModel);
    }

    public void updateIgnoredTable(StatusIgnoredTableModel tableModelIgnored) {
//        DefaultTableModel dtm = uiUtils.buildIgnoredTableModel(status);
//        StatusIgnoredTableModel tableModelIgnored = uiUtils.getTableModelIgnored(status);
//        SwingUtilities.invokeLater(() -> {
        jTableFiles.removeAll();
        uiUtils.setTableSingleSelect(jTableFiles, tableModelIgnored);
//            callback.doNext(null);
//        });
    }

    public void updateStagedTable(StatusStagedTableModel tableModelStaged) {
//        DefaultTableModel dtm = uiUtils.buildStagedTableModel(status);
//        StatusStagedTableModel tableModelStaged = uiUtils.getTableModelStaged(status);
//        SwingUtilities.invokeLater(() -> {
        jTableFiles.removeAll();
        uiUtils.setTableMultiSelect(jTableFiles, tableModelStaged);
//            callback.doNext(null);
//        });
    }

    public void updateUnstagedTable(StatusUnstagedTableModel tableModelUnstaged) {
//        DefaultTableModel dtm = uiUtils.buildUnstagedTableModel(status);
//        StatusUnstagedTableModel tableModelUnstaged = uiUtils.getTableModelUnstaged(status);
//
//        SwingUtilities.invokeLater(() -> {
        jTableFiles.removeAll();
        uiUtils.setTableMultiSelect(jTableFiles, tableModelUnstaged);
//            callback.doNext(null);
//        });
    }

    private ListSelectionListener getListSelectionListenerFile() {
        ListSelectionListener listSelectionListener = (ListSelectionEvent lse) -> {
            List<String> selectionList = uiUtils.getSelectedFromTable(jTableFiles);
//            boolean invalidSelection = (selectionList == null || selectionList.isEmpty());
//            String path = invalidSelection ? null : selectionList.get(0);
//            receiver.onFileStatusWidgetListSelectionChanged(path);
            receiver.onFileStatusWidgetListSelectionChanged(selectionList);

        };
        return listSelectionListener;
    }

}
