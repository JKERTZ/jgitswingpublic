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
package com.jkertz.jgitswing.dialogs;

import com.jkertz.jgitswing.logger.JGSlogger;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.RebaseResult;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.eclipse.jgit.transport.TrackingRefUpdate;

/**
 *
 * @author jkertz
 */
public class JGSdialogPanelFactory {

    private static JGSdialogPanelFactory INSTANCE = null;
    private final JGSlogger logger;

    private JGSdialogPanelFactory() {
        logger = JGSlogger.getINSTANCE();
    }

    protected static JGSdialogPanelFactory getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new JGSdialogPanelFactory();
        }
        return INSTANCE;
    }

    protected JPanel getDialogPanelPushResults(Iterable<PushResult> pushResults) {
        logger.getLogger().fine("getDialogPanel PushResult");
        JPanel dialogPanel = getDialogPanel("PushResult");

        for (PushResult pushResult : pushResults) {
            dialogPanel.add(getDialogPanel(pushResult));
        }
        return makeScrollable(dialogPanel);
    }

    protected JPanel getDialogPanel(PullResult pullResult) {
        logger.getLogger().fine("getDialogPanel PullResult");
        JPanel dialogPanel = getDialogPanel("PullResult");

        FetchResult fetchResult = pullResult.getFetchResult();
        MergeResult mergeResult = pullResult.getMergeResult();
        RebaseResult rebaseResult = pullResult.getRebaseResult();
        if (fetchResult != null) {
            JPanel fetchResultPanel = getDialogPanel(fetchResult);
            dialogPanel.add(fetchResultPanel);
        }
        if (mergeResult != null) {
            JPanel mergeResultPanel = getDialogPanel(mergeResult);
            dialogPanel.add(mergeResultPanel);
        }
        if (rebaseResult != null) {
            JPanel rebaseResultPanel = getDialogPanel(rebaseResult);
            dialogPanel.add(rebaseResultPanel);
        }

        return makeScrollable(dialogPanel);
    }

    protected JPanel getDialogPanel(FetchResult fetchResult) {
        logger.getLogger().fine("getDialogPanel FetchResult");
        JPanel dialogPanel = getDialogPanel("FetchResult");

        dialogPanel.add(getNameValuePanel("fetchResult.getMessages()", fetchResult.getMessages()));

        Collection<Ref> advertisedRefs = fetchResult.getAdvertisedRefs();
        if (advertisedRefs != null) {
            List<String> advertisedRefsStrings = advertisedRefs.stream().map(e -> e.getName()).collect(Collectors.toList());
            dialogPanel.add(getStringListPanel("advertisedRefs", advertisedRefsStrings));
        }

        Collection<TrackingRefUpdate> trackingRefUpdates = fetchResult.getTrackingRefUpdates();
        if (trackingRefUpdates != null) {
            List<String> trackingRefUpdatesStrings = trackingRefUpdates.stream().map(e -> e.getLocalName()).collect(Collectors.toList());
            dialogPanel.add(getStringListPanel("trackingRefUpdates", trackingRefUpdatesStrings));
        }

        return makeScrollable(dialogPanel);
    }

    protected JPanel getDialogPanel(MergeResult mergeResult) {
        logger.getLogger().fine("getDialogPanel MergeResult");
        JPanel dialogPanel = getDialogPanel("MergeResult");

        dialogPanel.add(getNameValuePanel("mergeResult.getMergeStatus()", mergeResult.getMergeStatus().toString()));
        dialogPanel.add(new JSeparator());
        dialogPanel.add(getNameValuePanel("mergeResult.getBase()", mergeResult.getBase().toString()));
        dialogPanel.add(new JSeparator());
        dialogPanel.add(getNameValuePanel("mergeResult.getNewHead()", mergeResult.getNewHead().toString()));

        List<String> checkoutConflicts = mergeResult.getCheckoutConflicts();
        if (checkoutConflicts != null) {
            dialogPanel.add(getStringListPanel("checkoutConflicts", mergeResult.getCheckoutConflicts()));
        }
//        Map<String, int[][]> conflicts = mergeResult.getConflicts();
        ObjectId[] mergedCommits = mergeResult.getMergedCommits();
        if (mergedCommits != null) {
            List<ObjectId> mergedCommitsList = Arrays.asList(mergedCommits);
            List<String> mergedCommitsStrings = mergedCommitsList.stream().map(e -> e.toString()).collect(Collectors.toList());
            dialogPanel.add(getStringListPanel("mergedCommits", mergedCommitsStrings));
        }
        return makeScrollable(dialogPanel);
    }

    protected JPanel getLabeledInput(String text, JTextField jTextField, String value, boolean isReadonly) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
