/*
 * Copyright (c) 2022. Arkady Dymkov townhospis<townhospis@gmail.com>
 */

package FullTests;

import FileSystem.*;
import ProjectSettings.ReadPropertyFile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


public class FullBodyTest {
    static List<File> files;
    static String mainDatabase;

    @BeforeAll
    static void beforeAll() {
        files = new ArrayList<>(List.of(
                new File("src/test/java/TestFiles/Helloworld.txt"),
                new File("src/test/java/TestFiles/Lorem5.txt"),
                new File("src/test/java/TestFiles/Lorem10.txt"),
                new File("src/test/java/TestFiles/pdf.pdf"),
                new File("src/test/java/TestFiles/photo.jpg")
        ));
        mainDatabase = "src/test/java/FullTests/mainDatabase.jb";
        ReadPropertyFile.reloadInstance();
    }
    private void cleanDataBase() {
        PrintWriter writeToProperties;
        writeToProperties = assertDoesNotThrow(() -> new PrintWriter(mainDatabase),
                "DataBase couldn't be loaded");
        writeToProperties.print("");
        writeToProperties.close();
    }

    private void placeObjectsInFileSystem(FileSystem fileSystem) {
        List<VirtualFolder> folders = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            folders.add(FileSystemObject.createFolder(String.valueOf(i)));
        }
        fileSystem.getRootFolder().moveHere(folders.get(0));
        fileSystem.getRootFolder().moveHere(folders.get(1));
        fileSystem.getRootFolder().moveHere(folders.get(2));
        folders.get(0).moveHere(folders.get(3));
        folders.get(0).moveHere(folders.get(4));
        folders.get(1).moveHere(folders.get(5));
        folders.get(1).moveHere(folders.get(6));
        folders.get(2).moveHere(folders.get(7));
        folders.get(2).moveHere(folders.get(8));
        folders.get(8).moveHere(folders.get(9));
        folders.get(8).moveHere(folders.get(10));
        folders.get(8).moveHere(folders.get(11));

        List<VirtualFile> vfiles = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            try {
                vfiles.add(FileSystemObject.createFileFromExistingFile(files.get(i % 5)));
                vfiles.get(i).setName(String.valueOf(i));
                vfiles.get(i).moveTo(folders.get(new Random().nextInt(12)));
                vfiles.get(i).close();
            } catch (IOException e) {
                continue;
            }
        }
        for (int i = 0; i < 12; i++) {
            try {
                vfiles.get(i).append("Hi!!!!!!!!".getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                continue;
            }
        }
    }


    @Test
    public void FullTest() {
        // Creating new file system
        FileSystem fileSystem;
        try {
            fileSystem = new FileSystemBuilder(Paths.get(mainDatabase)).build();
        } catch (IOException e) {
            System.out.println("File with path " + mainDatabase + "cannot be loaded");
            return;
        }
        placeObjectsInFileSystem(fileSystem);
        assertDoesNotThrow(fileSystem::save);
        FileSystem fileSystem2 = assertDoesNotThrow(() -> FileSystem.openExisted(mainDatabase));
        // check monually

        List<FileSystemObject> children = new ArrayList<>(fileSystem2.getRootFolder().getChildren());
        for (var file : children) {
            if (file instanceof VirtualFile) {
                assertDoesNotThrow(() -> ((VirtualFile) file).delete());
            }
        }

        for (var file : children) {
            if (file instanceof VirtualFolder) {
                assertDoesNotThrow(() -> ((VirtualFolder) file).hardDelete());
            }
        }
        assertDoesNotThrow(fileSystem2::save);
        FileSystem fileSystem3 = assertDoesNotThrow(() -> FileSystem.openExisted(mainDatabase));
        //check manually
        assertDoesNotThrow(fileSystem3::save);
        cleanDataBase();
    }
}
