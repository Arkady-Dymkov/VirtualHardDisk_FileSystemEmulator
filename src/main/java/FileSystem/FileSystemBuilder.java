/*
 * Copyright (c) 2022. Arkady Dymkov townhospis<townhospis@gmail.com>
 */

package FileSystem;

import Connections.Connector;
import ProjectSettings.ReadPropertyFile;

import java.io.IOException;
import java.nio.file.Path;

/**
 *It's a file system builder. Allows you to securely create and set all file system options.
 */
public class FileSystemBuilder {
    private final Path fileSystemPath;
    private int blockSize;
    private final VirtualFolder rootFolder;

    /**
     * Returns new FileSystemBuilder instance
     * @param pathToDiskCreate Path to the file that will be used for saving the file system
     */
    public FileSystemBuilder(Path pathToDiskCreate) {
        fileSystemPath = pathToDiskCreate;
        this.rootFolder = FileSystemObject.createFolder("ROOT");
        try {
            this.blockSize = ReadPropertyFile.getBlockSize();
        } catch (IOException e) {
            this.blockSize = -1;
        }
    }

    public FileSystemBuilder setBlockSize(int blockSize) {
        this.blockSize = blockSize;
        return this;
    }

    public FileSystemBuilder setRootFolderName(String name){
        this.rootFolder.setName(name);
        return this;
    }

    public FileSystem build() throws IOException {
        Connector connector;
        FileSystem fileSystem;
        if(this.blockSize != -1){
            connector = new Connector.Builder(fileSystemPath).setBlockSize(this.blockSize).build();
            fileSystem = new FileSystem(connector, this.rootFolder);
            this.rootFolder.setFilesystem(fileSystem);
            fileSystem.getConnector().setFileSystem(fileSystem);
            return fileSystem;
        }
        fileSystem = new FileSystem(new Connector.Builder(this.fileSystemPath).build(), this.rootFolder);
        this.rootFolder.setFilesystem(fileSystem);
        fileSystem.getConnector().setFileSystem(fileSystem);
        return fileSystem;
    }
}
