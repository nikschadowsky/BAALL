package de.nikschadowsky.baall.compiler.syntax.analysis;

import de.nikschadowsky.baall.compiler._utility.TokenQueueTestBuilder;
import de.nikschadowsky.baall.compiler._utility.ast.NodeAssertionFactory;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.PartialParseResult;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ParenthesizedExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.UnaryExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FunctionCallNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.TermNode;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.nikschadowsky.baall.compiler._utility.BaseAssertion.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @since 03.09.2024
 */
class ExpressionParserImplTest {

    private final ASTNodeFactory astFactory = new ASTNodeFactory(new NodeDiagnosticCollector());

    private LiteralParser literalParser;
    private ProgramParser programParser;
    private AuxiliaryParser auxiliaryParser;
    private ExpressionParser expressionParser;

    @BeforeEach
    void setUp() {
        programParser = mock(ProgramParserImpl.class);
        literalParser = new LiteralParserImpl(programParser, astFactory);
        auxiliaryParser = new AuxiliaryParserImpl(programParser, astFactory);
        expressionParser = new ExpressionParserImpl(programParser, astFactory);

        when(programParser.getAuxiliaryParser()).thenReturn(auxiliaryParser);
        when(programParser.getExpressionParser()).thenReturn(expressionParser);
        when(programParser.getLiteralParser()).thenReturn(literalParser);
    }

