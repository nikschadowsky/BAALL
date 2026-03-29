package de.nikschadowsky.baall.compiler.symbol;

import de.nikschadowsky.baall.compiler._utility.SymbolTableTestcaseBuilder;
import de.nikschadowsky.baall.compiler.semantic.attribute.Scope;
import de.nikschadowsky.baall.compiler.semantic.type.*;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.IdentifierNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.ImportsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.PrimitiveTypeNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.nikschadowsky.baall.compiler._utility.AstTestBuilder.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;

/**
 * @since 26.03.2025
 */
class SymbolTableTest {

    private TypeTableMock typeTable;
    private BaallTypeFactory typeFactory;

    private PrimitiveType primitiveType1;
    private PrimitiveType primitiveType2;
    private FunctionType functionType1;
    private FunctionType functionType2;
    private ListType listType1;

    private TypeReference typeReference1;
    private UserDefinedType userType1;

    private final Scope mainScope = Scope.create(Scope.ROOT);
    private final Scope minorChildA = Scope.createLogicalScope(mainScope);
    private final Scope minorChildB = Scope.createLogicalScope(mainScope);
    private final Scope majorChildA = Scope.create(minorChildA);
    private final Scope minorGrandchildOfA = Scope.createLogicalScope(majorChildA);

    private final LineInformation lineInformation1 = new LineInformation(0, 0);

    // unit under test
    private SymbolTable symbolTable;

    private SymbolTableTestcaseBuilder testcaseBuilder;

    @BeforeEach
    void setUp() {
        typeTable = new TypeTableMock(imports());
        typeFactory = new BaallTypeFactory(typeTable);

        testcaseBuilder = new SymbolTableTestcaseBuilder(typeFactory);

        typeReference1 = mock(TypeReference.class);

        primitiveType1 = typeFactory.createPrimitiveType(PrimitiveTypeNode.Kind.STRUCT);
        primitiveType2 = typeFactory.createPrimitiveType(PrimitiveTypeNode.Kind.NUMBER);
        functionType1 = typeFactory.createFunctionType(primitiveType1, List.of());
        functionType2 = typeFactory.createFunctionType(functionType1, List.of(primitiveType1));
        listType1 = typeFactory.createListType(functionType1);
        userType1 = typeFactory.createUserDefinedType(typeReference1, false);

        typeTable.registerComponent(typeReference1, identifier("a"), primitiveType1);


        symbolTable = new SymbolTable(typeFactory);
    }

    @Test
    void registerFunction() throws SymbolAlreadyExistsException {
        testcaseBuilder.whenConstantFunctionIsRegistered(identifier("function1"), mainScope, functionType1)
                       .andWhenConstantFunctionIsRegistered(identifier("function2"), minorChildA, functionType1)
                       .andWhenConstantFunctionIsRegistered(identifier("function2"), minorChildB, functionType1)
                       .thenConstantFunctionCanBeRegistered(identifier("function1"), majorChildA, functionType1)
                       .thenConstantFunctionCanBeRegistered(identifier("function2"), majorChildA, functionType1)
                       .thenConstantFunctionCannotBeRegistered(identifier("function1"), mainScope, functionType1)
                       .thenConstantFunctionCannotBeRegistered(identifier("function1"), minorChildA, functionType1)
                       .thenConstantFunctionCannotBeRegistered(identifier("function2"), minorChildA, functionType1)
                       .andWhenConstantFunctionIsRegistered(identifier("function3"), minorGrandchildOfA, functionType1)
                       .thenConstantFunctionCannotBeRegistered(identifier("function3"), minorGrandchildOfA, functionType1);

        testcaseBuilder.whenSymbolIsRegistered(identifier("identifier"), minorChildB, primitiveType1, false)
                       .thenConstantFunctionCannotBeRegistered(identifier("identifier"), mainScope, functionType1)
                       .thenConstantFunctionCanBeRegistered(identifier("identifier"), minorChildA, functionType1)
                       .thenVariableFunctionCanBeRegistered(identifier("identifier"), mainScope, functionType1);

        testcaseBuilder.whenVariableFunctionIsRegistered(identifier("function1"), mainScope, functionType1)
                       .thenConstantFunctionCannotBeRegistered(identifier("function1"), minorChildA, functionType1)
                       .andWhenVariableFunctionIsRegistered(identifier("function2"), minorChildA, functionType1)
                       .thenConstantFunctionCannotBeRegistered(identifier("function2"), mainScope, functionType1)
                       .thenVariableFunctionCanBeRegistered(identifier("function2"), mainScope, functionType1);
    }

