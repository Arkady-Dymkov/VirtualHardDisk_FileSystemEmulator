/*
 * Copyright (c) 2022. Arkady Dymkov townhospis<townhospis@gmail.com>
 */

package AdditionalStructures;

import java.io.Serializable;

/**
 * Coordinate of file in diskFile
 * @param startBlock Block from start of file
 * @param length length in blocks of file
 */
public record FileCoordinate(long startBlock, int length) implements Serializable {
}
