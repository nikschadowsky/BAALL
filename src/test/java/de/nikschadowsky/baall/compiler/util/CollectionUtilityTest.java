package de.nikschadowsky.baall.compiler.util;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class CollectionUtilityTest {

    @Test
    void duplicates() {
        List<Integer> ints = List.of(1, 2, 3, 5, 4, 2);
        List<Integer> foundDuplicateInts = CollectionUtility.duplicates(ints, Function.identity());
        assertThat(foundDuplicateInts).containsExactly(2);

        List<TestRecord> elements = List.of(
                new TestRecord("Test1", 1),
                new TestRecord("Test2", 2),
                new TestRecord("Test3", 3),
                new TestRecord("Test4", 1),
                new TestRecord("Test5", 1)
        );
        List<Integer> foundDuplicateTestRecords = CollectionUtility.duplicates(elements, TestRecord::age);
        assertThat(foundDuplicateTestRecords).containsExactly(1,1);
    }

    private record TestRecord(String name, int age) {
    }
}
