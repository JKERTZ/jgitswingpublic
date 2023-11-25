/*
 * Copyright (C) 2023 jkertz
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
package com.jkertz.jgitswing.tablemodels;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author jkertz
 */
public class JGSTableModel extends AbstractTableModel {

    protected final List<String> columnNames;
    protected final List<Object[]> data;

    public JGSTableModel() {
        this.columnNames = new ArrayList<>();
        this.data = new ArrayList<>();
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    @Override
    public Object getValueAt(int row, int col) {
        Object[] rowData = data.get(row);
        return rowData[col];
    }

    /**
     * automatically determine column data type
     *
     * @param c
     * @return
     */
    @Override
    public Class getColumnClass(int c) {
        if (getRowCount() > 0) {
            return getValueAt(0, c).getClass();
        }
        //empty data, so type c
        return Object.class;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames.get(col);
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
//    @Override
//    public boolean isCellEditable(int row, int col) {
//        return true;
//    }
    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
//    @Override
//    public void setValueAt(Object value, int row, int col) {
//        Object[] rowData = data.get(row);
//        rowData[col] = value;
//        fireTableCellUpdated(row, col);
//    }
}
