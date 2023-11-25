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
package com.jkertz.jgitswing.widgets.graph;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Stroke;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.eclipse.jgit.awtui.UIText;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revplot.PlotCommit;
import org.eclipse.jgit.revplot.PlotCommitList;

/**
 *
 * @author jkertz
 */
public class JGSgraphPane extends JTable {

    private static final long serialVersionUID = 1L;

    private final JGScommitList allCommits;

    /**
     * Create a new empty panel.
     */
    public JGSgraphPane() {
        allCommits = new JGScommitList();
        configureHeader();
        setShowHorizontalLines(false);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        configureRowHeight();
    }

    private void configureRowHeight() {
        int h = 0;
        for (int i = 0; i < getColumnCount(); ++i) {
            TableCellRenderer renderer = getDefaultRenderer(getColumnClass(i));
            Component c = renderer.getTableCellRendererComponent(this,
                    "ÅOj", false, false, 0, i); //$NON-NLS-1$
            h = Math.max(h, c.getPreferredSize().height);
        }
        setRowHeight(h + getRowMargin());
    }

    /**
     * Get the commit list this pane renders from.
     *
     * @return the list the caller must populate.
     */
    public PlotCommitList getCommitList() {
        return allCommits;
    }

    @Override
    public void setModel(final TableModel dataModel) {
        if (dataModel != null && !(dataModel instanceof CommitTableModel)) {
            throw new ClassCastException(UIText.get().mustBeSpecialTableModel);
        }
        super.setModel(dataModel);
    }

    @Override
    protected TableModel createDefaultDataModel() {
        return new CommitTableModel();
    }

    private void configureHeader() {
        final JTableHeader th = getTableHeader();
        final TableColumnModel cols = th.getColumnModel();

        final TableColumn graph = cols.getColumn(0);
        final TableColumn author = cols.getColumn(1);
        final TableColumn date = cols.getColumn(2);

        graph.setHeaderValue(""); //$NON-NLS-1$
        author.setHeaderValue(UIText.get().author);
        date.setHeaderValue(UIText.get().date);

        graph.setCellRenderer(new GraphCellRender());
        author.setCellRenderer(new NameCellRender());
        date.setCellRenderer(new DateCellRender());
    }

    class CommitTableModel extends AbstractTableModel {

        private static final long serialVersionUID = 1L;

        PlotCommit<JGSswingLane> lastCommit;

        PersonIdent lastAuthor;

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public int getRowCount() {
            return allCommits != null ? allCommits.size() : 0;
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            final PlotCommit<JGSswingLane> c = allCommits.get(rowIndex);
            if (c == null) {
                return null;
            }
            switch (columnIndex) {
                case 0:
                    return c;
                case 1:
                    return authorFor(c);
                case 2:
                    return authorFor(c);
                default:
                    return null;
            }
        }

        PersonIdent authorFor(final PlotCommit<JGSswingLane> c) {
            if (c != lastCommit) {
                lastCommit = c;
                lastAuthor = c.getAuthorIdent();
            }
            return lastAuthor;
        }
    }

    static class NameCellRender extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(final JTable table,
                final Object value, final boolean isSelected,
                final boolean hasFocus, final int row, final int column) {
            final PersonIdent pi = (PersonIdent) value;

            final String valueStr;
            if (pi != null) {
                valueStr = pi.getName() + " <" + pi.getEmailAddress() + ">"; //$NON-NLS-1$ //$NON-NLS-2$
            } else {
                valueStr = ""; //$NON-NLS-1$
            }
            return super.getTableCellRendererComponent(table, valueStr,
                    isSelected, hasFocus, row, column);
        }
    }

    static class DateCellRender extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;

        private final DateFormat fmt = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$

        @Override
        public Component getTableCellRendererComponent(final JTable table,
                final Object value, final boolean isSelected,
                final boolean hasFocus, final int row, final int column) {
            final PersonIdent pi = (PersonIdent) value;

            final String valueStr;
            if (pi != null) {
                valueStr = fmt.format(pi.getWhen());
            } else {
                valueStr = ""; //$NON-NLS-1$
            }
            return super.getTableCellRendererComponent(table, valueStr,
                    isSelected, hasFocus, row, column);
        }
    }

    static class GraphCellRender extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;

        private final JGSplotRenderer renderer = new JGSplotRenderer(this);

        PlotCommit<JGSswingLane> commit;

        @Override
        @SuppressWarnings("unchecked")
        public Component getTableCellRendererComponent(final JTable table,
                final Object value, final boolean isSelected,
                final boolean hasFocus, final int row, final int column) {
            super.getTableCellRendererComponent(table, value, isSelected,
                    hasFocus, row, column);
            commit = (PlotCommit<JGSswingLane>) value;
            return this;
        }

        @Override
        protected void paintComponent(final Graphics inputGraphics) {
            if (inputGraphics == null) {
                return;
            }
            renderer.paint(inputGraphics, commit);
        }
    }

    static final Stroke[] strokeCache;

    static {
        strokeCache = new Stroke[4];
        for (int i = 1; i < strokeCache.length; i++) {
            strokeCache[i] = new BasicStroke(i);
        }
    }

    static Stroke stroke(final int width) {
        if (width < strokeCache.length) {
            return strokeCache[width];
        }
        return new BasicStroke(width);
    }

}
