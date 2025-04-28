package de.nikschadowsky.baall.compiler.symbol;

import de.nikschadowsky.baall.compiler.semantic.attribute.Scope;
import de.nikschadowsky.baall.compiler.semantic.type.BaallTypeFactory;
import de.nikschadowsky.baall.compiler.semantic.type.FunctionType;
import de.nikschadowsky.baall.compiler.semantic.type.PrimitiveType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.PrimitiveTypeNode;
import org.junit.jupiter.api.BeforeEach;
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
    private final Scope minorScope1 = Scope.createLogicalScope(majorScope);
    private final Scope majorSubscope = Scope.create(majorScope);
    private final Scope minorScope2 = Scope.createLogicalScope(majorSubscope);
    private final LineInformation lineInformation1 = new LineInformation(0, 0);
    private final LineInformation lineInformation2 = new LineInformation(0, 2);

    private SymbolTable symbolTable;

    @BeforeEach
    void setUp() {
        symbolTable = new SymbolTable();
    }

    @Test
    void registerSymbol() {
        assertThatCode(() -> symbolTable.registerSymbol(
                StringIdentifier.of("identifier"),
                majorScope,
                primitiveType,
                true,
                lineInformation1
        )).doesNotThrowAnyException();

        // there is no symbol overloading
        assertThatThrownBy(() -> symbolTable.registerSymbol(
                StringIdentifier.of("identifier"),
                majorScope,
                primitiveType,
                false,
                lineInformation2
        )).isInstanceOf(SymbolAlreadyExistsException.class);

        // ... even when you mix functions and symbols
        assertThatThrownBy(() -> symbolTable.registerFunction(
                StringIdentifier.of("identifier"),
                majorScope,
                functionType,
                true,
                lineInformation1
        )).isInstanceOf(SymbolAlreadyExistsException.class);

        assertThatCode(() -> symbolTable.registerSymbol(
                StringIdentifier.of("identifier"),
                majorSubscope,
                primitiveType,
                true,
                lineInformation1
        )).doesNotThrowAnyException();

        assertThatCode(() -> symbolTable.registerFunction(
                StringIdentifier.of("identifier"),
                minorScope2,
                functionType,
                true,
                lineInformation1
        )).doesNotThrowAnyException();

        assertThatCode(() -> symbolTable.registerSymbol(
                NestedIdentifier.of("first", "second1"),
                majorScope,
                primitiveType,
                false,
                lineInformation1
        )).doesNotThrowAnyException();
        assertThatCode(() -> symbolTable.registerSymbol(
                NestedIdentifier.of("first", "second2"),
                majorScope,
                primitiveType,
                false,
                lineInformation1
        )).doesNotThrowAnyException();
        assertThatThrownBy(() -> symbolTable.registerSymbol(
                NestedIdentifier.of("first", "second2"),
                minorScope1,
                primitiveType,
                false,
                lineInformation1
        )).isInstanceOf(SymbolAlreadyExistsException.class);
    }

    @Test
    void hasSymbolRegisteredInScope() throws SymbolAlreadyExistsException {
        assertThat(symbolTable).doesNotHaveSymbolRegistered(StringIdentifier.of("identifier"), Scope.ROOT);
        symbolTable.registerSymbol(
                StringIdentifier.of("identifier"),
                majorScope,
                primitiveType,
                true,
                lineInformation1
        );

        assertThat(symbolTable).hasSymbolRegistered(StringIdentifier.of("identifier"), majorScope)
                               .hasSymbolRegistered(StringIdentifier.of("identifier"), minorScope1)
                               .doesNotHaveSymbolRegistered(StringIdentifier.of("identifier"), Scope.ROOT)
                               .doesNotHaveSymbolRegistered(StringIdentifier.of("function"), majorScope);

        symbolTable.registerFunction(
                StringIdentifier.of("function"),
                majorScope,
                functionType,
                false,
                lineInformation2
        );
        assertThat(symbolTable).hasSymbolRegistered(StringIdentifier.of("identifier"), majorScope)
                               .hasSymbolRegistered(StringIdentifier.of("function"), majorScope)
                               .hasSymbolRegistered(StringIdentifier.of("function"), minorScope1)
                               .doesNotHaveSymbolRegistered(StringIdentifier.of("identifier"), Scope.ROOT);

        symbolTable.registerSymbol(
                StringIdentifier.of("identifier"),
                Scope.ROOT,
                primitiveType,
                false,
                lineInformation1
        );
        assertThat(symbolTable).hasSymbolRegistered(StringIdentifier.of("identifier"), Scope.ROOT);

        symbolTable.registerSymbol(
                NestedIdentifier.of("first", "second"),
                majorScope,
                primitiveType,
                true,
                lineInformation1
        );
        assertThat(symbolTable).hasSymbolRegistered(NestedIdentifier.of("first", "second"), minorScope1);
    }

    @Test
    void testScopeHierarchy() throws SymbolAlreadyExistsException {
        symbolTable.registerSymbol(
                StringIdentifier.of("identifier"),
                majorScope,
                primitiveType,
                true,
                lineInformation1
        );

        // registering in another major should work
        assertThatCode(
                () -> symbolTable.registerSymbol(
                        StringIdentifier.of("identifier"),
                        Scope.ROOT,
                        primitiveType,
                        true,
                        lineInformation1
                )
        ).doesNotThrowAnyException();
        // registering in the same major should not work
        assertThatThrownBy(
                () -> symbolTable.registerSymbol(
                        StringIdentifier.of("identifier"),
                        majorScope,
                        functionType,
                        true,
                        lineInformation1
                )
        ).isInstanceOf(SymbolAlreadyExistsException.class);

        // registering with the same identifier in a minor scope should fail
        assertThatThrownBy(
                () -> symbolTable.registerSymbol(
                        StringIdentifier.of("identifier"),
                        minorScope1,
                        primitiveType,
                        false,
                        lineInformation2
                )
        ).isInstanceOf(SymbolAlreadyExistsException.class);
        assertThatThrownBy(
                () -> symbolTable.registerFunction(
                        StringIdentifier.of("identifier"),
                        minorScope1,
                        functionType,
                        true,
                        lineInformation2
                )
        ).isInstanceOf(SymbolAlreadyExistsException.class);

        // nested elements are allowed
        assertThatCode(() -> symbolTable.registerSymbol(
                NestedIdentifier.of("identifier", "element1"),
                majorScope,
                functionType,
                true,
                lineInformation2
        )).doesNotThrowAnyException();
        // even in minor scopes
        assertThatCode(() -> symbolTable.registerSymbol(
                NestedIdentifier.of("identifier", "element2"),
                minorScope1,
                functionType,
                true,
                lineInformation2
        )).doesNotThrowAnyException();

        symbolTable.registerSymbol(StringIdentifier.of("other"), minorScope1, primitiveType, true, lineInformation2);
        assertThatCode(() -> symbolTable.registerSymbol(
                StringIdentifier.of("other"),
                Scope.createLogicalScope(majorScope),
                functionType,
                true,
                lineInformation2
        )).doesNotThrowAnyException();
    }

    @Test
    void getTypeInformation() {
        fail("not yet implemented. see SymbolTable#getTypeInformation() for details");
    }

    @Test
    void isConstant() throws SymbolAlreadyExistsException {
        symbolTable.registerSymbol(
                StringIdentifier.of("identifier"),
                majorScope,
                primitiveType,
                true,
                lineInformation1
        );
        assertThat(symbolTable).isConstant(StringIdentifier.of("identifier"), majorScope);

        symbolTable.registerSymbol(
                StringIdentifier.of("identifier2"),
                majorScope,
                primitiveType,
                false,
                lineInformation2
        );
        assertThat(symbolTable).isNotConstant(StringIdentifier.of("identifier2"), majorScope);

        assertThatThrownBy(() -> symbolTable.isConstant(
                StringIdentifier.of("invalid"),
                majorScope
        )).isInstanceOf(NoSymbolFoundException.class);
    }
}