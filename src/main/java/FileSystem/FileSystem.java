/*
 * Copyright (c) 2022. Arkady Dymkov townhospis<townhospis@gmail.com>
 */

package FileSystem;

import AdditionalStructures.Saver;
import Connections.Connector;

import java.io.IOException;
import java.io.Serializable;

/**
 * Main class of the FileSystem. It contains the root folder of the filesystem and information about the container;
 */
@SuppressWarnings("ClassCanBeRecord")
public class FileSystem implements Serializable {
    // Connector to the container
    private final Connector connector;

    // Root folder of the filesystem
    private final VirtualFolder rootFolder;


    /**
     * Gets connector.
     *
     * @return the connector
     */
    public Connector getConnector() {
        return connector;
    }

    protected FileSystem(Connector connector, VirtualFolder rootFolder) {
        this.connector = connector;
        this.rootFolder = rootFolder;
    }

    /**
     * Gets root folder.
     *
     * @return the root folder
     */
    public VirtualFolder getRootFolder() {
        return rootFolder;
    }

    /**
     * Save's this file system
     *
     * @throws IOException the io exception
     */
    public void save() throws IOException {
        Saver.saveFileSystem(this, connector);
    }

    /**
     * Open existed file system.
     *
     * @param path the path
     * @return the file system
     * @throws IOException            the io exception
     * @throws ClassNotFoundException the class not found exception
     */
    public static FileSystem openExisted(String path) throws IOException, ClassNotFoundException {
        return Saver.loadFileSystem(path);
    }

}
