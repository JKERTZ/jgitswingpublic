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
package com.jkertz.jgitswing.tabs.repository;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import com.jkertz.jgitswing.tabs.common.JGScommonPanel;
import com.jkertz.jgitswing.toolbars.repository.IJGSrepositoryToolbar;
import com.jkertz.jgitswing.toolbars.repository.JGSrepositoryToolbar;

/**
 *
 * @author jkertz
 */
public class JGSrepositoryPanel extends JGScommonPanel implements IJGSrepositoryToolbar {

    private final JTabbedPane jTabbedPane;
    private final IJGSrepositoryPanel receiver;
    private final JGSrepositoryToolbar jGSrepositoryToolbar;

    protected JGSrepositoryPanel(IJGSrepositoryPanel receiver) {
        super(new BorderLayout());
        this.receiver = receiver;

        jTabbedPane = new JTabbedPane();
        jGSrepositoryToolbar = new JGSrepositoryToolbar(this);

        this.add(jGSrepositoryToolbar, BorderLayout.NORTH);
        this.add(jTabbedPane, BorderLayout.CENTER);

        initTabClickListener(jTabbedPane);

    }

    protected void addTab(String title, JPanel tab) {
        jTabbedPane.addTab(title, tab);
    }

    protected void removeTab(String title) {
        int tabCount = jTabbedPane.getTabCount();
        for (int i = 0; i < tabCount; i++) {
            System.out.println("Tabtitle at index " + i + " : " + jTabbedPane.getTitleAt(i));
            if (jTabbedPane.getTitleAt(i).equals(title)) {
                jTabbedPane.removeTabAt(i);
                break;
            }
        }
    }

    protected JLabel getLabelBranch() {
        return jGSrepositoryToolbar.getLabelBranch();
    }

    protected void showRightClickMenu() {

    }

    @Override
    public void onRepositoryToolbarClickedFetch() {
        receiver.onRepositoryPanelClickedFetch();
    }

    @Override
    public void onRepositoryToolbarClickedPull() {
        receiver.onRepositoryPanelClickedPull();
    }

    @Override
    public void onRepositoryToolbarClickedPush() {
        receiver.onRepositoryPanelClickedPush();
    }

    @Override
    public void onRepositoryToolbarClickedRefresh() {
        receiver.onRepositoryPanelClickedRefresh();
    }

    @Override
    public void onRepositoryToolbarClickedPushAndFetch() {
        receiver.onRepositoryPanelClickedPushAndFetch();
    }

    private void onRepositoryTabRightClick(JTabbedPane tabbedPane, int x, int y) {
        receiver.onRepositoryTabRightClick(tabbedPane, x, y);
    }

    private void initTabClickListener(JTabbedPane tabbedPane) {
        tabbedPane.setToolTipText("right click for options");
        tabbedPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    onRepositoryTabRightClick(tabbedPane, e.getX(), e.getY());
                }
            }
        });
    }

}
