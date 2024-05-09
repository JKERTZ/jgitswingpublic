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
package com.jkertz.jgitswing.main;

import com.jkertz.jgitswing.dialogs.JGSopenRepositoryFileChooser;
import com.jkertz.jgitswing.dialogs.JgitSwingToast;
import com.jkertz.jgitswing.settings.JGSsettings;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

/**
 *
 * @author jkertz
 */
public class JGSmainView {

    private final JFrame jFrame;
    private final JMenu jMenuRepository;
    private final JMenu jMenuThemes;
    private final JMenuBar jMenuBar;
    private final JProgressBar jProgressBar;
    private final JTabbedPane jTabbedPane;
    private final JGSlookAndFeels jGSlookAndFeels;
    private final IJGSmainView receiver;

    protected JGSmainView(IJGSmainView receiver) {
        this.receiver = receiver;
        this.jGSlookAndFeels = JGSlookAndFeels.getINSTANCE();

        // enable custom window decorations (not working with native LAFs)
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

        //set default theme
        try {
            String theme = JGSsettings.getINSTANCE().getTheme();
            System.out.println("Theme from settings: " + theme);
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                System.out.println("Found LookAndFeel: " + info.getName());
                if (theme.equals(info.getName())) {
//                if ("DarkNimbusLookAndFeel".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    System.out.println("Set LookAndFeel: " + info.getName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        //create View
        jFrame = new JFrame("JGSmainView");
        jFrame.setSize(800, 600);
        LayoutManager layout = new BorderLayout();
        jFrame.setLayout(layout);

//        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);//always close window
        jFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);//handle close

        jMenuRepository = new JMenu("Repository");
        jMenuThemes = new JMenu("Themes");
        jMenuBar = new JMenuBar();
        jMenuBar.add(jMenuRepository);
        jMenuBar.add(jMenuThemes);

        jProgressBar = new JProgressBar();

        jTabbedPane = new JTabbedPane();

        jFrame.setJMenuBar(jMenuBar);
        jFrame.add(jProgressBar, BorderLayout.SOUTH);
        jFrame.add(jTabbedPane, BorderLayout.CENTER);

        jFrame.addWindowListener(getMainWindowListener());

        jFrame.setVisible(true);//making the frame visible

        // Show tool tips immediately
        ToolTipManager.sharedInstance().setInitialDelay(0);

        initMenus();
        initTabCloseListener(jTabbedPane);

    }

    protected JFrame getjFrame() {
        return jFrame;
    }

    protected JMenu getjMenuRepository() {
        return jMenuRepository;
    }

    protected JMenu getjMenuThemes() {
        return jMenuThemes;
    }

    protected JTabbedPane getjTabbedPane() {
        return jTabbedPane;
    }

    protected void showProgressBar(String text) {
        jProgressBar.setVisible(true);
        jProgressBar.setIndeterminate(true);
        jProgressBar.setString(text);
        jProgressBar.setStringPainted(true);
    }

    protected void hideProgressBar() {
        jProgressBar.setVisible(false);
    }

    protected void showToast(String message) {
        JgitSwingToast.getINSTANCE().show(jFrame, message, new Color(0, 0, 1f, 0.9f));
    }

    protected void showInfoToast(String message) {
        JgitSwingToast.getINSTANCE().show(jFrame, message, new Color(0, 1f, 0, 0.9f));
    }

    protected void showWarningToast(String message) {
        JgitSwingToast.getINSTANCE().show(jFrame, message, new Color(1f, 1f, 0, 0.9f));
    }

    protected void showErrorToast(String message) {
        JgitSwingToast.getINSTANCE().show(jFrame, message, new Color(1f, 0, 0, 0.9f));
    }

    protected void addTab(String title, JPanel tab, boolean autoselect) {
        jTabbedPane.addTab(title, tab);
        if (autoselect) {
            //autoselect new added tab
            int count = jTabbedPane.getTabCount();
            jTabbedPane.setSelectedIndex(count - 1);
        }
    }

    protected void updateRepositoryMenu(List<String> recentRepositoryPaths) {
        updateRepositoryMenu();
        jMenuRepository.add(new JSeparator());
        for (String recentRepositoryPath : recentRepositoryPaths) {
            jMenuRepository.add(getMenuItemOpenRepository(recentRepositoryPath));
        }
    }

    private void initMenus() {
        updateRepositoryMenu();
        updateThemesMenu();
    }

    private void updateRepositoryMenu() {
        jMenuRepository.removeAll();
        jMenuRepository.add(getMenuItemOpenRepository());
        jMenuRepository.add(getMenuItemInitRepository());
        jMenuRepository.add(getMenuItemInitBareRepository());
        jMenuRepository.add(getMenuItemCloneRepository());
        jMenuRepository.add(new JSeparator());
        jMenuRepository.add(getMenuItemEditSettings());

    }

    private void updateThemesMenu() {
        //System Themes
        jMenuThemes.add(getSubMenuThemes("System Themes", jGSlookAndFeels.getSystemLookAndFeels()));

        //JGS Themes
        jMenuThemes.add(getSubMenuThemes("JGS Themes", jGSlookAndFeels.getJgsLookAndFeels()));

        //FlatLAF LAFs
        jMenuThemes.add(getSubMenuThemes("FlatLAF LAFs", jGSlookAndFeels.getFlatLafLookAndFeels()));

        //FlatLAF Themes
        jMenuThemes.add(getSubMenuThemes("FlatLAF Themes", jGSlookAndFeels.getFlatLafThemes()));

        //FlatLAF Material Themes
        jMenuThemes.add(getSubMenuThemes("FlatLAF Material", jGSlookAndFeels.getFlatLafMaterialThemes()));

    }

    private JMenu getSubMenuThemes(String name, Map<String, String> themes) {
        JMenu subMenu = new JMenu(name);
        for (String theme : themes.keySet()) {
            subMenu.add(getMenuItemTheme(theme));
        }
        return subMenu;
    }

    private JMenuItem getMenuItemOpenRepository() {
        JMenuItem newItem = new JMenuItem();
        newItem.setText("Open Repository");
        newItem.setToolTipText("Open existing Repository");
        newItem.addActionListener(getActionListenerOpenRepository());
        return newItem;
    }

    private JMenuItem getMenuItemInitRepository() {
        JMenuItem newItem = new JMenuItem();
        newItem.setText("Init Repository");
        newItem.setToolTipText("Init new local Repository");
        newItem.addActionListener(getActionListenerInitRepository());
        return newItem;
    }

    private JMenuItem getMenuItemInitBareRepository() {
        JMenuItem newItem = new JMenuItem();
        newItem.setText("Init Bare Repository");
        newItem.setToolTipText("Init new local bare Repository");
        newItem.addActionListener(getActionListenerInitBareRepository());
        return newItem;
    }

    private JMenuItem getMenuItemCloneRepository() {
        JMenuItem newItem = new JMenuItem();
        newItem.setText("Clone Repository");
        newItem.setToolTipText("Clone remote Repository");
        newItem.addActionListener(getActionListenerCloneRepository());
        return newItem;
    }

    private JMenuItem getMenuItemEditSettings() {
        JMenuItem newItem = new JMenuItem();
        newItem.setText("Edit Settings");
        newItem.setToolTipText("Edit Settings");
        newItem.addActionListener(getActionListenerEditSettings());
        return newItem;
    }

    private JMenuItem getMenuItemTheme(String theme) {
        JMenuItem newItem = new JMenuItem();
        newItem.setText(theme);
        newItem.addActionListener(getActionListenerTheme(theme));
        return newItem;
    }

    private JMenuItem getMenuItemOpenRepository(String recent) {
        JMenuItem newItem = new JMenuItem();
        newItem.setText(recent);
        newItem.setToolTipText("Open existing Repository " + recent);
        newItem.addActionListener(getActionListenerOpenRepository(recent));
        return newItem;
    }

    private ActionListener getActionListenerOpenRepository(String chooseDirectory) {
        ActionListener actionListener = (ActionEvent e) -> {
            receiver.onOpenRepositoryClicked(chooseDirectory);
        };
        return actionListener;
    }

    private ActionListener getActionListenerOpenRepository() {
        ActionListener actionListener = (ActionEvent e) -> {
            receiver.onOpenRepositoryClicked();
        };
        return actionListener;
    }

    private ActionListener getActionListenerInitRepository() {
        ActionListener actionListener = (ActionEvent e) -> {
            receiver.onInitRepositoryClicked();
        };
        return actionListener;
    }

    private ActionListener getActionListenerInitBareRepository() {
        ActionListener actionListener = (ActionEvent e) -> {
            receiver.onInitBareRepositoryClicked();
        };
        return actionListener;
    }

    private ActionListener getActionListenerCloneRepository() {
        ActionListener actionListener = (ActionEvent e) -> {
            receiver.onCloneRepositoryClicked();
        };
        return actionListener;
    }

    private ActionListener getActionListenerEditSettings() {
        ActionListener actionListener = (ActionEvent e) -> {
            receiver.onEditSettingsClicked();
        };
        return actionListener;
    }

    private void closeTab(String tabTitle) {
        receiver.onCloseTab(tabTitle);
    }

    private ActionListener getActionListenerTheme(String theme) {
        ActionListener actionListener = (ActionEvent e) -> {
            changeTheme(theme);
        };
        return actionListener;
    }

    private void changeTheme(String theme) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (theme.equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    SwingUtilities.updateComponentTreeUI(jFrame);
                    JGSsettings.getINSTANCE().setTheme(theme);
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }

    }

