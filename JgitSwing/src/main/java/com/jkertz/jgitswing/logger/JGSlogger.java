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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jürgen Kertz
 */
public class JGSlogger {

    private static JGSlogger INSTANCE = null;
    private static Logger logger;
    private final JGSlogHandler handler;
    private final List<Level> availableLevels;

    private JGSlogger() {
        logger = Logger.getLogger("JGSlogger");
        logger.setLevel(Level.ALL);
        handler = JGSlogHandler.getINSTANCE();
        logger.addHandler(handler);
        availableLevels = new ArrayList<>();
        availableLevels.add(Level.ALL);
        availableLevels.add(Level.FINE);
        availableLevels.add(Level.INFO);
        availableLevels.add(Level.WARNING);
        availableLevels.add(Level.SEVERE);
        availableLevels.add(Level.OFF);
    }

    public static JGSlogger getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new JGSlogger();
        }
        return INSTANCE;
    }

    public Logger getLogger() {
        return logger;
    }

    public Logger getLogger(String name) {
        return Logger.getLogger(name);
    }

    public void setLogLevel(Level level) {
        System.out.println("setLogLevel: " + level);
        logger.setLevel(level);
    }

    public Level getLogLevel() {
        return logger.getLevel();
    }

    public List<Level> getAvailableLevels() {
        return availableLevels;
    }

}
