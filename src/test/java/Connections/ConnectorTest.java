package Connections;

import FileSystemObjects.FileSystemObject;
import FileSystemObjects.VirtualFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
        VirtualFile file = FileSystemObject.createFile(new File("/Users/f1nc/Downloads/2"));
        test.addFile(file);
        assertEquals(32, Files.readAllBytes(new File("/Users/f1nc/Downloads/1").toPath()).length);
    }

    @Test
    void removeFile() {
    }

    @Test
    void changeFile() {
    }

    @Test
    void getFileBody() {
    }

    @Test
    void close() {
    }
}