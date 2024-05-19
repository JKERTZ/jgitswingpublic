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
package com.jkertz.jgitswing.tabs.tags;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import com.jkertz.jgitswing.tablemodels.IterableRevCommitTableModel;
import com.jkertz.jgitswing.tablemodels.ListJGStagsTableModel;
import com.jkertz.jgitswing.tabs.common.JGScommonPanel;
import com.jkertz.jgitswing.toolbars.tags.IJGStagsHistoryToolbar;
import com.jkertz.jgitswing.toolbars.tags.IJGStagsToolbar;
import com.jkertz.jgitswing.toolbars.tags.JGStagsHistoryToolbar;
import com.jkertz.jgitswing.toolbars.tags.JGStagsToolbar;
import com.jkertz.jgitswing.widgets.tags.IJGShistoryTableWidget;
import com.jkertz.jgitswing.widgets.tags.IJGStagsWidget;
import com.jkertz.jgitswing.widgets.tags.JGShistoryTableWidget;
import com.jkertz.jgitswing.widgets.tags.JGStagsWidget;

/**
 *
 * @author jkertz
 */
public class JGStagsPanel extends JGScommonPanel implements IJGStagsWidget, IJGStagsToolbar, IJGStagsHistoryToolbar, IJGShistoryTableWidget {

    private final IJGStagsPanel receiver;
    private final JGStagsToolbar jGStagsToolbar;
    private final JGStagsHistoryToolbar JGStagsHistoryToolbar;
    private final JGStagsWidget jGStagsWidget;
    private final JGShistoryTableWidget jGShistoryTableWidget;

    protected JGStagsPanel(IJGStagsPanel receiver) {
        super(new BorderLayout());
        this.receiver = receiver;

        jGStagsToolbar = new JGStagsToolbar(this);
        JGStagsHistoryToolbar = new JGStagsHistoryToolbar(this);
        jGStagsWidget = new JGStagsWidget(this);
        jGShistoryTableWidget = new JGShistoryTableWidget(this);

        JPanel gridPanel = new JPanel(new GridLayout(2, 1));

        gridPanel.add(getTagsPanel());
        gridPanel.add(getHistoryPanel());

        this.add(gridPanel, BorderLayout.CENTER);
    }

    protected void updateHistoryTable(IterableRevCommitTableModel tableModel) {
        jGShistoryTableWidget.updateHistoryTable(tableModel);
    }

//    protected void updateTagTable(List<Ref> result, IJGScallbackChain callback) {
//        jGStagsWidget.render(result, callback);
//    }
    void updateTagTable(ListJGStagsTableModel tableModelJGStags) {
        jGStagsWidget.render(tableModelJGStags);
    }

    private JPanel getHistoryPanel() {
        JPanel jPanel = new JPanel(new BorderLayout());
        jPanel.add(JGStagsHistoryToolbar, BorderLayout.NORTH);
        jPanel.add(jGShistoryTableWidget, BorderLayout.CENTER);
        return jPanel;
    }

    private JPanel getTagsPanel() {
        JPanel jPanel = new JPanel(new BorderLayout());
        jPanel.add(jGStagsToolbar, BorderLayout.NORTH);
        jPanel.add(jGStagsWidget, BorderLayout.CENTER);
        return jPanel;
    }

    @Override
    public void onTagsToolbarClickedShow5() {
        receiver.onTagsToolbarClickedShow5();
    }

    @Override
    public void onTagsToolbarClickedShow100() {
        receiver.onTagsToolbarClickedShow100();
    }

    @Override
    public void onTagsToolbarClickedShowAll() {
        receiver.onTagsToolbarClickedShowAll();
    }

    @Override
    public void onTagsToolbarClickedPushTags() {
        receiver.onTagsToolbarClickedPushTags();
    }

    @Override
    public void onHistoryTableWidgetSelectionChanged(String _commitId) {
        receiver.onHistoryTableWidgetSelectionChanged(_commitId);
    }

    @Override
    public void onTagsWidgetSelectionChanged() {
        receiver.onTagsWidgetSelectionChanged();
    }

    @Override
    public void onTagsHistoryToolbarClickedCreateTag() {
        receiver.onTagsHistoryToolbarClickedCreateTag();
    }

    @Override
    public void onTagsHistoryToolbarClickedShow5() {
        receiver.onTagsHistoryToolbarClickedShow5();
    }

    @Override
    public void onTagsHistoryToolbarClickedShow100() {
        receiver.onTagsHistoryToolbarClickedShow100();
    }

    @Override
    public void onTagsHistoryToolbarClickedShowAll() {
        receiver.onTagsHistoryToolbarClickedShowAll();
    }

}
