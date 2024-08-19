package de.nikschadowsky.baall.compiler.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @since 14.01.2024
 */
public class ArrayUtilityTest {

    @Test
    void testTrimArrayToMaxLength() {
        // larger array, notnull elements
        Object[] arr1 = new Object[10];
        Arrays.fill(arr1, new Object());

        Object[] trimmed1 = ArrayUtility.trimArrayToMaxLength(arr1, 5);
        assertThat(trimmed1).hasSize(5);

        IntStream.range(0, trimmed1.length).forEach(i -> assertThat(trimmed1[i]).isEqualTo(arr1[i]));

        // larger array, null elements
        Class<?>[] arr2 = new Class[10];
        Arrays.fill(arr2, null);

        Class<?>[] trimmed2 = ArrayUtility.trimArrayToMaxLength(arr2, 3);

        IntStream.range(0, trimmed2.length).forEach(i -> assertThat(trimmed2[i]).isEqualTo(arr2[i]));

        // smaller array, notnull elements
        Integer[] integer = {1, 2, 3};

        assertThat(ArrayUtility.trimArrayToMaxLength(integer, integer.length + 10)).isEqualTo(integer);
    }

    @Test
    void testSubarray() {
        Integer[] arr1 = new Integer[10];
        IntStream.range(0, 10).forEach(i -> arr1[i] = i);

        assertThat(ArrayUtility.subarray(arr1, 0, 10)).isEqualTo(arr1);
        assertThat(ArrayUtility.subarray(arr1, 1, 4)).isEqualTo(Arrays.stream(new int[]{1, 2, 3}).boxed().toArray());
        assertThat(ArrayUtility.subarray(arr1, 1, 1)).isEqualTo(new Integer[0]);
        assertThatThrownBy(() -> ArrayUtility.subarray(arr1, 1, 0)).isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
