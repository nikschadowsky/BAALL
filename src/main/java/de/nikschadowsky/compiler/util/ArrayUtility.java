package de.nikschadowsky.compiler.util;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * File created on 14.01.2024
 */
public class ArrayUtility {

    /**
     * Copy the {@code array} argument and trim it, if its length is larger than the second argument {@code length}
     *
     * @param array  array to copy and trim
     * @param length the max length
     * @param <T>    type of array
     * @return copy of the array with maximum length
     */
    public static <T> T[] trimArrayToMaxLength(T[] array, int length) {
        if (array.length < length) return Arrays.copyOf(array, array.length);

        return Arrays.copyOf(array, length);
    }

    public static <T> T[] subarray(@NotNull T[] array, int lowerIndex, int upperIndex) {
        if (upperIndex > array.length) {
            throw new IllegalArgumentException("Upper index may not lie outside array!");
        }
        if (upperIndex == -1) {
            return Arrays.copyOfRange(array, lowerIndex, array.length);
        }

        return Arrays.copyOfRange(array, lowerIndex, upperIndex);
    }

    public static int[] range(int startInclusive, int endExclusive) {
        return IntStream.range(startInclusive, endExclusive).toArray();
    }

    public static int[] rangeClosed(int startInclusive, int endInclusive) {
        return range(startInclusive, endInclusive + 1);
    }

}
