package de.nikschadowsky.baall.compiler._utility.ast.nodes;


import de.nikschadowsky.baall.compiler._utility.BaseAssertion;

/**
 * @since 16.03.2025
 */
public interface NodeAssertionBuilder<ASSERTION extends BaseAssertion<ASSERTION, ?>> {

    void assertThat(ASSERTION assertion);

}
