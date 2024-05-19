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

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 *
 * @author jkertz
 */
public class JGSworker {

    private final Executor executor;

    public JGSworker(Executor executor) {
        this.executor = executor;
    }

    public JGSworker() {
        this.executor = Executors.newSingleThreadExecutor(); // Create a single thread executor
    }

    public void runOnWorkerThread(Runnable task) {
        executor.execute(task);
    }
}
