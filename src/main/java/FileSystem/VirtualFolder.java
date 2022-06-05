package FileSystem;

import java.util.ArrayList;
import java.util.List;

public class VirtualFolder extends FileSystemObject {
    List<FileSystemObject> children;


    protected VirtualFolder(String name) {
        super(name, null);
        this.children = new ArrayList<>();
    }

    public void moveHere(FileSystemObject fileSystemObject) {
        if (fileSystemObject.parent != null) {
            fileSystemObject.parent.children.remove(fileSystemObject);
        }
        fileSystemObject.setParent(this);
        this.children.add(fileSystemObject);
    }


    @Override
    public void export(String path) {
    }

    @Override
    public void delete() throws Exception {
        if (this.children == null || this.children.size() == 0) {
            this.parent.children.remove(this);
            this.parent = null;
            return;
        }
        throw new Exception("Folder should be empty do delete it. You can use hard Delete if you want to delete it anyway.");
    }

    public void hardDelete() throws Exception {
        for (var child : this.children) {
            if (child instanceof VirtualFolder) {
                ((VirtualFolder) child).hardDelete();
            } else {
                child.delete();
            }
        }
        this.delete();
    }
}
