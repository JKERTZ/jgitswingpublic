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
package com.jkertz.jgitswing.toolbars.branches;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import com.jkertz.jgitswing.toolbars.common.JGScommonToolbar;

/**
 *
 * @author jkertz
 */
public final class JGSbranchesToolbar extends JGScommonToolbar {

    private final IJGSbranchesToolbar receiver;
    private final JButton buttonCreate;
    private final JButton buttonCheckout;
    private final JButton buttonMerge;
    private final JButton buttonDelete;

    public JGSbranchesToolbar(IJGSbranchesToolbar receiver) {
        super();
        this.receiver = receiver;
        buttonCreate = new JButton("Create branch");
        buttonCheckout = new JButton("↳ Checkout");
        buttonMerge = new JButton("⤞ Merge");
        buttonDelete = new JButton("⌫ Delete");

        buttonCreate.addActionListener(getActionListenerCreate());
        buttonCheckout.addActionListener(getActionListenerCheckout());
        buttonMerge.addActionListener(getActionListenerMerge());
        buttonDelete.addActionListener(getActionListenerDelete());

        this.add(buttonCreate);
        this.add(buttonCheckout);
        this.add(buttonMerge);
        this.add(buttonDelete);

        disableBranchButtos();
    }

    public void enableBranchButtos() {
        buttonCheckout.setEnabled(true);
        buttonMerge.setEnabled(true);
        buttonDelete.setEnabled(true);
    }

    public void disableBranchButtos() {
        buttonCheckout.setEnabled(false);
        buttonMerge.setEnabled(false);
        buttonDelete.setEnabled(false);

    }

    private ActionListener getActionListenerCreate() {
        ActionListener actionListener = (ActionEvent e) -> {
            receiver.onBranchesToolbarClickedCreate();
        };
        return actionListener;
    }

    private ActionListener getActionListenerCheckout() {
        ActionListener actionListener = (ActionEvent e) -> {
            receiver.onBranchesToolbarClickedCheckout();
        };
        return actionListener;
    }

    private ActionListener getActionListenerMerge() {
        ActionListener actionListener = (ActionEvent e) -> {
            receiver.onBranchesToolbarClickedMerge();
        };
        return actionListener;
    }

    private ActionListener getActionListenerDelete() {
        ActionListener actionListener = (ActionEvent e) -> {
            receiver.onBranchesToolbarClickedDelete();
        };
        return actionListener;
    }

}
