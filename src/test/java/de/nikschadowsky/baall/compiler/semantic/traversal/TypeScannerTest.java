package de.nikschadowsky.baall.compiler.semantic.traversal;

import de.nikschadowsky.baall.compiler.output.error.DiagnosticCollector;
import de.nikschadowsky.baall.compiler.semantic.SemanticDiagnostic;
import de.nikschadowsky.baall.compiler.semantic.attribute.Scope;
import de.nikschadowsky.baall.compiler.symbol.SymbolTable;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.ProgramNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.List;

import static de.nikschadowsky.baall.compiler._utility.AstTestBuilder.*;
import static de.nikschadowsky.baall.compiler._utility.BaseAssertion.assertThat;
import static de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.PrimitiveTypeNode.Kind.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @since 29.03.2025
 */
class TypeScannerTest {

    private ProgramNode program;
    private MockedStatic<Scope> scopeMockedStatic;
    private final Scope first = mock(Scope.class);
    private final Scope second = mock(Scope.class);

    @BeforeEach
    void setUp() {
        scopeMockedStatic = mockStatic(Scope.class);
        when(Scope.create(Scope.ROOT)).thenCallRealMethod();
        when(Scope.create(any())).thenReturn(first, second);

        program = program(
                imports("import1", "import2"),
                statements(
                        constantDeclaration(
                                primitiveType(STRUCT),
                                "MyStruct",
                                structDefinitionLiteral(field(primitiveType(NUMBER), "field"))
                        ),
                        functionCall(identifier("function"), numberLiteral("2")),
                        whileLoop(
                                booleanLiteral("true"), statements(
                                        variableDeclaration(primitiveType(EXCEPTION), "VarException", null)
                                )
                        ),
                        variableDeclaration(listType(identifierType(identifier("myType"), false)), "MyVariable", null),
                        constantDeclaration(
                                functionType(primitiveType(STRING)),
                                "MyFunction",
                                functionDefinition(statements(
                                        constantDeclaration(
                                                primitiveType(STRUCT),
                                                "MyInnerScope",
                                                structDefinitionLiteral(field(primitiveType(STRING), "field"))
                                        ),
                                        variableDeclaration(
                                                primitiveType(STRUCT),
                                                "MyInnerScope",
                                                null
                                        )
                                ))
                        ),
                        constantDeclaration(primitiveType(EXCEPTION), "MyException", structDefinitionLiteral())
                ),
                exports("namespace", identifier("MyFunction"))
        );
    }

    @AfterEach
    void tearDown() {
        scopeMockedStatic.close();
    }

    @Test
    void scanTypes() {
        SymbolTable table = new SymbolTable();
        DiagnosticCollectorMock diagnosticCollector = new DiagnosticCollectorMock();
        TypeScanner scanner = new TypeScanner(table, diagnosticCollector);

        scanner.visitProgram(program, Scope.ROOT);

        assertThat(table).hasSymbolRegistered("MyStruct", first)
                         .hasSymbolRegistered("MyException", first)
                         .hasSymbolRegistered("MyInnerScope", second);
        assertThat(diagnosticCollector.errors).hasSize(2);
        assertThat(diagnosticCollector.warnings).hasSize(0);
        assertThat(diagnosticCollector.information).hasSize(0);
    }

    // todo remove when diagnostic collector is fully implemented
    private static class DiagnosticCollectorMock extends DiagnosticCollector<SemanticDiagnostic> {

        private final List<SemanticDiagnostic> errors = new ArrayList<>();
        private final List<SemanticDiagnostic> warnings = new ArrayList<>();
        private final List<SemanticDiagnostic> information = new ArrayList<>();

        @Override
        public void addError(SemanticDiagnostic diagnostic) {
            errors.add(diagnostic);
            super.addError(diagnostic);
        }

        @Override
        public void addWarning(SemanticDiagnostic diagnostic) {
            warnings.add(diagnostic);
            super.addWarning(diagnostic);
        }

        @Override
        public void addInformation(SemanticDiagnostic diagnostic) {
            information.add(diagnostic);
            super.addInformation(diagnostic);
        }
    }

}