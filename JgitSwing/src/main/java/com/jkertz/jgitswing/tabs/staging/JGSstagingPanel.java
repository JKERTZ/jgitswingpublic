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
package com.jkertz.jgitswing.tabs.staging;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.JPanel;
import com.jkertz.jgitswing.tablemodels.StatusStagedTableModel;
import com.jkertz.jgitswing.tablemodels.StatusUnstagedTableModel;
import com.jkertz.jgitswing.tabs.common.JGScommonPanel;
import com.jkertz.jgitswing.toolbars.staged.IJGSstagedToolbar;
import com.jkertz.jgitswing.toolbars.staged.JGSstagedToolbar;
import com.jkertz.jgitswing.toolbars.unstaged.IJGSunstagedToolbar;
import com.jkertz.jgitswing.toolbars.unstaged.JGSunstagedToolbar;
import com.jkertz.jgitswing.widgets.currentdiff.IJGSfileStatusWidget;
import com.jkertz.jgitswing.widgets.currentdiff.JGSfileStatusWidget;

/**
 *
 * @author jkertz
 */
public class JGSstagingPanel extends JGScommonPanel implements IJGSunstagedToolbar, IJGSstagedToolbar {

    private final IJGSstagingPanel receiver;
    private JGSunstagedToolbar jGSunstagedToolbar;
    private JGSstagedToolbar jGSstagedToolbar;
    private final JGSfileStatusWidget jGSfileStatusWidgetUnstaged;
    private final JGSfileStatusWidget jGSfileStatusWidgetStaged;

    protected JGSstagingPanel(IJGSstagingPanel receiver) {
        super(new BorderLayout());
        this.receiver = receiver;

        jGSfileStatusWidgetUnstaged = new JGSfileStatusWidget(unstagedListSelectionChangedCallback());
        jGSfileStatusWidgetStaged = new JGSfileStatusWidget(stagedListSelectionChangedCallback());

        JPanel gridPanel = new JPanel(new GridLayout(2, 1));

        gridPanel.add(getUnstagedPanel());
        gridPanel.add(getStagedPanel());

        this.add(gridPanel, BorderLayout.CENTER);
    }

    public void updateStagedTable(StatusStagedTableModel tableModelStaged) {
        jGSfileStatusWidgetStaged.updateStagedTable(tableModelStaged);
    }

    public void updateUnstagedTable(StatusUnstagedTableModel tableModelUnstaged) {
        jGSfileStatusWidgetUnstaged.updateUnstagedTable(tableModelUnstaged);
    }

    private IJGSfileStatusWidget unstagedListSelectionChangedCallback() {
        IJGSfileStatusWidget callback = (List<String> selectionList) -> {
            receiver.onUnstagedListSelectionChanged(selectionList);
        };
        return callback;
    }

    private IJGSfileStatusWidget stagedListSelectionChangedCallback() {
        IJGSfileStatusWidget callback = (List<String> selectionList) -> {
            receiver.onStagedListSelectionChanged(selectionList);
        };
        return callback;
    }

    private JPanel getUnstagedPanel() {
        JPanel jPanel = new JPanel(new BorderLayout());
        jGSunstagedToolbar = new JGSunstagedToolbar(this);
        jPanel.add(jGSunstagedToolbar, BorderLayout.NORTH);
        jPanel.add(jGSfileStatusWidgetUnstaged, BorderLayout.CENTER);

        return jPanel;
    }

    private JPanel getStagedPanel() {
        JPanel jPanel = new JPanel(new BorderLayout());
        jGSstagedToolbar = new JGSstagedToolbar(this);
        jPanel.add(jGSstagedToolbar, BorderLayout.NORTH);
        jPanel.add(jGSfileStatusWidgetStaged, BorderLayout.CENTER);

        return jPanel;
    }

    @Override
    public void onUnstagedToolbarClickedStage() {
        receiver.onStagingPanelClickedStage();
    }

    @Override
    public void onUnstagedToolbarClickedRemove() {
        receiver.onStagingPanelClickedRemove();
    }

    @Override
    public void onUnstagedToolbarClickedStageAll() {
        receiver.onStagingPanelClickedStageAll();
    }

    @Override
    public void onUnstagedToolbarClickedHardReset() {
        receiver.onStagingPanelClickedHardReset();
    }

    @Override
    public void onStagedToolbarClickedUnstage() {
        receiver.onStagingPanelClickedUnstage();
    }

    @Override
    public void onStagedToolbarClickedUnstageAll() {
        receiver.onStagingPanelClickedUnstageAll();
    }

    @Override
    public void onStagedToolbarClickedCommit() {
        receiver.onStagingPanelClickedCommit();
    }

    @Override
    public void onUnstagedToolbarClickedResetFile() {
        receiver.onStagingPanelClickedResetFile();
    }

}
