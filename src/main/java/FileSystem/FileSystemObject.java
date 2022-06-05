package FileSystem;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;

public abstract class FileSystemObject implements Serializable {
    // TODO: Implement
    protected FileSystem filesystem;

    // Name of the file system object
    protected String name;

    // file system object parent
    protected VirtualFolder parent;

    protected FileSystemObject(String name, VirtualFolder parent) {
        this.name = name;
        this.parent = parent;
    }


    public static VirtualFile createFileFromExistingFile(File file) throws IOException {
        return new VirtualFile(Files.readAllBytes(file.toPath()),
                file.getName(), null);
    }

    public static VirtualFile createFileByName(String name) throws IOException {
        return new VirtualFile(null, name, null);
    }


    public static VirtualFolder createFolder(String name) {
        return new VirtualFolder(name);
    }

    // TODO: When folder creating , the name should be created too. Folder must be with name

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FileSystemObject getParent() {
        return parent;
    }

    public void setParent(VirtualFolder parent) {
        this.parent = parent;
    }

    public abstract void export(String path);

    public abstract void delete() throws Exception;
}
