package ProjectSettings;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Singleton class for reading and get information from config file
 */
public class ReadPropertyFile {
    // Singleton instance
    private static ReadPropertyFile instance;

    // Properties file
    private final Properties properties;

    /**
     * Private constructor for singleton instance
     * @throws IOException if an I/O error occurs during initialization of ReadPropertyFile instance it means
     * that the property file couldn't be loaded
     */
    private ReadPropertyFile() throws IOException {
        properties = new Properties();
        FileInputStream ip = new FileInputStream("src/main/java/config.properties");
        properties.load(ip);
    }

    /**
     * Singleton method to initialize ReadPropertyFile or return an existed one if it exists
     * @return ReadPropertyFile instance
     * @throws IOException if property file does not exist or cannot be opened
     */
    private static ReadPropertyFile getInstance() throws IOException {
        if (instance == null) {
            instance = new ReadPropertyFile();
        }
        return instance;
    }

    /**
     * Reloads the property file
     */
    public static void reloadInstance() {
        instance = null;
    }

    /**
     * Returns the blockSIze property
     *
     * @return BlockSIze property if it exists and -1 otherwise
     * @throws IOException throws exception if ReadPropertyFile couldn't be created.
     *                     The reason is that the property file doesn't exist or couldn't be loaded
     */
    public static int getBlockSize() throws IOException {
        var property = ReadPropertyFile.getInstance().properties.getProperty("blockSize");
        if (property == null) {
            return -1;
        }
        int blockSize;
        try {
            blockSize = Integer.parseInt(property);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Block size must be more than zero " +
                    "and multiple of 32 and in int range");
        }
        if (blockSize <= 0 || blockSize % 32 != 0) {
            throw new IllegalArgumentException("Block size must be more than zero " +
                    "and multiple of 32 and in int range");
        }
        return Integer.parseInt(property);
    }
}
