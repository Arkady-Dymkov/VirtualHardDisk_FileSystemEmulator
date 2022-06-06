/*
 * Copyright (c) 2022. Arkady Dymkov townhospis<townhospis@gmail.com>
 */

package FileSystem;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FileSystemBuilderTest {

    static String testDatabase;

    @BeforeAll
    static void beforeAll() {
        testDatabase = "src/test/java/TestFiles/database.jb";
    }

    @Test
    void buildWithDefaultBlockSize() {
        FileSystem filesystem = assertDoesNotThrow(() ->
                new FileSystemBuilder(Paths.get(testDatabase))
                        .setRootFolderName("newRoot")
                        .build());
        assertEquals("newRoot", filesystem.getRootFolder().getName());
    }

    @Test
    void buildWithCustomBlockSize() {
        assertDoesNotThrow(() ->
                new FileSystemBuilder(Paths.get(testDatabase))
                        .setBlockSize(64)
                        .build());
        assertThrows(IllegalArgumentException.class, () ->
                new FileSystemBuilder(Paths.get(testDatabase))
                        .setBlockSize(11)
                        .build());
        assertDoesNotThrow(() ->
                new FileSystemBuilder(Paths.get(testDatabase))
                        .setBlockSize(-1)
                        .build());
    }
}