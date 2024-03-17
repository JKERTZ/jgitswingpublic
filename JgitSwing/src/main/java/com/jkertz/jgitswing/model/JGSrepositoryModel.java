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
package com.jkertz.jgitswing.model;

import com.jkertz.jgitswing.businesslogic.JGSBranchTrackingStatus;
import com.jkertz.jgitswing.businesslogic.JGSCommitsAhead;
import com.jkertz.jgitswing.businesslogic.JGSConfigInfo;
import com.jkertz.jgitswing.businesslogic.JGSCurrentDiff;
import com.jkertz.jgitswing.businesslogic.JGSCurrentDiffFile;
import com.jkertz.jgitswing.businesslogic.JGSDiff;
import com.jkertz.jgitswing.businesslogic.JGSDiffFile;
import com.jkertz.jgitswing.businesslogic.JGSLocalBranches;
import com.jkertz.jgitswing.businesslogic.JGSPlotWalk;
import com.jkertz.jgitswing.businesslogic.JGSRemoteBranches;
import com.jkertz.jgitswing.businesslogic.JGSRemoteConfig;
import com.jkertz.jgitswing.businesslogic.JGSallCommits;
import com.jkertz.jgitswing.businesslogic.JGScommits;
import com.jkertz.jgitswing.businesslogic.JGSstatus;
import com.jkertz.jgitswing.businesslogic.JGStags;
import com.jkertz.jgitswing.logger.JGSlogger;
import com.jkertz.jgitswing.tabs.common.JGSuiUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.events.ConfigChangedEvent;
import org.eclipse.jgit.events.ConfigChangedListener;
import org.eclipse.jgit.events.IndexChangedEvent;
import org.eclipse.jgit.events.IndexChangedListener;
import org.eclipse.jgit.events.RefsChangedEvent;
import org.eclipse.jgit.events.RefsChangedListener;
import org.eclipse.jgit.events.RepositoryListener;
import org.eclipse.jgit.events.WorkingTreeModifiedEvent;
import org.eclipse.jgit.events.WorkingTreeModifiedListener;
import org.eclipse.jgit.lib.BranchTrackingStatus;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revplot.PlotWalk;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

/**
 * This unifies JGSbc and JGSgitModel
 *
 * all methods are blocking
 *
 * long running results are cached
 *
 * @author jkertz
 */
public class JGSrepositoryModel implements ConfigChangedListener, IndexChangedListener, RefsChangedListener, RepositoryListener, WorkingTreeModifiedListener {

    private Set<IJGSrepositoryModel> receivers;

    private final Git git;
    private final JGSlogger logger;
    private final JGSallCommits jGSallCommits;
    private final JGSCommitsAhead jGSCommitsAhead;
    private final JGScommits jGScommits;
    private final JGSConfigInfo jGSConfigInfo;
    private final JGSCurrentDiff jGSCurrentDiff;
    private final JGSCurrentDiffFile jGSCurrentDiffFile;
    private final JGSDiff jGSDiff;
    private final JGSDiffFile jGSDiffFile;
    private final JGStags jGStags;
    private final JGSLocalBranches jGSLocalBranches;
    private final JGSPlotWalk jGSPlotWalk;
    private final JGSRemoteBranches jGSRemoteBranches;
    private final JGSstatus jGSstatus;
    private final JGSBranchTrackingStatus jGSBranchTrackingStatus;
    private final JGSRemoteConfig jGSRemoteConfig;

    public JGSrepositoryModel(Git git) {
        this.logger = JGSlogger.getINSTANCE();
        this.git = git;
        this.receivers = new HashSet<>();

        this.jGSallCommits = new JGSallCommits(git);
        this.jGSCommitsAhead = new JGSCommitsAhead(git);
        this.jGScommits = new JGScommits(git);
        this.jGSConfigInfo = new JGSConfigInfo(git);
        this.jGSCurrentDiff = new JGSCurrentDiff(git);
        this.jGSCurrentDiffFile = new JGSCurrentDiffFile(git);
        this.jGSDiff = new JGSDiff(git);
        this.jGSDiffFile = new JGSDiffFile(git);
        this.jGStags = new JGStags(git);
        this.jGSLocalBranches = new JGSLocalBranches(git);
        this.jGSPlotWalk = new JGSPlotWalk(git);
        this.jGSRemoteBranches = new JGSRemoteBranches(git);
        this.jGSstatus = new JGSstatus(git);
        this.jGSBranchTrackingStatus = new JGSBranchTrackingStatus(git);
        this.jGSRemoteConfig = new JGSRemoteConfig(git);

        git.getRepository().getListenerList().addConfigChangedListener(this);
        git.getRepository().getListenerList().addIndexChangedListener(this);
        git.getRepository().getListenerList().addRefsChangedListener(this);
        git.getRepository().getListenerList().addWorkingTreeModifiedListener(this);
    }

