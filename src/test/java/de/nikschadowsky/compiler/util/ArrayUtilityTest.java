package de.nikschadowsky.compiler.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * File created on 14.01.2024
 */
public class ArrayUtilityTest {

    @Test
    void testTrimArrayToMaxLength() {

        // larger array, notnull elements
        Object[] arr1 = new Object[10];
        Arrays.fill(arr1, new Object());

        Object[] trimmed1 = ArrayUtility.trimArrayToMaxLength(arr1, 5);
        assertEquals(5, trimmed1.length);

        IntStream.range(0, trimmed1.length).forEach(i -> assertEquals(arr1[i], trimmed1[i]));

        // larger array, null elements
        Class<?>[] arr2 = new Class[10];
        Arrays.fill(arr2, null);

        Class<?>[] trimmed2 = ArrayUtility.trimArrayToMaxLength(arr2, 3);

        IntStream.range(0, trimmed2.length).forEach(i -> assertEquals(arr2[i], trimmed2[i]));

        // smaller array, notnull elements
        Integer[] integer = {1, 2, 3};

        assertArrayEquals(integer, ArrayUtility.trimArrayToMaxLength(integer, integer.length + 10));
    }

    @Test
    void testSubarray() {

        Integer[] arr1 = new Integer[10];
        IntStream.range(0, 10).forEach(i -> arr1[i] = i);

        assertArrayEquals(arr1, ArrayUtility.subarray(arr1,0, arr1.length));

        assertArrayEquals(Arrays.stream(new int[]{1,2,3}).boxed().toArray(), ArrayUtility.subarray(arr1, 1,4));

        assertArrayEquals(new Integer[0], ArrayUtility.subarray(arr1, 1,1));

        assertThrows(IllegalArgumentException.class, () -> ArrayUtility.subarray(arr1, 1,0));
    }
}
