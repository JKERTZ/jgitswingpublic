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
package com.jkertz.jgitswing.businesslogic;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revplot.PlotWalk;

/**
 *
 * @author jkertz
 */
public class JGSPlotWalk extends JGScachedResult {

    private PlotWalk plotWalk;

    public JGSPlotWalk(Git git) {
        super(git);
    }

    public PlotWalk getPlotWalk(String branch)
            throws Exception {
        if (!isValid) {
            plotWalk = wrapper.getPlotWalk(git, branch);
            //TODO: reenable after finding correct invalidation
//            isValid = true;
        }
        return plotWalk;
    }
}
