package Connections;

import AdditionalStructures.FileCoordinate;
import FileSystemObjects.VirtualFile;
import ProjectSettings.ReadPropertyFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

public class Connector {
    private Connection connection;
    private final int blockSize;

    private Connector(int blockSize, File diskFile) throws IOException {
        this.blockSize = blockSize;
        connection = Connection.connectByFile(diskFile);
    }

    public static class Builder {
        private int blockSize;
        private Path diskPath;

        public Builder(Path diskPath) {
            this.diskPath = diskPath;
            this.blockSize = -1;
        }

        public void setBlockSize(int blockSize) {
            if (blockSize <= 0 || blockSize % 32 != 0) {
                throw new IllegalArgumentException("Block size must be more than zero and multiple of 32");
            }
            this.blockSize = blockSize;
        }

        public Connector build() throws IOException {
            if (blockSize == -1) {
                blockSize = ReadPropertyFile.getBlockSize();
            }
            if (blockSize == -1) {
                throw new IllegalArgumentException("Property couldn't found. " +
                        "Create a new property in properties file or setProperty in builder'");
            }
            return new Connector(blockSize, diskPath.toFile());
        }
    }

    public void addFile(VirtualFile file) throws IOException {
        long dataLength = file.getData().length;
        if (dataLength % blockSize != 0) {
            throw new IllegalArgumentException("The length of the information " +
                    "must be divisible into blocks of " + blockSize + " bytes.");
        }
        file.setCoordinate(new FileCoordinate(connection.writeToTheEnd(file.getData()),
                Math.toIntExact(dataLength)));
    }

    public boolean removeFile(FileCoordinate coordinate) {
        return false;
    }

    public FileCoordinate changeFile(FileCoordinate coordinate, byte[] newFile) {
        return null;
    }

    public VirtualFile getFileBody(FileCoordinate coordinate) {
        return null;
    }

    // TODO: Arguments are - FileSystemTree. It will be saved
    public void close() {

    }
}