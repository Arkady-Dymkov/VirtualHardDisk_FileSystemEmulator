/*
 * Copyright (c) 2022. Arkady Dymkov townhospis<townhospis@gmail.com>
 */

package FileSystem;

import AdditionalStructures.Saver;
import Connections.Connector;

import java.io.IOException;
import java.io.Serializable;

@SuppressWarnings("ClassCanBeRecord")
public class FileSystem implements Serializable {
    private final Connector connector;
    private final VirtualFolder rootFolder;

    protected Connector getConnector() {
        return connector;
    }

    protected FileSystem(Connector connector, VirtualFolder rootFolder) {
        this.connector = connector;
        this.rootFolder = rootFolder;
    }

    public VirtualFolder getRootFolder() {
        return rootFolder;
    }

    public void save() throws IOException {
        Saver.saveFileSystem(this, connector);
    }

    public static FileSystem openExisted(String path) throws IOException, ClassNotFoundException {
        return Saver.loadFileSystem(path);
    }
}
