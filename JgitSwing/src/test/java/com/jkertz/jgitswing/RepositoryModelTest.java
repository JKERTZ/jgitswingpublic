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

import com.jkertz.jgitswing.model.JGSrepositoryModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 *
 * @author jkertz
 */
public class RepositoryModelTest {

    private final String subDirectory = "temp";

    public void hello() {
        System.out.println("hello");
    }

    public void testRepositoryModelgetAllCommits() throws Exception {
        String pathString = "/home/jkertz/Develop/jgitswingexperimental/";
        System.out.println("testRepositoryModelgetAllCommits: " + pathString);
        File repoPathFile = Paths.get(pathString).toFile();
        Git git = Git.open(repoPathFile);

        JGSrepositoryModel model = new JGSrepositoryModel(git);

        Iterable<RevCommit> allCommits = model.getAllCommits();

        for (RevCommit com : allCommits) {
            System.out.println("Commit: " + com);
        }
    }

    private void createTestRepository() {
        checkAndCreateUserSubdir();
    }

//    private String getFullFileName() {
//        String userDir = getUserDir();
//        String filename;
//        String sep = getFileSeparator();
//        String result = userDir + sep + subDirectory + sep + filename;
//        return result;
//    }
    private String getFileSeparator() {
        String fileSeparator = System.getProperty("file.separator");
        System.out.println("File separator: " + fileSeparator);
        return fileSeparator;
    }

    private String getUserDir() {
        String currentUsersHomeDir = System.getProperty("user.home");
        return currentUsersHomeDir;
    }

    private void checkAndCreateUserSubdir() {
        String userDir = getUserDir();
        String sep = getFileSeparator();

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

}
