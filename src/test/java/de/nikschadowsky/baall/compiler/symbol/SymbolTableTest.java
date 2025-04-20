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

    private final PrimitiveType primitiveType = BaallTypeFactory.create()
                                                                .createPrimitiveType(PrimitiveTypeNode.Kind.STRUCT);
    private final FunctionType functionType = BaallTypeFactory.create().createFunctionType(primitiveType, List.of());
    private final Scope majorScope = Scope.create(Scope.ROOT);
    private final LineInformation lineInformation1 = new LineInformation(0, 0);
    private final LineInformation lineInformation2 = new LineInformation(0, 2);

    @Test
    void registerSymbol() {
        SymbolTable symbolTable = new SymbolTable();
        assertThatCode(() -> symbolTable.registerSymbol("identifier", majorScope, primitiveType, true, lineInformation1)).doesNotThrowAnyException();

        // there is no symbol overloading
        assertThatThrownBy(() -> symbolTable.registerSymbol("identifier", majorScope, primitiveType, false, lineInformation2)).isInstanceOf(SymbolAlreadyExistsException.class);

        // ... even when you mix functions and symbols
        assertThatThrownBy(() -> symbolTable.registerFunction("identifier", majorScope, functionType, true, lineInformation1)).isInstanceOf(SymbolAlreadyExistsException.class);

        Scope subScope1 = Scope.create(majorScope);
        Scope subScope2 = Scope.create(majorScope);

        assertThatCode(() -> symbolTable.registerSymbol("identifier", subScope1, primitiveType, true, lineInformation1)).doesNotThrowAnyException();

        assertThatCode(() -> symbolTable.registerFunction("identifier", subScope2, functionType, true, lineInformation1)).doesNotThrowAnyException();
    }

    @Test
    void hasSymbolRegisteredInScope() throws SymbolAlreadyExistsException {
        SymbolTable symbolTable = new SymbolTable();
        Scope minorScope = Scope.createLogicalScope(majorScope);

        assertThat(symbolTable).doesNotHaveSymbolRegistered("identifier", Scope.ROOT);
        symbolTable.registerSymbol("identifier", majorScope, primitiveType, true, lineInformation1);

        assertThat(symbolTable).hasSymbolRegistered("identifier", majorScope)
                               .hasSymbolRegistered("identifier", minorScope)
                               .doesNotHaveSymbolRegistered("identifier", Scope.ROOT)
                               .doesNotHaveSymbolRegistered("function", majorScope);

        symbolTable.registerFunction("function", majorScope, functionType, false, lineInformation2);
        assertThat(symbolTable).hasSymbolRegistered("identifier", majorScope)
                               .hasSymbolRegistered("function", majorScope)
                               .hasSymbolRegistered("function", minorScope)
                               .doesNotHaveSymbolRegistered("identifier", Scope.ROOT);

        symbolTable.registerSymbol("identifier", Scope.ROOT, primitiveType, false, lineInformation1);
        assertThat(symbolTable).hasSymbolRegistered("identifier", Scope.ROOT);
    }

    @Test
    void testScopeHierarchy() throws SymbolAlreadyExistsException {
        SymbolTable symbolTable = new SymbolTable();
        Scope minorScope = Scope.createLogicalScope(majorScope);
        symbolTable.registerSymbol("id", majorScope, primitiveType, true, lineInformation1);

        // registering in another major should work
        assertThatCode(
                () -> symbolTable.registerSymbol("id", Scope.ROOT, primitiveType, true, lineInformation1)
        ).doesNotThrowAnyException();
        // registering in the same major should not work
        assertThatThrownBy(
                () -> symbolTable.registerSymbol("id", majorScope, functionType, true, lineInformation1)
        ).isInstanceOf(SymbolAlreadyExistsException.class);

        // registering with the same identifier in a minor scope should fail
        assertThatThrownBy(
                () -> symbolTable.registerSymbol("id", minorScope, primitiveType, false, lineInformation2)
        ).isInstanceOf(SymbolAlreadyExistsException.class);
        assertThatThrownBy(
                () -> symbolTable.registerFunction("id", minorScope, functionType, true, lineInformation2)
        ).isInstanceOf(SymbolAlreadyExistsException.class);

        symbolTable.registerSymbol("other", minorScope, primitiveType, true, lineInformation2);
        assertThatCode(() -> symbolTable.registerSymbol("other", Scope.createLogicalScope(majorScope), functionType, true, lineInformation2)).doesNotThrowAnyException();
    }

    @Test
    void getTypeInformation() {
        fail("not yet implemented. see SymbolTable#getTypeInformation() for details");
    }

    @Test
    void isConstant() throws SymbolAlreadyExistsException {
        SymbolTable symbolTable = new SymbolTable();

        symbolTable.registerSymbol("identifier", majorScope, primitiveType, true, lineInformation1);
        assertThat(symbolTable).isConstant("identifier", majorScope);

        symbolTable.registerSymbol("identifier2", majorScope, primitiveType, false, lineInformation2);
        assertThat(symbolTable).isNotConstant("identifier2", majorScope);

        assertThatThrownBy(() -> symbolTable.isConstant("invalid", majorScope)).isInstanceOf(NoSymbolFoundException.class);
    }
}