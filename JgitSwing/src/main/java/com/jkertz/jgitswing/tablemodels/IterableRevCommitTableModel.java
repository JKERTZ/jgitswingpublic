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

import java.time.Instant;
import java.util.Date;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 *
 * @author jkertz
 */
public class IterableRevCommitTableModel extends JGSTableModel {

    public IterableRevCommitTableModel(Iterable<RevCommit> allCommits) {
        super();
        // names of columns
        columnNames.add("Date");
        columnNames.add("Message");
        columnNames.add("CommitId");
        columnNames.add("Author");
        columnNames.add("Email");

        // data of the table
        for (RevCommit commit : allCommits) {
            long commitTime = commit.getCommitTime();
            Instant ofEpochSecond = Instant.ofEpochSecond(commitTime);

//            Date commitDate = new Date(commitTime * 1000); // git is "by sec" and java is "by ms"
            Date commitDate = new Date(ofEpochSecond.toEpochMilli());
            String fullMessage = commit.getFullMessage();
            String name = commit.getId().getName();
            String author = commit.getAuthorIdent().getName();
            String emailAddress = commit.getAuthorIdent().getEmailAddress();
            Object[] row = {commitDate, fullMessage, name, author, emailAddress};
            data.add(row);
        }
    }

}
