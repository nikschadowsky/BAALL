package de.nikschadowsky.baall.compiler.syntax.analysis;

import de.nikschadowsky.baall.compiler._utility.ParserMockerExtension;
import de.nikschadowsky.baall.compiler._utility.TokenQueueTestBuilder;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.BreakStatementNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.ContinueStatementNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.ReturnStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.RaiseStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static de.nikschadowsky.baall.compiler._utility.BaseAssertion.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

    @RegisterExtension
    private ParserMockerExtension parserMockerExtension = new ParserMockerExtension();

    @BeforeEach
    void setUp() {
        programParser = mock(ProgramParserImpl.class);
        controlStatementParser = new ControlStatementParserImpl(programParser, astFactory);
        auxiliaryParser = new AuxiliaryParserImpl(programParser, astFactory);
        literalParser = new LiteralParserImpl(programParser, astFactory);

        when(programParser.getControlStatementParser()).thenReturn(controlStatementParser);
        when(programParser.getAuxiliaryParser()).thenReturn(auxiliaryParser);
        when(programParser.getLiteralParser()).thenReturn(literalParser);
    }

    @Test
    void parseControlStatement() {
        ExpressionNode mockedExpression = mock(ExpressionNode.class);
        parserMockerExtension.mockExpressionParserExecution(
                programParser,
                exprParser -> exprParser.parseExpression(any()),
                mockedExpression,
                ";",
                "(",
                ")"
        );

        TokenQueue queue = new TokenQueueTestBuilder().keyword("continue").separator(";").build();
        assertThat(controlStatementParser.parseControlStatement(queue)).isSuccessful()
                                                                       .resultMatches(node -> node instanceof ContinueStatementNodeImpl);
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("break").separator(";").build();
        assertThat(controlStatementParser.parseControlStatement(queue)).isSuccessful()
                                                                       .resultMatches(node -> node instanceof BreakStatementNodeImpl);
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("return").operator("expression").separator(";").build();
        assertThat(controlStatementParser.parseControlStatement(queue)).isSuccessful()
                                                                       .resultMatches(node -> node instanceof ReturnStatementNode)
                                                                       .resultMatches(node -> ((ReturnStatementNode) node).getReturnExpression() == mockedExpression);
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("raise")
                                           .identifier("MyException")
                                           .separator("(")
                                           .operator("expression")
                                           .separator(")")
                                           .separator(";")
                                           .build();
        assertThat(controlStatementParser.parseControlStatement(queue)).isSuccessful()
                                                                       .resultMatches(node -> node instanceof RaiseStatementNode)
                                                                       .resultMatches(node -> {
                                                                           RaiseStatementNode raiseStatementNode =
                                                                                   (RaiseStatementNode) node;
                                                                           return "MyException".equals(
                                                                                   raiseStatementNode.getException()
                                                                                                     .getIdentifier()
                                                                                                     .getIdentifier()
                                                                                                     .getIdentifier()
                                                                                                     .value());
                                                                       }).resultMatches(node -> {
                                                                           RaiseStatementNode raiseStatementNode =
                                                                                   (RaiseStatementNode) node;
                                                                           return raiseStatementNode.getException()
                                                                                                    .getArguments().size() == 1;
                                                                       }).resultMatches(node -> {
                                                                           RaiseStatementNode raiseStatementNode =
                                                                                   (RaiseStatementNode) node;
                                                                           return raiseStatementNode.getException()
                                                                                                    .getArguments().get(0) == mockedExpression;
                                                                       });
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().separator(";").build();
        assertThat(controlStatementParser.parseControlStatement(queue)).isUnsuccessful()
                                                                       .syntaxDiagnosticContains("Not a statement");
    }
}