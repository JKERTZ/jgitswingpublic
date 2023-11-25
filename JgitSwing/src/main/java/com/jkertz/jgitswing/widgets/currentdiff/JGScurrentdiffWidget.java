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
package com.jkertz.jgitswing.widgets.currentdiff;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultStyledDocument;
import com.jkertz.jgitswing.callback.IJGScallbackChain;
import com.jkertz.jgitswing.widgets.common.JGScommonScrollwidget;

/**
 *
 * @author jkertz
 */
public class JGScurrentdiffWidget extends JGScommonScrollwidget {

    private final IJGScurrentdiffWidget receiver;
    private JTextPane jTextPane1;

    public JGScurrentdiffWidget(IJGScurrentdiffWidget receiver) {
        super();
        this.receiver = receiver;

        jTextPane1 = new JTextPane();

        this.setViewportView(jTextPane1);
    }

    public void updateCurrentfile(String currentDiffFile, IJGScallbackChain callback) {
        DefaultStyledDocument doc = uiUtils.buildStyledDocumentFromFileDiff(currentDiffFile);
        SwingUtilities.invokeLater(() -> {
            jTextPane1.setDocument(doc);
            callback.doNext(null);
        });
    }

}