    @Test
    void registerSymbol() throws SymbolAlreadyExistsException {
        testcaseBuilder.whenSymbolIsRegistered(identifier("symbol1"), mainScope, primitiveType1, true)
                       .thenSymbolCannotBeRegistered(identifier("symbol1"), mainScope, primitiveType1, true)
                       .andWhenSymbolIsRegistered(identifier("symbol2"), minorChildA, primitiveType1, true)
                       .thenSymbolCanBeRegistered(identifier("symbol2"), minorChildB, primitiveType1, true)
                       .thenSymbolCanBeRegistered(identifier("symbol2"), majorChildA, primitiveType1, true)
                       .thenSymbolCannotBeRegistered(identifier("symbol1"), minorChildA, primitiveType1, true)
                       .thenSymbolCannotBeRegistered(identifier("symbol2"), minorChildA, primitiveType1, true);

        // the order of registering affects the result
        testcaseBuilder.whenSymbolIsRegistered(identifier("symbol3"), minorGrandchildOfA, primitiveType1, true)
                       .thenSymbolCanBeRegistered(identifier("symbol3"), majorChildA, primitiveType1, true);

        testcaseBuilder.whenConstantFunctionIsRegistered(identifier("function"), minorChildB, functionType1)
                       .thenSymbolCanBeRegistered(identifier("function"), minorChildA, functionType1, false)
                       .thenSymbolCanBeRegistered(identifier("function"), mainScope, primitiveType1, false);
    }

    @Test
    void hasSymbolRegistered() throws SymbolAlreadyExistsException {
        symbolTable.registerSymbol(identifier("symbol1"), mainScope, primitiveType1, true, lineInformation1);
        assertThat(symbolTable.canDeclareVariableInScope(identifier("symbol1"), minorChildB)).isFalse();
        assertThat(symbolTable.canDeclareVariableInScope(identifier("symbol1"), majorChildA)).isTrue();

        symbolTable.registerSymbol(identifier("symbol2"), minorChildA, primitiveType1, true, lineInformation1);
        assertThat(symbolTable.canDeclareVariableInScope(identifier("symbol2"), minorChildA)).isFalse();
        assertThat(symbolTable.canDeclareVariableInScope(identifier("symbol2"), mainScope)).isTrue();
        assertThat(symbolTable.canDeclareVariableInScope(identifier("symbol2"), minorChildB)).isTrue();
        assertThat(symbolTable.canDeclareVariableInScope(identifier("symbol2"), majorChildA)).isTrue();

        symbolTable.registerFunction(identifier("function1"), mainScope, functionType1, true, lineInformation1);
        assertThat(symbolTable.canDeclareVariableInScope(identifier("function1"), mainScope)).isFalse();
        assertThat(symbolTable.canDeclareVariableInScope(identifier("function1"), minorChildA)).isFalse();
        assertThat(symbolTable.canDeclareVariableInScope(identifier("function1"), majorChildA)).isTrue();

        symbolTable.registerFunction(identifier("function2"), minorChildA, functionType1, true, lineInformation1);
        assertThat(symbolTable.canDeclareVariableInScope(identifier("function2"), minorChildA)).isFalse();
        assertThat(symbolTable.canDeclareVariableInScope(identifier("function2"), mainScope)).isTrue();
        assertThat(symbolTable.canDeclareVariableInScope(identifier("function2"), minorChildB)).isTrue();
        assertThat(symbolTable.canDeclareVariableInScope(identifier("function2"), majorChildA)).isTrue();

        symbolTable.registerFunction(identifier("function3"), minorChildA, functionType1, false, lineInformation1);
        assertThat(symbolTable.canDeclareVariableInScope(identifier("function3"), minorChildA)).isFalse();
        assertThat(symbolTable.canDeclareVariableInScope(identifier("function3"), mainScope)).isTrue();
    }

