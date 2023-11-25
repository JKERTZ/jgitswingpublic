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
package com.jkertz.jgitswing.model;

import com.jkertz.jgitswing.logger.JGSlogger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.events.ConfigChangedEvent;
import org.eclipse.jgit.events.ConfigChangedListener;
import org.eclipse.jgit.events.IndexChangedEvent;
import org.eclipse.jgit.events.IndexChangedListener;
import org.eclipse.jgit.events.RefsChangedEvent;
import org.eclipse.jgit.events.RefsChangedListener;
import org.eclipse.jgit.events.RepositoryListener;
import org.eclipse.jgit.events.WorkingTreeModifiedEvent;
import org.eclipse.jgit.events.WorkingTreeModifiedListener;

/**
 *
 * @author jkertz
 */
public class JGSgitModel implements ConfigChangedListener, IndexChangedListener, RefsChangedListener, RepositoryListener, WorkingTreeModifiedListener {

    private IJGSgitModel receiver;
    private JGSlogger logger;

    private Git git;

    public JGSgitModel(IJGSgitModel receiver) {
        this.receiver = receiver;
        logger = JGSlogger.getINSTANCE();
    }

    public Git getGit() {
        return git;
    }

    /**
     * sets current git repository and JGIT changelisteners
     *
     * @param git
     */
    public void setGit(Git git) {
        if (git == null) {
            logger.getLogger().warning("setGit null");
            return;
        }
        this.git = git;
        git.getRepository().getListenerList().addConfigChangedListener(this);
        git.getRepository().getListenerList().addIndexChangedListener(this);
        git.getRepository().getListenerList().addRefsChangedListener(this);
        git.getRepository().getListenerList().addWorkingTreeModifiedListener(this);
    }

    @Override
    public void onConfigChanged(ConfigChangedEvent cce) {
        logger.getLogger().fine("onConfigChanged");
        receiver.onIJGSmodelConfigChanged();
    }

    @Override
    public void onIndexChanged(IndexChangedEvent ice) {
        logger.getLogger().fine("onIndexChanged");
        receiver.onIJGSmodelIndexChanged();
    }

    @Override
    public void onRefsChanged(RefsChangedEvent rce) {
        logger.getLogger().fine("onRefsChanged");
        receiver.onIJGSmodelRefsChanged();
    }

    @Override
    public void onWorkingTreeModified(WorkingTreeModifiedEvent wtme) {
        logger.getLogger().fine("onWorkingTreeModified");
        receiver.onIJGSmodelWorkingTreeModified();
    }

    public void deconstruct() {
        String className = this.getClass().getName();
        System.out.println(className + " deconstruct");
        receiver = null;
        logger = null;
        git.close();
        git = null;
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
