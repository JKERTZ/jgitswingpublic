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
package com.jkertz.jgitswing.model;

/**
 *
 * @author jkertz
 */
public class JGStag {

    boolean annotated;
    String tagRef;
    String tagName;
    String tagMessage;
    String revCommitId;
    String revCommitMessage;

    public boolean isAnnotated() {
        return annotated;
    }

    public void setAnnotated(boolean annotated) {
        this.annotated = annotated;
    }

    public String getTagRef() {
        return tagRef;
    }

    public void setTagRef(String tagRef) {
        this.tagRef = tagRef;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagMessage() {
        return tagMessage;
    }

    public void setTagMessage(String tagMessage) {
        this.tagMessage = tagMessage;
    }

    public String getRevCommitId() {
        return revCommitId;
    }

    public void setRevCommitId(String revCommitId) {
        this.revCommitId = revCommitId;
    }

    public String getRevCommitMessage() {
        return revCommitMessage;
    }

    public void setRevCommitMessage(String revCommitMessage) {
        this.revCommitMessage = revCommitMessage;
    }

}
