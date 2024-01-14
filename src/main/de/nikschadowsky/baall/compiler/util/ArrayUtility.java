package de.nikschadowsky.baall.compiler.util;

import java.util.Arrays;

/**
 * File created on 14.01.2024
 */
public class ArrayUtility {

    /**
     * Copy the {@code array} argument and trim it, if its length is larger than the second argument {@code length}
     * @param array array to copy and trim
     * @param length the max length
     * @return copy of the array with maximum length
     * @param <T> type of array
     */
    public static <T> T[] trimArrayToMaxLength(T[] array, int length){
        if(array.length < length) return Arrays.copyOf(array, array.length);

        return Arrays.copyOf(array, length);
    }

}
