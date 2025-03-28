package de.nikschadowsky.baall.compiler.syntax.analysis;

import de.nikschadowsky.baall.compiler._utility.ParserMockerExtension;
import de.nikschadowsky.baall.compiler._utility.TokenQueueTestBuilder;
import de.nikschadowsky.baall.compiler._utility.ast.NodeAssertionFactory;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.ControlStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static de.nikschadowsky.baall.compiler._utility.BaseAssertion.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @since 08.09.2024
 */
class ControlStatementParserImplTest {

    private final ASTNodeFactory astFactory = new ASTNodeFactory(new NodeDiagnosticCollector());

    private ControlStatementParser controlStatementParser;
    private AuxiliaryParser auxiliaryParser;
    private LiteralParser literalParser;
    private ProgramParser programParser;
    private ExpressionParser expressionParser;

    @RegisterExtension
    private ParserMockerExtension parserMockerExtension = new ParserMockerExtension();

    @BeforeEach
    void setUp() {
        programParser = mock(ProgramParserImpl.class);
        controlStatementParser = new ControlStatementParserImpl(programParser, astFactory);
        auxiliaryParser = new AuxiliaryParserImpl(programParser, astFactory);
        literalParser = new LiteralParserImpl(programParser, astFactory);
        expressionParser = new ExpressionParserImpl(programParser, astFactory);

        when(programParser.getControlStatementParser()).thenReturn(controlStatementParser);
        when(programParser.getAuxiliaryParser()).thenReturn(auxiliaryParser);
        when(programParser.getLiteralParser()).thenReturn(literalParser);
        when(programParser.getExpressionParser()).thenReturn(expressionParser);
    }

    @Test
    void parseControlStatement() {
        TokenQueue queue = new TokenQueueTestBuilder().keyword("continue").separator(";").build();
        ParseResult<ControlStatementNode> actual = controlStatementParser.parseControlStatement(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isContinueStatement();
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("break").separator(";").build();
        actual = controlStatementParser.parseControlStatement(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isBreakStatement();
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("return").number("number").separator(";").build();
        actual = controlStatementParser.parseControlStatement(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isReturnStatement()
                          .hasExpressionMatching(a -> a.isNumberLiteral().hasValue("number"));
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("raise")
                                           .identifier("MyException")
                                           .separator("(")
                                           .number("number")
                                           .separator(")")
                                           .separator(";")
                                           .build();
        actual = controlStatementParser.parseControlStatement(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isRaiseStatement()
                          .hasExceptionCall(a -> a.hasFunctionNameMatching(a1 -> a1.isIdentifier()
                                                                                   .hasName("MyException"))
                                                  .hasArguments(1)
                                                  .hasArgumentMatching(
                                                          0,
                                                          a1 -> a1.isNumberLiteral().hasValue("number")
                                                  )
                          );
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().separator(";").build();
        actual = controlStatementParser.parseControlStatement(queue);
        assertThat(actual).isUnsuccessful().syntaxDiagnosticContains("Not a statement");
    }
}