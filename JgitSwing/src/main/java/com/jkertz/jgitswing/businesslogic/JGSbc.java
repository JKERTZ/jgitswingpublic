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
package com.jkertz.jgitswing.businesslogic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.jkertz.jgitswing.callback.IJGScallbackDirCache;
import com.jkertz.jgitswing.callback.IJGScallbackDirConfigInfoMap;
import com.jkertz.jgitswing.callback.IJGScallbackListDiffEntry;
import com.jkertz.jgitswing.callback.IJGScallbackListDirCache;
import com.jkertz.jgitswing.callback.IJGScallbackListInteger;
import com.jkertz.jgitswing.callback.IJGScallbackListJGStags;
import com.jkertz.jgitswing.callback.IJGScallbackListRef;
import com.jkertz.jgitswing.callback.IJGScallbackListRefCommit;
import com.jkertz.jgitswing.callback.IJGScallbackListString;
import com.jkertz.jgitswing.callback.IJGScallbackMergeResult;
import com.jkertz.jgitswing.callback.IJGScallbackPlotWalk;
import com.jkertz.jgitswing.callback.IJGScallbackPullResult;
import com.jkertz.jgitswing.callback.IJGScallbackPushResult;
import com.jkertz.jgitswing.callback.IJGScallbackRef;
import com.jkertz.jgitswing.callback.IJGScallbackRefCommit;
import com.jkertz.jgitswing.callback.IJGScallbackStatus;
import com.jkertz.jgitswing.callback.IJGScallbackString;
import com.jkertz.jgitswing.logger.JGSlogger;
import com.jkertz.jgitswing.model.IJGSgitModel;
import com.jkertz.jgitswing.model.JGSgitModel;
import com.jkertz.jgitswing.model.JGStag;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revplot.PlotWalk;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.PushResult;

/**
 *
 * @author jkertz
 */
public class JGSbc implements IJGSgitModel {

    private Set<IJGSbc> receivers;
    private JGSgitModel model;
    private JGScommandWrapper wrapper;
    private JGSlogger logger;
    private List<Thread> runningThreads;

    private JGSbc() {
        logger = JGSlogger.getINSTANCE();
        this.runningThreads = new ArrayList<>();

        this.receivers = new HashSet<>();
        model = new JGSgitModel(this);

        wrapper = JGScommandWrapper.getINSTANCE();
    }

    /**
     *
     * @param receiver
     */
    public void addReceiver(IJGSbc receiver) {
        if (receivers != null) {
            synchronized (receivers) {
                receivers.add(receiver);
            }
        }
    }

    public void removeReceiver(IJGSbc receiver) {
        if (receivers != null) {
            synchronized (receivers) {
                receivers.remove(receiver);
            }
        }
    }

    /**
     *
     * @return
     */
    public List<String> getRunningThreadNames() {
        List<String> runningThreadNames = new ArrayList<>();
        synchronized (runningThreads) {
            for (Thread runningThread : runningThreads) {
                runningThreadNames.add(runningThread.getName());
            }
        }

        return runningThreadNames;
    }

