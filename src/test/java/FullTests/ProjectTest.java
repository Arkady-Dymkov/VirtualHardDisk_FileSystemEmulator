/*
 * Copyright (c) 2022. Arkady Dymkov townhospis<townhospis@gmail.com>
 */

package FullTests;

import FileSystem.FileSystem;
import FileSystem.*;
import ProjectSettings.ReadPropertyFile;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectTest {

    /**
     * DataBase to save project
     */
    static Path mainDatabase;

    /**
     * DataBase to save deleted files
     */
    static Path deletedFileBase;

    /**
     * Path to the real project
     */
    static Path projectPath;

    /**
     * Path to the directory containing full project files and project with deleted file
     */
    static Path copyProjectPath;

    /**
     * Path to the directory containing deleted files copy
     */
    static Path pathToDeletedFilesCopy;

    /**
     * Path to the directory containing full project files copy.
     */
    static Path pathToFullProjectCopy;

    /**
     * Path to the directory containing deleted files copy
     */
    static Path pathToProjectWithDeletedFiles;

    static FileSystem copyProject;
    static FileSystem deletedFiles;

    static int countOfFiles;
    static int filesToDelete;
    static VirtualFolder folder;

    @BeforeAll
    static void beforeAll() {
        ReadPropertyFile.reloadInstance();
        mainDatabase = Path.of("../copyProject.jb");
        projectPath = Path.of("../VirtualHardDisk");
        deletedFileBase = Path.of("../deletedCopy.jb");
        pathToDeletedFilesCopy = Path.of("../DeletedCopies");
        copyProjectPath = Path.of("../VirtualHardDiskCopy");
        pathToFullProjectCopy = Path.of("../VirtualHardDiskCopy/VirtualHardDisk");
        pathToProjectWithDeletedFiles = Path.of("../VirtualHardDiskCopy/Another_Copy");

        if (!Files.exists(copyProjectPath)) {
            assertDoesNotThrow(() -> Files.createDirectory(copyProjectPath));
        }
        if (!Files.exists(pathToDeletedFilesCopy)) {
            assertDoesNotThrow(() -> Files.createDirectory(pathToDeletedFilesCopy));
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
                        .setRootFolderName(pathToDeletedFilesCopy.toFile().getName())
                        .build()
        );
        countOfFiles = 0;
        filesToDelete = 0;
        folder = null;
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
    @Order(1)
    public void saveProjectToFileSystem() {
        // Saving the project to the fileSystem
        if (projectPath.toFile().isDirectory()) {
            folder = FileSystemObject.createFolder(pathToFullProjectCopy.toFile().getName());
            copyProject.getRootFolder().moveHere(folder);
            saveProject(Objects.requireNonNull(projectPath.toFile().listFiles()), folder);
        }
        // Calculate count of files to delete
        filesToDelete = (int) Math.ceil(countOfFiles * 0.7);

        // Another copy
        if (projectPath.toFile().isDirectory()) {
            folder = FileSystemObject.createFolder(pathToProjectWithDeletedFiles.toFile().getName());
            copyProject.getRootFolder().moveHere(folder);
            saveProject(Objects.requireNonNull(projectPath.toFile().listFiles()), folder);
        }
    }

    @Test
    @Order(2)
    public void deleteRandomFiles() {
        // Random number of files delete
        if (folder != null) {
            deleteRandomFiles(folder.getChildren());
        }
    }

    @Test
    @Order(3)
    public void saveFileSystems() {
        // Save project
        assertDoesNotThrow(copyProject::save);
        // Save deleted files
        assertDoesNotThrow(deletedFiles::save);
    }

    @Test
    @Order(4)
    public void loadAndRestoreFileSystems() {
        copyProject = null;
        // Open project folder
        copyProject = assertDoesNotThrow(() -> FileSystem.openExisted(mainDatabase.toString()));

        restoreProject(copyProject.getRootFolder().getChildren(), copyProjectPath.toString());

        deletedFiles = null;
        // Open deleted files folder
        deletedFiles = assertDoesNotThrow(() -> FileSystem.openExisted(deletedFileBase.toString()));
        restoreProject(deletedFiles.getRootFolder().getChildren(), pathToDeletedFilesCopy.toString());

    }

    @Test
    @Order(5)
    public void verifyProjectAreEqual() {
        assertDoesNotThrow(() -> verifyDirsAreEqual(
                projectPath,
                pathToFullProjectCopy));
    }

    @Test
    @Order(6)
    public void verifyDeletedFilesAreDeleted() {
        assertDoesNotThrow(() -> verifyNoDeletedFiles(
                Objects.requireNonNull(pathToProjectWithDeletedFiles.toFile().listFiles()),
                pathToDeletedFilesCopy.toFile().listFiles()));
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

    private void verifyNoDeletedFiles(File[] files, File[] deletedFiles) {
        for (File file : files) {
            if (file.isDirectory()) {
                verifyNoDeletedFiles(Objects.requireNonNull(file.listFiles()), deletedFiles);
            } else {
                for (var deletedFile : List.of(deletedFiles)) {
                    if (assertDoesNotThrow(() -> equalsOfFiles(file.toPath(), deletedFile.toPath()))) {
                        System.out.println("Deleted file " + deletedFile + " is not deleted" + file);
                    }
                }
            }
        }
    }


    private static boolean equalsOfFiles(Path one, Path other) throws IOException {
        byte[] otherBytes = Files.readAllBytes(other);
        byte[] theseBytes = Files.readAllBytes(one);
        return Arrays.equals(otherBytes, theseBytes) && one.toFile().getName().equals(other.toFile().getName());
    }

    private static void verifyDirsAreEqual(Path one, Path other) throws IOException {
        Files.walkFileTree(one, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file,
                                             BasicFileAttributes attrs)
                    throws IOException {
                FileVisitResult result = super.visitFile(file, attrs);

                // get the relative file name from path "one"
                Path relativize = one.relativize(file);
                // construct the path for the counterpart file in "other"
                Path fileInOther = other.resolve(relativize);

                byte[] otherBytes = Files.readAllBytes(fileInOther);
                byte[] theseBytes = Files.readAllBytes(file);
                if (!Arrays.equals(otherBytes, theseBytes)) {
                    System.out.println(file + " is not equal to " + fileInOther);
                }
                return result;
            }
        });
    }
}
