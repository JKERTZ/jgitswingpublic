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

import com.jkertz.jgitswing.model.JGStag;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.RemoteRemoveCommand;
import org.eclipse.jgit.api.RemoteSetUrlCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheIterator;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.BranchTrackingStatus;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.revplot.PlotWalk;
import org.eclipse.jgit.revwalk.DepthWalk.RevWalk;
import org.eclipse.jgit.revwalk.RevBlob;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.FileTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

/**
 *
 * @author jkertz
 */
public class JGScommandWrapper {

    private static JGScommandWrapper INSTANCE = null;

    private JGScommandWrapper() {
    }

    protected static JGScommandWrapper getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new JGScommandWrapper();
        }
        return INSTANCE;
    }

    /**
     * open an existing git repository
     *
     * @param pathString
     * @return
     * @throws IOException
     */
    protected Git openGit(String pathString) throws IOException {
        File repoPathFile = Paths.get(pathString).toFile();
        Git git = Git.open(repoPathFile);
        return git;
    }

    /**
     * init a new local repository
     *
     * @param pathString
     * @param bare
     * @return
     * @throws GitAPIException
     */
    protected Git initRepository(String pathString, boolean bare) throws GitAPIException {
        File repoPathFile = Paths.get(pathString).toFile();
        Git git = Git.init()
                .setDirectory(repoPathFile)
                .setBare(bare)
                .call();
        return git;
    }

    /**
     * clones a local repository
     *
     * @param pathString
     * @param uri
     * @return
     * @throws GitAPIException
     */
    protected Git cloneRepository(String pathString, String uri) throws GitAPIException {
        File repoPathFile = Paths.get(pathString).toFile();
        Git git = Git.cloneRepository()
                .setURI(uri)
                .setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))
                .setDirectory(repoPathFile)
                .call();
        return git;
    }

    /**
     * clones a remote repository
     *
     * @param pathString
     * @param uri
     * @param username
     * @param password
     * @return
     * @throws GitAPIException
     */
    protected Git cloneRepository(String pathString, String uri, String username, String password) throws GitAPIException {
        File repoPathFile = Paths.get(pathString).toFile();
        Git git = Git.cloneRepository()
                .setURI(uri)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
                .setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))
                .setDirectory(repoPathFile)
                .call();
        return git;
    }

    /**
     * gets the curent repository info as a Map
     *
     * @param git
     * @return
     * @throws IOException
     */
    protected Map<String, Map<String, Map<String, String>>> getConfigInfo(Git git) throws IOException {
        //section,subsection,name,value
        Map<String, Map<String, Map<String, String>>> completeInfoMap = new HashMap<>();

        StoredConfig config = git.getRepository().getConfig();
        //user section
        Map<String, String> userMap = new HashMap<>();
        String username = config.getString(ConfigConstants.CONFIG_USER_SECTION,
                null, ConfigConstants.CONFIG_KEY_NAME);
        System.out.println("username: " + username);
        userMap.put(ConfigConstants.CONFIG_KEY_NAME, username);
        String useremail = config.getString(ConfigConstants.CONFIG_USER_SECTION,
                null, ConfigConstants.CONFIG_KEY_EMAIL);
        System.out.println("useremail: " + useremail);
        userMap.put(ConfigConstants.CONFIG_KEY_EMAIL, useremail);

        Map<String, Map<String, String>> subSectionMapUser = new HashMap<>();
        //subsection
        subSectionMapUser.put(null, userMap);
        //section
        completeInfoMap.put(ConfigConstants.CONFIG_USER_SECTION, subSectionMapUser);

        //branch section
        Map<String, String> branchMap = new HashMap<>();
        String branchName = git.getRepository().getBranch();
        String branchRemote = config.getString(ConfigConstants.CONFIG_BRANCH_SECTION,
                branchName, ConfigConstants.CONFIG_KEY_REMOTE);
        System.out.println("branchRemote: " + branchRemote);
        branchMap.put(ConfigConstants.CONFIG_KEY_REMOTE, branchRemote);
        String branchMerge = config.getString(ConfigConstants.CONFIG_BRANCH_SECTION,
                branchName, ConfigConstants.CONFIG_KEY_MERGE);
        System.out.println("branchMerge: " + branchMerge);
        branchMap.put(ConfigConstants.CONFIG_KEY_MERGE, branchMerge);

        Map<String, Map<String, String>> subSectionMapBranch = new HashMap<>();
        //subsection
        subSectionMapBranch.put(branchName, branchMap);
        //section
        completeInfoMap.put(ConfigConstants.CONFIG_BRANCH_SECTION, subSectionMapBranch);

        //remote section
        Map<String, String> remoteMap = new HashMap<>();
        String remoteUrl = config.getString(ConfigConstants.CONFIG_REMOTE_SECTION,
                branchRemote, ConfigConstants.CONFIG_KEY_URL);
        System.out.println("remoteUrl: " + remoteUrl);
        remoteMap.put(ConfigConstants.CONFIG_KEY_URL, remoteUrl);

        Map<String, Map<String, String>> subSectionMapRemote = new HashMap<>();
        //subsection
        subSectionMapRemote.put(branchRemote, remoteMap);
        //section
        completeInfoMap.put(ConfigConstants.CONFIG_REMOTE_SECTION, subSectionMapRemote);

        return completeInfoMap;
    }

    /**
     *
     * @param git
     * @param completeInfoMap
     * @throws IOException
     */
    protected void saveConfigInfo(Git git, Map<String, Map<String, Map<String, String>>> completeInfoMap) throws IOException {
        StoredConfig config = git.getRepository().getConfig();
        Set<String> sections = completeInfoMap.keySet();
        for (String section : sections) {
            System.out.println("Section: " + section);
            Map<String, Map<String, String>> sectionMap = completeInfoMap.get(section);
            Set<String> subSections = sectionMap.keySet();
            for (String subSection : subSections) {
                System.out.println("-SubSection: " + subSection);
                Map<String, String> nameMap = sectionMap.get(subSection);
                Set<String> names = nameMap.keySet();
                for (String name : names) {
                    String value = nameMap.get(name);
                    System.out.println("--Name: " + name + " Value: " + value);
                    config.setString(section, subSection, name, value);
                }
            }
        }
        config.save();
    }

    /**
     * fetch from remote, returns FetchResult
     *
     * @param git
     * @param dryRun
     * @param checkFetchedObjects
     * @param removeDeletedRefs
     * @return
     * @throws GitAPIException
     */
    protected FetchResult fetch(Git git, boolean dryRun, boolean checkFetchedObjects, boolean removeDeletedRefs) throws GitAPIException {
        FetchResult result = git.fetch()
                .setDryRun(dryRun)
                .setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))
                .setCheckFetchedObjects(checkFetchedObjects)
                .setRemoveDeletedRefs(removeDeletedRefs)
                .call();
        return result;
    }

    /**
     * fetch from remote with credentials, returns FetchResult
     *
     * @param git
     * @param username
     * @param password
     * @param dryRun
     * @param checkFetchedObjects
     * @param removeDeletedRefs
     * @return
     * @throws GitAPIException
     */
    protected FetchResult fetch(Git git, String username, String password, boolean dryRun, boolean checkFetchedObjects, boolean removeDeletedRefs) throws GitAPIException {
        FetchResult result = git.fetch()
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
                .setDryRun(dryRun)
                .setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))
                .setCheckFetchedObjects(checkFetchedObjects)
                .setRemoveDeletedRefs(removeDeletedRefs)
                .call();
        return result;
    }

    /**
     * pull from remote, returns PullResults
     *
     * @param git
     * @return
     * @throws GitAPIException
     */
    protected PullResult pull(Git git) throws GitAPIException {
        PullResult result = git.pull()
                .setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))
                .call();
        return result;
    }

    /**
     * pull from remote with credentials, returns PullResults
     *
     * @param git
     * @param username
     * @param password
     * @return
     * @throws GitAPIException
     */
    protected PullResult pull(Git git, String username, String password) throws GitAPIException {
        PullResult result = git.pull()
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
                .setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))
                .call();
        return result;
    }

    /**
     * push to remote, returns pushresuls
     *
     * @param git
     * @param branch
     * @param originName
     * @param dryRun
     * @return
     * @throws GitAPIException
     */
    protected Iterable<PushResult> push(Git git, String branch, String originName, boolean dryRun) throws GitAPIException {
        // git push -u origin master
        System.out.println("push:" + " branch " + branch + " originName " + originName);
        PushCommand pushCommand = git.push();
        pushCommand.add(branch);
        pushCommand.setRemote(originName);
        pushCommand.setDryRun(dryRun);
        pushCommand.setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)));

        Iterable<PushResult> pushResult = pushCommand.call();
        return pushResult;

    }

    /**
     * push to remote with credentials, returns pushresuls
     *
     * @param git
     * @param branch
     * @param originName
     * @param username
     * @param password
     * @param dryRun
     * @return
     * @throws GitAPIException
     */
    protected Iterable<PushResult> push(Git git, String branch, String originName, String username, String password, boolean dryRun) throws GitAPIException {
        // git push -u origin master
        System.out.println("push:" + " branch " + branch + " originName " + originName);
        PushCommand pushCommand = git.push();
        pushCommand.add(branch);
        pushCommand.setRemote(originName);
        pushCommand.setDryRun(dryRun);
        pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password));
        pushCommand.setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)));
        Iterable<PushResult> pushResult = pushCommand.call();
        return pushResult;

    }

    /**
     * checkout a specific branch
     *
     * @param git
     * @param branchName
     * @return
     * @throws GitAPIException
     */
    protected Ref checkoutBranch(Git git, String branchName) throws GitAPIException {
        Ref call = git.checkout()
                .setName(branchName)
                .setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))
                .call();
        return call;
    }

    /**
     * create and checkout a specific branch
     *
     * @param git
     * @param branchName
     * @return
     * @throws GitAPIException
     */
    protected Ref createAndCheckoutBranch(Git git, String branchName) throws GitAPIException {
        Ref call = git.checkout()
                .setName(branchName)
                .setCreateBranch(true)
                .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                .setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))
                .call();
        return call;
    }

    /**
     * create and checkout a specific branch
     *
     * @param git
     * @param branchName
     * @param remoteAndBranchName
     * @return
     * @throws GitAPIException
     */
    protected Ref createAndCheckoutRemoteBranch(Git git, String branchName, String remoteAndBranchName) throws GitAPIException {
        Ref call = git.checkout()
                .setName(branchName)
                .setCreateBranch(true)
                .setStartPoint(remoteAndBranchName)
                .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                .setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))
                .call();
        return call;
    }

    /**
     * trigger a merge into the current branch
     *
     * @param git
     * @param sourceBranch
     * @return
     * @throws IncorrectObjectTypeException
     * @throws RevisionSyntaxException
     * @throws IOException
     * @throws GitAPIException
     */
    protected MergeResult mergeIntoCurrentBranch(Git git, String sourceBranch) throws IncorrectObjectTypeException, RevisionSyntaxException, IOException, GitAPIException {
        ObjectId branchObjectId = git.getRepository().resolve(sourceBranch);
        MergeResult mergeResult = git.merge()
                // .setFastForward(MergeCommand.FastForwardMode.NO_FF)
                // .setCommit(false)
                // .setMessage("merge")
                .include(branchObjectId)
                .setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))
                .call();
        return mergeResult;
    }

    /**
     * delete Branch
     *
     * @param git
     * @param branchName
     * @param force
     * @return
     * @throws GitAPIException
     */
    protected List<String> deleteBranch(Git git, String branchName, boolean force) throws GitAPIException {
        List<String> call = git.branchDelete()
                .setBranchNames(branchName)
                .setForce(force)
                .call();
        return call;
    }

    /**
     * list Local Branches
     *
     * @param git
     * @return
     * @throws GitAPIException
     */
    protected List<Ref> listLocalBranches(Git git) throws GitAPIException {
        List<Ref> branches = git.branchList()
                .call();
        return branches;
    }

    /**
     * list Remote Branches
     *
     * @param git
     * @return
     * @throws GitAPIException
     */
    protected List<Ref> listRemoteBranches(Git git) throws GitAPIException {
        List<Ref> branches = git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE)
                .call();
        return branches;
    }

    /**
     * get the staging status of the repository
     *
     * @param git
     * @return
     * @throws GitAPIException
     */
    protected Status getStatus(Git git) throws GitAPIException {
        Status status = git.status()
                .setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))
                .call();
        return status;
    }

    /**
     * stages all files for commit
     *
     * @param git
     * @return
     * @throws GitAPIException
     */
    protected DirCache addAll(Git git) throws GitAPIException {
        DirCache call = git.add().addFilepattern(".")
                .call();
        return call;
    }

    /**
     * hard reset the current branch
     *
     * @param git
     * @return
     * @throws GitAPIException
     */
    protected Ref resetHard(Git git) throws GitAPIException {
        Ref call = git.reset()
                .setMode(ResetCommand.ResetType.HARD)
                .setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))
                .call();
        return call;
    }

    /**
     * revert single file to HEAD revision
     *
     * @param git
     * @param file
     * @return
     * @throws GitAPIException
     */
    protected Ref resetFile(Git git, String file) throws GitAPIException {
        Ref call = git.checkout()
                .addPath(file)
                .setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))
                .call();
        return call;
    }

    /**
     * stages a specific file for commit
     *
     * @param git
     * @param fileName
     * @return
     * @throws GitAPIException
     */
    protected DirCache addFile(Git git, String fileName) throws GitAPIException {
        DirCache call = git.add()
                .addFilepattern(fileName)
                .call();
        return call;
    }

    /**
     * stages a specific file for remove on commit
     *
     * @param git
     * @param fileName
     * @return
     * @throws GitAPIException
     */
    protected DirCache rmFile(Git git, String fileName) throws GitAPIException {
        DirCache call = git.rm()
                .addFilepattern(fileName)
                .call();
        return call;
    }

    /**
     * soft reset = ustage specific file
     *
     * @param git
     * @param file
     * @return
     * @throws GitAPIException
     */
    protected Ref resetSoft(Git git, String file) throws GitAPIException {
        Ref call = git.reset()
                .addPath(file)
                .setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))
                .call();
        return call;
    }

    /**
     * soft reset = unstage ALL
     *
     * @param git
     * @return
     * @throws GitAPIException
     */
    protected Ref resetSoft(Git git) throws GitAPIException {
        Ref call = git.reset()
                //                .setMode(ResetCommand.ResetType.SOFT)
                .setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))
                .call();
        return call;
    }

    /**
     * commit with specific user,email,message
     *
     * @param git
     * @param user
     * @param email
     * @param message
     * @return
     * @throws GitAPIException
     */
    protected RevCommit commit(Git git, String user, String email, String message) throws GitAPIException {
        RevCommit commit = git.commit()
                .setCommitter(user, email)
                .setMessage(message)
                .call();
        return commit;
    }

    /**
     * get the commit history of the current repository
     *
     * @param git
     * @return
     * @throws IOException
     * @throws GitAPIException
     */
    protected Iterable<RevCommit> getAllCommits(Git git) throws IOException, GitAPIException {
        Iterable<RevCommit> commits = git.log()
                .all()
                .call();
        return commits;
    }

    /**
     * get limited commit history of the current repository
     *
     * @param git
     * @param maxCount
     * @return
     * @throws IOException
     * @throws GitAPIException
     */
    protected Iterable<RevCommit> getCommits(Git git, Integer maxCount) throws IOException, GitAPIException {
        Iterable<RevCommit> commits = git.log()
                .setMaxCount(maxCount)
                .call();
        return commits;
    }

    /**
     * gets the Diff between two objects (usually branches, can also be commits)
     *
     * @param git
     * @param oldBranchName
     * @param newBranchName
     * @return
     * @throws IncorrectObjectTypeException
     * @throws RevisionSyntaxException
     * @throws IOException
     * @throws GitAPIException
     */
    protected List<DiffEntry> diff(Git git, String oldBranchName, String newBranchName) throws IncorrectObjectTypeException, RevisionSyntaxException, IOException, GitAPIException, Exception {
        ObjectReader reader = git.getRepository().newObjectReader();

        ObjectId oldObject = git.getRepository().resolve(oldBranchName);
        ObjectId newObject = git.getRepository().resolve(newBranchName);

        if (oldObject == null) {
            throw new Exception(oldBranchName + " cannot be resolved");
        }
        if (newObject == null) {
            throw new Exception(newBranchName + " cannot be resolved");
        }

        CanonicalTreeParser oldIter = new CanonicalTreeParser();
        oldIter.reset(reader, oldObject);
        CanonicalTreeParser newIter = new CanonicalTreeParser();
        newIter.reset(reader, newObject);

        List<DiffEntry> diffs = git.diff().setNewTree(newIter).setOldTree(oldIter).call();
        return diffs;
    }

    /**
     * gets the Diff between one file of diffenent commits
     *
     * @param git
     * @param oldBranchName
     * @param newBranchName
     * @param path
     * @return
     * @throws IncorrectObjectTypeException
     * @throws RevisionSyntaxException
     * @throws IOException
     * @throws GitAPIException
     * @throws Exception
     */
    protected String diffFile(Git git, String oldBranchName, String newBranchName, String path) throws IncorrectObjectTypeException, RevisionSyntaxException, IOException, GitAPIException, Exception {
        ObjectReader reader = git.getRepository().newObjectReader();

        ObjectId oldObject = git.getRepository().resolve(oldBranchName);
        ObjectId newObject = git.getRepository().resolve(newBranchName);

        if (oldObject == null) {
            throw new Exception(oldBranchName + " cannot be resolved");
        }
        if (newObject == null) {
            throw new Exception(newBranchName + " cannot be resolved");
        }

        CanonicalTreeParser oldIter = new CanonicalTreeParser();
        oldIter.reset(reader, oldObject);
        CanonicalTreeParser newIter = new CanonicalTreeParser();
        newIter.reset(reader, newObject);
        OutputStream out = new ByteArrayOutputStream();
        git.diff()
                .setPathFilter(PathFilter.create(path))
                .setOutputStream(out)
                .setNewTree(newIter)
                .setOldTree(oldIter)
                .setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))
                .call();
        return out.toString();
    }

    /**
     *
     * @param git
     * @return
     * @throws GitAPIException
     */
    protected List<DiffEntry> currentDiff(Git git) throws GitAPIException {
        List<DiffEntry> diffs = git.diff()
                .setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))
                .call();
        return diffs;
    }

    /**
     *
     * @param git
     * @param path
     * @return
     * @throws GitAPIException
     */
    protected String currentDiffFile(Git git, String path) throws GitAPIException {
        OutputStream out = new ByteArrayOutputStream();
        git.diff()
                .setPathFilter(PathFilter.create(path))
                .setOutputStream(out)
                .setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))
                .call();
        return out.toString();
    }

    /**
     * get number of commits ahead/behind as a List, index 0=ahead,1=behind
     *
     * @param git
     * @return
     * @throws IOException
     */
    protected List<Integer> getCommitsAhead(Git git) throws IOException, Exception {
        Repository repository = git.getRepository();
        String branchName = repository.getBranch();
        BranchTrackingStatus trackingStatus = getBranchTrackingStatus(git, branchName);
        List<Integer> counts = new ArrayList<>();
        if (trackingStatus != null) {
            int aheadCount = trackingStatus.getAheadCount();
            counts.add(aheadCount);
            int behindCount = trackingStatus.getBehindCount();
            counts.add(behindCount);
        } else {
            throw new Exception("BranchTrackingStatus not available");
        }
        return counts;
    }

    /**
     *
     * @param git
     * @param branchName
     * @return
     * @throws IOException
     */
    protected BranchTrackingStatus getBranchTrackingStatus(Git git, String branchName) throws IOException {
        Repository repository = git.getRepository();
        BranchTrackingStatus trackingStatus = BranchTrackingStatus.of(repository, branchName);
        return trackingStatus;
    }

    /**
     * get the curent working tree as path strings
     *
     * @param git
     * @return
     * @throws IncorrectObjectTypeException
     * @throws RevisionSyntaxException
     * @throws IOException
     */
    protected List<String> getWorkingTree(Git git) throws IncorrectObjectTypeException, RevisionSyntaxException, IOException {
        List<String> paths = new ArrayList<>();

        Repository repository = git.getRepository();
        ObjectId commit = repository.resolve("HEAD");
        TreeWalk tw = new TreeWalk(repository);
        RevWalk rw = new RevWalk(repository, 100);
        RevCommit commitToCheck = rw.parseCommit(commit);

        tw.addTree(commitToCheck.getTree());
        tw.addTree(new DirCacheIterator(repository.readDirCache()));
        tw.addTree(new FileTreeIterator(repository));
        tw.setRecursive(true);
        while (tw.next()) {
            System.out.printf(
                    "path: %s, Commit(mode/oid): %s/%s, Index(mode/oid): %s/%s, Workingtree(mode/oid): %s/%s\n",
                    tw.getPathString(), tw.getFileMode(0), tw.getObjectId(0), tw.getFileMode(1), tw.getObjectId(1),
                    tw.getFileMode(2), tw.getObjectId(2));
            paths.add(tw.getPathString());
        }
        return paths;
    }

    /**
     * get the plotwalk, usually for drawing a branch graph
     *
     * @param git
     * @param branch
     * @return
     * @throws IncorrectObjectTypeException
     * @throws RevisionSyntaxException
     * @throws IOException
     */
    protected PlotWalk getPlotWalk(Git git, String branch) throws IncorrectObjectTypeException, RevisionSyntaxException, IOException {
        Repository repository = git.getRepository();
        PlotWalk plotWalk = new PlotWalk(repository);
        ObjectId rootId = repository.resolve(branch);
        RevCommit root = plotWalk.parseCommit(rootId);
        plotWalk.markStart(root);
        return plotWalk;
    }

    /**
     * get the list of tags (not the contents)
     *
     * @param git
     * @return
     * @throws GitAPIException
     */
    protected List<Ref> getTags(Git git) throws GitAPIException {
        List<Ref> tags = git.tagList()
                .call();
        return tags;
    }

    protected List<JGStag> getJGStags(Git git, int amount) throws GitAPIException, IncorrectObjectTypeException, IOException {
        Repository repository = git.getRepository();
        List<JGStag> jgsTags = new ArrayList<>();
        RevWalk walk = new RevWalk(repository, amount);
        List<Ref> tags = git.tagList()
                .call();
        for (Ref tagRef : tags) {
            JGStag jGStag = new JGStag();
//                 System.out.println("---------------------------------------------");
//                System.out.println("tagRef.getName: " + tagRef.getName());
            jGStag.setTagRef(tagRef.getName());

            Ref peeledRef = repository.peel(tagRef);
            ObjectId peeledObjectId = peeledRef.getPeeledObjectId();
            RevObject parseAny = null;
            if (peeledObjectId == null) {
//                System.out.println("---unannotated tag---");
                parseAny = walk.parseAny(peeledRef.getObjectId());
                jGStag.setAnnotated(false);
            } else {
//                System.out.println("---annotated tag---");
                parseAny = walk.parseAny(peeledObjectId);
                RevTag tag = walk.parseTag(peeledRef.getObjectId());
//                System.out.println("annotated tag: " + tag.getTagName() + " : " + tag.getFullMessage());
                jGStag.setAnnotated(true);
                jGStag.setTagName(tag.getTagName());
                jGStag.setTagMessage(tag.getFullMessage());
            }

            if (parseAny instanceof RevCommit) {
                RevCommit castRevCommit = (RevCommit) parseAny;
//            System.out.println("RevCommit: " + castRevCommit.getName());
//            System.out.println("RevCommit Message: " + castRevCommit.getFullMessage());
//            System.out.println("---------------------------------------------");
                jGStag.setRevCommitId(castRevCommit.getName());
                jGStag.setRevCommitMessage(castRevCommit.getFullMessage());
            }
            if (parseAny instanceof RevBlob) {
                System.out.println("RevBlob!" + tagRef.getName());
            }

            jgsTags.add(jGStag);
        }

        return jgsTags;
    }

    /**
     * tag a RevObject,
     *
     * @param git
     * @param tagName
     * @param tagMessage
     * @param taggerName
     * @param taggerEmail
     * @param commit
     * @return
     * @throws GitAPIException
     */
    protected Ref tag(Git git, String tagName, String tagMessage, String taggerName, String taggerEmail, String commit) throws GitAPIException, IncorrectObjectTypeException, RevisionSyntaxException, IOException {
        PersonIdent tagger = new PersonIdent(taggerName, taggerEmail);
        Repository repository = git.getRepository();
        ObjectId commitObject = repository.resolve(commit);
        RevWalk rw = new RevWalk(repository, Integer.MAX_VALUE);
        RevCommit revCommit = rw.parseCommit(commitObject);

        Ref tagRef = git.tag()
                .setName(tagName)
                .setMessage(tagMessage)
                .setTagger(tagger)
                .setObjectId(revCommit)
                .call();
        return tagRef;
    }

    /**
     * push tags to remote
     *
     * @param git
     * @return
     * @throws GitAPIException
     */
    protected Iterable<PushResult> pushTags(Git git) throws GitAPIException {
        Iterable<PushResult> pushResult = git.push()
                .setPushTags()
                .setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))
                .call();
        return pushResult;
    }

    /**
     *
     * @param git
     * @param name
     * @param uri
     * @return
     * @throws GitAPIException
     */
    protected RemoteConfig remoteAdd(Git git, String name, URIish uri) throws GitAPIException {
        RemoteConfig call = git.remoteAdd().setName(name).setUri(uri).call();
        return call;
    }

    /**
     *
     * @param git
     * @return
     * @throws GitAPIException
     */
    protected List<RemoteConfig> remoteList(Git git) throws GitAPIException {
        List<RemoteConfig> call = git.remoteList().call();
        return call;
    }

    /**
     *
     * @param git
     * @param remoteName
     * @return
     * @throws GitAPIException
     */
    protected RemoteConfig remoteRemove(Git git, String remoteName) throws GitAPIException {
        RemoteRemoveCommand remoteRemove = git.remoteRemove();
        remoteRemove.setRemoteName(remoteName);
        RemoteConfig call = remoteRemove.call();
        return call;
    }

    /**
     *
     * @param git
     * @param remoteName
     * @param remoteUri
     * @return
     * @throws GitAPIException
     */
    protected RemoteConfig remoteSetUrl(Git git, String remoteName, URIish remoteUri) throws GitAPIException {
        RemoteSetUrlCommand remoteSetUrl = git.remoteSetUrl();
        remoteSetUrl.setRemoteUri(remoteUri);
        remoteSetUrl.setRemoteName(remoteName);
        RemoteConfig call = remoteSetUrl.call();
        return call;
    }

}
