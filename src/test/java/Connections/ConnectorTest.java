/*
 * Copyright (c) 2022. Arkady Dymkov townhospis<townhospis@gmail.com>
 */

package Connections;

import FileSystem.FileSystemObject;
import FileSystem.VirtualFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConnectorTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    // TODO: replace with resource files
    @Test
    void addFile() throws IOException {
        Connector test = new Connector.Builder(Path.of("/Users/f1nc/Downloads/1")).build();
        VirtualFile file = FileSystemObject.createFileFromExistingFile(new File("/Users/f1nc/Downloads/2"));
        VirtualFile file2 = FileSystemObject.createFileFromExistingFile(new File("/Users/f1nc/Downloads/3"));
        test.addFile(file);
        test.addFile(file2);

        System.out.println(file.getCoordinate().length() + " " + file.getCoordinate().startBlock());
        //assertEquals(32, Files.readAllBytes(new File("/Users/f1nc/Downloads/1").toPath()).length);
    }

    @Test
    void removeFile() throws IOException {
        Connector test = new Connector.Builder(Path.of("/Users/f1nc/Downloads/1")).build();
        VirtualFile file = FileSystemObject.createFileFromExistingFile(new File("/Users/f1nc/Downloads/2"));
        VirtualFile file2 = FileSystemObject.createFileFromExistingFile(new File("/Users/f1nc/Downloads/3"));
        test.addFile(file);
        String correctResult = Arrays.toString(Files.readAllBytes(Path.of("/Users/f1nc/Downloads/1")));
        test.addFile(file2);
        test.removeFile(file2);
        assertEquals(correctResult, Arrays.toString(Files.readAllBytes(Path.of("/Users/f1nc/Downloads/1"))));
    }

    @Test
    void changeFile() throws IOException {
        Connector test = new Connector.Builder(Path.of("/Users/f1nc/Downloads/1")).build();
        VirtualFile file = FileSystemObject.createFileFromExistingFile(new File("/Users/f1nc/Downloads/2"));
        VirtualFile file2 = FileSystemObject.createFileFromExistingFile(new File("/Users/f1nc/Downloads/3"));
        test.addFile(file2);
        test.addFile(file);
        String correctResult = Arrays.toString(Files.readAllBytes(Path.of("/Users/f1nc/Downloads/1")));

        Connector test2 = new Connector.Builder(Path.of("/Users/f1nc/Downloads/1p1")).build();
        file = FileSystemObject.createFileFromExistingFile(new File("/Users/f1nc/Downloads/2"));
        file2 = FileSystemObject.createFileFromExistingFile(new File("/Users/f1nc/Downloads/3"));
        byte[] bytes = file.getData();
        test2.addFile(file);
        test2.addFile(file2);
        // file2.setData(new byte[]{12,12,12,12,12,12,1,2});
        file.setData(bytes);
        test2.changeFile(file);

        String correctResult2 = Arrays.toString(Files.readAllBytes(Path.of("/Users/f1nc/Downloads/1p1")));

        assertEquals(correctResult, correctResult2);
    }

    @Test
    void getFileBody() throws IOException {
        Connector test = new Connector.Builder(Path.of("/Users/f1nc/Downloads/1")).build();
        VirtualFile file = FileSystemObject.createFileFromExistingFile(new File("/Users/f1nc/Downloads/2"));
        VirtualFile file2 = FileSystemObject.createFileFromExistingFile(new File("/Users/f1nc/Downloads/3"));
        byte[] bytes = file.getData();
        byte[] bytes2 = file2.getData();
        test.addFile(file2);
        test.addFile(file);
        test.getFileBody(file);
        test.getFileBody(file2);

        assertEquals(Arrays.toString(bytes), Arrays.toString(file.getData()));
        assertEquals(Arrays.toString(bytes2), Arrays.toString(file2.getData()));
    }

    @Test
    void close() {
    }
}