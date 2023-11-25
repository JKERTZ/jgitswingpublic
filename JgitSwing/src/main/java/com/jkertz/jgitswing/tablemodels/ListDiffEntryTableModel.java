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
import org.eclipse.jgit.diff.DiffEntry;

/**
 *
 * @author jkertz
 */
public class ListDiffEntryTableModel extends JGSTableModel {

    public ListDiffEntryTableModel(List<DiffEntry> currentDiff) {
        super();
        // names of columns
        columnNames.add("File");
        columnNames.add("Status");
        columnNames.add("Path");

        // data of the table
        for (DiffEntry entry : currentDiff) {

            String path = entry.getNewPath();
            if (path.equals("/dev/null")) {
                path = entry.getOldPath();
            }

            String fileName = JGSuiUtils.getINSTANCE().extractFilenameFromPath(path);
            String status = entry.getChangeType().toString();
            Object[] row = {fileName, status, path};

            data.add(row);
        }

    }

}
