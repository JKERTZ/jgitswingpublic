/*
 * Copyright (C) 2022 Jürgen Kertz
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
package com.jkertz.jgitswing.logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 *
 * @author Jürgen Kertz
 */
public class JGSlogHandler extends Handler {

    private static JGSlogHandler INSTANCE = null;
    private final Set<IJGSlogHandler> receivers = new HashSet<>();

    private JGSlogHandler() {
    }

    public static JGSlogHandler getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new JGSlogHandler();
        }
        return INSTANCE;
    }

    public void addReceiver(IJGSlogHandler receiver) {
        receivers.add(receiver);
    }

    public void removeReceiver(IJGSlogHandler receiver) {
        receivers.remove(receiver);
    }

    @Override
    public void publish(LogRecord record) {
        Level level = record.getLevel();
        String message = "";
        message += new Date(record.getMillis());
        message += " ";
        message += record.getSourceClassName();
        message += " ";
        message += record.getSourceMethodName();
        message += "\n";
        message += record.getLevel().getLocalizedName();
        message += ": ";
        message += record.getMessage();
        Throwable exception = record.getThrown();
        if (exception != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            String stacktrace = sw.toString();
            message += "\n";
            message += stacktrace;
        }
        System.out.println("JGSlogHandler: " + message);

        if (level.equals(Level.INFO)) {
            notifyInfo(message);
        }
        if (level.equals(Level.WARNING)) {
            notifyWarning(message);
        }
        if (level.equals(Level.SEVERE)) {
            notifyError(message);
        }
        if (level.equals(Level.FINE)) {
            notifyFine(message);
        }

    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }

    private void notifyInfo(String message) {
        for (IJGSlogHandler receiver : receivers) {
            receiver.onLoggerInfo(message);
        }

    }

    private void notifyWarning(String message) {
        for (IJGSlogHandler receiver : receivers) {
            receiver.onLoggerWarning(message);
        }
    }

    private void notifyError(String message) {
        for (IJGSlogHandler receiver : receivers) {
            receiver.onLoggerError(message);
        }
    }

    private void notifyFine(String message) {
        for (IJGSlogHandler receiver : receivers) {
            receiver.onLoggerFine(message);
        }
    }

}
