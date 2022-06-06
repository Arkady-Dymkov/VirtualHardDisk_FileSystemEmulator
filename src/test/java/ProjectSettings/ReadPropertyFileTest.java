package ProjectSettings;

import TestingMetrics.ResourcesChecker;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;


/**
 * The type Read property file test.
 */
class ReadPropertyFileTest {
    /**
     * The Read properties.
     */
    static FileInputStream readProperties;
    /**
     * To Write to properties.
     */
    static PrintWriter writeToProperties;
    /**
     * The Resources checker.
     */
    static ResourcesChecker resourcesChecker;


    /**
     * Before all.
     */
    @BeforeAll
    static void beforeAll() {
        readProperties = assertDoesNotThrow(() -> new FileInputStream("src/main/java/config.properties"),
                "File to check couldn't be loaded to read correct answer. Test failed.");
        writeToProperties = assertDoesNotThrow(() -> new PrintWriter("src/main/java/config.properties"),
                "File to check couldn't be loaded to write new test value. Test failed.");
        resourcesChecker = new ResourcesChecker();
        System.out.println("Memory before tests: " + resourcesChecker.getMemory());
        resourcesChecker.startTimeCounting();
    }

    /**
     * After all.
     */
    @AfterAll
    static void afterAll() {
        assertDoesNotThrow(() -> readProperties.close(),
                "File to check couldn't be closed. Test failed.");
        assertDoesNotThrow(() -> writeToProperties.print(("blockSize=" + 32)),
                "File to check couldn't be restore to default value. Test failed.");
        assertDoesNotThrow(() -> writeToProperties.close(),
                "File to check couldn't be closed. Test failed.");
        System.out.println("Memory after tests: " + resourcesChecker.getMemory());
        System.out.println("Time for tests: " + resourcesChecker.endTimeCounting() + " milliseconds");
    }

    /**
     * Write new value to properties.
     *
     * @param newProperties the new properties
     */
    void writeNewValueToProperties(long newProperties) {
        writeToProperties = assertDoesNotThrow(() -> new PrintWriter("src/main/java/config.properties"),
                "File to check couldn't be loaded to write new test value. Test failed.");
        assertDoesNotThrow(() ->
                        writeToProperties.print("blockSize=" + newProperties),
                "Test value couldn't be written to properties. Test failed.");
        writeToProperties.close();
    }

    /**
     * Read correct block size.
     */
    @Test
    void readCorrectBlockSize() {
        for (int newValue = 32; newValue < 100_000; newValue += 32) {
            writeNewValueToProperties(newValue);
            ReadPropertyFile.reloadInstance();
            int methodResult = assertDoesNotThrow(ReadPropertyFile::getBlockSize,
                    "Property cannot be read on test " + newValue + ", test number " + newValue / 32);
            assertEquals(newValue, methodResult, "Tested Value: " + newValue);
        }
    }

    /**
     * Read missing value.
     */
    @Test
    void readMissingValue() {
        writeToProperties = assertDoesNotThrow(() -> new PrintWriter("src/main/java/config.properties"),
                "File to check couldn't be loaded to write new test value. Test failed.");
        assertDoesNotThrow(() ->
                        writeToProperties.print("blockSize="),
                "Test value couldn't be written to properties. Test failed.");
        writeToProperties.close();
        ReadPropertyFile.reloadInstance();
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                ReadPropertyFile::getBlockSize,
                "Property cannot be read on test with no value in property");
        assertEquals("Block size must be more than zero and multiple of 32 and in int range",
                thrown.getMessage(), "No value in property file");

        writeToProperties = assertDoesNotThrow(() -> new PrintWriter("src/main/java/config.properties"),
                "File to check couldn't be loaded to write new test value. Test failed.");
        assertDoesNotThrow(() ->
                        writeToProperties.print(""),
                "Test value couldn't be written to properties. Test failed.");
        writeToProperties.close();
        ReadPropertyFile.reloadInstance();
        int methodResult = assertDoesNotThrow(ReadPropertyFile::getBlockSize,
                "Property cannot be read on test no properties in file");
        assertEquals(-1, methodResult, "No Property in property file");
    }

    /**
     * Read not multiples.
     */
    @Test
    void readNotMultiples() {
        for (int newValue = 1; newValue < 1_000; newValue++) {
            if (newValue % 32 == 0) {
                continue;
            }
            writeNewValueToProperties(newValue);
            ReadPropertyFile.reloadInstance();
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                    ReadPropertyFile::getBlockSize,
                    "Property cannot be read on test " + newValue + ", test number " + newValue);
            assertEquals("Block size must be more than zero and multiple of 32 and in int range",
                    thrown.getMessage());
        }
    }

    /**
     * Read negative numbers.
     */
    @Test
    void readNegativeNumbers() {
        for (int newValue = 0; newValue > -1_000; newValue--) {
            writeNewValueToProperties(newValue);
            ReadPropertyFile.reloadInstance();
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                    ReadPropertyFile::getBlockSize,
                    "Property cannot be read on test " + newValue + ", test number " + newValue * (-1));
            assertEquals("Block size must be more than zero and multiple of 32 and in int range",
                    thrown.getMessage());
        }
    }

    /**
     * Read non int number.
     */
    @Test
    void readNonIntNumber() {
        long positiveValue = 2_147_483_699L;
        writeNewValueToProperties(positiveValue);
        ReadPropertyFile.reloadInstance();
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                ReadPropertyFile::getBlockSize,
                "Property cannot be read on test " + positiveValue);
        assertEquals("Block size must be more than zero and multiple of 32 and in int range",
                thrown.getMessage(), "wrong message was thrown");

        long negativeValue = -2_147_483_699L;
        writeNewValueToProperties(positiveValue);
        ReadPropertyFile.reloadInstance();
        thrown = assertThrows(IllegalArgumentException.class,
                ReadPropertyFile::getBlockSize,
                "Property cannot be read on test " + negativeValue);
        assertEquals("Block size must be more than zero and multiple of 32 and in int range",
                thrown.getMessage(), "wrong message was thrown");
    }
}