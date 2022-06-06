/*
 * Copyright (c) 2022. Arkady Dymkov townhospis<townhospis@gmail.com>
 */

package FileSystem;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;

/**
 * The type File system object.
 */
public abstract class FileSystemObject implements Serializable {
    /**
     * The Filesystem.
     */
    protected FileSystem filesystem;

    /**
     * Name of the file system object
     */
    protected String name;

    /**
     * file system object parent
     */
    protected VirtualFolder parent;

    /**
     * Sets filesystem to all children.
     *
     * @param filesystem the filesystem
     */
    protected void setFilesystem(FileSystem filesystem) {
        this.filesystem = filesystem;
        if(this instanceof VirtualFolder){
            for(FileSystemObject child : ((VirtualFolder) this).getChildren()){
                child.setFilesystem(this.filesystem);
            }
        }
    }

    /**
     * Instantiates a new File system object.
     *
     * @param name   the name
     * @param parent the parent
     */
    protected FileSystemObject(String name, VirtualFolder parent) {
        this.name = name;
        this.parent = parent;
        if (parent != null) {
            this.filesystem = parent.filesystem;
        }
    }

    /**
     * Create virtual file from existing file
     *
     * @param file the file
     * @return the virtual file
     * @throws IOException the io exception
     */
    public static VirtualFile createFileFromExistingFile(File file) throws IOException {
        return new VirtualFile(Files.readAllBytes(file.toPath()),
                file.getName(), null);
    }

    /**
     * Create file by name virtual file.
     *
     * @param name the name
     * @return the virtual file
     */
    public static VirtualFile createFileByName(String name) {
        return new VirtualFile(null, name, null);
    }


    /**
     * Create folder virtual folder.
     *
     * @param name the name
     * @return the virtual folder
     */
    public static VirtualFolder createFolder(String name) {
        return new VirtualFolder(name);
    }


    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets parent.
     *
     * @return the parent
     */
    public VirtualFolder getParent() {
        return parent;
    }

    /**
     * Sets parent.
     *
     * @param parent the parent
     */
    protected void setParent(VirtualFolder parent) {
        this.parent = parent;
    }

    /**
     * Delete.
     *
     * @throws Exception the exception
     */
    protected abstract void delete() throws Exception;
}
