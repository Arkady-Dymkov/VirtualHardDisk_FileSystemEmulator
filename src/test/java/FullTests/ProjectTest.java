/*
 * Copyright (c) 2022. Arkady Dymkov townhospis<townhospis@gmail.com>
 */

package FullTests;

import FileSystem.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ProjectTest {

    static Path mainDatabase;
    static Path projectPath;
    static Path copyProjectPath;
    static FileSystem copyProject;
    int countOfFiles;
    int filesToDelete;

    @BeforeAll
    static void beforeAll() {
        mainDatabase = Path.of("../copyProject.jb");
        projectPath = Path.of("../VirtualHardDisk");
        copyProjectPath = Path.of("../VirtualHardDiskCopy");

        if (!Files.exists(copyProjectPath)) {
            assertDoesNotThrow(() -> Files.createDirectory(copyProjectPath));
        }

        if (!Files.exists(mainDatabase)) {
            assertDoesNotThrow(() -> Files.createFile(mainDatabase));
        }

        // Creating file system for copy the project
        copyProject = assertDoesNotThrow(() ->
                new FileSystemBuilder(mainDatabase)
                        .setRootFolderName(projectPath.toFile().getName())
                        .build()
        );
    }

    private static void cleanDataBase() {
        PrintWriter writeToProperties;
        writeToProperties = assertDoesNotThrow(() -> new PrintWriter(mainDatabase.toString()),
                "DataBase couldn't be loaded");
        writeToProperties.print("");
        writeToProperties.close();
    }

    @AfterAll
    static void afterAll() {
        assertDoesNotThrow(ProjectTest::cleanDataBase);
    }

    @Test
    public void destroyAndRemoveTest() {
        if (projectPath.toFile().isDirectory()) {
            saveProject(Objects.requireNonNull(projectPath.toFile().listFiles()), copyProject.getRootFolder());
        }
        filesToDelete = (int) Math.ceil(countOfFiles * 0.7);
        deleteRandomFiles(copyProject.getRootFolder().getChildren());
        assertDoesNotThrow(copyProject::save);
        copyProject = null;
        copyProject = assertDoesNotThrow(() -> FileSystem.openExisted(mainDatabase.toString()));
        restoreProject(copyProject.getRootFolder().getChildren(), copyProjectPath.toString());
    }

    private void deleteRandomFiles(List<FileSystemObject> files) {
        List<FileSystemObject> children = new ArrayList<>(files);
        for (var file : children) {
            if (file instanceof VirtualFile) {
                if (filesToDelete >= 0 && new Random().nextBoolean()) {
                    assertDoesNotThrow(() ->
                            ((VirtualFile) file).delete()
                    );
                    filesToDelete--;
                }
            } else {
                deleteRandomFiles(((VirtualFolder) file).getChildren());
            }
        }
    }

    public void restoreProject(List<FileSystemObject> files, String parentPath) {
        for (var file : files) {
            Path currentPath = Paths.get(parentPath + "/" + file.getName());
            if (file instanceof VirtualFolder) {
                assertDoesNotThrow(() ->
                        Files.createDirectory(currentPath)
                );
                restoreProject(((VirtualFolder) file).getChildren(), currentPath.toString());
            } else {
                assertDoesNotThrow(() ->
                        Files.createFile(currentPath)
                );
                assertDoesNotThrow(() -> ((VirtualFile) file).readContent());
                assertDoesNotThrow(()->Files.write(currentPath, ((VirtualFile) file).getData()));
            }
        }
    }

    private void saveProject(File[] files, VirtualFolder parent) {
        for (File file : files) {
            countOfFiles++;
            if (file.isDirectory()) {
                VirtualFolder newFolder = FileSystemObject.createFolder(file.getName());
                parent.moveHere(newFolder);
                saveProject(Objects.requireNonNull(file.listFiles()), newFolder);
            } else {
                VirtualFile newFile = assertDoesNotThrow(() ->
                        FileSystemObject.createFileFromExistingFile(file));
                parent.moveHere(newFile);
                assertDoesNotThrow(newFile::close);
            }
        }
    }
}