    public void addReceiver(IJGSrepositoryModel receiver) {
        receivers.add(receiver);
    }

    public void removeReceiver(IJGSrepositoryModel receiver) {
        receivers.remove(receiver);
    }

    @Override
    public void onConfigChanged(ConfigChangedEvent cce) {
        System.out.println("JGSrepositoryModel onConfigChanged");
        notifyGitConfigChanged();
    }

    @Override
    public void onIndexChanged(IndexChangedEvent ice) {
        boolean internal = ice.isInternal();
        logger.getLogger().info("internal: " + internal);

        System.out.println("JGSrepositoryModel onIndexChanged");
        //caused by staging
        jGSstatus.invalidate();
        notifyGitIndexChanged();
    }

    @Override
    public void onRefsChanged(RefsChangedEvent rce) {
        System.out.println("JGSrepositoryModel onRefsChanged");
        //caused by create commit
        //caused by branch checkout
        //caused by pull
        notifyGitRefChanged();
    }

    @Override
    public void onWorkingTreeModified(WorkingTreeModifiedEvent wtme) {
        Collection<String> deleted = wtme.getDeleted();
        Collection<String> modified = wtme.getModified();
        logger.getLogger().info("deleted: " + deleted);
        logger.getLogger().info("modified: " + modified);

        System.out.println("JGSrepositoryModel onWorkingTreeModified");
        notifyGitWorkingTreeModified();
    }

    public Git getGit() {
        return git;
    }

    public synchronized Iterable<RevCommit> getAllCommits() throws Exception {
        Iterable<RevCommit> allCommits = jGSallCommits.getAllCommits();
        return allCommits;
    }

    public synchronized String getBranchName() throws Exception {
        String branch = git.getRepository().getBranch();
        return branch;
    }

    public synchronized List<Integer> getCommitsAhead() throws Exception {
        List<Integer> commitsAhead = jGSCommitsAhead.getCommitsAhead();
        return commitsAhead;
    }

    public synchronized Iterable<RevCommit> getCommits(Integer amount) throws Exception {
        Iterable<RevCommit> commits = jGScommits.getCommits(amount);
        return commits;
    }

