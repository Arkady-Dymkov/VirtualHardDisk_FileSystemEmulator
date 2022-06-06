/*
 * Copyright (c) 2022. Arkady Dymkov townhospis<townhospis@gmail.com>
 */

package AdditionalStructures;

import java.io.Serializable;

/**
 * This class will represent information needed to restore the fileSystem from the database file
 */
public class FileSystemTreeInformation implements Serializable {
    /**
     * The number of blocks occupied by information about the structure of the file system
     */
    private int countOfBlocks;

    /**
     * size of the block in bytes
     */
    private int blockSize;

    /**
     * Control sum to check if the file system broken or not
     */
    private int controlSum;

    /**
     * Gets count of blocks.
     *
     * @return the count of blocks
     */
    public int getCountOfBlocks() {
        return countOfBlocks;
    }

    /**
     * Sets count of blocks.
     *
     * @param countOfBlocks the count of blocks
     */
    public void setCountOfBlocks(int countOfBlocks) {
        this.countOfBlocks = countOfBlocks;
    }

    /**
     * Gets block size.
     *
     * @return the block size
     */
    public int getBlockSize() {
        return blockSize;
    }

    /**
     * Sets block size.
     *
     * @param blockSize the block size
     */
    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    /**
     * Gets control sum.
     *
     * @return the control sum
     */
    public int getControlSum() {
        return controlSum;
    }

    /**
     * Sets control sum.
     *
     * @param controlSum the control sum
     */
    public void setControlSum(int controlSum) {
        this.controlSum = controlSum;
    }
}