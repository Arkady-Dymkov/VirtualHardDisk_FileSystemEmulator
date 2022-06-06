/*
 * Copyright (c) 2022. Arkady Dymkov townhospis<townhospis@gmail.com>
 */

package AdditionalStructures;

import Connections.Connector;
import FileSystem.FileSystem;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public final class Saver {
    private static final int informationByteLength = 120;
    private static final String tmpFileName = "serialized";

    private Saver() {
    }

    private static void saveToTmp(FileSystem fileSystem) throws IOException {
        FileOutputStream fileOutputStream
                = new FileOutputStream(tmpFileName);
        ObjectOutputStream objectOutputStream
                = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(fileSystem);
        objectOutputStream.flush();
        objectOutputStream.close();
    }

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

    private static void deleteTmp() {
        //noinspection ResultOfMethodCallIgnored
        new File(tmpFileName).delete();
    }

    public static void saveBytesToTmp(byte[] bytes) throws IOException {
        File tmp = new File(tmpFileName);
        FileOutputStream outputStream = new FileOutputStream(tmp);
        outputStream.write(bytes);
    }

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

    public static void saveFileSystem(FileSystem fileSystem, Connector connector) throws IOException {
        saveToTmp(fileSystem);
        byte[] bytes = Files.readAllBytes(Paths.get(tmpFileName));
        deleteTmp();
        connector.close(bytes);
    }

    public static FileSystem loadFileSystem(String path) throws IOException, ClassNotFoundException {
        return new Connector.Builder(Paths.get(path)).build().restoreFileSystem(informationByteLength);
    }
}
