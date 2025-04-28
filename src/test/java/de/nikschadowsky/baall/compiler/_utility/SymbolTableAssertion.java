package de.nikschadowsky.baall.compiler._utility;


import de.nikschadowsky.baall.compiler.semantic.attribute.Scope;
import de.nikschadowsky.baall.compiler.symbol.NoSymbolFoundException;
import de.nikschadowsky.baall.compiler.symbol.SymbolTable;
import de.nikschadowsky.baall.compiler.symbol.SymbolTableIdentifier;

/**
 * @since 29.03.2025
 */
public class SymbolTableAssertion extends BaseAssertion<SymbolTableAssertion, SymbolTable> {

    protected SymbolTableAssertion(SymbolTable actual) {
        super(actual, SymbolTableAssertion.class);
    }

    public SymbolTableAssertion hasSymbolRegistered(SymbolTableIdentifier identifier, Scope scope) {
        return truthinessAssert(
                a -> "Symbol '%s' is not registered in scope '%s'".formatted(identifier, scope),
                a -> a.hasSymbolRegisteredInScope(identifier, scope)
        );
    }

    public SymbolTableAssertion doesNotHaveSymbolRegistered(SymbolTableIdentifier identifier, Scope scope) {
        return falsenessAssert(
                a -> "Symbol '%s' is registered in scope '%s'".formatted(identifier, scope),
                a -> a.hasSymbolRegisteredInScope(identifier, scope)
        );
    }

    public SymbolTableAssertion isConstant(SymbolTableIdentifier identifier, Scope scope) {
        return truthinessAssert(
                a -> "Symbol '%s' in scope '%s' is not constant".formatted(identifier, scope),
                a -> {
                    try {
                        return a.isConstant(identifier, scope);
                    } catch (NoSymbolFoundException e) {
                        throw failure(e.getMessage());
                    }
                }
        );
    }

    public SymbolTableAssertion isNotConstant(SymbolTableIdentifier identifier, Scope scope) {
        return falsenessAssert(
                a -> "Symbol '%s' in scope '%s' is constant".formatted(identifier, scope),
                a -> {
                    try {
                        return a.isConstant(identifier, scope);
                    } catch (NoSymbolFoundException e) {
                        throw failure(e.getMessage());
                    }
                }
        );
    }

    /*public SymbolTableAssertion hasTypeForSymbol(String identifier, Scope scope, BaallType expectedType) {
        return baseAssert("symbol type", a -> a.resolveSymbol(identifier, scope), expectedType);
    }*/
}
