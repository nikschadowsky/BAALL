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

    private final Scope mainScope = Scope.create(Scope.ROOT);
    private final Scope minorChildA = Scope.createLogicalScope(mainScope);
    private final Scope minorChildB = Scope.createLogicalScope(mainScope);
    private final Scope majorChildA = Scope.create(minorChildA);
    private final Scope minorGrandchildOfA = Scope.createLogicalScope(majorChildA);

    private final LineInformation lineInformation1 = new LineInformation(0, 0);
    private final LineInformation lineInformation2 = new LineInformation(0, 2);

    // unit under test
    private SymbolTable symbolTable;

    @BeforeEach
    void setUp() {
        symbolTable = new SymbolTable();
    }

    @Test
    void registerSymbolAndFunction() {
        assertThatCode(() -> symbolTable.registerSymbol(
                StringIdentifier.of("identifier"),
                mainScope,
                primitiveType,
                true,
                lineInformation1
        )).doesNotThrowAnyException();

        // registering a symbol with the same identifier in a minor scope should fail
        assertThatThrownBy(() -> symbolTable.registerSymbol(
                StringIdentifier.of("identifier"),
                minorChildA,
                primitiveType,
                false,
                lineInformation2
        )).isInstanceOf(SymbolAlreadyExistsException.class);

        // registering a function with the same identifier in a minor scope should fail
        assertThatThrownBy(() -> symbolTable.registerFunction(
                StringIdentifier.of("identifier"),
                minorChildB,
                functionType,
                true,
                lineInformation1
        )).isInstanceOf(SymbolAlreadyExistsException.class);

        // registering in another major scope should work
        assertThatCode(() -> symbolTable.registerSymbol(
                StringIdentifier.of("identifier"),
                majorChildA,
                primitiveType,
                true,
                lineInformation1
        )).doesNotThrowAnyException();

        assertThatCode(() -> symbolTable.registerSymbol(
                NestedIdentifier.of("first", "second1"),
                minorChildA,
                primitiveType,
                false,
                lineInformation1
        )).doesNotThrowAnyException();
        assertThatCode(() -> symbolTable.registerSymbol(
                NestedIdentifier.of("first", "second2"),
                minorChildA,
                primitiveType,
                false,
                lineInformation1
        )).doesNotThrowAnyException();
        assertThatCode(() -> symbolTable.registerSymbol(
                NestedIdentifier.of("first", "second1"),
                minorChildB,
                primitiveType,
                false,
                lineInformation1
        )).doesNotThrowAnyException();

        assertThatThrownBy(() -> symbolTable.registerSymbol(
                NestedIdentifier.of("first", "second1"),
                minorChildA,
                primitiveType,
                false,
                lineInformation1
        )).isInstanceOf(SymbolAlreadyExistsException.class);
    }

    @Test
    void hasSymbolRegisteredInScope() throws SymbolAlreadyExistsException {
        assertThat(symbolTable).doesNotHaveSymbolRegistered(StringIdentifier.of("identifier"), mainScope);
        symbolTable.registerSymbol(
                StringIdentifier.of("identifier"),
                mainScope,
                primitiveType,
                true,
                lineInformation1
        );

        assertThat(symbolTable).hasSymbolRegistered(StringIdentifier.of("identifier"), mainScope)
                               .hasSymbolRegistered(StringIdentifier.of("identifier"), minorChildA)
                               .hasSymbolRegistered(StringIdentifier.of("identifier"), minorChildB)
                               .doesNotHaveSymbolRegistered(StringIdentifier.of("identifier"), Scope.ROOT)
                               .doesNotHaveSymbolRegistered(StringIdentifier.of("identifier"), Scope.ROOT)
                               .doesNotHaveSymbolRegistered(StringIdentifier.of("function"), mainScope);

        symbolTable.registerFunction(
                StringIdentifier.of("function"),
                mainScope,
                functionType,
                false,
                lineInformation2
        );
        assertThat(symbolTable).hasSymbolRegistered(StringIdentifier.of("identifier"), mainScope)
                               .hasSymbolRegistered(StringIdentifier.of("function"), mainScope)
                               .hasSymbolRegistered(StringIdentifier.of("function"), minorChildA)
                               .doesNotHaveSymbolRegistered(StringIdentifier.of("identifier"), Scope.ROOT)
                               .doesNotHaveSymbolRegistered(StringIdentifier.of("identifier"), majorChildA);

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
                mainScope,
                primitiveType,
                true,
                lineInformation1
        );
        assertThat(symbolTable).hasSymbolRegistered(NestedIdentifier.of("first", "second"), minorChildA)
                               .hasSymbolRegistered(NestedIdentifier.of("first", "second"), mainScope)
                               .doesNotHaveSymbolRegistered(NestedIdentifier.of("first", "second"), majorChildA);
    }

    @Test
    void getTypeInformation() {
        fail("not yet implemented. see SymbolTable#getTypeInformation() for details");
    }

    @Test
    void isConstant() throws SymbolAlreadyExistsException {
        symbolTable.registerSymbol(
                StringIdentifier.of("main"),
                mainScope,
                primitiveType,
                true,
                lineInformation1
        );
        assertThat(symbolTable).isConstant(StringIdentifier.of("main"), mainScope);
        assertThatThrownBy(
                () -> symbolTable.isConstant(StringIdentifier.of("main"), minorChildB)
        ).isInstanceOf(NoSymbolFoundException.class);

        symbolTable.registerSymbol(
                StringIdentifier.of("identifier2"),
                majorChildA,
                primitiveType,
                false,
                lineInformation2
        );
        assertThat(symbolTable).isNotConstant(StringIdentifier.of("identifier2"), majorChildA);
        assertThatThrownBy(
                () -> symbolTable.isConstant(StringIdentifier.of("identifier2"), minorGrandchildOfA)
        ).isInstanceOf(NoSymbolFoundException.class);

        assertThatThrownBy(() -> symbolTable.isConstant(
                StringIdentifier.of("invalid"),
                mainScope
        )).isInstanceOf(NoSymbolFoundException.class);
    }

}