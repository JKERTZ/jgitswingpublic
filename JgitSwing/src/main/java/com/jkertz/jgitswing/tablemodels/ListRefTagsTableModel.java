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
import org.eclipse.jgit.lib.Ref;

/**
 *
 * @author jkertz
 */
public class ListRefTagsTableModel extends JGSTableModel {

    public ListRefTagsTableModel(List<Ref> tags) {
        super();
        // names of columns
        columnNames.add("Name");
        columnNames.add("ObjectId");

        // data of the table
        for (Ref tag : tags) {
            String name = tag.getName();
            String objectId = tag.getObjectId().toString();
            Object[] row = {name, objectId};
            data.add(row);
        }

    }

}