    @Test
    void canDeclareFunctionInScope() throws SymbolAlreadyExistsException {
        symbolTable.registerFunction(identifier("function1"), mainScope, functionType1, true, lineInformation1);

        assertThat(symbolTable.canDeclareFunctionInScope(identifier("function1"), mainScope, true)).isFalse();
        assertThat(symbolTable.canDeclareFunctionInScope(identifier("function1"), minorChildA, true)).isFalse();
        assertThat(symbolTable.canDeclareFunctionInScope(identifier("function1"), majorChildA, true)).isTrue();

        symbolTable.registerFunction(identifier("function2"), minorChildA, functionType1, true, lineInformation1);
        assertThat(symbolTable.canDeclareFunctionInScope(identifier("function2"), minorChildA, true)).isFalse();
        assertThat(symbolTable.canDeclareFunctionInScope(identifier("function2"), mainScope, true)).isFalse();
        assertThat(symbolTable.canDeclareFunctionInScope(identifier("function2"), minorChildB, true)).isTrue();
        assertThat(symbolTable.canDeclareFunctionInScope(identifier("function2"), majorChildA, true)).isTrue();

        symbolTable.registerFunction(identifier("function3"), minorChildA, functionType1, false, lineInformation1);
        assertThat(symbolTable.canDeclareFunctionInScope(identifier("function3"), minorChildA, true)).isFalse();
        assertThat(symbolTable.canDeclareFunctionInScope(identifier("function3"), mainScope, true)).isFalse();
        assertThat(symbolTable.canDeclareFunctionInScope(identifier("function3"), mainScope, false)).isTrue();

        symbolTable.registerSymbol(identifier("identifier1"), minorChildA, functionType1, false, lineInformation1);
        assertThat(symbolTable.canDeclareFunctionInScope(identifier("identifier1"), minorChildA, true)).isFalse();
        assertThat(symbolTable.canDeclareFunctionInScope(identifier("identifier1"), mainScope, true)).isFalse();
        assertThat(symbolTable.canDeclareFunctionInScope(identifier("identifier1"), mainScope, false)).isTrue();

        symbolTable.registerSymbol(identifier("identifier2"), mainScope, primitiveType1, true, lineInformation1);
        assertThat(symbolTable.canDeclareFunctionInScope(identifier("identifier2"), mainScope, true)).isFalse();
        assertThat(symbolTable.canDeclareFunctionInScope(identifier("identifier2"), mainScope, false)).isFalse();
        assertThat(symbolTable.canDeclareFunctionInScope(identifier("identifier2"), minorChildB, true)).isFalse();
    }

    @Test
    void isConstant() throws SymbolAlreadyExistsException, NoSymbolFoundException {
        symbolTable.registerFunction(identifier("constant function"), mainScope, functionType1, true, lineInformation1);
        assertThat(symbolTable.isConstant(identifier("constant function"), mainScope)).isTrue();

        symbolTable.registerFunction(identifier("variable function"), minorChildA, functionType1, false, lineInformation1);
        assertThat(symbolTable.isConstant(identifier("variable function"), minorChildA)).isFalse();

        symbolTable.registerSymbol(identifier("constant symbol"), mainScope, primitiveType1, true, lineInformation1);
        assertThat(symbolTable.isConstant(identifier("constant symbol"), mainScope)).isTrue();

        symbolTable.registerSymbol(identifier("variable symbol"), minorChildA, primitiveType1, false, lineInformation1);
        assertThat(symbolTable.isConstant(identifier("variable symbol"), minorChildA)).isFalse();

        assertThatThrownBy(() -> symbolTable.isConstant(identifier("constant function"), minorChildA)).isInstanceOf(NoSymbolFoundException.class);
    }

