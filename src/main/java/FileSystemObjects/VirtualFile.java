package FileSystemObjects;

import AdditionalStructures.CustomMath;
import AdditionalStructures.FileCoordinate;
import ProjectSettings.ReadPropertyFile;

import java.io.IOException;
import java.util.Arrays;

public class VirtualFile extends FileSystemObject {
    private FileCoordinate coordinate;
    private byte[] data;

    private void fixDataLength() throws IOException {
        long blockSize = ReadPropertyFile.getBlockSize();
        int newSize = Math.toIntExact(CustomMath.closest(data.length, blockSize));
        data = Arrays.copyOf(data, newSize);
    }

    protected VirtualFile(byte[] data, String name, FileSystemObject parent, FileCoordinate coordinate) throws IOException {
        super.name = name;
        this.data = data;
        fixDataLength();
        this.coordinate = coordinate;
        this.parent = parent;
    }

    public byte[] getData() {
        return data;
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

    @Override
    public void export(String path) {
    }
}
