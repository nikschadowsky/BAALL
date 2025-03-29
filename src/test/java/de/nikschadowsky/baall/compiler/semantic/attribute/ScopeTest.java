package de.nikschadowsky.baall.compiler.semantic.attribute;

import org.junit.jupiter.api.Test;

import static de.nikschadowsky.baall.compiler._utility.BaseAssertion.assertThat;

/**
 * @since 03.02.2025
 */
class ScopeTest {

    @Test
    void canAccess() {
        Scope a = Scope.create(Scope.ROOT);

        assertThat(a).canAccess(Scope.ROOT);
        assertThat(a).canAccess(a);

        Scope b = Scope.create(a);
        assertThat(b).canAccess(Scope.ROOT);
        assertThat(b).canAccess(a);
        assertThat(a).cannotAccess(b);

        Scope c = Scope.create(a);
        assertThat(c).canAccess(Scope.ROOT);
        assertThat(c).cannotAccess(b);
    }
}