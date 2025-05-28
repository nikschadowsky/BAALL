package de.nikschadowsky.baall.compiler.semantic.type;

import de.nikschadowsky.baall.compiler.semantic.attribute.Scope;
import de.nikschadowsky.baall.compiler.symbol.NoSuchFieldException;
import de.nikschadowsky.baall.compiler.symbol.OutOfScopeException;
import de.nikschadowsky.baall.compiler.symbol.TypeAlreadyExistsException;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.IdentifierNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.ScopeElevationNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.IdentifierTypeNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.PrimitiveTypeNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.StructDefinitionLiteralNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.nikschadowsky.baall.compiler._utility.AstTestBuilder.*;
import static de.nikschadowsky.baall.compiler._utility.BaseAssertion.assertThat;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Fail.fail;

class TypeTableTest {

    private final Scope main = Scope.create(Scope.ROOT);
    private final Scope minor = Scope.createLogicalScope(main);
    private final Scope childMajor = Scope.create(minor);
    private final StructDefinitionLiteralNode structDefinition =
            structDefinitionLiteral(field(primitiveType(PrimitiveTypeNode.Kind.NUMBER), "a"));


    /**
     * unit under test
     */
    private TypeTable typeTable;

    @BeforeEach
    void setUp() {
        typeTable = new TypeTable(imports());
    }

    @Test
    void registerType() {
        assertThatCode(
                () -> typeTable.registerType(
                        identifier("type1"),
                        main,
                        TypeReference.Kind.EXCEPTION,
                        structDefinition
                )
        ).doesNotThrowAnyException();
        assertThatThrownBy(
                () -> typeTable.registerType(
                        identifier("type1"),
                        minor,
                        TypeReference.Kind.EXCEPTION,
                        structDefinition
                )
        ).isInstanceOf(TypeAlreadyExistsException.class);
        assertThatThrownBy(
                () -> typeTable.registerType(
                        identifier("type1"),
                        main,
                        TypeReference.Kind.STRUCT,
                        structDefinition
                )
        ).isInstanceOf(TypeAlreadyExistsException.class);

        assertThatCode(
                () -> typeTable.registerType(
                        identifier("type2"),
                        minor,
                        TypeReference.Kind.STRUCT,
                        structDefinition
                )
        ).doesNotThrowAnyException();
        assertThatThrownBy(
                () -> typeTable.registerType(
                        identifier("type2"),
                        main,
                        TypeReference.Kind.STRUCT,
                        structDefinition
                )
        ).isInstanceOf(TypeAlreadyExistsException.class);
    }

    @Test
    void hasTypeRegistered() throws TypeAlreadyExistsException {
        typeTable.registerType(identifier("type1"), main, TypeReference.Kind.STRUCT, structDefinition);

        assertThat(typeTable).hasTypeRegistered("type1", main);
        assertThat(typeTable).hasTypeRegistered("type1", minor);

        assertThat(typeTable).doesNotHaveTypeRegistered("type2", main);

        typeTable.registerType(identifier("type2"), minor, TypeReference.Kind.STRUCT, structDefinition);
        assertThat(typeTable).hasTypeRegistered("type2", minor);
        assertThat(typeTable).hasTypeRegistered("type2", main);
    }

    @Test
    void resolveType() throws TypeAlreadyExistsException {
        IdentifierNode identifier1 = identifier("identifier1");
        IdentifierNode identifier2 = identifier("identifier2");

        IdentifierTypeNode identifierType1NoneSafe = identifierType(identifier1, true);
        IdentifierTypeNode identifierType1 = identifierType(identifier1, true);

        typeTable.registerType(identifier1, main, TypeReference.Kind.STRUCT, structDefinition);
        assertThat(typeTable.resolveType(identifierType1NoneSafe, main)).isStruct();
        assertThat(typeTable.resolveType(identifierType1NoneSafe, minor)).isStruct();
        assertThat(typeTable.resolveType(identifierType1, minor)).isStruct();
        assertThat(typeTable.resolveType(identifierType1, childMajor)).isStruct();
        assertThat(typeTable.resolveType(identifierType1, Scope.ROOT)).isUnknown()
                                                                      .hasReasonContaining("No type");

        IdentifierTypeNode identifierType2 = identifierType(identifier2, true);
        assertThat(typeTable.resolveType(identifierType2, Scope.ROOT)).isUnknown();

        // single scope elevation
        ScopeElevationNode scopeElevationNode = scopeElevation(identifier1);
        IdentifierTypeNode scopeElevationIdentifierType = identifierType(scopeElevationNode, true);
        assertThat(typeTable.resolveType(scopeElevationIdentifierType, main)).isStruct();
        assertThat(typeTable.resolveType(scopeElevationIdentifierType, minor)).isStruct();
        assertThat(typeTable.resolveType(scopeElevationIdentifierType, childMajor)).isUnknown()
                                                                                   .hasReasonContaining("No type");
        assertThat(typeTable.resolveType(scopeElevationIdentifierType, Scope.ROOT)).isUnknown()
                                                                                   .hasReasonContaining("No type");

        // double scope elevation
        IdentifierTypeNode scopeElevationIdentifierType2 = identifierType(scopeElevation(scopeElevationNode), false);
        assertThat(typeTable.resolveType(scopeElevationIdentifierType2, childMajor)).isStruct();
        assertThat(typeTable.resolveType(scopeElevationIdentifierType2, main)).isUnknown()
                                                                              .hasReasonContaining("No type");
        assertThat(typeTable.resolveType(scopeElevationIdentifierType2, minor)).isUnknown()
                                                                               .hasReasonContaining("No type");
        assertThat(typeTable.resolveType(scopeElevationIdentifierType2, Scope.ROOT)).isUnknown()
                                                                                    .hasReasonContaining("No type");
        assertThatThrownBy(() -> typeTable.resolveType(scopeElevationIdentifierType2, Scope.IMPORTS)).isInstanceOf(OutOfScopeException.class);

        IdentifierTypeNode identifierType3 = identifierType(indexedAccess(identifier1, numberLiteral("1")), false);

        assertThat(typeTable.resolveType(identifierType3, main)).isUnknown().hasReasonContaining("Type is dynamic");

        // namespace reference
        fail("Namespace reference and type import is not implemented yet");
    }

    @Test
    void hasField() throws TypeAlreadyExistsException {
        IdentifierNode identifierNode = identifier("identifier");

        typeTable.registerType(identifierNode, main, TypeReference.Kind.STRUCT, structDefinition);
        TypeReference typeReference = typeTable.resolveType(identifierType(identifierNode, true), main);

        assertThat(typeReference).hasField(typeTable, identifier("a"))
                                 .doesNotHaveField(typeTable, identifier("invalid"));
    }

    @Test
    void getComponentType() throws TypeAlreadyExistsException, NoSuchFieldException {
        IdentifierNode identifierNode = identifier("identifier");

        typeTable.registerType(identifierNode, main, TypeReference.Kind.STRUCT, structDefinition);
        TypeReference typeReference = typeTable.resolveType(identifierType(identifierNode, true), main);

        assertThat(typeTable.getComponentType(typeReference, identifier("a"))).isPrimitive();
    }
}