/*
 * Copyright (c) 2022. Arkady Dymkov townhospis<townhospis@gmail.com>
 */

package FileSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Folder in the virtual file system
 */
public class VirtualFolder extends FileSystemObject {

    /**
     * The Children of the virtual folder.
     */
    private final List<FileSystemObject> children;


    /**
     * Instantiates a new Virtual folder.
     *
     * @param name the name
     */
    protected VirtualFolder(String name) {
        super(name, null);
        this.children = new ArrayList<>();
    }

    /**
     * Moving file system object in this folder
     *
     * @param fileSystemObject the file system object
     */
    public void moveHere(FileSystemObject fileSystemObject) {
        if (fileSystemObject.parent != null) {
            fileSystemObject.parent.children.remove(fileSystemObject);
        }
        if (fileSystemObject instanceof VirtualFolder) {
            ((VirtualFolder) fileSystemObject).children.remove(this);
        }
        fileSystemObject.setParent(this);
        this.children.add(fileSystemObject);
        fileSystemObject.setFilesystem(this.filesystem);
    }

    /**
     * Trying to delete folder. If the folder is not empty, throws exception.
     * To delete full folder use hard delete.
     *
     * @throws Exception folder does not empty
     */
    @Override
    public void delete() throws Exception {
        if (this.children == null || this.getChildren().size() == 0) {
            if (parent != null) {
                this.parent.getChildren().remove(this);
            }
            this.parent = null;
            this.filesystem = null;
            return;
        }
        throw new Exception("Folder should be empty do delete it." +
                "You can use hard Delete if you want to delete it anyway.");
    }

    /**
     * Delete folder and all children in it.
     *
     * @throws Exception the exception if deleting the folder fails
     */
    public void hardDelete() throws Exception {
        List<FileSystemObject> childrenToDelete = new ArrayList<>(this.children);
        for (var child : childrenToDelete) {
            if (child instanceof VirtualFolder) {
                ((VirtualFolder) child).hardDelete();
            } else {
                child.delete();
            }
        }
        this.delete();
    }

    public List<FileSystemObject> getChildren() {
        return this.children;
    }
}