//        panel.setBackground(Color.YELLOW);

        JPanel subpanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//        subpanel.setBackground(Color.RED);

        JLabel jLabel = new JLabel(text, JLabel.TRAILING);
        jTextField.setColumns(40);
        jLabel.setLabelFor(jTextField);
        jTextField.setEditable(!isReadonly);
        if (value != null) {
            jTextField.setText(value);
        }
        subpanel.add(jLabel);
        subpanel.add(jTextField);

        subpanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        panel.add(subpanel);

        return panel;
    }

    protected JPanel getParameterMapPanel(Map<String, JTextField> inputMap, Map<String, String> parameters, boolean isReadonly) {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        for (String key : parameters.keySet()) {
            String value = parameters.get(key);
            panel.add(new JLabel(key));
            JTextField input = new JTextField();
            input.setEditable(!isReadonly);
            if (value != null) {
                input.setText(value);
            }
            input.setColumns(30);
            panel.add(input);
            inputMap.put(key, input);
        }
        return panel;
    }

    protected JPanel getParameterMapPanel(Map<String, JTextField> inputMap, Map<String, JCheckBox> optionMap, Map<String, String> parameters, Map<String, Boolean> options, boolean isReadonly) {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        for (String key : parameters.keySet()) {
            String value = parameters.get(key);
            panel.add(new JLabel(key));
            JTextField input = new JTextField("input123");
            input.setEditable(!isReadonly);
            if (value != null) {
                input.setText(value);
            }
            input.setColumns(30);
            panel.add(input);
            inputMap.put(key, input);
        }

        for (String key : options.keySet()) {
            Boolean value = options.get(key);
            JCheckBox check = new JCheckBox(key);
            check.setEnabled(!isReadonly);
            if (value != null) {
                check.setSelected(value);
            }
            panel.add(check);
            optionMap.put(key, check);
        }
        return panel;
    }

    protected JPanel getParameterMapPanelSectional(Map<String, JTextField> inputMap, Map<String, Map<String, Map<String, String>>> parameters, boolean isReadonly) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        for (String section : parameters.keySet()) {
            System.out.println("Section: " + section);
            JPanel sectionPanel = new JPanel(new GridLayout(0, 1));
            TitledBorder sectionBorder = new TitledBorder(section);
            sectionPanel.setBorder(sectionBorder);
            panel.add(sectionPanel);

            Map<String, Map<String, String>> subSectionMap = parameters.get(section);
            for (String subSection : subSectionMap.keySet()) {
                System.out.println("-SubSection: " + subSection);
                JPanel subSectionPanel = new JPanel(new GridLayout(0, 1));

                TitledBorder subSectionBorder = new TitledBorder(subSection);
                subSectionPanel.setBorder(subSectionBorder);
                sectionPanel.add(subSectionPanel);

                Map<String, String> nameMap = subSectionMap.get(subSection);
                for (String name : nameMap.keySet()) {
                    String value = nameMap.get(name);
                    System.out.println("--Name: " + name + " Value: " + value);

                    JTextField input = new JTextField();
                    JPanel nameValuePanel = JGSdialogPanelFactory.getINSTANCE().getLabeledInput(name, input, value, isReadonly);

                    subSectionPanel.add(nameValuePanel);
                    String key = section + subSection + name;
                    inputMap.put(key, input);
                }
            }
        }

        return panel;
    }

    private JPanel getDialogPanel(RebaseResult rebaseResult) {
        logger.getLogger().fine("getDialogPanel RebaseResult");
        JPanel dialogPanel = getDialogPanel("RebaseResult");
        dialogPanel.add(getNameValuePanel("rebaseResult.getStatus()", rebaseResult.getStatus().toString()));

        return dialogPanel;
    }

    private JPanel getNameValuePanel(String name, String value) {
        JPanel nameValuePanel = new JPanel(new GridLayout(0, 1));
        nameValuePanel.add(new JLabel(name));
        JTextField input = new JTextField();
        input.setEditable(false);
        if (value != null) {
            input.setText(value);
        }
        input.setColumns(40);
        nameValuePanel.add(input);
        return nameValuePanel;
    }

    private JPanel getStringListPanel(String title, List<String> values) {
        logger.getLogger().fine("getDialogPanel MergeResult");
        JPanel dialogPanel = new JPanel(new GridLayout(0, 1));
        TitledBorder dialogBorder = new TitledBorder(title);
        dialogPanel.setBorder(dialogBorder);

        for (String value : values) {
            dialogPanel.add(new JLabel(value));
        }
        return dialogPanel;
    }

    private JPanel getDialogPanel(String title) {
        JPanel dialogPanel = new JPanel();
        dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.PAGE_AXIS));
        TitledBorder dialogBorder = new TitledBorder(title);
        dialogPanel.setBorder(dialogBorder);
        return dialogPanel;
    }

    private JPanel getDialogPanel(PushResult pushResult) {
        logger.getLogger().fine("getDialogPanel PushResult");
        JPanel dialogPanel = getDialogPanel("PushResult");
        Collection<RemoteRefUpdate> remoteUpdates = pushResult.getRemoteUpdates();
        for (RemoteRefUpdate remoteUpdate : remoteUpdates) {
            dialogPanel.add(getDialogPanel(remoteUpdate));
        }
        return dialogPanel;
    }

    private JPanel getDialogPanel(RemoteRefUpdate remoteUpdate) {
        logger.getLogger().fine("getDialogPanel RemoteRefUpdate");
        JPanel dialogPanel = getDialogPanel("RemoteRefUpdate: " + remoteUpdate.getRemoteName());

        dialogPanel.add(getNameValuePanel("getMessage", remoteUpdate.getMessage()));
        dialogPanel.add(getNameValuePanel("getRemoteName", remoteUpdate.getRemoteName()));
        dialogPanel.add(getNameValuePanel("getSrcRef", remoteUpdate.getSrcRef()));
        dialogPanel.add(getNameValuePanel("getStatus", remoteUpdate.getStatus().toString()));
        dialogPanel.add(getNameValuePanel("getNewObjectId", remoteUpdate.getNewObjectId().getName()));
        if (remoteUpdate.getExpectedOldObjectId() != null) {
            dialogPanel.add(getNameValuePanel("getExpectedOldObjectId", remoteUpdate.getExpectedOldObjectId().getName()));

        }
        return dialogPanel;
    }

    private JPanel makeScrollable(JPanel panel) {
        int prefHeight = panel.getPreferredSize().height;
        int prefWidth = panel.getPreferredSize().width;
        int maxHeight = 400;
        if (prefHeight > maxHeight) {
            logger.getLogger().fine("makeScrollable prefHeight: " + prefHeight);
            JScrollPane jScrollPane = new JScrollPane();
            int scrollbarWidth = jScrollPane.getVerticalScrollBar().getPreferredSize().width;
            jScrollPane.setPreferredSize(new Dimension(prefWidth + scrollbarWidth + 10, maxHeight));

            jScrollPane.setViewportView(panel);
            JPanel scrollable = new JPanel();
            scrollable.add(jScrollPane);
            return scrollable;
        }
        return panel;
    }

}
