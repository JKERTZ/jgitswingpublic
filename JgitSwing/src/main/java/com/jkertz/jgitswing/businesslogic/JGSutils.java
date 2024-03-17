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
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.PushResult;

/**
 *
 * @author jkertz
 */
public class JGSutils {

    private static JGSutils INSTANCE = null;
    private final JGScommandWrapper wrapper;

    private JGSutils() {
        wrapper = JGScommandWrapper.getINSTANCE();
    }

    public static JGSutils getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new JGSutils();
        }
        return INSTANCE;
    }

    public Git openRepository(String chooseDirectory) throws Exception {
        Git openGit = wrapper.openGit(chooseDirectory);
        return openGit;
    }

    public Git cloneRepository(String chooseDirectory, Map<String, String> parameters) throws Exception {
        String uriinput = parameters.get("URI");
        String usernameinput = parameters.get("Username");
        String passwordinput = parameters.get("Password");
        Git openGit;
        if (uriinput.startsWith("http")) {
            openGit = wrapper.cloneRepository(chooseDirectory, uriinput, usernameinput, passwordinput);
        } else {
            openGit = wrapper.cloneRepository(chooseDirectory, uriinput);
        }
        return openGit;
    }

    public Git initRepository(String chooseDirectory, boolean isBare) throws Exception {
        Git openGit = wrapper.initRepository(chooseDirectory, isBare);
        return openGit;
    }

    public FetchResult fetchRemote(Git git, boolean dryRun, boolean checkFetchedObjects, boolean removeDeletedRefs, String usernameInput, String passwordInput) throws Exception {
        String remoteUrl = getRemoteUrlFromConfig(git);

        FetchResult result = null;
        if (remoteUrl.startsWith("http")) {
            result = wrapper.fetch(git, usernameInput, passwordInput, dryRun, checkFetchedObjects, removeDeletedRefs);
        } else {
            result = wrapper.fetch(git, dryRun, checkFetchedObjects, removeDeletedRefs);
        }
        return result;

    }

    public PullResult pullRemote(Git git, String usernameInput, String passwordInput) throws Exception {
        String remoteUrl = getRemoteUrlFromConfig(git);

        PullResult result = null;
        if (remoteUrl.startsWith("http")) {
            result = wrapper.pull(git, usernameInput, passwordInput);
        } else {
            result = wrapper.pull(git);
        }
        return result;
    }

    public Iterable<PushResult> pushRemote(Git git, boolean dryRun, String usernameInput, String passwordInput) throws Exception {
        String branch = git.getRepository().getBranch();
        String remoteUrl = getRemoteUrlFromConfig(git);

        Iterable<PushResult> pushResult = null;
        if (remoteUrl.startsWith("http")) {
            pushResult = wrapper.push(git, branch, remoteUrl, usernameInput, passwordInput, dryRun);
        } else {
            pushResult = wrapper.push(git, branch, remoteUrl, dryRun);
        }
        return pushResult;

    }

    public Ref createBranch(Git git, String name) throws Exception {
        if (name == null || name.isEmpty()) {
            return null;
        }
        Ref createAndCheckoutBranch = wrapper.createAndCheckoutBranch(git, name);
        return createAndCheckoutBranch;
    }

    public Ref checkoutLocalBranch(Git git, String name) throws Exception {
        Ref checkoutBranch = wrapper.checkoutBranch(git, name);
        return checkoutBranch;
    }

    public Ref checkoutRemoteBranch(Git git, String name, String remoteAndBranchName) throws Exception {
        Ref checkoutBranch = wrapper.createAndCheckoutRemoteBranch(git, name, remoteAndBranchName);
        return checkoutBranch;
    }

    public MergeResult mergeIntoCurrentBranch(Git git, String name) throws Exception {
        MergeResult mergeIntoCurrentBranch = wrapper.mergeIntoCurrentBranch(git, name);
        return mergeIntoCurrentBranch;
    }

    public List<String> deleteBranch(Git git, String name) throws Exception {
        List<String> deleteBranch = wrapper.deleteBranch(git, name, false);
        return deleteBranch;
    }

    public List<DirCache> stageSelectedTable(Git git, List<String> selectionList) throws Exception {
        List<DirCache> addFiles = new ArrayList<>();
        for (String fileName : selectionList) {
            DirCache addFile = wrapper.addFile(git, fileName);
            addFiles.add(addFile);
        }
        return addFiles;
    }

    public List<DirCache> removeSelectedTable(Git git, List<String> selectionList) throws Exception {
        List<DirCache> rmFiles = new ArrayList<>();
        for (String fileName : selectionList) {
            DirCache rmFile = wrapper.rmFile(git, fileName);
            rmFiles.add(rmFile);
        }
        return rmFiles;
    }

    public DirCache stageAll(Git git) throws Exception {
        DirCache addAll = wrapper.addAll(git);
        return addAll;
    }

    public Ref resetHard(Git git) throws Exception {
        Ref resetHard = wrapper.resetHard(git);
        return resetHard;
    }

    public List<Ref> unstageSelectedTable(Git git, List<String> selectionList) throws Exception {
        List<Ref> resetSofts = new ArrayList<>();
        for (String fileName : selectionList) {
            Ref resetSoft = wrapper.resetSoft(git, fileName);
            resetSofts.add(resetSoft);
        }
        return resetSofts;
    }

    public List<Ref> resetFile(Git git, List<String> selectionList) throws Exception {
        List<Ref> resetFiles = new ArrayList<>();
        for (String fileName : selectionList) {
            Ref resetFile = wrapper.resetFile(git, fileName);
            resetFiles.add(resetFile);
        }
        return resetFiles;
    }

    public Ref unstageAll(Git git) throws Exception {
        Ref resetSoft = wrapper.resetSoft(git);
        return resetSoft;
    }

    public RevCommit commit(Git git, String user, String email, String message) throws Exception {
        RevCommit commit = wrapper.commit(git, user, email, message);
        return commit;
    }

    public String getRemoteUrlFromConfig(Git git) throws IOException {
        Map<String, Map<String, Map<String, String>>> configInfoMap = wrapper.getConfigInfo(git);
        Map<String, Map<String, String>> remoteSectionMap = configInfoMap.get(ConfigConstants.CONFIG_REMOTE_SECTION);
        Set<String> subSections = remoteSectionMap.keySet();
        String subSection = subSections.iterator().next();
        Map<String, String> remoteMap = remoteSectionMap.get(subSection);
        String remoteUrl = remoteMap.get(ConfigConstants.CONFIG_KEY_URL);
        return remoteUrl;
    }

}
