package de.nikschadowsky.baall.compiler.symbol;

import de.nikschadowsky.baall.compiler.semantic.attribute.Scope;
import de.nikschadowsky.baall.compiler.semantic.type.BaallTypeFactory;
import de.nikschadowsky.baall.compiler.semantic.type.FunctionType;
import de.nikschadowsky.baall.compiler.semantic.type.PrimitiveType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.PrimitiveTypeNode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.nikschadowsky.baall.compiler._utility.BaseAssertion.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Fail.fail;

/**
 * @since 26.03.2025
 */
class SymbolTableTest {

    private final PrimitiveType primitiveType =
            BaallTypeFactory.create().createPrimitiveType(PrimitiveTypeNode.Kind.STRUCT);
    private final FunctionType functionType = BaallTypeFactory.create().createFunctionType(primitiveType, List.of());
    private final Scope scope = Scope.create(Scope.ROOT);
    private final LineInformation lineInformation1 = new LineInformation(0, 0);
    private final LineInformation lineInformation2 = new LineInformation(0, 2);

    @Test
    void registerSymbol() {
        SymbolTable symbolTable = new SymbolTable();
        assertThatCode(() -> symbolTable.registerSymbol(
                "identifier",
                scope,
                primitiveType,
                true,
                lineInformation1
        )).doesNotThrowAnyException();

        // there is no symbol overloading
        assertThatThrownBy(() -> symbolTable.registerSymbol(
                "identifier",
                scope,
                primitiveType,
                false,
                lineInformation2
        )).isInstanceOf(SymbolAlreadyExistsException.class);

        // ... even when you mix functions and symbols
        assertThatThrownBy(() -> symbolTable.registerFunction(
                "identifier",
                scope,
                functionType,
                true,
                lineInformation1
        )).isInstanceOf(SymbolAlreadyExistsException.class);

        Scope subScope1 = Scope.create(scope);
        Scope subScope2 = Scope.create(scope);

        assertThatCode(() -> symbolTable.registerSymbol(
                "identifier",
                subScope1,
                primitiveType,
                true,
                lineInformation1
        )).doesNotThrowAnyException();

        assertThatCode(() -> symbolTable.registerFunction(
                "identifier",
                subScope2,
                functionType,
                true,
                lineInformation1
        )).doesNotThrowAnyException();
    }

    @Test
    void hasSymbolRegisteredInScope() throws SymbolAlreadyExistsException {
        SymbolTable symbolTable = new SymbolTable();

        assertThat(symbolTable).doesNotHaveSymbolRegistered("identifier", Scope.ROOT);
        symbolTable.registerSymbol("identifier", scope, primitiveType, true, lineInformation1);

        assertThat(symbolTable).hasSymbolRegistered("identifier", scope)
                               .doesNotHaveSymbolRegistered("identifier", Scope.ROOT)
                               .doesNotHaveSymbolRegistered("function", scope);

        symbolTable.registerFunction("function", scope, functionType, false, lineInformation2);
        assertThat(symbolTable).hasSymbolRegistered("identifier", scope)
                               .hasSymbolRegistered("function", scope)
                               .doesNotHaveSymbolRegistered("identifier", Scope.ROOT);
    }

    @Test
    void getTypeInformation() {
        fail("not yet implemented. see SymbolTable#getTypeInformation() for details");
    }

    @Test
    void isConstant() throws SymbolAlreadyExistsException {
        SymbolTable symbolTable = new SymbolTable();

        symbolTable.registerSymbol("identifier", scope, primitiveType, true, lineInformation1);
        assertThat(symbolTable).isConstant("identifier", scope);

        symbolTable.registerSymbol("identifier2", scope, primitiveType, false, lineInformation2);
        assertThat(symbolTable).isNotConstant("identifier2", scope);

        assertThatThrownBy(() -> symbolTable.isConstant("invalid", scope)).isInstanceOf(NoSymbolFoundException.class);
    }
}