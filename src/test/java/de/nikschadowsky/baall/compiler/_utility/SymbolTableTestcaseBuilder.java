package de.nikschadowsky.baall.compiler._utility;

import de.nikschadowsky.baall.compiler.semantic.attribute.Scope;
import de.nikschadowsky.baall.compiler.semantic.type.BaallType;
import de.nikschadowsky.baall.compiler.semantic.type.BaallTypeFactory;
import de.nikschadowsky.baall.compiler.semantic.type.FunctionType;
import de.nikschadowsky.baall.compiler.symbol.LineInformation;
import de.nikschadowsky.baall.compiler.symbol.Symbol;
import de.nikschadowsky.baall.compiler.symbol.SymbolTable;
import de.nikschadowsky.baall.compiler.symbol.SymbolAlreadyExistsException;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.ElementAccessNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.IdentifierNode;

import static de.nikschadowsky.baall.compiler._utility.BaseAssertion.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SymbolTableTestcaseBuilder {

    private final BaallTypeFactory typeFactory;

    private SymbolTable symbolTable;

    private static final LineInformation LINE_INFORMATION = new LineInformation(1, 1);

    public SymbolTableTestcaseBuilder(BaallTypeFactory typeFactory) {
        this.typeFactory = typeFactory;
    }

    public SymbolTableTestcaseBuilder whenConstantFunctionIsRegistered(IdentifierNode identifier, Scope scope, FunctionType type) throws SymbolAlreadyExistsException {
        symbolTable = new SymbolTable(typeFactory);
        return andWhenConstantFunctionIsRegistered(identifier, scope, type);
    }

    public SymbolTableTestcaseBuilder whenVariableFunctionIsRegistered(IdentifierNode identifier, Scope scope, FunctionType type) throws SymbolAlreadyExistsException {
        symbolTable = new SymbolTable(typeFactory);
        return andWhenVariableFunctionIsRegistered(identifier, scope, type);
    }

    public SymbolTableTestcaseBuilder whenSymbolIsRegistered(IdentifierNode identifier, Scope scope, BaallType type, boolean isConstant) throws SymbolAlreadyExistsException {
        symbolTable = new SymbolTable(typeFactory);
        return andWhenSymbolIsRegistered(identifier, scope, type, isConstant);
    }

    public SymbolTableTestcaseBuilder andWhenSymbolIsRegistered(IdentifierNode identifier, Scope scope, BaallType type, boolean isConstant) throws SymbolAlreadyExistsException {
        symbolTable.registerSymbol(identifier, scope, type, isConstant, LINE_INFORMATION);
        return this;
    }

    public SymbolTableTestcaseBuilder andWhenConstantFunctionIsRegistered(IdentifierNode identifier, Scope scope, FunctionType type) throws SymbolAlreadyExistsException {
        symbolTable.registerFunction(identifier, scope, type, true, LINE_INFORMATION);
        return this;
    }

    public SymbolTableTestcaseBuilder andWhenVariableFunctionIsRegistered(IdentifierNode identifier, Scope scope, FunctionType type) throws SymbolAlreadyExistsException {
        symbolTable.registerFunction(identifier, scope, type, false, LINE_INFORMATION);
        return this;
    }

    public SymbolTableTestcaseBuilder whenNoSymbolIsRegistered() {
        symbolTable = new SymbolTable(typeFactory);
        return this;
    }

    public SymbolTableTestcaseBuilder thenSymbolCannotBeRegistered(IdentifierNode identifier, Scope scope, BaallType type, boolean isConstant) {
        assertThatThrownBy(() -> symbolTable.registerSymbol(identifier, scope, type, isConstant, LINE_INFORMATION)).withFailMessage("Symbol '%s' was unexpectedly allowed to be registered by this method!".formatted(identifier))
                                                                                                                   .isInstanceOf(SymbolAlreadyExistsException.class);
        return this;
    }

    public SymbolTableTestcaseBuilder thenConstantFunctionCannotBeRegistered(IdentifierNode identifier, Scope scope, FunctionType type) {
        assertThatThrownBy(() -> symbolTable.registerFunction(identifier, scope, type, true, LINE_INFORMATION)).withFailMessage("Constant function '%s' was unexpectedly allowed to be registered by this method!".formatted(identifier))
                                                                                                               .isInstanceOf(SymbolAlreadyExistsException.class);
        return this;
    }

    public SymbolTableTestcaseBuilder thenVariableFunctionCannotBeRegistered(IdentifierNode identifier, Scope scope, FunctionType type) {
        assertThatThrownBy(() -> symbolTable.registerFunction(identifier, scope, type, false, LINE_INFORMATION)).withFailMessage("Variable function '%s' was unexpectedly allowed to be registered by this method!".formatted(identifier))
                                                                                                                .isInstanceOf(SymbolAlreadyExistsException.class);
        return this;
    }

    public SymbolTableTestcaseBuilder thenSymbolCanBeRegistered(IdentifierNode identifier, Scope scope, BaallType type, boolean isConstant) {
        assertThatCode(() -> symbolTable.registerSymbol(identifier, scope, type, isConstant, LINE_INFORMATION)).withFailMessage("Symbol '%s' was unexpectedly disallowed to be registered by this method!".formatted(identifier))
                                                                                                               .doesNotThrowAnyException();
        return this;
    }

    public SymbolTableTestcaseBuilder thenConstantFunctionCanBeRegistered(IdentifierNode identifier, Scope scope, FunctionType type) {
        assertThatCode(() -> symbolTable.registerFunction(identifier, scope, type, true, LINE_INFORMATION)).withFailMessage("Constant function '%s' was unexpectedly disallowed to be registered by this method!".formatted(identifier))
                                                                                                           .doesNotThrowAnyException();
        return this;
    }

    public SymbolTableTestcaseBuilder thenVariableFunctionCanBeRegistered(IdentifierNode identifier, Scope scope, FunctionType type) {
        assertThatCode(() -> symbolTable.registerFunction(identifier, scope, type, false, LINE_INFORMATION)).withFailMessage("Variable function '%s' was unexpectedly disallowed to be registered by this method!".formatted(identifier))
                                                                                                            .doesNotThrowAnyException();
        return this;
    }

    public ResolutionMatcher thenResolving(ElementAccessNode element, Scope scope) {
        return new ResolutionMatcher(this, symbolTable.resolveElement(element, scope));
    }

    public static class ResolutionMatcher {

        private final SymbolTableTestcaseBuilder caller;
        private final Symbol resolved;

        private ResolutionMatcher(SymbolTableTestcaseBuilder caller, Symbol resolved) {
            this.caller = caller;
            this.resolved = resolved;
        }

        public SymbolTableTestcaseBuilder matches(BaallType type, boolean isConstant) {
            return isConstant ? matchesConstant(type) : matchesVariable(type);
        }

        public SymbolTableTestcaseBuilder matchesConstant(BaallType type) {
            assertThat(resolved).isKnown().isConstant().hasTypeEquals(type);
            return caller;
        }

        public SymbolTableTestcaseBuilder matchesVariable(BaallType type) {
            assertThat(resolved).isKnown().isVariable().hasTypeEquals(type);
            return caller;
        }

        public SymbolTableTestcaseBuilder resultsInUnknown() {
            assertThat(resolved).isUnknown();
            return caller;
        }

    }
}
