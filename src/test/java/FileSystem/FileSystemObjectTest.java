package FileSystem;

import AdditionalStructures.FileSystemTreeInformation;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FileSystemObjectTest {

    @Test
    void createFileFromExistingFile() {
    }

    @Test
    void createFileByName() {
    }

    @Test
    void createFolder() {
    }

    @Test
    void getName() {
    }

    @Test
    void setName() {
    }

    @Test
    void getParent() {
    }

    @Test
    void setParent() {
    }

    @Test
    void save() throws IOException, ClassNotFoundException {
        FileSystemTreeInformation info = new FileSystemTreeInformation();
        info.setControlSum(2_147_483_647);
        info.setCountOfBlocks(2_147_483_647);
        info.setBlockSize(2_147_483_647);
        Object o = info;
        FileOutputStream fileOutputStream
                = new FileOutputStream("serialized");
        ObjectOutputStream objectOutputStream
                = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(o);
        objectOutputStream.flush();
        objectOutputStream.close();

        System.out.println(Files.readAllBytes(Paths.get("serialized")).length);

        /*
        FileInputStream fileInputStream
                = new FileInputStream("serialized.txt");
        ObjectInputStream objectInputStream
                = new ObjectInputStream(fileInputStream);
        VirtualFolder rootFolder2 = (VirtualFolder) objectInputStream.readObject();
        objectInputStream.close();
         */
    }
}