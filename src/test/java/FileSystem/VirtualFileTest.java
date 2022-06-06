/*
 * Copyright (c) 2022. Arkady Dymkov townhospis<townhospis@gmail.com>
 */

package FileSystem;

import ProjectSettings.ReadPropertyFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VirtualFileTest {

    static List<File> files;
    static String testDatabase;
    static PrintWriter writeToProperties;


    @BeforeAll
    static void beforeAll() {
        files = new ArrayList<>(List.of(
                new File("src/test/java/TestFiles/Helloworld.txt"),
                new File("src/test/java/TestFiles/Lorem5.txt"),
                new File("src/test/java/TestFiles/Lorem10.txt"),
                new File("src/test/java/TestFiles/pdf.pdf"),
                new File("src/test/java/TestFiles/photo.jpg")
        ));
        testDatabase = "src/test/java/TestFiles/database.jb";
        ReadPropertyFile.reloadInstance();
    }

    @AfterEach
    void tearDown() {
        cleanDataBase();
    }

    private void cleanDataBase() {
        writeToProperties = assertDoesNotThrow(() -> new PrintWriter(testDatabase),
                "DataBase couldn't be loaded");
        writeToProperties.print("");
        writeToProperties.close();
    }

    @Test
    void createNewFileByName() {
        VirtualFile file = assertDoesNotThrow(() ->
                FileSystemObject.createFileByName("test.txt"));
        assertEquals("test.txt", file.getName());
        assertNull(file.getCoordinate());
        assertNull(file.getData());
        assertNull(file.getParent());
    }

    @Test
    void createNewFile() {
        VirtualFile virtualFile;
        for (var file : files) {
            virtualFile = assertDoesNotThrow(() ->
                    FileSystemObject.createFileFromExistingFile(file));
            assertEquals(file.getName(), virtualFile.getName());
            assertNull(virtualFile.getCoordinate());
            assertNull(virtualFile.getParent());
            assertEquals(
                    Arrays.toString(assertDoesNotThrow(() -> Files.readAllBytes(file.toPath()))),
                    Arrays.toString(virtualFile.getData())
            );
        }
    }


    private FileSystem createFileSystemFromFileList() {
        FileSystem fileSystem = assertDoesNotThrow(() ->
                new FileSystemBuilder(Paths.get(testDatabase)).setBlockSize(32).build());
        for (var file : files) {
            VirtualFile virtualFile = assertDoesNotThrow(() ->
                    FileSystemObject.createFileFromExistingFile(file));
            assertDoesNotThrow(() -> virtualFile.moveTo(fileSystem.getRootFolder()));
            assertNotNull(virtualFile.getParent());
            assertDoesNotThrow(virtualFile::close);
        }
        return fileSystem;
    }

    @Test
    void writeAndReadContentToDataBase() {
        FileSystem fileSystem = createFileSystemFromFileList();
        int iterator = 0;
        for (var fileSystemObject : fileSystem.getRootFolder().getChildren()) {
            if (fileSystemObject instanceof VirtualFile) {
                assertDoesNotThrow(() ->
                        ((VirtualFile) fileSystemObject).readContent());
                int finalIterator = iterator;
                assertEquals(
                        Arrays.toString(assertDoesNotThrow(() ->
                                Files.readAllBytes(files.get(finalIterator).toPath()))),
                        Arrays.toString(((VirtualFile) fileSystemObject).getData()),
                        "at file " + fileSystemObject.getName());
            }
            iterator++;
        }
    }

    @Test
    void writeContent() {
        FileSystem fileSystem = createFileSystemFromFileList();
        VirtualFile file = (VirtualFile) fileSystem.getRootFolder().getChildren().get(0);
        assertDoesNotThrow(() -> file.writeContent(Files.readAllBytes(files.get(1).toPath())));
        assertEquals(65110, file.getCoordinate().startBlock());
        assertEquals(16, file.getCoordinate().length());
    }

    @Test
    void append() {
        FileSystem fileSystem = createFileSystemFromFileList();
        VirtualFile file = (VirtualFile) fileSystem.getRootFolder().getChildren().get(0);
        assertDoesNotThrow(() -> file.append("Yep) Hello!".getBytes(StandardCharsets.UTF_8)));
        assertEquals(65110, file.getCoordinate().startBlock());
    }

    @Test
    void delete() {
        FileSystem fileSystem = createFileSystemFromFileList();
        List<FileSystemObject> filesCopy =
                new ArrayList<FileSystemObject>(fileSystem.getRootFolder().getChildren());
        for (var file : filesCopy) {
            assertDoesNotThrow(file::delete);
        }
        assertEquals(0, fileSystem.getRootFolder().getChildren().size());
        for (var file : filesCopy) {
            assertNull(file.getParent());
        }
    }
}