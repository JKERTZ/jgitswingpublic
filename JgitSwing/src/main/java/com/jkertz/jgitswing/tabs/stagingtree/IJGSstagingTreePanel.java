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
package com.jkertz.jgitswing.tabs.stagingtree;

import java.util.List;

/**
 *
 * @author jkertz
 */
public interface IJGSstagingTreePanel {

    public void onStagingPanelClickedStage();

    public void onStagingPanelClickedRemove();

    public void onStagingPanelClickedStageAll();

    public void onStagingPanelClickedHardReset();

    public void onStagingPanelClickedUnstage();

    public void onStagingPanelClickedUnstageAll();

    public void onStagingPanelClickedCommit();

    public void onStagingPanelClickedResetFile();

    public void onUnstagedListSelectionChanged(List<String> selectionList);

    public void onStagedListSelectionChanged(List<String> selectionList);

}
