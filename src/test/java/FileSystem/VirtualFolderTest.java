/*
 * Copyright (c) 2022. Arkady Dymkov townhospis<townhospis@gmail.com>
 */

package FileSystem;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class VirtualFolderTest {
    static String defaultName;
    VirtualFolder mainFolder;

    @BeforeAll
    static void beforeAll() {
        defaultName = "Folder";
    }

    @BeforeEach
    void setUp() {
        mainFolder = assertDoesNotThrow(() -> FileSystemObject.createFolder(defaultName),
                "Folder cannot be created");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createFolder() {
        assertEquals(defaultName, mainFolder.getName(), "folder name sets incorrect");
        assertNull(mainFolder.getParent(), "Created folder parent should be not existed");
        assertEquals(0, mainFolder.getChildren().size(), "Created folder children should be empty");
    }

    @Test
    void getName() {
        assertEquals(defaultName, assertDoesNotThrow(mainFolder::getName), "folder name sets incorrect");
        mainFolder.name = defaultName + "newName";
        assertEquals(defaultName + "newName", assertDoesNotThrow(mainFolder::getName),
                "folder name sets incorrect");
    }

    @Test
    void setName() {
        assertEquals(defaultName, assertDoesNotThrow(mainFolder::getName), "folder name sets incorrect");
        assertDoesNotThrow(() -> mainFolder.setName(defaultName + "newName"),
                "folder name cannot be set");
        assertEquals(defaultName + "newName", assertDoesNotThrow(mainFolder::getName),
                "folder name sets incorrect");
    }

    @Test
    void getParent() {
        VirtualFolder childFolder = assertDoesNotThrow(() -> FileSystemObject.createFolder(defaultName),
                "Folder cannot be created");
        assertNull(assertDoesNotThrow(childFolder::getParent,
                "Just created folder should not have a parent"));
        assertDoesNotThrow(() -> mainFolder.moveHere(childFolder),
                "child folder cannot be moved to the main folder");
        assertEquals(mainFolder, assertDoesNotThrow(childFolder::getParent,
                "Parent sets incorrect while moving"));
        assertSame(mainFolder, assertDoesNotThrow(childFolder::getParent,
                "Parent sets incorrect while moving"));
    }

    @Test
    void setParent() {
        VirtualFolder childFolder = assertDoesNotThrow(() -> FileSystemObject.createFolder(defaultName),
                "Folder cannot be created");
        assertDoesNotThrow(() -> childFolder.setParent(mainFolder),
                "child folder cannot be moved to the main folder");
        assertEquals(mainFolder, assertDoesNotThrow(childFolder::getParent,
                "Parent sets incorrect"));
        assertSame(mainFolder, assertDoesNotThrow(childFolder::getParent,
                "Parent sets incorrect"));
    }

    @Test
    @DisplayName("Moving one object with no parents into another")
    void moveHereWithoutMovingBetween() {
        FileSystemObject childFolder = assertDoesNotThrow(() -> FileSystemObject.createFolder(defaultName),
                "Folder cannot be created");
        // ONLY FOR TEST
        mainFolder.setFilesystem(new FileSystem(null, null));
        assertNotNull(mainFolder.filesystem,
                "Main folder filesystem is null, but shouldn't be");
        assertNull(childFolder.filesystem,
                "Children folder filesystem isn't null, but shouldn't be");
        assertDoesNotThrow(() -> mainFolder.moveHere(childFolder),
                "child folder cannot be moved to the main folder");
        assertEquals(mainFolder, assertDoesNotThrow(childFolder::getParent,
                "Parent sets incorrect while moving"));
        assertSame(mainFolder, assertDoesNotThrow(childFolder::getParent,
                "Parent sets incorrect while moving"));
        assertTrue(mainFolder.getChildren().contains(childFolder), "After moving the parent folder should contains child object");
        assertEquals(1, mainFolder.getChildren().size(),
                "Main folder should contains only one child");
        assertSame(childFolder.filesystem, mainFolder.filesystem,
                "Parent folder and child folders should have the same filesystem");
    }

    @Test
    @DisplayName("Moving one object with parent into another")
    void moveHereMovingBetween() {
        FileSystemObject childFolder = assertDoesNotThrow(() -> FileSystemObject.createFolder(defaultName),
                "Folder cannot be created");
        VirtualFolder prevFolder = assertDoesNotThrow(() -> FileSystemObject.createFolder(defaultName),
                "Folder cannot be created");

        // ONLY FOR TEST
        mainFolder.setFilesystem(new FileSystem(null, null));
        prevFolder.setFilesystem(new FileSystem(null, null));

        assertNotNull(mainFolder.filesystem, "Main folder filesystem is null, but shouldn't be");
        assertNotNull(prevFolder.filesystem, "Main folder filesystem is null, but shouldn't be");
        assertNull(childFolder.filesystem, "Children folder filesystem isn't null");

        prevFolder.moveHere(childFolder);

        assertDoesNotThrow(() -> mainFolder.moveHere(childFolder),
                "child folder cannot be moved to the main folder");
        assertEquals(mainFolder, assertDoesNotThrow(childFolder::getParent,
                "Parent sets incorrect while moving"));
        assertSame(mainFolder, assertDoesNotThrow(childFolder::getParent,
                "Parent sets incorrect while moving"));
        assertTrue(mainFolder.getChildren().contains(childFolder),
                "After moving the parent folder should contains child object");
        assertEquals(1, mainFolder.getChildren().size(),
                "Main folder should contains only one child");
        assertSame(childFolder.filesystem, mainFolder.filesystem,
                "Parent folder and child folders should have the same filesystem");
        assertEquals(0, prevFolder.getChildren().size());
    }

    @Test
    @DisplayName("Change places folder and its child")
    void moveHereChange() {
        VirtualFolder childFolder = assertDoesNotThrow(() -> FileSystemObject.createFolder(defaultName),
                "Folder cannot be created");
        // ONLY FOR TEST
        mainFolder.setFilesystem(new FileSystem(null, null));
        assertNotNull(mainFolder.filesystem,
                "Main folder filesystem is null, but shouldn't be");
        assertNull(childFolder.filesystem,
                "Children folder filesystem isn't null, but shouldn't be");
        assertDoesNotThrow(() -> mainFolder.moveHere(childFolder),
                "child folder cannot be moved to the main folder");

        // Now lets change
        assertDoesNotThrow(() -> childFolder.moveHere(mainFolder),
                "child folder cannot be moved to the main folder");

        assertEquals(childFolder, assertDoesNotThrow(mainFolder::getParent,
                "Parent sets incorrect while moving"));
        assertSame(childFolder, assertDoesNotThrow(mainFolder::getParent,
                "Parent sets incorrect while moving"));

        assertTrue(childFolder.getChildren().contains(mainFolder),
                "After moving the parent folder should contains child object");
        assertEquals(1, childFolder.getChildren().size(),
                "Main folder should contains only one child");
        assertSame(childFolder.filesystem, mainFolder.filesystem,
                "Parent folder and child folders should have the same filesystem");
        assertEquals(0, mainFolder.getChildren().size(),
                "Main folder should contains no child");

    }


    @Test
    @DisplayName("Moving one full folder into another")
    void moveHereWithFoldersWithChildren() {
        VirtualFolder childFolder = assertDoesNotThrow(() -> FileSystemObject.createFolder(defaultName),
                "Folder cannot be created");
        VirtualFolder prevFolder = assertDoesNotThrow(() -> FileSystemObject.createFolder(defaultName),
                "Folder cannot be created");

        // Additional folders
        VirtualFolder grandChild1 = assertDoesNotThrow(() -> FileSystemObject.createFolder(defaultName),
                "Folder cannot be created");
        VirtualFolder grandChild2 = assertDoesNotThrow(() -> FileSystemObject.createFolder(defaultName),
                "Folder cannot be created");
        VirtualFolder grandChild3 = assertDoesNotThrow(() -> FileSystemObject.createFolder(defaultName),
                "Folder cannot be created");
        VirtualFolder grandChild4 = assertDoesNotThrow(() -> FileSystemObject.createFolder(defaultName),
                "Folder cannot be created");
        VirtualFolder grandChild5 = assertDoesNotThrow(() -> FileSystemObject.createFolder(defaultName),
                "Folder cannot be created");

        // ONLY FOR TEST
        mainFolder.setFilesystem(new FileSystem(null, null));
        prevFolder.setFilesystem(new FileSystem(null, null));

        childFolder.moveHere(grandChild1);
        childFolder.moveHere(grandChild2);
        childFolder.moveHere(grandChild3);

        mainFolder.moveHere(grandChild4);
        prevFolder.moveHere(grandChild5);


        assertNotNull(mainFolder.filesystem, "Main folder filesystem is null, but shouldn't be");
        assertNotNull(prevFolder.filesystem, "Main folder filesystem is null, but shouldn't be");
        assertNull(grandChild1.filesystem, "Children folder filesystem isn't null");
        assertNull(grandChild2.filesystem, "Children folder filesystem isn't null");
        assertNull(grandChild3.filesystem, "Children folder filesystem isn't null");
        assertNotNull(grandChild4.filesystem, "Children folder filesystem isn't null");
        assertNotNull(grandChild5.filesystem, "Children folder filesystem isn't null");
        assertNull(childFolder.filesystem, "Children folder filesystem isn't null");

        prevFolder.moveHere(childFolder);

        assertDoesNotThrow(() -> mainFolder.moveHere(childFolder),
                "child folder cannot be moved to the main folder");
        assertEquals(mainFolder, assertDoesNotThrow(childFolder::getParent,
                "Parent sets incorrect while moving"));
        assertSame(mainFolder, assertDoesNotThrow(childFolder::getParent,
                "Parent sets incorrect while moving"));
        assertTrue(mainFolder.getChildren().contains(childFolder),
                "After moving the parent folder should contains child object");
        assertEquals(2, mainFolder.getChildren().size(),
                "Main folder should contains only one child");
        assertSame(childFolder.filesystem, mainFolder.filesystem,
                "Parent folder and child folders should have the same filesystem");
        assertSame(grandChild1.filesystem, mainFolder.filesystem,
                "Parent folder and child folders should have the same filesystem");
        assertSame(grandChild2.filesystem, mainFolder.filesystem,
                "Parent folder and child folders should have the same filesystem");
        assertSame(grandChild3.filesystem, mainFolder.filesystem,
                "Parent folder and child folders should have the same filesystem");

        assertEquals(1, prevFolder.getChildren().size());
    }

    @Test
    void delete() {
        FileSystemObject childFolder = assertDoesNotThrow(() -> FileSystemObject.createFolder(defaultName),
                "Folder cannot be created");
        // ONLY FOR TEST
        mainFolder.setFilesystem(new FileSystem(null, null));
        assertNotNull(mainFolder.filesystem,
                "Main folder filesystem is null, but shouldn't be");
        assertNull(childFolder.filesystem,
                "Children folder filesystem isn't null, but shouldn't be");
        assertDoesNotThrow(() -> mainFolder.moveHere(childFolder),
                "child folder cannot be moved to the main folder");

        Throwable th = assertThrows(Exception.class, mainFolder::delete,
                "main folder should not be deleted as it contains children");
        assertEquals("Folder should be empty do delete it." +
                        "You can use hard Delete if you want to delete it anyway.",
                th.getMessage());
        assertDoesNotThrow(childFolder::delete,
                "main folder should be deleted as it doesnt contains children");
        assertNull(childFolder.getParent());
        assertEquals(0, mainFolder.getChildren().size());
    }

    @Test
    void hardDelete() {
        FileSystemObject childFolder = assertDoesNotThrow(() -> FileSystemObject.createFolder(defaultName),
                "Folder cannot be created");
        // ONLY FOR TEST
        mainFolder.setFilesystem(new FileSystem(null, null));
        assertNotNull(mainFolder.filesystem,
                "Main folder filesystem is null, but shouldn't be");
        assertNull(childFolder.filesystem,
                "Children folder filesystem isn't null, but shouldn't be");
        assertDoesNotThrow(() -> mainFolder.moveHere(childFolder),
                "child folder cannot be moved to the main folder");

        assertDoesNotThrow(mainFolder::hardDelete,
                "main folder should be deleted with all children");
        assertNull(childFolder.getParent());
        assertEquals(0, mainFolder.getChildren().size());
        assertNull(mainFolder.getParent());
    }
}