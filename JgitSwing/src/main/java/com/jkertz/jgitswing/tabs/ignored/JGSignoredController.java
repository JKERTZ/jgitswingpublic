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
package com.jkertz.jgitswing.tabs.ignored;

import com.jkertz.jgitswing.callback.IJGScallbackRefresh;
import com.jkertz.jgitswing.callback.IJGScallbackStatus;
import com.jkertz.jgitswing.model.JGSrepositoryModel;
import com.jkertz.jgitswing.tabs.common.IJGScommonController;
import com.jkertz.jgitswing.tabs.common.JGScommonController;
import java.util.logging.Level;
import org.eclipse.jgit.api.Status;

/**
 *
 * @author jkertz
 */
public final class JGSignoredController extends JGScommonController implements IJGSignoredPanel, IJGScommonController {

    JGSignoredPanel panel;

    public JGSignoredController(JGSrepositoryModel jGSrepositoryModel) {
        super("Ignored", jGSrepositoryModel);
        panel = new JGSignoredPanel(this);
        setPanel(panel);
//        bc.addReceiver(this);
    }

//    @Override
//    public void onIJGSbcIndexChanged() {
//        logger.getLogger().fine("onIJGSbcIndexChanged");
//        refresh();
//    }
//    @Override
//    public void onIJGSbcRefsChanged() {
//        logger.getLogger().fine("onIJGSbcRefsChanged");
//        refresh();
//    }
    @Override
    public void updateWidgets(IJGScallbackRefresh refresh) {
        //chain only independent methods here
//        bc.getStatus(updateIgnoredTableCallback(refresh));
        updateIgnoredTable(refresh);

    }

    private void updateIgnoredTable(IJGScallbackRefresh refresh) {

        new Thread(() -> {
            try {
                Status status = jGSrepositoryModel.getStatus();
                panel.updateIgnoredTable(status, endOfChainCallback(refresh));
            } catch (Exception ex) {
                logger.getLogger().log(Level.SEVERE, "updateIgnoredTable", ex);
            }
            refresh.finish();
        }).start();
    }

    private IJGScallbackStatus updateIgnoredTableCallback(IJGScallbackRefresh refresh) {
        IJGScallbackStatus callback = new IJGScallbackStatus() {
            @Override
            public void onSuccess(Status result) {
                panel.updateIgnoredTable(result, endOfChainCallback(refresh));
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
                showErrorDialog("updateTables", "getStatus ERROR:\n" + ex.getMessage());
                refresh.finish();
            }
        };
        return callback;
    }

    @Override
    public void deconstruct() {
        String className = this.getClass().getName();
        System.out.println(className + " deconstruct");
        panel = null;
//        bc.removeReceiver(this);
        super.deconstruct();

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
