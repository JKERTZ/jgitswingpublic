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
package com.jkertz.jgitswing.tabs.ignored;

import java.awt.BorderLayout;
import java.util.List;
import com.jkertz.jgitswing.tablemodels.StatusIgnoredTableModel;
import com.jkertz.jgitswing.tabs.common.JGScommonPanel;
import com.jkertz.jgitswing.widgets.currentdiff.IJGSfileStatusWidget;
import com.jkertz.jgitswing.widgets.currentdiff.JGSfileStatusWidget;

/**
 *
 * @author jkertz
 */
public class JGSignoredPanel extends JGScommonPanel implements IJGSfileStatusWidget {

    private final IJGSignoredPanel receiver;
    private final JGSfileStatusWidget jGSfileStatusWidget;

    protected JGSignoredPanel(IJGSignoredPanel receiver) {
        super(new BorderLayout());
        this.receiver = receiver;

        jGSfileStatusWidget = new JGSfileStatusWidget(this);

        this.add(jGSfileStatusWidget, BorderLayout.CENTER);
    }

    public void updateIgnoredTable(StatusIgnoredTableModel tableModelIgnored) {
        jGSfileStatusWidget.updateIgnoredTable(tableModelIgnored);
    }

    @Override
    public void onFileStatusWidgetListSelectionChanged(List<String> selectionList) {
    }

}
