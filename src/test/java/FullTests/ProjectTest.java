/*
 * Copyright (c) 2022. Arkady Dymkov townhospis<townhospis@gmail.com>
 */

package FullTests;

import FileSystem.*;
import ProjectSettings.ReadPropertyFile;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
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
    static Path deletedFileBase;
    static Path projectPath;
    static Path copyProjectPath;
    static Path deletesCopy;

    static FileSystem copyProject;
    static FileSystem deletedFiles;

    int countOfFiles;
    int filesToDelete;

    @BeforeAll
    static void beforeAll() {
        ReadPropertyFile.reloadInstance();
        mainDatabase = Path.of("../copyProject.jb");
        projectPath = Path.of("../VirtualHardDisk");
        deletedFileBase = Path.of("../deletedCopy.jb");
        deletesCopy = Path.of("../DeletedCopies");
        copyProjectPath = Path.of("../VirtualHardDiskCopy");

        if (!Files.exists(copyProjectPath)) {
            assertDoesNotThrow(() -> Files.createDirectory(copyProjectPath));
        }
        if (!Files.exists(deletesCopy)) {
            assertDoesNotThrow(() -> Files.createDirectory(deletesCopy));
        }
        if (!Files.exists(mainDatabase)) {
            assertDoesNotThrow(() -> Files.createFile(mainDatabase));
        }
        if (!Files.exists(deletedFileBase)) {
            assertDoesNotThrow(() -> Files.createFile(deletedFileBase));
        }

        // Creating file system for copy the project
        copyProject = assertDoesNotThrow(() ->
                new FileSystemBuilder(mainDatabase)
                        .setRootFolderName(projectPath.toFile().getName())
                        .build()
        );
        // Creating file system for copy the project
        deletedFiles = assertDoesNotThrow(() ->
                new FileSystemBuilder(deletedFileBase)
                        .setRootFolderName(deletesCopy.toFile().getName())
                        .build()
        );
    }

    private static void cleanDataBase() {
        PrintWriter writeToProperties;
        writeToProperties = assertDoesNotThrow(() -> new PrintWriter(mainDatabase.toString()),
                "DataBase couldn't be loaded");
        writeToProperties.print("");
        writeToProperties.close();
        writeToProperties = assertDoesNotThrow(() -> new PrintWriter(deletedFileBase.toString()),
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
        countOfFiles = 0;
        filesToDelete = 0;
        VirtualFolder folder;
        // Saving the project to the fileSystem
        if (projectPath.toFile().isDirectory()) {
            folder = FileSystemObject.createFolder(projectPath.toFile().getName());
            copyProject.getRootFolder().moveHere(folder);
            saveProject(Objects.requireNonNull(projectPath.toFile().listFiles()), folder);
        }
        // Calculate count of files to delete
        filesToDelete = (int) Math.ceil(countOfFiles * 0.7);
        // Random number of files delete
        deleteRandomFiles(copyProject.getRootFolder().getChildren());

        // Another copy
        if (projectPath.toFile().isDirectory()) {
            folder = FileSystemObject.createFolder("Another_Copy");
            copyProject.getRootFolder().moveHere(folder);
            saveProject(Objects.requireNonNull(projectPath.toFile().listFiles()), folder);
        }


        // Save project
        assertDoesNotThrow(copyProject::save);
        // Save deleted files
        assertDoesNotThrow(deletedFiles::save);

        copyProject = null;
        // Open project folder
        copyProject = assertDoesNotThrow(() -> FileSystem.openExisted(mainDatabase.toString()));

        restoreProject(copyProject.getRootFolder().getChildren(), copyProjectPath.toString());

        deletedFiles = null;
        // Open deleted files folder
        deletedFiles = assertDoesNotThrow(() -> FileSystem.openExisted(deletedFileBase.toString()));
        restoreProject(deletedFiles.getRootFolder().getChildren(), deletesCopy.toString());
    }

    private void saveDeletedFile(VirtualFile file) {
        VirtualFile newFile = FileSystemObject.createFileByName(file.getName());
        deletedFiles.getRootFolder().moveHere(newFile);
        assertDoesNotThrow(file::readContent);
        newFile.setData(file.getData());
        assertDoesNotThrow(newFile::close);
    }

    private void deleteRandomFiles(List<FileSystemObject> files) {
        List<FileSystemObject> children = new ArrayList<>(files);
        for (var file : children) {
            if (file instanceof VirtualFile) {
                if (filesToDelete >= 0 && new Random().nextBoolean()) {
                    saveDeletedFile((VirtualFile) file);
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
                try {
                    Files.createFile(currentPath);
                    assertDoesNotThrow(() -> ((VirtualFile) file).readContent());
                    Files.write(currentPath, ((VirtualFile) file).getData());
                } catch (IOException ignored) {
                }
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