    private void initTabCloseListener(JTabbedPane tabbedPane) {
        tabbedPane.setToolTipText("right click for options");
        tabbedPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    String selectedTabTitle = getSelectedTabTitle(tabbedPane);
                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem closer = new JMenuItem(new AbstractAction("Close " + selectedTabTitle) {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            closeTab(selectedTabTitle);
                        }
                    });
                    menu.add(closer);
                    menu.show(tabbedPane, e.getX(), e.getY());
                }
            }
        });
    }

    private String getSelectedTabTitle(JTabbedPane tabbedPane) {
        String tabTitle = "";
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex >= 0) {
            tabTitle = tabbedPane.getTitleAt(selectedIndex);
        }
        return tabTitle;
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

    protected String chooseOpenRepository() {
        JGSopenRepositoryFileChooser fileChooser = new JGSopenRepositoryFileChooser();
        int returnVal = fileChooser.showOpenDialog(jFrame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String directory = file.getAbsolutePath();
            System.out.println(directory);
            return directory;
        } else {
            System.out.println("File access cancelled by user.");
            return null;
        }

    }

    private WindowListener getMainWindowListener() {
        return new WindowAdapter() {
            // Method
            @Override
            public void windowClosing(WindowEvent e) {
                receiver.onCloseWindow();
            }
        };
    }

}
