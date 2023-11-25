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
package com.jkertz.jgitswing.settings;

import com.jkertz.jgitswing.logger.JGSlogger;
import com.jkertz.jgitswing.model.JGSrecent;
import com.jkertz.jgitswing.model.JGSsetting;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author jkertz
 */
public class JGSsettings {

    private JGSsetting setting;
    private static JGSsettings INSTANCE = null;
    private final Set<IJGSsettings> receivers;

    private final JGSlogger logger;
    private final String filename;
    private final String subDirectory;
    private final String sep;

    private final String defaultTheme = "FlatHiberbeeDarkIJTheme";//"Nimbus"
    private final String defaultFileName = "JGSsettings.ser";
    private final String defaultSubDirectory = ".JGS";

    private JGSsettings() {
        this.logger = JGSlogger.getINSTANCE();
        this.filename = defaultFileName;
        this.subDirectory = defaultSubDirectory;
        this.sep = getFileSeparator();
        this.receivers = new HashSet<>();
//        validateSettingsFile();
        checkAndCreateUserSubdir();
        if (!settingsFileExists()) {
            this.setting = createNewSettingsFile();
            save();
        } else {
            this.setting = load();

        }

    }

    public static JGSsettings getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new JGSsettings();
        }
        return INSTANCE;
    }

    public void addReceiver(IJGSsettings receiver) {
        receivers.add(receiver);
    }

    public void loadRecentRepositories() {
        notifyRecentRepositoryChanged();
    }

    /**
     *
     * @param newTheme
     */
    public void setTheme(String newTheme) {
        String theme = setting.getTheme();
        if (newTheme != null && !newTheme.isEmpty() && !newTheme.equals(theme)) {
            setting.setTheme(newTheme);
            save();
        }
    }

    /**
     *
     * @return
     */
    public String getTheme() {
        String theme = setting.getTheme();
        return theme;
    }

    /**
     *
     * @param repoPath
     * @param user
     * @param password
     * @param uri
     */
    public void setUserAndPassword(String repoPath, String user, String password, String uri) {
        boolean userValid = user != null && !user.isEmpty();
        boolean passwordValid = password != null && !password.isEmpty();
        boolean repoPathValid = repoPath != null && !repoPath.isEmpty();
        if (userValid && passwordValid && repoPathValid) {
            JGSrecent recent = findJGSrecentByPath(repoPath);
            if (recent != null) {
                //existing recent
                String recentRemoteUsername = recent.getRemoteUsername();
                String recentRemotePassword = recent.getRemotePassword();
                String recentUri = recent.getUri();
                boolean userChanged = !user.equals(recentRemoteUsername);
                boolean passwordChanged = !password.equals(recentRemotePassword);
                boolean uriChanged = uri != null && !uri.equals(recentUri);
                boolean saveRequired = false;

                if (userChanged) {
                    recent.setRemoteUsername(user);
                    saveRequired = true;
                }
                if (passwordChanged) {
                    recent.setRemotePassword(password);
                    saveRequired = true;
                }
                if (uriChanged) {
                    recent.setUri(uri);
                    saveRequired = true;
                }
                if (saveRequired) {
                    save();
                }
            } else {
                //new recent
                recent = new JGSrecent();
                recent.setRemoteUsername(user);
                recent.setRemotePassword(password);
                recent.setLocalPath(repoPath);
                recent.setUri(uri);
                setting.getRecents().add(recent);
                save();
            }
        }
    }

    /**
     *
     * @param repoPath
     * @param user
     * @param password
     */
    public void setUserAndPassword(String repoPath, String user, String password) {
        boolean userValid = user != null && !user.isEmpty();
        boolean passwordValid = password != null && !password.isEmpty();
        boolean repoPathValid = repoPath != null && !repoPath.isEmpty();
        if (userValid && passwordValid && repoPathValid) {
            JGSrecent recent = findJGSrecentByPath(repoPath);
            if (recent != null) {
                //existing recent
                String recentRemoteUsername = recent.getRemoteUsername();
                String recentRemotePassword = recent.getRemotePassword();
                String recentUri = recent.getUri();
                boolean userChanged = !user.equals(recentRemoteUsername);
                boolean passwordChanged = !password.equals(recentRemotePassword);
                boolean saveRequired = false;

                if (userChanged) {
                    recent.setRemoteUsername(user);
                    saveRequired = true;
                }
                if (passwordChanged) {
                    recent.setRemotePassword(password);
                    saveRequired = true;
                }
                if (saveRequired) {
                    save();
                }
            } else {
                //new recent
                recent = new JGSrecent();
                recent.setRemoteUsername(user);
                recent.setRemotePassword(password);
                recent.setLocalPath(repoPath);
                setting.getRecents().add(recent);
                save();
            }
        }
    }

    /**
     *
     * @param path
     * @return
     */
    public String getUsername(String path) {
        JGSrecent currenRepository = findJGSrecentByPath(path);
        if (currenRepository != null) {
            return currenRepository.getRemoteUsername();
        }
        return null;
    }

    /**
     *
     * @param path
     * @return
     */
    public String getPassword(String path) {
        JGSrecent currenRepository = findJGSrecentByPath(path);
        if (currenRepository != null) {
            return currenRepository.getRemotePassword();
        }
        return null;
    }

    public JGSrecent getRemoteSettings(String path) {
        JGSrecent currenRepository = findJGSrecentByPath(path);
        return currenRepository;
    }

    /**
     *
     * @param path
     */
    public void setPath(String path) {
        logger.getLogger().fine("setPath");
//        JGSrecent savedRecent = JGSutils.getINSTANCE().findSavedRecent(directory, recentRepositorys);
        JGSrecent savedRecent = findJGSrecentByPath(path);
        if (savedRecent == null) {
            //not found
            logger.getLogger().fine("setPath not found");
            JGSrecent recent = new JGSrecent();
            recent.setLocalPath(path);
            setting.getRecents().add(recent);
            save();
            notifyRecentRepositoryChanged();
        } else {
            //already saved, set saved as current
            logger.getLogger().fine("setPath already saved");
        }
    }