    /**
     *
     * @param chooseDirectory
     * @param callback
     */
    public void openRepository(String chooseDirectory, IJGScallbackString callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            try {
                checkDirectory(chooseDirectory);
                Git openGit = wrapper.openGit(chooseDirectory);
                model.setGit(openGit);
                logger.getLogger().info("openRepository success");
                callback.onSuccess("openRepository success");
            } catch (IOException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("openRepository" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    /**
     *
     * @param chooseDirectory
     * @param isBare
     * @param callback
     */
    public void initRepository(String chooseDirectory, boolean isBare, IJGScallbackString callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            try {
                checkDirectory(chooseDirectory);
                Git openGit = wrapper.initRepository(chooseDirectory, isBare);
                model.setGit(openGit);
                logger.getLogger().info("initRepository success");
                callback.onSuccess("initRepository success");
            } catch (GitAPIException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("initRepository" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    /**
     *
     * @param chooseDirectory
     * @param parameters
     * @param callback
     */
    public void cloneRepository(String chooseDirectory, Map<String, String> parameters, IJGScallbackString callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            try {
                checkDirectory(chooseDirectory);
                String uriinput = parameters.get("URI");
                String usernameinput = parameters.get("Username");
                String passwordinput = parameters.get("Password");
                Git openGit;
                if (uriinput.startsWith("http")) {
                    openGit = wrapper.cloneRepository(chooseDirectory, uriinput, usernameinput, passwordinput);
                } else {
                    openGit = wrapper.cloneRepository(chooseDirectory, uriinput);
                }
                model.setGit(openGit);
                logger.getLogger().info("cloneRepository success");
                callback.onSuccess("cloneRepository success");

            } catch (GitAPIException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("cloneRepository" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    /**
     *
     * @param dryRun
     * @param usernameInput
     * @param passwordInput
     * @param callback
     */
//    public void fetchRemote(boolean dryRun, String usernameInput, String passwordInput, IJGScallbackFetchResult callback) {
//        Thread thread = new Thread(() -> { // Lambda Expression
//
//            try {
//                checkRepositoryOpen();
//                Git git = model.getGit();
//                Map<String, Map<String, Map<String, String>>> configInfoMap = wrapper.getConfigInfo(model.getGit());
//                Map<String, Map<String, String>> remoteSectionMap = configInfoMap.get(ConfigConstants.CONFIG_REMOTE_SECTION);
//                Set<String> subSections = remoteSectionMap.keySet();
//                String subSection = subSections.iterator().next();
//                Map<String, String> remoteMap = remoteSectionMap.get(subSection);
//                String remoteUrl = remoteMap.get(ConfigConstants.CONFIG_KEY_URL);
//
//                FetchResult result = null;
//                if (remoteUrl.startsWith("http")) {
//                    result = wrapper.fetch(git, usernameInput, passwordInput, dryRun);
//                } else {
//                    result = wrapper.fetch(git, dryRun);
//                }
//                logger.getLogger().info("fetchRemote success");
//                callback.onSuccess(result);
//
//            } catch (GitAPIException | IOException ex) {
//                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
//                callback.onError(ex);
//            } catch (Exception ex) {
//                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
//                callback.onError(ex);
//            }
//            removeRunningThread(Thread.currentThread());
//        });
//        thread.setName("fetchRemote" + thread.getId());
//        thread.start();
//        addRunningThread(thread);
//    }
    /**
     *
     * @param usernameInput
     * @param passwordInput
     * @param callback
     */
    public void pullRemote(String usernameInput, String passwordInput, IJGScallbackPullResult callback) {
        Thread thread = new Thread(() -> { // Lambda Expression

            try {
                checkRepositoryOpen();
                Git git = model.getGit();
                Map<String, Map<String, Map<String, String>>> configInfoMap = wrapper.getConfigInfo(model.getGit());
                Map<String, Map<String, String>> remoteSectionMap = configInfoMap.get(ConfigConstants.CONFIG_REMOTE_SECTION);
                Set<String> subSections = remoteSectionMap.keySet();
                String subSection = subSections.iterator().next();
                Map<String, String> remoteMap = remoteSectionMap.get(subSection);
                String remoteUrl = remoteMap.get(ConfigConstants.CONFIG_KEY_URL);

                PullResult result = null;
                if (remoteUrl.startsWith("http")) {
                    result = wrapper.pull(git, usernameInput, passwordInput);
                } else {
                    result = wrapper.pull(git);
                }
                logger.getLogger().info("pullRemote success");
                callback.onSuccess(result);

            } catch (GitAPIException | IOException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("pullRemote" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    /**
     *
     * @param dryRun
     * @param usernameInput
     * @param passwordInput
     * @param callback
     */
    public void pushRemote(boolean dryRun, String usernameInput, String passwordInput, IJGScallbackPushResult callback) {
        Thread thread = new Thread(() -> { // Lambda Expression

            try {
                checkRepositoryOpen();
                Git git = model.getGit();
                String branch = git.getRepository().getBranch();
                Map<String, Map<String, Map<String, String>>> configInfoMap = wrapper.getConfigInfo(model.getGit());
                Map<String, Map<String, String>> remoteSectionMap = configInfoMap.get(ConfigConstants.CONFIG_REMOTE_SECTION);
                Set<String> subSections = remoteSectionMap.keySet();
                String subSection = subSections.iterator().next();
                Map<String, String> remoteMap = remoteSectionMap.get(subSection);
                String remoteUrl = remoteMap.get(ConfigConstants.CONFIG_KEY_URL);

                Iterable<PushResult> pushResult = null;
                if (remoteUrl.startsWith("http")) {
                    pushResult = wrapper.push(git, branch, remoteUrl, usernameInput, passwordInput, dryRun);
                } else {
                    pushResult = wrapper.push(git, branch, remoteUrl, dryRun);
                }
                callback.onSuccess(pushResult);

            } catch (GitAPIException | IOException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("pushRemote" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    /**
     *
     * @param name
     * @param callback
     */
    public void checkoutLocalBranch(String name, IJGScallbackRef callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            try {
                checkRepositoryOpen();
                Ref checkoutBranch = wrapper.checkoutBranch(model.getGit(), name);
                logger.getLogger().fine("checkoutLocalBranch success");
                callback.onSuccess(checkoutBranch);
            } catch (GitAPIException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("checkoutLocalBranch" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    /**
     *
     * @param name
     * @param callback
     */
    public void checkoutRemoteBranch(String name, IJGScallbackRef callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            try {
                checkRepositoryOpen();
                Ref checkoutBranch = wrapper.createAndCheckoutBranch(model.getGit(), name);
                logger.getLogger().fine("checkoutRemoteBranch success");
                callback.onSuccess(checkoutBranch);
            } catch (GitAPIException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("checkoutRemoteBranch" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    /**
     *
     * @return
     */
    public String getDirectoryFromRepositoryName() {
        try {
            checkRepositoryOpen();
            String _directory = model.getGit().getRepository().getDirectory().toString();
            String directory = _directory.replace(".git", "");
//            JGSappModel.getINSTANCE().setPath(directory);
            return directory;
        } catch (Exception ex) {
            logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
        }
        return null;
    }

    /**
     *
     * @param name
     * @param callback
     */
    public void createBranch(String name, IJGScallbackRef callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            if (name == null || name.isEmpty()) {
                return;
            }
            try {
                checkRepositoryOpen();
                Ref createAndCheckoutBranch = wrapper.createAndCheckoutBranch(model.getGit(), name);
                logger.getLogger().info("createBranch success");
                callback.onSuccess(createAndCheckoutBranch);
            } catch (GitAPIException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("createBranch" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    /**
     *
     * @param name
     * @param callback
     */
    public void mergeIntoCurrentBranch(String name, IJGScallbackMergeResult callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            try {
                checkRepositoryOpen();
                MergeResult mergeIntoCurrentBranch = wrapper.mergeIntoCurrentBranch(model.getGit(), name);
                logger.getLogger().info("mergeIntoCurrentBranch success");
                callback.onSuccess(mergeIntoCurrentBranch);
            } catch (RevisionSyntaxException | IOException | GitAPIException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("mergeIntoCurrentBranch" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    /**
     *
     * @param name
     * @param callback
     */
    public void deleteBranch(String name, IJGScallbackListString callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            try {
                checkRepositoryOpen();
                List<String> deleteBranch = wrapper.deleteBranch(model.getGit(), name, false);
                logger.getLogger().info("deleteBranch success");
                callback.onSuccess(deleteBranch);
            } catch (GitAPIException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("deleteBranch" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    /**
     *
     * @param callback
     */
    public void getLocalBranches(IJGScallbackListRef callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            try {
                checkRepositoryOpen();

                List<Ref> listLocalBranches = wrapper.listLocalBranches(model.getGit());
                callback.onSuccess(listLocalBranches);
            } catch (GitAPIException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("getLocalBranches" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    /**
     *
     * @param callback
     */
    public void getRemoteBranches(IJGScallbackListRef callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            try {
                checkRepositoryOpen();
                List<Ref> listRemoteBranches = wrapper.listRemoteBranches(model.getGit());
                callback.onSuccess(listRemoteBranches);
            } catch (GitAPIException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("getRemoteBranches" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    /**
     *
     * @param callback
     */
    public void getStatus(IJGScallbackStatus callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            try {
                checkRepositoryOpen();
                Status status = wrapper.getStatus(model.getGit());
                callback.onSuccess(status);
            } catch (GitAPIException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("getStatus" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    /**
     *
     * @param callback
     */
    public void stageAll(IJGScallbackDirCache callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            try {
                DirCache addAll = wrapper.addAll(model.getGit());
                logger.getLogger().info("stageAll success");
                callback.onSuccess(addAll);
            } catch (GitAPIException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("stageAll" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    /**
     *
     * @param callback
     */
    public void resetHard(IJGScallbackRef callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            try {
                Ref resetHard = wrapper.resetHard(model.getGit());
                logger.getLogger().info("resetHard success");
                callback.onSuccess(resetHard);
            } catch (GitAPIException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("resetHard" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    public void resetFile(List<String> selectionList, IJGScallbackListRef callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            try {
                List<Ref> resetFiles = new ArrayList<>();
                for (String fileName : selectionList) {
                    Ref resetFile = wrapper.resetFile(model.getGit(), fileName);
                    resetFiles.add(resetFile);
                }
                logger.getLogger().info("resetFile success");
                callback.onSuccess(resetFiles);
            } catch (GitAPIException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("resetFile" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    /**
     *
     * @param selectionList
     * @param callback
     */
    public void stageSelectedTable(List<String> selectionList, IJGScallbackListDirCache callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            try {
                List<DirCache> addFiles = new ArrayList<>();
                for (String fileName : selectionList) {
                    DirCache addFile = wrapper.addFile(model.getGit(), fileName);
                    addFiles.add(addFile);
                    logger.getLogger().info("stageSelectedTable success");
                }
                callback.onSuccess(addFiles);
            } catch (GitAPIException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("stageSelectedTable" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    /**
     *
     * @param selectionList
     * @param callback
     */
    public void removeSelectedTable(List<String> selectionList, IJGScallbackListDirCache callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            try {
                List<DirCache> rmFiles = new ArrayList<>();
                for (String fileName : selectionList) {
                    DirCache rmFile = wrapper.rmFile(model.getGit(), fileName);
                    rmFiles.add(rmFile);
                    logger.getLogger().info("removeSelectedTable success");
                }
                callback.onSuccess(rmFiles);
            } catch (GitAPIException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("removeSelectedTable" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    /**
     *
     * @param selectionList
     * @param callback
     */
    public void unstageSelectedTable(List<String> selectionList, IJGScallbackListRef callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            try {
                List<Ref> resetSofts = new ArrayList<>();
                for (String fileName : selectionList) {
                    Ref resetSoft = wrapper.resetSoft(model.getGit(), fileName);
                    resetSofts.add(resetSoft);
                    logger.getLogger().info("unstageSelectedTable success");
                }
                callback.onSuccess(resetSofts);
            } catch (GitAPIException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("unstageSelectedTable" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    /**
     *
     * @param callback
     */
    public void unstageAll(IJGScallbackRef callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            try {
                Ref resetSoft = wrapper.resetSoft(model.getGit());
                logger.getLogger().info("unstageAll success");
                callback.onSuccess(resetSoft);
            } catch (GitAPIException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("unstageAll" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    /**
     *
     * @param user
     * @param email
     * @param message
     * @param callback
     */
    public void commit(String user, String email, String message, IJGScallbackRefCommit callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            try {
                RevCommit commit = wrapper.commit(model.getGit(), user, email, message);
                logger.getLogger().info("commit success");
                callback.onSuccess(commit);
            } catch (GitAPIException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("commit" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    /**
     *
     * @param callback
     */
    public void getConfigInfo(IJGScallbackDirConfigInfoMap callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            try {
                checkRepositoryOpen();
                Map<String, Map<String, Map<String, String>>> configInfoMap = wrapper.getConfigInfo(model.getGit());
                callback.onSuccess(configInfoMap);
            } catch (IOException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("getConfigInfo" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    public void saveConfigInfo(Map<String, Map<String, Map<String, String>>> configInfoMap, IJGScallbackString callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            try {
                checkRepositoryOpen();
                wrapper.saveConfigInfo(model.getGit(), configInfoMap);
                callback.onSuccess("OK");
            } catch (IOException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("saveConfigInfo" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    /**
     *
     * @param callback
     */
    private void getAllCommits(IJGScallbackListRefCommit callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            try {
                checkRepositoryOpen();
                Iterable<RevCommit> allCommits = wrapper.getAllCommits(model.getGit());
                callback.onSuccess(allCommits);
            } catch (IOException | GitAPIException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("getAllCommits" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    /**
     *
     * @param amount
     * @param callback
     */
    private void getCommits(Integer amount, IJGScallbackListRefCommit callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            try {
                checkRepositoryOpen();
                Iterable<RevCommit> commits = wrapper.getCommits(model.getGit(), amount);
                callback.onSuccess(commits);
            } catch (IOException | GitAPIException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("getCommits" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    /**
     *
     * @param previousCommitId
     * @param commitId
     * @param callback
     */
    private void getDiff(String previousCommitId, String commitId, IJGScallbackListDiffEntry callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            try {
                checkRepositoryOpen();
                List<DiffEntry> diff = wrapper.diff(model.getGit(), previousCommitId, commitId);
                callback.onSuccess(diff);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("getDiff" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    /**
     *
     * @param previousCommitId
     * @param commitId
     * @param path
     * @param callback
     */
    private void getDiffFile(String previousCommitId, String commitId, String path, IJGScallbackString callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            try {
                checkRepositoryOpen();
                String diffFile = wrapper.diffFile(model.getGit(), previousCommitId, commitId, path);
                callback.onSuccess(diffFile);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("getDiffFile" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    /**
     *
     * @param callback
     */
    private void getCurrentDiff(IJGScallbackListDiffEntry callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            try {
                checkRepositoryOpen();
                List<DiffEntry> currentDiff = wrapper.currentDiff(model.getGit());
                callback.onSuccess(currentDiff);
            } catch (GitAPIException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("getCurrentDiff" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    /**
     *
     * @param path
     * @param callback
     */
    private void getCurrentDiffFile(String path, IJGScallbackString callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            try {
                checkRepositoryOpen();
                String currentDiffFile = wrapper.currentDiffFile(model.getGit(), path);
                callback.onSuccess(currentDiffFile);
            } catch (GitAPIException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("getCurrentDiffFile" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    /**
     *
     * @param callback
     */
    private void getBranchName(IJGScallbackString callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            try {
                checkRepositoryOpen();
                String branch = model.getGit().getRepository().getBranch();
                callback.onSuccess(branch);
            } catch (IOException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("getBranchName" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    /**
     *
     * @param callback
     */
    private void getCommitsAhead(IJGScallbackListInteger callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            try {
                checkRepositoryOpen();
                List<Integer> commitsAhead = wrapper.getCommitsAhead(model.getGit());
                callback.onSuccess(commitsAhead);
            } catch (IOException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("getCommitsAhead" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    private void getWorkingTree(IJGScallbackListString callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            try {
                checkRepositoryOpen();
                List<String> paths = wrapper.getWorkingTree(model.getGit());
                callback.onSuccess(paths);
            } catch (IOException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("getWorkingTree" + thread.getId());
        thread.start();
        addRunningThread(thread);

    }

    private void getPlotWalk(String branch, IJGScallbackPlotWalk callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            try {
                PlotWalk plotWalk = wrapper.getPlotWalk(model.getGit(), branch);
                callback.onSuccess(plotWalk);

            } catch (RevisionSyntaxException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (IOException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("getPlotWalk" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    private void getTags(IJGScallbackListRef callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            try {
                List<Ref> tags = wrapper.getTags(model.getGit());
                callback.onSuccess(tags);

            } catch (RevisionSyntaxException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (GitAPIException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("getTags" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    private void getJGStags(int amount, IJGScallbackListJGStags callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            try {
                List<JGStag> jgStags = wrapper.getJGStags(model.getGit(), amount);
                callback.onSuccess(jgStags);
            } catch (GitAPIException | IOException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("getJGStags" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    public JGSgitModel getModel() {
        return model;
    }

    public void tagCommit(String tagName, String tagMessage, String taggerName, String taggerEmail, String commit, IJGScallbackRef callback) {
        Thread thread = new Thread(() -> { // Lambda Expression
            try {
                Ref tagRef = wrapper.tag(model.getGit(), tagName, tagMessage, taggerName, taggerEmail, commit);
                callback.onSuccess(tagRef);
            } catch (RevisionSyntaxException | GitAPIException | IOException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
            removeRunningThread(Thread.currentThread());
        });
        thread.setName("tagCommit" + thread.getId());
        thread.start();
        addRunningThread(thread);
    }

    public void pushTags(IJGScallbackPushResult callback) {
        Thread thread = new Thread(() -> {
            try {
                // Lambda Expression
                Iterable<PushResult> pushTags = wrapper.pushTags(model.getGit());
                callback.onSuccess(pushTags);
            } catch (GitAPIException ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            } catch (Exception ex) {
                logger.getLogger().severe(Arrays.toString(ex.getStackTrace()));
                callback.onError(ex);
            }
        });
        thread.setName("pushTags" + thread.getId());
        thread.start();
        addRunningThread(thread);

    }

    /**
     * throws Exception if directory is invalid
     *
     * @param chooseDirectory
     * @throws Exception
     */
    private void checkDirectory(String chooseDirectory) throws Exception {
        if (chooseDirectory == null) {
            throw new Exception("invalid directory");
        }
    }

    /**
     * throws Exception if git is null
     *
     * @throws Exception
     */
    private void checkRepositoryOpen() throws Exception {
        if (model.getGit() == null) {
            throw new Exception("no repository");
        }
    }

    @Override
    public void onIJGSmodelConfigChanged() {
        logger.getLogger().fine("onIJGSmodelConfigChanged");
        notifyIJGSbcConfigChanged();
    }

    @Override
    public void onIJGSmodelIndexChanged() {
        logger.getLogger().fine("onIJGSmodelIndexChanged");
        notifyIJGSbcIndexChanged();
    }

    @Override
    public void onIJGSmodelRefsChanged() {
        logger.getLogger().fine("onIJGSmodelRefsChanged");
        notifyIJGSbcRefsChanged();
    }

    @Override
    public void onIJGSmodelWorkingTreeModified() {
        logger.getLogger().fine("onIJGSmodelWorkingTreeModified");
        notifyIJGSbcWorkingTreeModified();
    }

    private void notifyIJGSbcConfigChanged() {
        synchronized (receivers) {
            for (IJGSbc receiver : receivers) {
                receiver.onIJGSbcConfigChanged();
            }
        }
    }

    private void notifyIJGSbcIndexChanged() {
        synchronized (receivers) {
            for (IJGSbc receiver : receivers) {
                receiver.onIJGSbcIndexChanged();
            }
        }
    }

    private void notifyIJGSbcRefsChanged() {
        synchronized (receivers) {
            for (IJGSbc receiver : receivers) {
                receiver.onIJGSbcRefsChanged();
            }
        }
    }

    private void notifyIJGSbcWorkingTreeModified() {
        synchronized (receivers) {
            for (IJGSbc receiver : receivers) {
                receiver.onIJGSbcWorkingTreeModified();
            }
        }
    }

    private void notifyIJGSbcThreadsChanged() {
        synchronized (receivers) {
            for (IJGSbc receiver : receivers) {
                receiver.onIJGSbcThreadsChanged();
            }
        }
    }

    private void addRunningThread(Thread thread) {
        synchronized (runningThreads) {
            runningThreads.add(thread);
        }
        notifyIJGSbcThreadsChanged();
    }

    private void removeRunningThread(Thread thread) {
        synchronized (runningThreads) {
            runningThreads.remove(thread);
        }
        notifyIJGSbcThreadsChanged();
    }

    public void deconstruct() {
        String className = this.getClass().getName();
        System.out.println(className + " deconstruct");
        if (runningThreads != null) {
            for (Thread runningThread : runningThreads) {
                runningThread.interrupt();
            }
            runningThreads = null;
        }
        receivers = null;
        if (model != null) {
            model.deconstruct();
            model = null;
        }
        wrapper = null;
        logger = null;

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
