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
package com.jkertz.jgitswing.tabs.currentdiff;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.JPanel;
import com.jkertz.jgitswing.tablemodels.ListDiffEntryTableModel;
import com.jkertz.jgitswing.tabs.common.JGScommonPanel;
import com.jkertz.jgitswing.widgets.currentdiff.IJGScurrentdiffWidget;
import com.jkertz.jgitswing.widgets.currentdiff.IJGSfileStatusWidget;
import com.jkertz.jgitswing.widgets.currentdiff.JGScurrentdiffWidget;
import com.jkertz.jgitswing.widgets.currentdiff.JGSfileStatusWidget;
import javax.swing.text.DefaultStyledDocument;

/**
 *
 * @author jkertz
 */
public class JGScurrentDiffPanel extends JGScommonPanel implements IJGScurrentdiffWidget, IJGSfileStatusWidget {

    private final IJGScurrentDiffPanel receiver;
    private final JGScurrentdiffWidget jGScurrentdiffWidget;
    private final JGSfileStatusWidget jGSfileStatusWidget;

    protected JGScurrentDiffPanel(IJGScurrentDiffPanel receiver) {
        super(new BorderLayout());
        this.receiver = receiver;

        jGScurrentdiffWidget = new JGScurrentdiffWidget(this);
        jGSfileStatusWidget = new JGSfileStatusWidget(this);

        JPanel gridPanel = new JPanel(new GridLayout(2, 1));

        gridPanel.add(jGSfileStatusWidget);
        gridPanel.add(jGScurrentdiffWidget);

        this.add(gridPanel, BorderLayout.CENTER);

    }

    public void updateFileTables(ListDiffEntryTableModel tableModel) {
        jGSfileStatusWidget.updateFileTables(tableModel);
    }

    public void updateCurrentfile(DefaultStyledDocument doc) {
        jGScurrentdiffWidget.updateCurrentfile(doc);
    }

    @Override
    public void onFileStatusWidgetListSelectionChanged(List<String> selectionList) {
        receiver.onFileStatusWidgetListSelectionChanged(selectionList);
    }

}
