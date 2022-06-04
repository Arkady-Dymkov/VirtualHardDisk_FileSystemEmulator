import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

// TODO: Comment and complete tests
class ConnectionTest {

    static List<File> files;
    static List<Connection> connections;
    static ResourcesChecker checker;
    ResourcesChecker localChecker;

    @BeforeAll
    static void beforeAll() {
        connections = new ArrayList<>();
        files = new ArrayList<>(List.of(
                // txt
                new File(Objects.requireNonNull(ConnectionTest.class.getResource("1")).getPath()),
                // pdf (about 1.2MB)
                new File(Objects.requireNonNull(ConnectionTest.class.getResource("2")).getPath()),
                // txt (200 KB)
                new File(Objects.requireNonNull(ConnectionTest.class.getResource("3")).getPath()),
                // zip (about 1.6 MB)
                new File(Objects.requireNonNull(ConnectionTest.class.getResource("4")).getPath()),
                // zip (about 4.6 MB)
                new File(Objects.requireNonNull(ConnectionTest.class.getResource("5")).getPath())
                // zip (about 122.6 MB)
                //new File(Objects.requireNonNull(ConnectionTest.class.getResource("6")).getPath()),
                //new File("/Users/f1nc/Downloads/1")
        ));
        checker = new ResourcesChecker();
        System.out.println("Before ALL tests memory usage is " + checker.getMemory() + " MB");
        checker.startTimeCounting();
    }

    @AfterAll
    static void afterAll() {
        System.out.println("All tests passed, time = " + checker.endTimeCounting() + " seconds");
    }

    @BeforeEach
    void setUp() {
        localChecker = new ResourcesChecker();
        localChecker.startTimeCounting();
        System.out.println("Before test memory usage is " + localChecker.getMemory() + " MB");
        connections.clear();
        for (var file : files) {
            assertDoesNotThrow(() -> {
                connections.add(Connection.connectByFile(file));
            }, "Creating by path object");
        }
        localChecker.startTimeCounting();
    }

    @AfterEach
    void tearDown() {
        beforeAll();
        System.out.println("Test runs by: " + localChecker.endTimeCounting() + " milliseconds");
        System.out.println("After memory usage is " + localChecker.getMemory() + " MB");
        for (var connection : connections) {
            assertDoesNotThrow(connection::close, "closing connection");
        }
    }


    @Test
    void connectByPathObject() {
        for (var file : files) {
            assertDoesNotThrow(() -> {
                connections.add(Connection.connectByFile(file));
            }, "Creating by path object");
        }
    }

    @Test
    void connectByPath() {
        for (int i = 0; i < files.size(); ++i) {
            int finalI1 = i;
            assertDoesNotThrow(() -> {
                Connection.connectByPath(
                        files.get(finalI1).getPath()
                );
            }, "Creating by path");
        }
    }

    @Test
    void remove() {
        for (var connection : connections) {
            assertDoesNotThrow(() -> connection.remove(2, 5));
        }
    }

    @Test
    void read() {
        try {
            for (int i = 0; i < connections.size(); i++) {
                assertEquals(Arrays.toString(Arrays.copyOfRange(Files.readAllBytes(files.get(i).toPath()), 3, 10)),
                        Arrays.toString(connections.get(i).read(3, 7)),
                        "at file 1");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void writeByRange() {
        byte[] bytes = "Hello, world".getBytes(StandardCharsets.UTF_8);
        int i = 0;
        for (var connection : connections) {
            assertDoesNotThrow(() -> connection.overwriteFrom(3, bytes));
            try {
                assertEquals(Arrays.toString(bytes),
                        Arrays.toString(connection.read(3, bytes.length)), "file: " + (i + 1));
            } catch (IOException e) {
                e.printStackTrace();
            }
            i++;
        }
    }

    @Test
    void testWriteToTheEnd() {
        byte[] bytes = "Hello, world".getBytes(StandardCharsets.UTF_8);
        int i = 0;
        for (var connection : connections) {
            assertDoesNotThrow(() -> connection.writeToTheEnd(bytes));
            try {
                assertEquals(Arrays.toString(bytes),
                        Arrays.toString(connection.read(connection.getSize() - bytes.length, bytes.length)), "file: " + (i + 1));
            } catch (IOException e) {
                e.printStackTrace();
            }
            i++;
        }
    }

    @Test
    void close() {
        for (var file : files) {
            assertDoesNotThrow(() -> {
                connections.add(Connection.connectByFile(file));
            }, "Creating by file object");
        }
        for (var connection : connections) {
            assertDoesNotThrow(connection::close, "closing connection");
        }
    }
}