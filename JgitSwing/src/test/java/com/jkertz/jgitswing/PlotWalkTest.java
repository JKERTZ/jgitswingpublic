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
package com.jkertz.jgitswing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revplot.PlotCommit;
import org.eclipse.jgit.revplot.PlotCommitList;
import org.eclipse.jgit.revplot.PlotLane;
import org.eclipse.jgit.revplot.PlotWalk;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

/**
 *
 * @author jkertz
 */
public class PlotWalkTest {

    public PlotWalkTest() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    private void hello() {
        System.out.println("hello");
    }

    private void testPlotWalk() {
        String pathString = "/home/jkertz/Develop/jgitswingexperimental/";
        System.out.println("testPlotWalk: " + pathString);
        try {
            File repoPathFile = Paths.get(pathString).toFile();
            Git git = Git.open(repoPathFile);
            Repository repository = git.getRepository();
            System.out.println("Branch: " + repository.getBranch());

            Map<AnyObjectId, Set<Ref>> allRefsByPeeledObjectId = repository.getAllRefsByPeeledObjectId();
            Set<AnyObjectId> keySet = allRefsByPeeledObjectId.keySet();
            for (AnyObjectId key : keySet) {
                System.out.println("allRefsByPeeledObjectId: " + key.toString());
                Set<Ref> refs = allRefsByPeeledObjectId.get(key);
                for (Ref ref : refs) {
                    System.out.println("ref: " + ref.getName());
                }
            }

            PlotWalk pw = new PlotWalk(repository);

            Iterator<RevCommit> iterator = git.log().call().iterator();
            pw.parseCommit(iterator.next());

//            RevCommit next = pw.next();
//            String shortMessage = next.getShortMessage();
//            System.out.println("shortMessage: " + shortMessage);
            PlotCommitList<PlotLane> commits = new PlotCommitList<>();

            commits.source(pw);
            int size1 = commits.size();
            System.out.println("size before fillto: " + size1);
            commits.fillTo(25);
            int size2 = commits.size();
            System.out.println("size after fillto: " + size2);

            for (PlotCommit<PlotLane> commit : commits) {
                System.out.println(commit);
            }
            repository.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(PlotWalkTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GitAPIException ex) {
            ex.printStackTrace();
            Logger.getLogger(PlotWalkTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void testPlotWalk2() {
        try {
            String pathString = "/home/jkertz/temp/cloneinitrep1/";
            System.out.println("testPlotWalk2: " + pathString);
            File repoPathFile = Paths.get(pathString).toFile();
            Git git = Git.open(repoPathFile);
            Repository repository = git.getRepository();
            PlotWalk revWalk = new PlotWalk(repository);
            ObjectId rootId = repository.resolve("refs/heads/master");
            RevCommit root = revWalk.parseCommit(rootId);
            revWalk.markStart(root);
            PlotCommitList<PlotLane> plotCommitList = new PlotCommitList<>();
            plotCommitList.source(revWalk);
            plotCommitList.fillTo(Integer.MAX_VALUE);

            System.out.println("Printing children of commit " + root);
            for (RevCommit com : revWalk) {
                System.out.println("Child: " + com);
            }

            System.out.println("Printing with next()");
            System.out.println("next: " + revWalk.next());
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(PlotWalkTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void testPlotWalk3() {
        try {
            String pathString = "/home/jkertz/temp/cloneinitrep1/";
            System.out.println("testPlotWalk3: " + pathString);
            File repoPathFile = Paths.get(pathString).toFile();
            Git git = Git.open(repoPathFile);
            Repository repository = git.getRepository();

            RevWalk walk = new RevWalk(repository);

            Iterator<RevCommit> iterator = git.log().call().iterator();
            walk.parseCommit(iterator.next());

            Date date = null;
            while (iterator.hasNext()) {
                RevCommit commit = iterator.next();
                System.out.println("commit: " + commit.getId().getName());
//                if (commit.getId().getName().equals(revision)) {
//                    date = new Date(Long.valueOf(commit.getCommitTime()) * 1000);
//                }
            }

            repository.close();
            git.close();

        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(PlotWalkTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GitAPIException ex) {
            Logger.getLogger(PlotWalkTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void revPlotTest1() {
        String pathString = "C:\\Develop\\Github\\jgitswingexperimental";
        System.out.println("revPlotTest1 " + pathString);
        File repoPathFile = Paths.get(pathString).toFile();
        try {
            Git git = Git.open(repoPathFile);
            Repository repository = git.getRepository();
            String branch = repository.getBranch();
            System.out.println("revPlotTest1 branch: " + branch);

            PlotWalk plotWalk = new PlotWalk(repository);
            ObjectId rootId = repository.resolve(branch);
//            ObjectId rootId = repository.resolve("refs/heads/master");
            RevCommit root = plotWalk.parseCommit(rootId);
            plotWalk.markStart(root);
            PlotCommitList<PlotLane> plotCommitList = new PlotCommitList<>();
            plotCommitList.source(plotWalk);
            plotCommitList.fillTo(Integer.MAX_VALUE);
            int plotCommitListSize = plotCommitList.size();
            System.out.println("plotCommitList size: " + plotCommitListSize);

            Iterator<PlotCommit<PlotLane>> commitListIterator = plotCommitList.iterator();
            while (commitListIterator.hasNext()) {
                System.out.println("revPlotTest1 commit: " + commitListIterator.next().getFullMessage());
//               commitIDList.add(commitListIterator.next().getName());
            }

//            RevWalk revWalkRepo = new RevWalk(repository, 10);
//            RevCommitList revCommitList = new RevCommitList<>();
//            revCommitList.source(revWalkRepo);
//            revCommitList.fillTo(Integer.MAX_VALUE);
//            int revCommitListSize = revCommitList.size();
//            System.out.println("revCommitList size: " + revCommitListSize);
//            System.out.println("Printing children of commit " + root);
            for (RevCommit com : plotWalk) {
                System.out.println("Child: " + com);
            }
            System.out.println("Printing with next()");
            System.out.println("next: " + plotWalk.next());

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
