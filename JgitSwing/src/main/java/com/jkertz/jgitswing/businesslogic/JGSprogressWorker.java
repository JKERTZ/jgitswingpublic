/*
 * Copyright (C) 2024 JKERTZ
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
package com.jkertz.jgitswing.businesslogic;

import java.util.List;
import javax.swing.SwingWorker;

/**
 * SwingWorker<Result ,Progress>
 *
 * @author jkertz
 */
public class JGSprogressWorker extends SwingWorker<Boolean, String> {

    JGSworkerProgress progress = new JGSworkerProgress();

    /**
     * Die "doInBackground()"-Methode wird in einem eigenen Background-Thread
     * ausgefuehrt. Sie darf nicht direkt Swing-Komponenten manipulieren.
     */
    @Override
    protected Boolean doInBackground() throws Exception {
        /**
         * Die "publish()"-Methode sendet Zwischenergebnis-Objekte an die
         * "process()"-Methode, in welcher Swing-Aktionen Thread-sicher
         * asynchron im EDT ausgefuehrt werden.
         */

        return null;

    }

    /**
     * Die "process()"-Methode empfaengt die ueber "publish()" uebergebenen
     * Objekte. Sie laeuft im EDT und kann asynchron Swing-Komponenten
     * manipulieren.
     */
    @Override
    protected void process(List chunks) {
        // define what the event dispatch thread
        // will do with the intermediate results
        // received while the thread is executing

    }

    /**
     * Die "done()"-Methode wird nach Beendigung der "doInBackground()"-Methode
     * aufgerufen. Sie laeuft im EDT und kann Swing-Komponenten manipulieren.
     */
    @Override
    protected void done() {

    }
}
