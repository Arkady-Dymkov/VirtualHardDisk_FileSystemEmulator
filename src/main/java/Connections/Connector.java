package Connections;

import AdditionalStructures.CustomMath;
import AdditionalStructures.FileCoordinate;
import AdditionalStructures.FileSystemTreeInformation;
import AdditionalStructures.Saver;
import FileSystem.FileSystem;
import FileSystem.FileSystemObject;
import FileSystem.VirtualFile;
import ProjectSettings.ReadPropertyFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * This class is responsible for transient transformations
 * before writing and deleting information using a connection.
 * It also adds several important methods, using a combination
 * of methods from the connection class
 */
public class Connector {
    // Connection to the fileSystemFile
    private final Connection connection;

    // Size of the blocks in the fileSystemFile
    private int blockSize;

    /**
     * Creates a new Connector instance.
     *
     * @param blockSize size of the blocks in the fileSystemFile
     * @param diskFile  File that will be used as filesystem file
     * @throws IOException if it is not possible to create a new Connection instance
     */
    private Connector(int blockSize, File diskFile) throws IOException {
        this.blockSize = blockSize;
        connection = Connection.connectByFile(diskFile);
    }

    // TODO: Move here fixeSize function

    /**
     * Required to securely create a new connector
     */
    public static class Builder {
        // Size of the block in the file system file
        private int blockSize;

        // File for the file that will be used to save file system file
        private final File diskFile;


        /**
         * Constructor of the builder
         *
         * @param diskPath Path to the file that will be used to save file system file. If the file doesn't exist, it will be created'
         * @throws IOException It will throw an IOException exception if the file cannot be created.
         */
        public Builder(Path diskPath) throws IOException {
            this.diskFile = diskPath.toFile();
            if (!this.diskFile.exists()) {
                Files.createFile(diskPath);
            }
            this.blockSize = -1;
        }

        /**
         * Sets new block size
         *
         * @param blockSize new block size
         * @return this builder instance
         */
        public Builder setBlockSize(int blockSize) {
            if (blockSize <= 0 || blockSize % 32 != 0) {
                throw new IllegalArgumentException("Block size must be more than zero and multiple of 32");
            }
            this.blockSize = blockSize;
            return this;
        }

        // TODO: Write restore method

        /**
         * Returns the block size. If the block size isn't specified, returns the default blockSize value from properties
         *
         * @return Block size
         * @throws IOException if it isn't possible to read the properties file
         */
        private int getBlockSize() throws IOException {
            if (blockSize == -1) {
                blockSize = ReadPropertyFile.getBlockSize();
            }
            return blockSize;
        }

        /**
         * Creates a new Connector instance
         *
         * @return Connector instance
         * @throws IOException if the block size cannot be loaded from properties file.
         */
        public Connector build() throws IOException {
            return new Connector(this.getBlockSize(), diskFile);
        }
    }


    /**
     * The length of the file should be The file size must be correctly divided into blocks.
     * This function adds empty bytes to the file to equalize the file size.
     */
    private void fixDataLength(VirtualFile file) {
        int newSize = Math.toIntExact(CustomMath.closest(file.getData().length, blockSize));
        file.setData(Arrays.copyOf(file.getData(), newSize));
    }


    /**
     * Adds a new file to the file system container
     *
     * @param file The virtual file whose contents you want to save
     * @throws IOException if the file cannot be added to the file system container
     */
    public void addFile(VirtualFile file) throws IOException {
        long dataLength = file.getData().length;
        if (dataLength % blockSize != 0) {
            fixDataLength(file);
        }
        file.setCoordinate(new FileCoordinate(
                connection.writeToTheEnd(file.getData()) / blockSize,
                Math.toIntExact(dataLength) / blockSize));
        file.setData(null);
    }

    /**
     * Removes the contents of a virtual file from a file system container
     *
     * @param file File to be removed
     * @throws IOException if this is impossible to remove the virtual file
     */
    public void removeFile(VirtualFile file) throws IOException {
        long startByte = file.getCoordinate().startBlock() * blockSize;
        int length = file.getCoordinate().length() * blockSize;
        connection.remove(startByte, length);
        file.setCoordinate(null);
    }

    /**
     * Modifies a previously loaded file in a file system container
     *
     * @param changedFile File with changed data
     * @throws IOException if impossible to remove or add file to the container
     */
    public void changeFile(VirtualFile changedFile) throws IOException {
        // First it removes the file from the container
        this.removeFile(changedFile);

        // Second it adds the file to the container again
        this.addFile(changedFile);
    }

    /**
     * Unloads file data from the file system
     *
     * @param file The file whose content is to be uploaded
     * @throws IOException if it is not possible to read information from the container
     */
    public void getFileBody(VirtualFile file) throws IOException {
        file.setData(this.connection.read(
                file.getCoordinate().startBlock() * blockSize,
                file.getCoordinate().length() * blockSize));
    }


    public void close(byte[] savedTree) throws IOException {
        VirtualFile file = FileSystemObject.createFileByName("system");
        file.setData(savedTree);
        this.addFile(file);
        FileSystemTreeInformation info = new FileSystemTreeInformation();
        info.setCountOfBlocks(file.getCoordinate().length() / blockSize);
        info.setBlockSize(this.blockSize);
        info.setControlSum(Math.toIntExact(this.connection.getSize()));
        this.connection.writeToTheEnd(Saver.getBytesFromObject(info));
        this.connection.close();
    }

    public FileSystem restoreFileSystem(int lengthInfoBlock) throws IOException, ClassNotFoundException {
        Saver.saveBytesToTmp(this.connection.read(connection.getSize() - lengthInfoBlock, lengthInfoBlock));
        this.connection.remove(connection.getSize() - lengthInfoBlock, lengthInfoBlock);
        FileSystemTreeInformation info = (FileSystemTreeInformation) Saver.getObjectFromTmp();
        this.blockSize = info.getBlockSize();
        if (info.getControlSum() != this.connection.getSize()) {
            throw new IOException("Control sum does not match. It can means that file was broken");
        }
        Saver.saveBytesToTmp(this.connection.read(
                connection.getSize() - (long) info.getCountOfBlocks() * blockSize,
                info.getCountOfBlocks() * blockSize)
        );
        FileSystem fileSystem = (FileSystem) Saver.getObjectFromTmp();
        this.connection.remove(connection.getSize() - (long) info.getCountOfBlocks() * blockSize,
                info.getCountOfBlocks() * blockSize);
        return fileSystem;
    }
}