    @Test
    void parseExpression() {
        TokenQueue queue = new TokenQueueTestBuilder().number("value").build();

        ParseResult<ExpressionNode> actual = expressionParser.parseExpression(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isNumberLiteral()
                          .hasValue("value");

        queue = new TokenQueueTestBuilder().separator("(")
                                           .identifier("MyIdentifier")
                                           .separator("(")
                                           .number("struct_initializer")
                                           .separator(")")
                                           .separator(")")
                                           .separator(";")
                                           .build();
        actual = expressionParser.parseExpression(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isParenthesizedExpression()
                          .mapToInner()
                          .isFunctionCall()
                          .hasFunctionNameMatching(a -> a.isIdentifier().hasName("MyIdentifier"))
                          .hasArguments(1)
                          .hasArgumentMatching(0, a -> a.isNumberLiteral().hasValue("struct_initializer"));
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().separator("(")
                                           .separator("(")
                                           .number("value")
                                           .separator(")")
                                           .separator(")")
                                           .separator(";")
                                           .build();
        actual = expressionParser.parseExpression(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isParenthesizedExpression()
                          .mapToInner()
                          .isParenthesizedExpression()
                          .mapToInner()
                          .isNumberLiteral()
                          .hasValue("value");
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().operator("!").number("prefixed").separator(";").build();
        actual = expressionParser.parseExpression(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isPrefixOperation()
                          .hasOperator("!")
                          .hasOperandMatching(a -> a.isNumberLiteral().hasValue("prefixed"));
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().number("operand1").operator("+").number("operand2").separator(";").build();
        actual = expressionParser.parseExpression(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isBinaryExpression()
                          .hasLeftOperandMatching(a -> a.isNumberLiteral().hasValue("operand1"))
                          .hasOperator("+")
                          .hasRightOperandMatching(a -> a.isNumberLiteral().hasValue("operand2"));

        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().number("value").separator(";").build();
        assertThat(expressionParser.parseExpression(queue)).isSuccessful()
                                                           .map(NodeAssertionFactory::create)
                                                           .isNumberLiteral()
                                                           .hasValue("value");

        queue = new TokenQueueTestBuilder().separator("(")
                                           .number("operand1")
                                           .operator("<<")
                                           .number("operand2")
                                           .separator(")")
                                           .operator("&")
                                           .separator("(")
                                           .identifier("MyIdentifier")
                                           .separator(")")
                                           .operator("*")
                                           .separator("[")
                                           .separator("]")
                                           .separator(";")
                                           .build();
        actual = expressionParser.parseExpression(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isBinaryExpression()
                          .hasOperator("&")
                          .hasLeftOperandMatching(a -> a.isParenthesizedExpression()
                                                        .mapToInner()
                                                        .isBinaryExpression()
                                                        .hasOperator("<<")
                                                        .hasLeftOperandMatching(a1 -> a1.isNumberLiteral()
                                                                                        .hasValue("operand1")
                                                        )
                                                        .hasRightOperandMatching(a1 -> a1.isNumberLiteral()
                                                                                         .hasValue("operand2")
                                                        )
                          )
                          .hasRightOperandMatching(a -> a.isBinaryExpression()
                                                         .hasOperator("*")
                                                         .hasLeftOperandMatching(a1 -> a1.isParenthesizedExpression()
                                                                                         .mapToInner()
                                                                                         .isElementAccess()
                                                                                         .isIdentifier()
                                                                                         .hasName("MyIdentifier")
                                                         )
                                                         .hasRightOperandMatching(a1 -> a1.isArrayLiteral()
                                                                                          .hasNoElements())
                          );
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().separator(";").build();
        assertThat(expressionParser.parseExpression(queue)).isUnsuccessful()
                                                           .syntaxDiagnosticContains("Not an expression");
    }

    @Test
    void parseTerm() {
        TokenQueue queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                                      .separator("[")
                                                      .number("index")
                                                      .separator("]")
                                                      .separator(";")
                                                      .build();
        ParseResult<TermNode> actual = expressionParser.parseTerm(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isElementAccess()
                          .isIndexedAccess()
                          .hasIndexMatching(a -> a.isNumberLiteral().hasValue("index"))
                          .mapToInner()
                          .isIdentifier()
                          .hasName("MyIdentifier");
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().separator("[").number("index").separator("]").separator(";").build();
        actual = expressionParser.parseTerm(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isArrayLiteral()
                          .hasElements(1)
                          .hasElementMatching(0, a -> a.isNumberLiteral().hasValue("index"));
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                           .separator("(")
                                           .separator(")")
                                           .separator(";")
                                           .build();
        actual = expressionParser.parseTerm(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isFunctionCall()
                          .hasFunctionNameMatching(a -> a.isIdentifier().hasName("MyIdentifier"))
                          .hasArguments(0);
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().operator("++").identifier("MyIdentifier").separator(";").build();
        actual = expressionParser.parseTerm(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isUnaryExpression()
                          .isPrefixOperation()
                          .hasOperator("++")
                          .mapToInner()
                          .isIdentifier()
                          .hasName("MyIdentifier");
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().separator("(").number("expression").separator(")").separator(";").build();
        assertThat(expressionParser.parseTerm(queue)).isSuccessful()
                                                     .map(NodeAssertionFactory::create)
                                                     .isParenthesizedExpression()
                                                     .mapToInner()
                                                     .isNumberLiteral()
                                                     .hasValue("expression");
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().separator(";").build();
        assertThat(expressionParser.parseTerm(queue)).isUnsuccessful().syntaxDiagnosticContains("Not an expression");
    }

    @Test
    void parseParenthesizedExpression() {
        TokenQueue queue =
                new TokenQueueTestBuilder().separator("(")
                                           .number("expression")
                                           .separator(")")
                                           .separator(";")
                                           .build();
        ParseResult<ParenthesizedExpressionNode> actual = expressionParser.parseParenthesizedExpression(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .mapToInner()
                          .isNumberLiteral()
                          .hasValue("expression");
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().separator("(").number("expression").separator(";").build();
        assertThat(expressionParser.parseParenthesizedExpression(queue)).isUnsuccessful()
                                                                        .syntaxDiagnosticContains("Expected ')'");

        queue = new TokenQueueTestBuilder().number("expression").separator(")").build();
        assertThat(expressionParser.parseParenthesizedExpression(queue)).isUnsuccessful()
                                                                        .syntaxDiagnosticContains("Expected '('");
    }

    @Test
    void parseFunctionCall() {
        TokenQueue queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                                      .separator("(")
                                                      .separator(")")
                                                      .separator(";")
                                                      .build();
        PartialParseResult<FunctionCallNode> actual = expressionParser.parseFunctionCall(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasFunctionNameMatching(a -> a.isIdentifier().hasName("MyIdentifier"))
                          .hasArguments(0);
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                           .separator("[")
                                           .number("index")
                                           .separator("]")
                                           .separator("(")
                                           .number("value")
                                           .separator(",")
                                           .identifier("IdentifierAccess")
                                           .separator(")")
                                           .separator(";")
                                           .build();
        actual = expressionParser.parseFunctionCall(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasFunctionNameMatching(a -> a.isIndexedAccess()
                                                         .hasIndexMatching(a1 -> a1.isNumberLiteral().hasValue("index"))
                                                         .mapToInner()
                                                         .isIdentifier()
                                                         .hasName("MyIdentifier"))
                          .hasArguments(2)
                          .hasArgumentMatching(0, a -> a.isNumberLiteral().hasValue("value"))
                          .hasArgumentMatching(1, a -> a.isElementAccess().isIdentifier().hasName("IdentifierAccess"));

        assertThat(queue).hasNextTokenValueMatch(";");
        queue = new TokenQueueTestBuilder().identifier("MyFunctionCall")
                                           .separator("(")
                                           .identifier("MyStruct")
                                           .separator("(")
                                           .number("StructArgument1")
                                           .separator(",")
                                           .identifier("StructArgument2")
                                           .separator(")")
                                           .separator(",")
                                           .string("Argument2")
                                           .separator(",")
                                           .separator("[")
                                           .identifier("ArrayElement1")
                                           .separator(",")
                                           .number("ArrayElement2")
                                           .separator("]")
                                           .separator(")")
                                           .separator(";")
                                           .build();
        actual = expressionParser.parseFunctionCall(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasFunctionNameMatching(a -> a.isIdentifier().hasName("MyFunctionCall"))
                          .hasArguments(3)
                          .hasArgumentMatching(
                                  0,
                                  a -> a.isFunctionCall()
                                        .hasFunctionNameMatching(a1 -> a1.isIdentifier().hasName("MyStruct"))
                                        .hasArguments(2)
                                        .hasArgumentMatching(0, a1 -> a1.isNumberLiteral().hasValue("StructArgument1"))
                                        .hasArgumentMatching(1, a1 -> a1.isElementAccess().isIdentifier())
                          )
                          .hasArgumentMatching(1, a1 -> a1.isStringLiteral().hasValue("Argument2"))
                          .hasArgumentMatching(2, a1 -> a1.isArrayLiteral().hasElements(2));
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().separator(";").build();
        actual = expressionParser.parseFunctionCall(queue);
        assertThat(actual).isUnsuccessful().syntaxDiagnosticContains("Not a statement");

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                           .separator("[")
                                           .number("1")
                                           .separator("]")
                                           .build();
        actual = expressionParser.parseFunctionCall(queue);
        assertThat(actual).isPartiallyParsed()
                          .syntaxDiagnosticContains("Expected '('")
                          .map(NodeAssertionFactory::create)
                          .hasFunctionNameMatching(
                                  a -> a.isIndexedAccess()
                                        .hasIndexMatching(a1 -> a1.isNumberLiteral().hasValue("1"))
                                        .mapToInner()
                                        .isIdentifier()
                                        .hasName("MyIdentifier"))
                          .hasArguments(0);
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                           .separator("[")
                                           .number("1")
                                           .separator("]")
                                           .separator("(")
                                           .separator(",")
                                           .separator(")")
                                           .build();
        actual = expressionParser.parseFunctionCall(queue);
        assertThat(actual).isPartiallyParsed().syntaxDiagnosticContains("Not an expression");
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                           .separator("[")
                                           .number("1")
                                           .separator("]")
                                           .separator("(")
                                           .number("expression")
                                           .build();
        actual = expressionParser.parseFunctionCall(queue);
        assertThat(actual).isPartiallyParsed().syntaxDiagnosticContains("Expected ')'");
        assertThat(queue).isAtEnd();
    }

    @Test
    void parseUnaryExpression() {
        TokenQueue queue = new TokenQueueTestBuilder().operator("++").identifier("MyIdentifier").separator(";").build();
        ParseResult<UnaryExpressionNode> actual = expressionParser.parseUnaryExpression(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasOperator("++")
                          .isPrefixOperation()
                          .mapToInner()
                          .isIdentifier()
                          .hasName("MyIdentifier");
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier").operator("--").separator(";").build();
        actual = expressionParser.parseUnaryExpression(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasOperator("--")
                          .isPostfixOperation()
                          .mapToInner()
                          .isIdentifier()
                          .hasName("MyIdentifier");
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier").build();
        actual = expressionParser.parseUnaryExpression(queue);
        assertThat(actual).isUnsuccessful().syntaxDiagnosticContains("Expected a unary operator");

        queue = new TokenQueueTestBuilder().operator("++").build();
        actual = expressionParser.parseUnaryExpression(queue);
        assertThat(actual).isUnsuccessful().syntaxDiagnosticContains("Expected an identifier");

        queue = new TokenQueueTestBuilder().build();
        actual = expressionParser.parseUnaryExpression(queue);
        assertThat(actual).isUnsuccessful().syntaxDiagnosticContains("Not a statement");
    }

}