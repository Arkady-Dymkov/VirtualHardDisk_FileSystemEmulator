package AdditionalStructures;

/**
 * Coordinate of file in diskFile
 * @param startBlock Block from start of file
 * @param length length in blocks of file
 */
public record FileCoordinate(long startBlock, int length) {
}
