/*
 * Copyright (c) 2022. Arkady Dymkov townhospis<townhospis@gmail.com>
 */

package FileSystem;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FileSystemTest {
    static List<File> files;
    static String testDatabase;

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
        PrintWriter writeToProperties = assertDoesNotThrow(() -> new PrintWriter(testDatabase),
                "DataBase couldn't be loaded");
        writeToProperties.print("");
        writeToProperties.close();
    }

    @AfterAll
    static void afterAll() {
        PrintWriter writeToProperties = assertDoesNotThrow(() -> new PrintWriter(testDatabase),
                "DataBase couldn't be loaded");
        writeToProperties.print("");
        writeToProperties.close();
    }

    @Test
    @Order(1)
    void save() {
        FileSystem fileSystem = assertDoesNotThrow(() ->
                new FileSystemBuilder(Paths.get(testDatabase)).build());
        for (var file : files) {
            VirtualFile virtualFile = assertDoesNotThrow(() ->
                    FileSystemObject.createFileFromExistingFile(file));
            assertDoesNotThrow(() -> virtualFile.moveTo(fileSystem.getRootFolder()));
            assertNotNull(virtualFile.getParent());
            assertDoesNotThrow(virtualFile::close);
        }
        assertDoesNotThrow(fileSystem::save);
    }

    @Test
    @Order(2)
    void openExisted() {
        FileSystem fileSystem = assertDoesNotThrow(() -> FileSystem.openExisted(testDatabase));
        VirtualFile file = (VirtualFile) assertDoesNotThrow(() ->
                fileSystem.getRootFolder().getChildren().get(0));
        System.out.println(new String(assertDoesNotThrow(file::readContent)));
    }
}