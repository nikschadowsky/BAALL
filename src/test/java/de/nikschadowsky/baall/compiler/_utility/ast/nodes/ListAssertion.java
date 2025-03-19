package de.nikschadowsky.baall.compiler._utility.ast.nodes;


import de.nikschadowsky.baall.compiler._utility.BaseAssertion;

import java.util.List;
import java.util.function.Function;

/**
 * @since 12.02.2025
 */
public class ListAssertion<T> extends BaseAssertion<ListAssertion<T>, List<T>> {

    public ListAssertion(List<T> actual) {
        super(actual, ListAssertion.class);
    }

    public ListAssertion<T> hasSize(int expectedSize) {
        return baseAssert("element count", List::size, expectedSize);
    }

    public <S extends BaseAssertion<S, T>> ListAssertion<T> hasElementMatching(int index, Function<T, S> assertionCreator, NodeAssertionBuilder<S> a) {
        a.assertThat(assertionCreator.apply(actual.get(index)).withFailMessage("Element does not match"));
        return this;
    }

    public ListAssertion<T> isEmpty() {
        return hasSize(0);
    }
}
