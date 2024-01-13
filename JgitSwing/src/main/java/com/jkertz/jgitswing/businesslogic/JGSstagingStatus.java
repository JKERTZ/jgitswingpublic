/*
 * Copyright (C) 2023 JKERTZ
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
package com.jkertz.jgitswing.businesslogic;

/**
 *
 * @author Jürgen Kertz
 */
public enum JGSstagingStatus {
    Changed, Added, Removed, Modified, Untracked, Conflicting, Missing;

    public static JGSstagingStatus lookup(String text) {
        for (JGSstagingStatus value : values()) {
            if (value.toString().equals(text)) {
                return value;
            }
        }
        return null;
    }
}
