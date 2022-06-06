/*
 * Copyright (c) 2022. Arkady Dymkov townhospis<townhospis@gmail.com>
 */

package AdditionalStructures;

/**
 * Class with custom math functions
 */
public final class CustomMath {

    /**
     * Returns the number, that heighten then "number", and disclosed on "multiple"
     *
     * @param number   The number increase
     * @param multiple The number to disclose on
     * @return closest number
     */
    public static long closest(long number, long multiple) {
        return (long) (Math.ceil((double) number / multiple) * multiple);
    }
}
