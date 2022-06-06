/*
 * Copyright (c) 2022. Arkady Dymkov townhospis<townhospis@gmail.com>
 */

package AdditionalStructures;

import Connections.Connector;
import FileSystem.FileSystem;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * The type Saver.
 */
public final class Saver {
    private static final int informationByteLength = 120;
    private static final String tmpFileName = "serialized";

    private Saver() {
    }

    /**
     * Serialize file system to tmp file
     * @param fileSystem file system to serialize
     * @throws IOException if saving to tmp file fails
     */
    private static void saveToTmp(FileSystem fileSystem) throws IOException {
        FileOutputStream fileOutputStream
                = new FileOutputStream(tmpFileName);
        ObjectOutputStream objectOutputStream
                = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(fileSystem);
        objectOutputStream.flush();
        objectOutputStream.close();
    }

    /**
     * Get bytes from object byte [ ]. By pushing it to tmp and reads it
     * @param Object object to get bytes from
     * @return the byte [ ]
     * @throws IOException the io exception
     */
    public static byte[] getBytesFromObject(Object o) throws IOException {
        FileOutputStream fileOutputStream
                = new FileOutputStream(tmpFileName);
        ObjectOutputStream objectOutputStream
                = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(o);
        objectOutputStream.flush();
        objectOutputStream.close();
        byte[] result = Files.readAllBytes(Paths.get(tmpFileName));
        deleteTmp();
        return result;
    }

    /**
     * Delete temporary file
     */
    private static void deleteTmp() {
        //noinspection ResultOfMethodCallIgnored
        new File(tmpFileName).delete();
    }

    /**
     * Save bytes to tmp.
     *
     * @param bytes the bytes
     * @throws IOException the io exception
     */
    public static void saveBytesToTmp(byte[] bytes) throws IOException {
        File tmp = new File(tmpFileName);
        FileOutputStream outputStream = new FileOutputStream(tmp);
        outputStream.write(bytes);
    }

    /**
     * Gets object from tmp.
     *
     * @return the object from tmp
     * @throws IOException            the io exception
     * @throws ClassNotFoundException the class not found exception
     */
    public static Object getObjectFromTmp() throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream
                = new FileInputStream(tmpFileName);
        ObjectInputStream objectInputStream
                = new ObjectInputStream(fileInputStream);
        Object object = objectInputStream.readObject();
        objectInputStream.close();
        deleteTmp();
        return object;
    }

    /**
     * Save file system.
     *
     * @param fileSystem the file system
     * @param connector  the connector
     * @throws IOException the io exception
     */
    public static void saveFileSystem(FileSystem fileSystem, Connector connector) throws IOException {
        saveToTmp(fileSystem);
        byte[] bytes = Files.readAllBytes(Paths.get(tmpFileName));
        deleteTmp();
        connector.close(bytes);
    }

    /**
     * Load file system.
     *
     * @param path the path
     * @return the file system
     * @throws IOException            the io exception
     * @throws ClassNotFoundException the class not found exception
     */
    public static FileSystem loadFileSystem(String path) throws IOException, ClassNotFoundException {
        return new Connector.Builder(Paths.get(path)).build().restoreFileSystem(informationByteLength);
    }
}