//    private void validateSettingsFile() {
//        checkAndCreateUserSubdir();
//        if (!settingsFileExists()) {
//            createNewSettingsFile();
//        }
//    }
    private JGSsetting load() {
        FileInputStream streamIn = null;
        ObjectInputStream objectinputstream = null;

        try {
            String fullFileName = getFullFileName();
            streamIn = new FileInputStream(fullFileName);
            objectinputstream = new ObjectInputStream(streamIn);

//            Set<JGSrecent> readCase = (Set<JGSrecent>) objectinputstream.readObject();
            Object readObject = objectinputstream.readObject();
            JGSsetting jGSsetting = (JGSsetting) readObject;
            return jGSsetting;
//            setting = (JGSsetting) objectinputstream.readObject();
        } catch (ClassNotFoundException e) {
            //saved settings do not match current code
            logger.getLogger().severe(e.getMessage());
            logger.getLogger().warning("creating new settings");

        } catch (IOException e) {
            logger.getLogger().severe(e.getMessage());
        } finally {
            if (objectinputstream != null) {
                try {
                    if (objectinputstream != null) {
                        objectinputstream.close();
                    }
                    if (streamIn != null) {
                        streamIn.close();
                    }
                } catch (IOException ex) {
                    logger.getLogger().severe(ex.getMessage());
                }
            }
        }
        return new JGSsetting();
    }

    private void save() {
        logger.getLogger().info("save");
        FileOutputStream fout = null;
        ObjectOutputStream oos = null;
        try {
            Set<JGSrecent> recents = setting.getRecents();
            printRecents(recents);
            String fullFileName = getFullFileName();
            fout = new FileOutputStream(fullFileName);
            oos = new ObjectOutputStream(fout);
            oos.writeObject(setting);
        } catch (FileNotFoundException ex) {
            logger.getLogger().severe(ex.getMessage());
        } catch (IOException ex) {
            logger.getLogger().severe(ex.getMessage());
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
                if (fout != null) {
                    fout.close();
                }
            } catch (IOException ex) {
                logger.getLogger().severe(ex.getMessage());

            }
        }
    }

    /**
     * print List/Set of recents
     *
     * @param recents
     */
    private void printRecents(Set<JGSrecent> recents) {
        for (JGSrecent recent : recents) {
            printRecent(recent);
        }
    }

    /**
     * print values of one recent
     *
     * @param recent
     */
    private void printRecent(JGSrecent recent) {
        System.out.println("LocalPath: " + recent.getLocalPath());
        System.out.println("RemoteUsername: " + recent.getRemoteUsername());
        System.out.println("RemotePassword: " + recent.getRemotePassword());
    }

    /**
     * get full file name, OS compliant
     *
     * @return full file name OS compliant as String
     */
    private String getFullFileName() {
        String userDir = getUserDir();
        String result = userDir + sep + subDirectory + sep + filename;
        return result;
    }

    /**
     * get Users Home Dir, OS compliant
     *
     * @return Users Home Dir, OS compliant as String
     */
    private String getUserDir() {
        String currentUsersHomeDir = System.getProperty("user.home");
        return currentUsersHomeDir;
    }

    /**
     * check And Create Subdir in user home, OS compliant
     */
    private void checkAndCreateUserSubdir() {
        String userDir = getUserDir();
        String userSubDir = userDir + sep + subDirectory;
        Path directory = Paths.get(userSubDir);

        if (Files.exists(directory)) {
            System.out.println("Directory already exists.");
        } else {
            try {
                Files.createDirectories(directory);
                System.out.println("Directory created successfully: " + userSubDir);
            } catch (IOException e) {
                System.out.println("Failed to create directory: " + userSubDir + e.getMessage());
            }
        }
    }

    /**
     * get Systems FileSeparator, OS compliant
     *
     * @return Systems FileSeparator, OS compliant
     */
    private String getFileSeparator() {
        String fileSeparator = System.getProperty("file.separator");
        System.out.println("File separator: " + fileSeparator);
        return fileSeparator;
    }

    /**
     * check if settings file exists
     *
     * @return
     */
    private boolean settingsFileExists() {
        String fullFileName = getFullFileName();
        Path path = Paths.get(fullFileName);
        return Files.exists(path);
    }

    /**
     * create/overwrite settings file
     */
    private JGSsetting createNewSettingsFile() {
        JGSsetting jGSsetting = new JGSsetting();
        Set<JGSrecent> recents = new LinkedHashSet();
        jGSsetting.setTheme(defaultTheme);
        jGSsetting.setRecents(recents);
        return jGSsetting;
    }

    private JGSrecent findJGSrecentByPath(String repoPath) {
        Set<JGSrecent> recents = setting.getRecents();
        for (JGSrecent recent : recents) {
            String localPath = recent.getLocalPath();
            if (repoPath.equals(localPath)) {
                return recent;
            }
        }
        return null;
    }

    private JGSrecent findJGSrecentByUri(String repoUri) {
        Set<JGSrecent> recents = setting.getRecents();
        for (JGSrecent recent : recents) {
            String localUri = recent.getUri();
            if (repoUri.equals(localUri)) {
                return recent;
            }
        }
        return null;
    }

    private void notifyRecentRepositoryChanged() {
        logger.getLogger().fine("notifyRecentRepositoryChanged");
        List<String> recentRepositoryPaths = getRecentRepositoryPaths();
        for (IJGSsettings receiver : receivers) {
            receiver.onIJGSsettingsRecentRepositoryChanged(recentRepositoryPaths);
        }
    }

    private List<String> getRecentRepositoryPaths() {
        List<String> recentRepositoryPaths = new ArrayList<>();
        Set<JGSrecent> recents = setting.getRecents();
        for (JGSrecent recent : recents) {
            String localPath = recent.getLocalPath();
            recentRepositoryPaths.add(localPath);
        }

        return recentRepositoryPaths;
    }

}
