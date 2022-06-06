/*
 * Copyright (c) 2022. Arkady Dymkov townhospis<townhospis@gmail.com>
 */

package FileSystem;

import AdditionalStructures.FileCoordinate;

import java.io.IOException;

/**
 * File class in virtual file system
 */
public class VirtualFile extends FileSystemObject {

    // coordinate of this virtual file in disk file
    private FileCoordinate coordinate;

    // file data
    private byte[] data;


    private VirtualFile() {
        super(null, null);
    }

    /**
     * Constructor to create new instance of file
     *
     * @param data   File data
     * @param name   File name
     * @param parent File parent (cannot be null)
     */
    protected VirtualFile(byte[] data, String name, VirtualFolder parent) {
        super(name, parent);
        this.data = data;
        this.coordinate = null;
    }

    /**
     * Returns file data
     *
     * @return File data
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Read content byte from file.
     *
     * @return the byte [ ]
     * @throws IOException the io exception if reading failed
     */
    public byte[] readContent() throws IOException {
        if (this.data != null) {
            return this.data;
        }
        this.filesystem.getConnector().getFileBody(this);
        return this.data;
    }

    /**
     * Write content to container.
     *
     * @param data the data
     * @throws IOException the io exception
     */
    public void writeContent(byte[] data) throws IOException {
        this.data = data;
        if(this.coordinate == null){
            this.filesystem.getConnector().addFile(this);
            return;
        }
        this.filesystem.getConnector().changeFile(this);
    }

    /**
     * Saved the file in DataBase
     *
     * @throws IOException the io exception if saving the file fails
     */
    public void close() throws IOException {
        this.filesystem.getConnector().changeFile(this);
    }

    /**
     * Sets data.
     *
     * @param data the data
     */
    public void setData(byte[] data) {
        this.data = data;
    }

    /**
     * Gets coordinate.
     *
     * @return the coordinate
     */
    public FileCoordinate getCoordinate() {
        return coordinate;
    }

    /**
     * Sets coordinate.
     *
     * @param coordinate the coordinate
     */
    public void setCoordinate(FileCoordinate coordinate) {
        this.coordinate = coordinate;
    }

    /**
     * Move this file to folder
     *
     * @param destinationFolder the destination folder
     */
    public void moveTo(VirtualFolder destinationFolder) {
        destinationFolder.moveHere(this);
    }

    /**
     * Append the data to the folder.
     *
     * @param newContents the new contents
     * @throws IOException the io exception if changing the file contents fails
     */
    public void append(byte[] newContents) throws IOException {
        this.filesystem.getConnector().getFileBody(this);
        byte[] added = new byte[newContents.length + this.data.length];

        //noinspection ManualArrayCopy
        for (int i = 0; i < this.data.length; i++){
            added[i] = this.data[i];
        }
        //noinspection ManualArrayCopy
        for(int i = this.data.length; i < added.length; i++){
            added[i] = newContents[i];
        }
        this.setData(added);
    }

    /**
     * Deletes this file from the tree and database
     * @throws Exception if removing from the database fails
     */
    @Override
    public void delete() throws Exception {
        this.filesystem.getConnector().removeFile(this);
        this.parent.getChildren().remove(this);
    }
}
