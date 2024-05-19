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

import com.jkertz.jgitswing.tablemodels.IterableRevCommitTableModel;
import com.jkertz.jgitswing.tablemodels.ListDiffEntryTableModel;
import com.jkertz.jgitswing.tabs.common.JGScommonPanel;
import com.jkertz.jgitswing.toolbars.history.IJGShistoryToolbar;
import com.jkertz.jgitswing.toolbars.history.JGShistoryToolbar;
import com.jkertz.jgitswing.widgets.currentdiff.IJGScurrentdiffWidget;
import com.jkertz.jgitswing.widgets.currentdiff.IJGSfileStatusWidget;
import com.jkertz.jgitswing.widgets.currentdiff.JGScurrentdiffWidget;
import com.jkertz.jgitswing.widgets.currentdiff.JGSfileStatusWidget;
import com.jkertz.jgitswing.widgets.tags.IJGShistoryTableWidget;
import com.jkertz.jgitswing.widgets.tags.JGShistoryTableWidget;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.text.DefaultStyledDocument;

/**
 *
 * @author jkertz
 */
public class JGShistoryPanel extends JGScommonPanel implements IJGShistoryToolbar, IJGShistoryTableWidget, IJGScurrentdiffWidget, IJGSfileStatusWidget {

    private final IJGShistoryPanel receiver;
    private final JGShistoryToolbar jGShistoryToolbar;
    private final JGShistoryTableWidget jGShistoryTableWidget;
    private final JGScurrentdiffWidget jGScurrentdiffWidget;
    private final JGSfileStatusWidget jGSfileStatusWidget;

    protected JGShistoryPanel(IJGShistoryPanel receiver) {
        super(new BorderLayout());
        this.receiver = receiver;

        jGShistoryToolbar = new JGShistoryToolbar(this);
//        jGShistoryWidget = new JGShistoryWidget(this);
        jGShistoryTableWidget = new JGShistoryTableWidget(this);
        jGScurrentdiffWidget = new JGScurrentdiffWidget(this);
        jGSfileStatusWidget = new JGSfileStatusWidget(this);

        JPanel gridPanel = new JPanel(new GridLayout(2, 1));
        JPanel subGridPanel = new JPanel(new GridLayout(1, 2));

        subGridPanel.add(jGSfileStatusWidget);
        subGridPanel.add(jGScurrentdiffWidget);

        gridPanel.add(jGShistoryTableWidget);
        gridPanel.add(subGridPanel);

        this.add(jGShistoryToolbar, BorderLayout.NORTH);
//        this.add(jGShistoryWidget, BorderLayout.CENTER);
        this.add(gridPanel, BorderLayout.CENTER);

    }

    public void updateHistoryTable(IterableRevCommitTableModel tableModel) {
        jGShistoryTableWidget.updateHistoryTable(tableModel);
    }

    public void updateCurrentfile(DefaultStyledDocument doc) {
        jGScurrentdiffWidget.updateCurrentfile(doc);
    }

    public void updateFileTables(ListDiffEntryTableModel tableModel) {
        jGSfileStatusWidget.updateFileTables(tableModel);
    }

    @Override
    public void onHistoryToolbarClickedShow100(String text) {
        receiver.onHistoryPanelClickedShow100(text);
    }

    @Override
    public void onHistoryToolbarClickedShowAll(String text) {
        receiver.onHistoryPanelClickedShowAll(text);
    }

    @Override
    public void onHistoryTableWidgetSelectionChanged(String _commitId) {
        receiver.onHistoryTableWidgetSelectionChanged(_commitId);
    }

    @Override
    public void onFileStatusWidgetListSelectionChanged(List<String> selectionList) {
        receiver.onFileStatusWidgetListSelectionChanged(selectionList);
    }

    @Override
    public void onCommonToolbarClickedRefresh() {
        receiver.onCommonToolbarClickedRefresh();
    }

    @Override
    public void onHistoryToolbarClickedCheckout() {
        receiver.onHistoryToolbarClickedCheckout();
    }

}
