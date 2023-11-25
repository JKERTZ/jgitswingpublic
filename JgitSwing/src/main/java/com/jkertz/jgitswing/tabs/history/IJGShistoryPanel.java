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
package com.jkertz.jgitswing.tabs.history;

import java.util.List;
import com.jkertz.jgitswing.toolbars.common.IJGScommonToolbar;

/**
 *
 * @author jkertz
 */
public interface IJGShistoryPanel extends IJGScommonToolbar {

    public void onHistoryPanelClickedShow100(String text);

    public void onHistoryPanelClickedShowAll(String text);

    public void onFileStatusWidgetListSelectionChanged(List<String> path);

    public void onHistoryTableWidgetSelectionChanged(String _commitId);

    public void onHistoryToolbarClickedCheckout();

}
