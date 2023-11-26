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
package com.jkertz.jgitswing.tabs.common;

import com.jkertz.jgitswing.businesslogic.JGSstagingStatus;
import static com.jkertz.jgitswing.businesslogic.JGSstagingStatus.Changed;

/**
 *
 * @author Jürgen Kertz
 */
public class JGShtmlUtils {

    private static JGShtmlUtils INSTANCE = null;

    private JGShtmlUtils() {

    }

    public static JGShtmlUtils getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new JGShtmlUtils();
        }
        return INSTANCE;
    }

    public String toBold(String text) {
        String result = "<b>" + text + "</b>";
        return result;
    }

    public String toItalic(String text) {
        String result = "<i>" + text + "</i>";
        return result;
    }

    public String toHtml(String text) {
        String result = "<html>" + text + "</html>";
        return result;
    }

    public String toAheadBehind(Integer aheadCount, Integer behindCount) {
        String aheadString = aheadCount.toString();
        String behindString = behindCount.toString();
        if (aheadCount > 0) {
            aheadString = toOrange(aheadString);
        }
        if (behindCount > 0) {
            behindString = toOrange(behindString);
        }

        String result = " (↑" + aheadString + " ↓" + behindString + ") ";
        return result;
    }

    public String toOrange(String text) {
        String result = "<font color=orange>" + text + "</font>";
        return result;
    }

    public String toBlue(String text) {
        String result = "<font color=blue>" + text + "</font>";
        return result;
    }

    public String toRed(String text) {
        String result = "<font color=red>" + text + "</font>";
        return result;
    }

    public String toLime(String text) {
        String result = "<font color=lime>" + text + "</font>";
        return result;
    }

    public String toMagenta(String text) {
        String result = "<font color=magenta>" + text + "</font>";
        return result;
    }

    public String toPurple(String text) {
        String result = "<font color=purple>" + text + "</font>";
        return result;
    }

    public String toOlive(String text) {
        String result = "<font color=olive>" + text + "</font>";
        return result;
    }

    public String toStagingStatus(String text) {
        String result = toItalic(text);
        final String changed = JGSstagingStatus.Changed.toString();
        String added = JGSstagingStatus.Added.toString();
        String removed = JGSstagingStatus.Removed.toString();
        String modified = JGSstagingStatus.Modified.toString();
        String untracked = JGSstagingStatus.Untracked.toString();
        String conflicting = JGSstagingStatus.Conflicting.toString();
        String missing = JGSstagingStatus.Missing.toString();

        JGSstagingStatus status = JGSstagingStatus.lookup(text);
        switch (status) {
            case Changed:
                result = toOlive(result);
                break;
            case Added:
                result = toLime(result);
                break;
            case Removed:
                result = toMagenta(result);
                break;
            case Modified:
                result = toOlive(result);
                break;
            case Untracked:
                result = toOrange(result);
                break;
            case Conflicting:
                result = toRed(result);
                break;
            case Missing:
                result = toMagenta(result);
                break;
            default:
                result = text;
                break;
        }
        return result;
    }

    public String toBackgroundRed(String text) {
        String result = "<font bgcolor=red>" + text + "</font>";
        return result;
    }

    public String toBackgroundOlive(String text) {
        String result = "<font bgcolor=olive>" + text + "</font>";
        return result;
    }

}
