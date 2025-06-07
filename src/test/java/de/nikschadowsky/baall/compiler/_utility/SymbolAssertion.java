package de.nikschadowsky.baall.compiler._utility;

import de.nikschadowsky.baall.compiler.semantic.type.BaallType;
import de.nikschadowsky.baall.compiler.symbol.Symbol;

public class SymbolAssertion extends BaseAssertion<SymbolAssertion, Symbol> {

    protected SymbolAssertion(Symbol actual) {
        super(actual, SymbolAssertion.class);
    }

    public SymbolAssertion isUnknown() {
        return truthinessAssert(
                s -> "Symbol " + s + " is not unknown!",
                Symbol::isUnknown
        );
    }

    public SymbolAssertion isKnown() {
        return falsenessAssert(
                s -> "Symbol " + s + " is not known!",
                Symbol::isUnknown
        );
    }

    public SymbolAssertion isConstant() {
        return truthinessAssert(
                s -> "Symbol " + s + " is variable!",
                Symbol::isConstant
        );
    }

    public SymbolAssertion isVariable() {
        return falsenessAssert(
                s -> "Symbol " + s + "is constant!",
                Symbol::isConstant
        );
    }

    public SymbolAssertion hasTypeEquals(BaallType expected) {
        return baseAssert("type", Symbol::type, expected);
    }

}