    public Iterable<RevCommit> getCommits(Integer amount, String _text) throws Exception {
        String toLowerCase = _text.toLowerCase();
        Iterable<RevCommit> allCommits = jGScommits.getCommits(amount);
        List<RevCommit> filteredCommitsList = new ArrayList<>();
        for (RevCommit commit : allCommits) {
            String _fullMessage = commit.getFullMessage();
            String fullMessage = _fullMessage.toLowerCase();
            if (fullMessage.contains(toLowerCase)) {
                filteredCommitsList.add(commit);
                continue;
            }

            String _commitId = commit.getId().getName();
            String commitId = _commitId + "^{tree}";
            String previousCommitId = _commitId + "~1^{tree}";
            try {
                List<DiffEntry> diff = getDiff(previousCommitId, commitId);
                for (DiffEntry entry : diff) {
                    String path = entry.getNewPath();
                    if (path.equals("/dev/null")) {
                        path = entry.getOldPath();
                    }

                    String _fileName = JGSuiUtils.getINSTANCE().extractFilenameFromPath(path);
                    String fileName = _fileName.toLowerCase();
                    if (fileName.contains(toLowerCase)) {
                        filteredCommitsList.add(commit);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Iterator<RevCommit> filteredCommits = filteredCommitsList.iterator();
        return getIterableFromIterator(filteredCommits);
    }

    private <T> Iterable<T> getIterableFromIterator(Iterator<T> iterator) {
        return () -> iterator;
    }

    public synchronized Map<String, Map<String, Map<String, String>>> getConfigInfo() throws Exception {
        Map<String, Map<String, Map<String, String>>> configInfo = jGSConfigInfo.getConfigInfo();
        return configInfo;
    }

    public synchronized List<DiffEntry> getCurrentDiff() throws Exception {
        List<DiffEntry> currentDiff = jGSCurrentDiff.getCurrentDiff();
        return currentDiff;
    }

    public synchronized String getCurrentDiffFile(String path) throws Exception {
        String currentDiffFile = jGSCurrentDiffFile.getCurrentDiffFile(path);
        return currentDiffFile;
    }

    public synchronized List<DiffEntry> getDiff(String previousCommitId, String commitId) throws Exception {
        List<DiffEntry> diff = jGSDiff.getDiff(previousCommitId, commitId);
        return diff;
    }

    public synchronized String getDiffFile(String previousCommitId, String commitId, String path) throws Exception {
        String diffFile = jGSDiffFile.getDiffFile(previousCommitId, commitId, path);
        return diffFile;
    }

    public synchronized String getDirectoryFromRepositoryName() {
        String _directory = git.getRepository().getDirectory().toString();
        String directory = _directory.replace(".git", "");
        return directory;
    }

    public synchronized List<JGStag> getJGStags(int amount) throws Exception {
        List<JGStag> jgStags = jGStags.getJGStags(amount);
        return jgStags;
    }

    public synchronized List<Ref> getLocalBranches() throws Exception {
        List<Ref> localBranches = jGSLocalBranches.getLocalBranches();
        return localBranches;
    }

    public synchronized PlotWalk getPlotWalk(String branch) throws Exception {
        PlotWalk plotWalk = jGSPlotWalk.getPlotWalk(branch);
        return plotWalk;
    }

    public synchronized List<Ref> getRemoteBranches() throws Exception {
        List<Ref> remoteBranches = jGSRemoteBranches.getRemoteBranches();
        return remoteBranches;
    }

    public synchronized Status getStatus() throws Exception {
        Status status = jGSstatus.getStatus();
        return status;
    }

    public synchronized List<JGStag> getTags(int amount) throws Exception {
        List<JGStag> jgStags = jGStags.getJGStags(amount);
        return jgStags;
    }

    public synchronized void saveConfigInfo(Map<String, Map<String, Map<String, String>>> configInfoMap) throws Exception {
        jGSConfigInfo.setConfigInfo(configInfoMap);
    }

    public synchronized Ref tagCommit(String tagName, String tagMessage, String taggerName, String taggerEmail, String commit) throws Exception {
        Ref tagCommit = jGStags.tagCommit(tagName, tagMessage, taggerName, taggerEmail, commit);
        return tagCommit;
    }

    public synchronized BranchTrackingStatus getBranchTrackingStatus(String branchName) throws Exception {
        BranchTrackingStatus branchTrackingStatus = jGSBranchTrackingStatus.getBranchTrackingStatus(branchName);
        return branchTrackingStatus;
    }

    public synchronized List<RemoteConfig> getRemoteList() throws Exception {
        List<RemoteConfig> remoteList = jGSRemoteConfig.getRemoteList();
        return remoteList;
    }

    public synchronized void setRemote(String remoteName, URIish remoteUri) throws Exception {
        jGSRemoteConfig.setRemote(remoteName, remoteUri);
    }

    public synchronized void addRemote(String remoteName, URIish remoteUri) throws Exception {
        jGSRemoteConfig.addRemote(remoteName, remoteUri);
    }

    private void notifyGitConfigChanged() {
        for (IJGSrepositoryModel receiver : receivers) {
            receiver.onGitConfigChanged();
        }
    }

    private void notifyGitIndexChanged() {
        for (IJGSrepositoryModel receiver : receivers) {
            receiver.onGitIndexChanged();
        }
    }

    private void notifyGitRefChanged() {
        for (IJGSrepositoryModel receiver : receivers) {
            receiver.onGitRefChanged();
        }
    }

    private void notifyGitWorkingTreeModified() {
        for (IJGSrepositoryModel receiver : receivers) {
            receiver.onGitWorkingTreeModified();
        }
    }

}
