package FileSystemObjects;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public abstract class FileSystemObject {
    protected String name;
    protected FileSystemObject parent;

    public static VirtualFile createFile(File file) throws IOException {
        return new VirtualFile(Files.readAllBytes(file.toPath()),
                file.getName(), null, null);
    }

    public abstract void export(String path);
}
