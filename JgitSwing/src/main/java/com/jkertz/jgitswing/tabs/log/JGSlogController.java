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
package com.jkertz.jgitswing.tabs.log;

import java.awt.Color;
import java.util.logging.Level;
import com.jkertz.jgitswing.logger.IJGSlogHandler;
import com.jkertz.jgitswing.logger.JGSlogHandler;
import com.jkertz.jgitswing.logger.JGSlogger;
import com.jkertz.jgitswing.tabs.common.IJGSsubTabController;

/**
 *
 * @author jkertz
 */
public class JGSlogController implements IJGSlogHandler, IJGSsubTabController, IJGSlogPanel {

    private final String name = "Log";
    private JGSlogPanel panel;
    private JGSlogHandler handler;
    private JGSlogger logger;

    public JGSlogController() {
        panel = new JGSlogPanel(this);
        logger = JGSlogger.getINSTANCE();
        handler = JGSlogHandler.getINSTANCE();
        handler.addReceiver(this);

        Level logLevel = logger.getLogLevel();
        panel.setLoglevel(logLevel);
    }

    public JGSlogPanel getPanel() {
        return panel;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void onLoggerInfo(String message) {
        panel.addFormattedLog(message, Color.GREEN);

    }

    @Override
    public void onLoggerWarning(String message) {
        panel.addFormattedLog(message, Color.ORANGE);
    }

    @Override
    public void onLoggerError(String message) {
        panel.addFormattedLog(message, Color.RED);
    }

    @Override
    public void onLoggerFine(String message) {
        panel.addFormattedLog(message, null);
    }

    @Override
    public void refresh() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void onLogPanelLevelChanged(Level selectedItem) {
        logger.setLogLevel(selectedItem);
    }

    @Override
    public void deconstruct() {
        String className = this.getClass().getName();
        System.out.println(className + " deconstruct");

        handler.removeReceiver(this);
        panel = null;
        logger = null;
        handler = null;

    }

    @Override
    protected void finalize() throws Throwable {
        try {
            // Cleanup operations
            String className = this.getClass().getName();
            System.out.println(className + " finalize");

        } finally {
            super.finalize();
        }
    }

}
