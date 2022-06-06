/*
 * Copyright (c) 2022. Arkady Dymkov townhospis<townhospis@gmail.com>
 */

package FileSystem;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;

public abstract class FileSystemObject implements Serializable {
    protected FileSystem filesystem;

    // Name of the file system object
    protected String name;

    // file system object parent
    protected VirtualFolder parent;

    protected void setFilesystem(FileSystem filesystem) {
        this.filesystem = filesystem;
        if(this instanceof VirtualFolder){
            for(FileSystemObject child : ((VirtualFolder) this).getChildren()){
                child.setFilesystem(this.filesystem);
            }
        }
    }

    protected FileSystemObject(String name, VirtualFolder parent) {
        this.name = name;
        this.parent = parent;
        if (parent != null) {
            this.filesystem = parent.filesystem;
        }
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


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected VirtualFolder getParent() {
        return parent;
    }

    protected void setParent(VirtualFolder parent) {
        this.parent = parent;
    }

    protected abstract void delete() throws Exception;
}
