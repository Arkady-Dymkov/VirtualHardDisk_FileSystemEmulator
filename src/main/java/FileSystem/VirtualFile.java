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
     * TODO: Make protected
     *
     * @return File data
     */
    public byte[] getData() {
        return data;
    }

    public byte[] readContent() throws IOException {
        if (this.data != null) {
            return this.data;
        }
        this.filesystem.getConnector().getFileBody(this);
        return this.data;
    }

    public void writeContent(byte[] data) throws IOException {
        this.data = data;
        if(this.coordinate == null){
            this.filesystem.getConnector().addFile(this);
            return;
        }
        this.filesystem.getConnector().changeFile(this);
    }

    public void close() throws IOException {
        this.filesystem.getConnector().changeFile(this);
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public FileCoordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(FileCoordinate coordinate) {
        this.coordinate = coordinate;
    }

    public void moveTo(VirtualFolder destinationFolder) {
        destinationFolder.moveHere(this);
    }

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

    @Override
    public void delete() throws Exception {
        this.filesystem.getConnector().removeFile(this);
        this.parent.getChildren().remove(this);
    }
}
