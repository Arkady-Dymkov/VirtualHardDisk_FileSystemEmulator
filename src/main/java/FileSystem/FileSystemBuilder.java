package FileSystem;

import Connections.Connector;

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
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public void setRootFolderName(String name){
        this.rootFolder.setName(name);
    }

    public FileSystem build() throws IOException {
        Connector connector;
        if(this.blockSize != -1){
            connector = new Connector.Builder(fileSystemPath).setBlockSize(this.blockSize).build();
            return new FileSystem(connector, this.rootFolder);
        }
        return new FileSystem(new Connector.Builder(this.fileSystemPath).build(), this.rootFolder);
    }
}
