package de.nikschadowsky.baall.compiler._utility;


import de.nikschadowsky.baall.compiler.semantic.attribute.Scope;
import de.nikschadowsky.baall.compiler.semantic.type.BaallType;
import de.nikschadowsky.baall.compiler.symbol.SymbolTable;

/**
 * @since 29.03.2025
 */
public class SymbolTableAssertion extends BaseAssertion<SymbolTableAssertion, SymbolTable> {

    protected SymbolTableAssertion(SymbolTable actual) {
        super(actual, SymbolTableAssertion.class);
    }

    public SymbolTableAssertion hasSymbolRegistered(String identifier, Scope scope) {
        return truthinessAssert(
                a -> "Symbol '%s' is not registered in scope '%s'".formatted(identifier, scope),
                a -> a.hasSymbolRegisteredInScope(identifier, scope)
        );
    }

    public SymbolTableAssertion hasTypeForSymbol(String identifier, Scope scope, BaallType expectedType) {
        return baseAssert("symbol type", a -> a.getTypeInformation(identifier, scope), expectedType);
    }
}