    @Test
    void resolveElement() throws SymbolAlreadyExistsException {
        testcaseBuilder.whenNoSymbolIsRegistered()
                       .thenResolving(identifier("not declared"), mainScope).resultsInUnknown();

        testcaseBuilder.whenSymbolIsRegistered(identifier("identifier"), mainScope, primitiveType1, true)
                       .thenResolving(identifier("identifier"), mainScope)
                       .matchesConstant(primitiveType1)
                       .thenResolving(identifier("identifier"), minorChildA)
                       .matchesConstant(primitiveType1)
                       .thenResolving(identifier("identifier"), minorGrandchildOfA)
                       .matchesConstant(primitiveType1)
                       .thenResolving(identifier("not declared"), mainScope)
                       .resultsInUnknown();

        testcaseBuilder.whenSymbolIsRegistered(identifier("identifier"), minorChildA, primitiveType1, false)
                       .thenResolving(identifier("identifier"), mainScope)
                       .resultsInUnknown()
                       .thenResolving(identifier("identifier"), minorChildB)
                       .resultsInUnknown()
                       .thenResolving(identifier("identifier"), minorChildA).matchesVariable(primitiveType1);


        testcaseBuilder.whenSymbolIsRegistered(identifier("identifier"), minorChildB, primitiveType1, false)
                       .thenResolving(identifier("identifier"), majorChildA)
                       .resultsInUnknown();

        testcaseBuilder.whenSymbolIsRegistered(identifier("identifier"), minorChildA, primitiveType1, false)
                       .andWhenConstantFunctionIsRegistered(identifier("constant function"), mainScope, functionType1)
                       .andWhenVariableFunctionIsRegistered(identifier("variable function"), mainScope, functionType1)
                       .thenResolving(identifier("constant function"), minorChildA)
                       .matchesConstant(functionType1)
                       .thenResolving(identifier("constant function"), minorChildB)
                       .matchesConstant(functionType1)
                       .thenResolving(identifier("variable function"), minorChildA)
                       .resultsInUnknown() // no hoisting on var functions
                       .thenResolving(identifier("variable function"), mainScope);

        testcaseBuilder.whenSymbolIsRegistered(identifier("identifier"), mainScope, primitiveType1, false)
                       .andWhenSymbolIsRegistered(identifier("identifier"), majorChildA, primitiveType2, false)
                       .thenResolving(identifier("identifier"), mainScope)
                       .matchesVariable(primitiveType1)
                       .thenResolving(identifier("identifier"), minorGrandchildOfA)
                       .matchesVariable(primitiveType2);


        testcaseBuilder.whenSymbolIsRegistered(identifier("identifier"), mainScope, primitiveType1, false)
                       .thenResolving(scopeElevation(identifier("identifier")), minorChildA)
                       .matchesVariable(primitiveType1)
                       .thenResolving(scopeElevation(identifier("identifier")), mainScope)
                       .matchesVariable(primitiveType1)
                       .thenResolving(scopeElevation(scopeElevation(identifier("identifier"))), mainScope)
                       .resultsInUnknown()
                       .thenResolving(scopeElevation(identifier("identifier")), Scope.ROOT)
                       .resultsInUnknown();

        testcaseBuilder.whenSymbolIsRegistered(identifier("identifier"), mainScope, userType1, false)
                       .thenResolving(memberReference(identifier("identifier"), identifier("a")), mainScope)
                       .matchesVariable(primitiveType1)
                       .thenResolving(scopeElevation(memberReference(identifier("identifier"), identifier("a"))), mainScope)
                       .matchesVariable(primitiveType1)
                       .thenResolving(scopeElevation(scopeElevation(memberReference(identifier("identifier"), identifier("a")))), majorChildA)
                       .matchesVariable(primitiveType1)
                       .thenResolving(scopeElevation(scopeElevation(memberReference(identifier("identifier"), identifier("a")))), mainScope)
                       .resultsInUnknown();

        testcaseBuilder.whenConstantFunctionIsRegistered(identifier("function"), minorChildA, functionType1)
                       .thenResolving(identifier("function"), minorChildB)
                       .resultsInUnknown()
                       .thenResolving(identifier("function"), mainScope)
                       .resultsInUnknown()
                       .thenResolving(identifier("function"), majorChildA)
                       .matchesConstant(functionType1);

        testcaseBuilder.whenSymbolIsRegistered(identifier("list"), mainScope, listType1, true)
                .thenResolving(identifier("list"), minorChildA)
                .matchesConstant(listType1)
                .thenResolving(scopeElevation(scopeElevation(identifier("list"))), majorChildA)
                .matchesConstant(listType1)
                .thenResolving(indexedAccess(identifier("list"), numberLiteral("1")), majorChildA)
                .matchesVariable(functionType1);

    }

    private static class TypeTableMock extends TypeTable {

        private final Map<TypeReference, HashMap<IdentifierNode, BaallType>> components = new HashMap<>();

        public TypeTableMock(ImportsNode imports) {
            super(imports);
        }

        public void registerComponent(TypeReference typeReference, IdentifierNode identifierNode, BaallType baallType) {
            components.putIfAbsent(typeReference, new HashMap<>());
            components.get(typeReference).put(identifierNode, baallType);
        }

        @Override
        public BaallType getComponentType(TypeReference type, IdentifierNode field) throws NoSuchTypeException, NoSuchFieldException {
            HashMap<IdentifierNode, BaallType> identifierNodeBaallTypeHashMap = components.get(type);

            if (identifierNodeBaallTypeHashMap == null) {
                throw new NoSuchTypeException("No such type: " + type);
            }
            BaallType baallType = identifierNodeBaallTypeHashMap.get(field);
            if (baallType == null) {
                throw new NoSuchFieldException("No such field: " + field);
            }
            return baallType;
        }

        @Override
        public boolean hasField(TypeReference type, IdentifierNode field) throws NoSuchTypeException {
            HashMap<IdentifierNode, BaallType> identifierNodeBaallTypeHashMap = components.get(type);
            if (identifierNodeBaallTypeHashMap == null) {
                throw new NoSuchTypeException("No such type: " + type);
            }
            return identifierNodeBaallTypeHashMap.containsKey(field);
        }
    }

}