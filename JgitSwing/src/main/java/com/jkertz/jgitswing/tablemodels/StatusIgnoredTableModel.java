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
public class StatusIgnoredTableModel extends JGSTableModel {

    public StatusIgnoredTableModel(Status status) {
        super();
        // names of columns
        columnNames.add("File");
        columnNames.add("Status");
        columnNames.add("Path");

        // data of the table
        List<Object[]> untrackedTableData = JGSuiUtils.getINSTANCE().getStatusTableData("Untracked Folders", status.getUntrackedFolders());
        List<Object[]> ignoredTableData = JGSuiUtils.getINSTANCE().getStatusTableData("Ignored Not Index", status.getIgnoredNotInIndex());

        data.addAll(untrackedTableData);
        data.addAll(ignoredTableData);
    }

}
