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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.io.Serializable;
import com.jkertz.jgitswing.widgets.graph.JGSgraphPane.GraphCellRender;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revplot.AbstractPlotRenderer;
import org.eclipse.jgit.revplot.PlotCommit;

/**
 *
 * @author jkertz
 */
public class JGSplotRenderer extends AbstractPlotRenderer<JGSswingLane, Color>
        implements Serializable {

    private static final long serialVersionUID = 1L;
    final GraphCellRender cell;
    transient Graphics2D g;

    final int yOffset = -2;

    JGSplotRenderer(final GraphCellRender c) {
        cell = c;
    }

    void paint(final Graphics in, final PlotCommit<JGSswingLane> commit) {
        g = (Graphics2D) in.create();
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        rh.add(new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON));
        g.setRenderingHints(rh);
        try {
            final int h = cell.getHeight();
            g.setColor(cell.getBackground());
            g.fillRect(0, 0, cell.getWidth(), h);
            if (commit != null) {
                paintCommit(commit, h);
            }
        } finally {
            g.dispose();
            g = null;
        }
    }

    @Override
    protected void drawLine(final Color color, int x1, int y1, int x2,
            int y2, int width) {
        g.setColor(color);
        g.setStroke(JGSgraphPane.stroke(width));

        if (y1 == y2) {
            x1 -= width / 2;
            x2 -= width / 2;
            g.drawLine(x1, y1, x2, y2);
        } else if (x1 == x2) {
            y1 -= width / 2;
            y2 -= width / 2;
            g.drawLine(x1, y1, x2, y2);
        } else if (x2 > x1 && y2 > y1) {
            int rx = x2 - x1;
            int ry = y2 - y1 + width * 2;
            g.drawArc(x1, y1, rx, ry, 0, 90);
        } else if (x1 > x2 && y2 > y1) {
            int rx = x1 - x2;
            int ry = y2 - y1 + width * 2;
            g.drawArc(x2, y1, rx, ry, 90, 90);
        } else if (x2 > x1 && y1 > y2) {
            int rx = x2 - x1;
            int ry = y1 - y2 + width * 2;
            g.drawArc(x1, y2 - width * 2, rx, ry, 0, -90);
        } else if (x1 > x2 && y1 > y2) {
            int rx = x1 - x2;
            int ry = y1 - y2 + width * 2;
            g.drawArc(x2, y2 - width * 2, rx, ry, -90, -90);
        }
    }

    @Override
    protected void drawCommitDot(final int x, final int y, final int w,
            final int h) {
        g.setColor(Color.blue);
        g.setStroke(JGSgraphPane.strokeCache[1]);
        g.fillOval(x, y, w, h);
        g.setColor(Color.black);
        g.drawOval(x, y, w, h);
    }

    @Override
    protected void drawBoundaryDot(final int x, final int y, final int w,
            final int h) {
        g.setColor(cell.getBackground());
        g.setStroke(JGSgraphPane.strokeCache[1]);
        g.fillOval(x, y, w, h);
        g.setColor(Color.black);
        g.drawOval(x, y, w, h);
    }

    @Override
    protected void drawText(final String msg, final int x, final int y) {
        final int texth = g.getFontMetrics().getHeight();
        final int y0 = yOffset + (y - texth) / 2 + (cell.getHeight() - texth) / 2;
        g.setColor(cell.getForeground());
        g.drawString(msg, x, y0 + texth - g.getFontMetrics().getDescent());
    }

    @Override
    protected Color laneColor(final JGSswingLane myLane) {
        return myLane != null ? myLane.color : Color.black;
    }

    void paintTriangleDown(final int cx, final int y, final int h) {
        final int tipX = cx;
        final int tipY = y + h;
        final int baseX1 = cx - 10 / 2;
        final int baseX2 = tipX + 10 / 2;
        final int baseY = y;
        final Polygon triangle = new Polygon();
        triangle.addPoint(tipX, tipY);
        triangle.addPoint(baseX1, baseY);
        triangle.addPoint(baseX2, baseY);
        g.fillPolygon(triangle);
        g.drawPolygon(triangle);
    }

    @Override
    protected int drawLabel(int x, int y, Ref ref) {
        String txt;
        String name = ref.getName();
        if (name.startsWith(Constants.R_HEADS)) {
            g.setBackground(Color.GREEN);
            txt = name.substring(Constants.R_HEADS.length());
        } else if (name.startsWith(Constants.R_REMOTES)) {
            g.setBackground(Color.LIGHT_GRAY);
            txt = name.substring(Constants.R_REMOTES.length());
        } else if (name.startsWith(Constants.R_TAGS)) {
            g.setBackground(Color.YELLOW);
            txt = name.substring(Constants.R_TAGS.length());
        } else {
            // Whatever this would be
            g.setBackground(Color.WHITE);
            if (name.startsWith(Constants.R_REFS)) {
                txt = name.substring(Constants.R_REFS.length());
            } else {
                txt = name; // HEAD and such
            }
        }
        if (ref.getPeeledObjectId() != null) {
            float[] colorComponents = g.getBackground().getRGBColorComponents(null);
            colorComponents[0] *= 0.9f;
            colorComponents[1] *= 0.9f;
            colorComponents[2] *= 0.9f;
            g.setBackground(new Color(colorComponents[0], colorComponents[1], colorComponents[2]));
        }
        if (txt.length() > 12) {
            txt = txt.substring(0, 11) + "\u2026"; // ellipsis "…" (in UTF-8) //$NON-NLS-1$
        }
        final int texth = g.getFontMetrics().getHeight();
        int textw = g.getFontMetrics().stringWidth(txt);
        g.setColor(g.getBackground());
        int arcHeight = texth / 4;
        int jgsHeight = texth - 1;

        int y0 = yOffset + y - texth / 2 + (cell.getHeight() - texth) / 2;
        g.fillRoundRect(x, y0, textw + arcHeight * 2, jgsHeight, jgsHeight, jgsHeight);
        g.setColor(g.getColor().darker());
        g.drawRoundRect(x, y0, textw + arcHeight * 2, jgsHeight, jgsHeight, jgsHeight);
        g.setColor(Color.BLACK);
        g.drawString(txt, x + arcHeight, y0 + texth - g.getFontMetrics().getDescent());

        return arcHeight * 3 + textw;
    }
}
