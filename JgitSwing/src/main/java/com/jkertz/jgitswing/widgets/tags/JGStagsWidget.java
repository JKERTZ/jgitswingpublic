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
package com.jkertz.jgitswing.widgets.tags;

import java.util.List;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import com.jkertz.jgitswing.callback.IJGScallbackChain;
import com.jkertz.jgitswing.model.JGStag;
import com.jkertz.jgitswing.tablemodels.ListJGStagsTableModel;
import com.jkertz.jgitswing.widgets.common.JGScommonScrollwidget;

/**
 *
 * @author jkertz
 */
public class JGStagsWidget extends JGScommonScrollwidget {

    private final IJGStagsWidget receiver;
    private JTable jTableTags;

    public JGStagsWidget(IJGStagsWidget receiver) {
        super();
        this.receiver = receiver;

        jTableTags = new JTable();
        jTableTags.getSelectionModel().addListSelectionListener(getListSelectionListenerTags());
        this.setViewportView(jTableTags);

    }

    public JTable getjTableTags() {
        return jTableTags;
    }

//    public void render(List<Ref> result, IJGScallbackChain callback) {
//        System.out.println("JGStagsWidget render BEGIN");
//
//        DefaultTableModel dtm = uiUtils.buildTableModelTags(result);
//
//        SwingUtilities.invokeLater(() -> {
//            jTableTags.removeAll();
//            uiUtils.setTableSingleSelect(jTableTags, dtm);
//            System.out.println("JGStagsWidget render FINISHED");
//            callback.doNext(null);
//        });
//    }
    public void render(List<JGStag> result, IJGScallbackChain callback) {
        System.out.println("JGStagsWidget render BEGIN");

//        DefaultTableModel dtm = uiUtils.buildTableModelJGStags(result);
        ListJGStagsTableModel tableModelJGStags = uiUtils.getTableModelJGStags(result);
        SwingUtilities.invokeLater(() -> {
            jTableTags.removeAll();
            uiUtils.setTableSingleSelect(jTableTags, tableModelJGStags);
            System.out.println("JGStagsWidget render FINISHED");
            callback.doNext(null);
        });
    }

    private ListSelectionListener getListSelectionListenerTags() {
        ListSelectionListener listSelectionListener = (ListSelectionEvent lse) -> {
            receiver.onTagsWidgetSelectionChanged();
        };
        return listSelectionListener;
    }

}
