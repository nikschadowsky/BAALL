package de.nikschadowsky.baall.compiler.syntax.analysis;

import de.nikschadowsky.baall.compiler._utility.ParserMockerExtension;
import de.nikschadowsky.baall.compiler._utility.TokenQueueTestBuilder;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntaxtree.util.NodeDiagnosticCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static de.nikschadowsky.baall.compiler._utility.BaseAssertion.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StatementParserImplTest {

    private final ASTNodeFactory astFactory = new ASTNodeFactory(new NodeDiagnosticCollector());

    private StatementParser statementParser;
    private ControlStructureParser controlStructureParser;
    private AuxiliaryParser auxiliaryParser;
    private LiteralParser literalParser;
    private ProgramParser programParser;
    private ExpressionParser expressionParser;

    @RegisterExtension
    private ParserMockerExtension parserMockerExtension = new ParserMockerExtension();

    @BeforeEach
    void setUp() {
        programParser = mock(ProgramParserImpl.class);
        statementParser = new StatementParserImpl(programParser, astFactory);
        controlStructureParser = new ControlStructureParserImpl(programParser, astFactory);
        auxiliaryParser = new AuxiliaryParserImpl(programParser, astFactory);
        literalParser = new LiteralParserImpl(programParser, astFactory);
        expressionParser = new ExpressionParserImpl(programParser, astFactory);

        when(programParser.getStatementParser()).thenReturn(statementParser);
        when(programParser.getControlStructureParser()).thenReturn(controlStructureParser);
        when(programParser.getAuxiliaryParser()).thenReturn(auxiliaryParser);
        when(programParser.getLiteralParser()).thenReturn(literalParser);
        when(programParser.getExpressionParser()).thenReturn(expressionParser);
    }

    @Test
    void parseImports() {
        TokenQueue queue = new TokenQueueTestBuilder().keyword("use")
                                                      .string("MyImport")
                                                      .separator(";")
                                                      .operator("end")
                                                      .build();
        assertThat(statementParser.parseImports(queue)).isSuccessful()
                                                       .resultMatches(imports -> imports.size() == 1)
                                                       .resultMatches(imports -> "MyImport".equals(imports.get(0)
                                                                                                          .value()));
        assertThat(queue).hasNextTokenValueMatch("end");

        queue = new TokenQueueTestBuilder().keyword("use")
                                           .string("MyImport1")
                                           .separator(";")
                                           .keyword("use")
                                           .string("MyImport2")
                                           .separator(";")
                                           .operator("end")
                                           .build();
        assertThat(statementParser.parseImports(queue)).isSuccessful()
                                                       .resultMatches(imports -> imports.size() == 2)
                                                       .resultMatches(imports -> "MyImport1".equals(imports.get(0)
                                                                                                           .value()))
                                                       .resultMatches(imports -> "MyImport2".equals(imports.get(1)
                                                                                                           .value()));
        assertThat(queue).hasNextTokenValueMatch("end");

        queue = new TokenQueueTestBuilder().keyword("use").separator(";").build();
        assertThat(statementParser.parseImports(queue)).isPartiallyParsed()
                                                       .resultMatches(imports -> imports.size() == 1)
                                                       .resultMatches(imports -> imports.get(0) == null)
                                                       .syntaxDiagnosticContains("Expected a string");
    }

    @Test
    void parseStatements() {
    }

    @Test
    void parseStatement() {
    }

    @Test
    void parseRawStatement() {
    }

    @Test
    void parseDeclaration() {
    }

    @Test
    void parseVariableDeclaration() {
    }

    @Test
    void parseConstantDeclaration() {
    }

    @Test
    void parseReassignment() {
    }

    @Test
    void parseVariableReassignment() {
    }

    @Test
    void parseExports() {
        TokenQueue queue = new TokenQueueTestBuilder().keyword("export")
                                                      .separator("{")
                                                      .identifier("MyIdentifier")
                                                      .separator("[")
                                                      .number("index")
                                                      .separator("]")
                                                      .separator("}")
                                                      .build();

    }
}