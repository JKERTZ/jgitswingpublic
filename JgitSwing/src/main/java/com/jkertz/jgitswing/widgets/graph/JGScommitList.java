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

import java.awt.Color;
import java.util.LinkedList;
import org.eclipse.jgit.revplot.PlotCommitList;
import org.eclipse.jgit.revplot.PlotLane;

/**
 *
 * @author jkertz
 */
public class JGScommitList extends PlotCommitList<JGSswingLane> {

    final LinkedList<Color> colors;

    JGScommitList() {
        colors = new LinkedList<>();
        repackColors();
    }

    private void repackColors() {
        colors.add(Color.green);
        colors.add(Color.blue);
        colors.add(Color.red);
        colors.add(Color.magenta);
        colors.add(Color.darkGray);
        colors.add(Color.yellow.darker());
        colors.add(Color.orange);
    }

    @Override
    protected JGSswingLane createLane() {
        final JGSswingLane lane = new JGSswingLane();
        if (colors.isEmpty()) {
            repackColors();
        }
        lane.color = colors.removeFirst();
        return lane;
    }

    @Override
    protected void recycleLane(final JGSswingLane lane) {
        colors.add(lane.color);
    }

//    @Override
//    protected SwingLane createLane() {
//        final SwingLane lane = new SwingLane();
//        if (colors.isEmpty()) {
//            repackColors();
//        }
//        lane.color = colors.removeFirst();
//        return lane;
//    }
//
//    @Override
//    protected void recycleLane(final SwingLane lane) {
//        colors.add(lane.color);
//    }
//
//    static class SwingLane extends PlotLane {
//
//        private static final long serialVersionUID = 1L;
//        Color color;
//    }
}
