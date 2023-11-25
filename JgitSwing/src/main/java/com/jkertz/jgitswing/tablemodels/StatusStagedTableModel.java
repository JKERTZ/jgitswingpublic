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
package com.jkertz.jgitswing.tablemodels;

import java.util.List;
import com.jkertz.jgitswing.tabs.common.JGSuiUtils;
import org.eclipse.jgit.api.Status;

/**
 *
 * @author jkertz
 */
public class StatusStagedTableModel extends JGSTableModel {

    public StatusStagedTableModel(Status status) {
        super();
        // names of columns
        columnNames.add("File");
        columnNames.add("Status");
        columnNames.add("Path");

        // data of the table
        List<Object[]> changedTableData = JGSuiUtils.getINSTANCE().getStatusTableData("Changed", status.getChanged());
        List<Object[]> addedableData = JGSuiUtils.getINSTANCE().getStatusTableData("Added", status.getAdded());
        List<Object[]> removedTableData = JGSuiUtils.getINSTANCE().getStatusTableData("Removed", status.getRemoved());

        data.addAll(changedTableData);
        data.addAll(addedableData);
        data.addAll(removedTableData);
    }

}
