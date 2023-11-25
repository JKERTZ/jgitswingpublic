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
import com.jkertz.jgitswing.model.JGStag;

/**
 *
 * @author jkertz
 */
public class ListJGStagsTableModel extends JGSTableModel {

    public ListJGStagsTableModel(List<JGStag> jgsTags) {
        super();
        // names of columns
        columnNames.add("TagRef");
        columnNames.add("TagName");
        columnNames.add("TagMessage");
        columnNames.add("CommitId");
        columnNames.add("CommitMessage");

        // data of the table
        for (JGStag jgsTag : jgsTags) {
            String tagRef = jgsTag.getTagRef();
            String tagName = jgsTag.getTagName();
            String tagMessage = jgsTag.getTagMessage();
            String revCommitId = jgsTag.getRevCommitId();
            String revCommitMessage = jgsTag.getRevCommitMessage();
            Object[] row = {tagRef, tagName, tagMessage, revCommitId, revCommitMessage};
            data.add(row);
        }
    }

}
