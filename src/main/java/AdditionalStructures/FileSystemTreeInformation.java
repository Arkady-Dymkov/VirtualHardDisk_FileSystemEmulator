/*
 * Copyright (c) 2022. Arkady Dymkov townhospis<townhospis@gmail.com>
 */

package AdditionalStructures;

import java.io.Serializable;

public class FileSystemTreeInformation implements Serializable {
    private int countOfBlocks;
    private int blockSize;
    private int controlSum;

    public int getCountOfBlocks() {
        return countOfBlocks;
    }

    public void setCountOfBlocks(int countOfBlocks) {
        this.countOfBlocks = countOfBlocks;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public int getControlSum() {
        return controlSum;
    }

    public void setControlSum(int controlSum) {
        this.controlSum = controlSum;
    }
}