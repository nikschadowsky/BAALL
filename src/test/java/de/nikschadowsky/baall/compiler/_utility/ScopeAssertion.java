package de.nikschadowsky.baall.compiler._utility;


import de.nikschadowsky.baall.compiler.semantic.attribute.Scope;

/**
 * @since 03.02.2025
 */
public class ScopeAssertion extends BaseAssertion<ScopeAssertion, Scope> {

    protected ScopeAssertion(Scope actual) {
        super(actual, ScopeAssertion.class);
    }

    public ScopeAssertion canAccess(Scope other) {
        return truthinessAssert(
                v -> v + "cannot but should be able to access " + other + "!",
                scope -> scope.canAccess(other)
        );
    }

    public ScopeAssertion cannotAccess(Scope other) {
        return falsenessAssert(
                v -> v + "can but should not be able to access " + other + "!",
                scope -> scope.canAccess(other)
        );
    }
}
