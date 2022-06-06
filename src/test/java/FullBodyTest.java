/*
 * Copyright (c) 2022. Arkady Dymkov townhospis<townhospis@gmail.com>
 */

import FileSystem.*;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


public class FullBodyTest {
    /**
     * This test will show you how to work with this library in detail.
     * More information about the library can be found in the documentation on GitHub.
     */
    @Test
    public void FullTest() {

        // Creating the file system with default values
        Path pathToDisk = Paths.get("src/test/java/TestFiles/database.jb");
        FileSystem fileSystem;
        try {
            fileSystem = new FileSystemBuilder(pathToDisk).build();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        // Create a folder
        VirtualFolder folder = FileSystemObject.createFolder("Programming");


        // Create file by name
        VirtualFile fileByName = FileSystemObject.createFileByName("file.txt");

        // Create file by exist folder
        try {
            VirtualFile fileByExists = FileSystemObject.createFileFromExistingFile(new File("src/test/java/TestFiles/pdf.pdf"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        // get the folder name
        folder.getName();

        // set new name for the folder
        folder.setName("new name");
        folder.getChildren();



    }
}
