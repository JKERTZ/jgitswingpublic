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
package com.jkertz.jgitswing;

import com.jkertz.jgitswing.model.JGStag;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.DepthWalk;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;

/**
 *
 * @author jkertz
 */
public class TagTest {

    public void hello() {
        System.out.println("hello");
    }

    public void testTagRead1() {
        String pathString = "/home/jkertz/Develop/eclipse-git-tagpilot_20221220/eclipse-git-tagpilot/tagpilot42/";
        System.out.println("testTagRead: " + pathString);
        try {
            File repoPathFile = Paths.get(pathString).toFile();
            Git git = Git.open(repoPathFile);
            Repository repository = git.getRepository();
            System.out.println("Branch: " + repository.getBranch());

            List<JGStag> jgsTags = new ArrayList<>();
            DepthWalk.RevWalk walk = new DepthWalk.RevWalk(repository, 100);
            List<Ref> tags = git.tagList()
                    .call();
            for (Ref tagRef : tags) {
                if (tagRef instanceof RevCommit) {
                    RevCommit castRevCommit = (RevCommit) tagRef;
                    System.out.println(tagRef.getName() + " is RevCommit: " + castRevCommit.getFullMessage());
                }
                if (tagRef instanceof RevTag) {
                    RevTag castRevTag = (RevTag) tagRef;
                    System.out.println(tagRef.getName() + " is RevTag: " + castRevTag.getFullMessage());
                }
                System.out.println("tagRef.getName: " + tagRef.getName());
                Ref peeledRef = repository.peel(tagRef);
                ObjectId peeledObjectId = peeledRef.getPeeledObjectId();
                if (peeledObjectId == null) {
                    System.out.println("---unannotated tag---");
                    RevObject parseAny = walk.parseAny(peeledRef.getObjectId());
                    if (parseAny == null) {
                        System.out.println("parseAny == null");
                    } else if (parseAny instanceof RevCommit) {
                        RevCommit castRevCommit = (RevCommit) parseAny;
                        System.out.println(parseAny.getName() + " is RevCommit: " + castRevCommit.getFullMessage());
                    } else if (parseAny instanceof RevTag) {
                        RevTag castRevTag = (RevTag) parseAny;
                        System.out.println(parseAny.getName() + " is RevTag: " + castRevTag.getFullMessage());
                    }
                } else {
                    System.out.println("---annotated tag---");
                    RevObject parseAny = walk.parseAny(peeledObjectId);
                    if (parseAny == null) {
                        System.out.println("parseAny == null");
                    } else if (parseAny instanceof RevCommit) {
                        RevCommit castRevCommit = (RevCommit) parseAny;
                        System.out.println(parseAny.getName() + " is RevCommit: " + castRevCommit.getFullMessage());
                    } else if (parseAny instanceof RevTag) {
                        RevTag castRevTag = (RevTag) parseAny;
                        System.out.println(parseAny.getName() + " is RevTag: " + castRevTag.getFullMessage());
                    }
                }
//                boolean annotatedTag = peeledObjectId != null;
//                if (annotatedTag) {
//
//                    RevTag parseTag = walk.parseTag(peeledObjectId);
//                    String tagName = parseTag.getTagName();
//                    String shortMessage = parseTag.getShortMessage();
//                    String fullMessage = parseTag.getFullMessage();
//                } else {
//
//                }
            }
            repository.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void testTagRead2() {
        String pathString = "/home/jkertz/Develop/eclipse-git-tagpilot_20221220/eclipse-git-tagpilot/tagpilot42/";
        System.out.println("testTagRead: " + pathString);
        try {
            File repoPathFile = Paths.get(pathString).toFile();
            Git git = Git.open(repoPathFile);
            Repository repository = git.getRepository();
            System.out.println("Branch: " + repository.getBranch());

            List<JGStag> jgsTags = new ArrayList<>();
            DepthWalk.RevWalk walk = new DepthWalk.RevWalk(repository, 100);
            List<Ref> tags = git.tagList()
                    .call();
            for (Ref tagRef : tags) {
                System.out.println("---------------------------------------------");
                System.out.println("tagRef.getName: " + tagRef.getName());
                Ref peeledRef = repository.peel(tagRef);
                ObjectId peeledObjectId = peeledRef.getPeeledObjectId();
                RevObject parseAny = null;
                if (peeledObjectId == null) {
                    System.out.println("---unannotated tag---");
                    parseAny = walk.parseAny(peeledRef.getObjectId());
                } else {
                    System.out.println("---annotated tag---");
                    parseAny = walk.parseAny(peeledObjectId);
                    RevTag tag = walk.parseTag(peeledRef.getObjectId());
                    System.out.println("annotated tag: " + tag.getTagName() + " : " + tag.getFullMessage());
                }

                RevCommit castRevCommit = (RevCommit) parseAny;
                System.out.println("RevCommit: " + castRevCommit.getName());
                System.out.println("RevCommit Message: " + castRevCommit.getFullMessage());
                System.out.println("---------------------------------------------");

//                if (parseAny == null) {
//                    System.out.println("parseAny == null");
//                } else if (parseAny instanceof RevCommit) {
//                    RevCommit castRevCommit = (RevCommit) parseAny;
//                    System.out.println(parseAny.getName() + " is RevCommit: " + castRevCommit.getFullMessage());
//                } else if (parseAny instanceof RevTag) {
//                    RevTag castRevTag = (RevTag) parseAny;
//                    System.out.println(parseAny.getName() + " is RevTag: " + castRevTag.getFullMessage());
//                }
            }
            repository.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
