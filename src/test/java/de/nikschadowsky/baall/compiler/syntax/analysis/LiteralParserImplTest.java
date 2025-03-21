package de.nikschadowsky.baall.compiler.syntax.analysis;

import de.nikschadowsky.baall.compiler._utility.ParserMockerExtension;
import de.nikschadowsky.baall.compiler._utility.TokenQueueTestBuilder;
import de.nikschadowsky.baall.compiler._utility.ast.NodeAssertionFactory;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.LiteralNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.FunctionDefinitionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.ListLiteralNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.PrimitiveLiteralNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.StructDefinitionLiteralNode;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static de.nikschadowsky.baall.compiler._utility.BaseAssertion.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @since 28.08.2024
 */
class LiteralParserImplTest {

    private final ASTNodeFactory astFactory = new ASTNodeFactory(new NodeDiagnosticCollector());

    private LiteralParser literalParser;
    private ProgramParser programParser;
    private AuxiliaryParser auxiliaryParser;

    @RegisterExtension
    private ParserMockerExtension parserMockerExtension = new ParserMockerExtension();

    @BeforeEach
    void setUp() {
        programParser = mock(ProgramParserImpl.class);
        literalParser = new LiteralParserImpl(programParser, astFactory);
        auxiliaryParser = new AuxiliaryParserImpl(programParser, astFactory);

        when(programParser.getAuxiliaryParser()).thenReturn(auxiliaryParser);
    }

    @Test
    void parseLiteral() {
        StatementsNode mockedStatements = mock(StatementsNode.class);
        parserMockerExtension.mockStatementParserExecution(
                programParser,
                statementParser -> statementParser.parseStatements(any()),
                mockedStatements,
                "(",
                ")",
                "{",
                "}",
                ","
        );
        ExpressionNode mockedExpression = mock(ExpressionNode.class);
        parserMockerExtension.mockExpressionParserExecution(
                programParser,
                exprParser -> exprParser.parseExpression(any()),
                mockedExpression,
                "[",
                "]",
                ","
        );

        TokenQueue queue = new TokenQueueTestBuilder().string("primitive").build();

        ParseResult<LiteralNode> actual = literalParser.parseLiteral(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isPrimitiveLiteral()
                          .hasPrimitiveValue("primitive")
                          .isString();
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().separator("[").separator("]").build();
        actual = literalParser.parseLiteral(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isArrayLiteral()
                          .hasNoElements();
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().keyword("none")
                                           .build();

        assertThat(literalParser.parseLiteral(queue)).isSuccessful()
                                                     .map(NodeAssertionFactory::create)
                                                     .isStructNoneLiteral();
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().separator("(")
                                           .keyword("string")
                                           .separator(":")
                                           .identifier("MyIdentifier")
                                           .separator(")")
                                           .build();
        actual = literalParser.parseLiteral(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isStructDefinitionLiteral()
                          .hasFields(1)
                          .hasFieldTypeMatching(0, a -> a.isPrimitiveType().hasIdentifier("string"))
                          .hasFieldName(0, "MyIdentifier");
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().separator("(")
                                           .keyword("string")
                                           .separator(":")
                                           .identifier("MyIdentifier")
                                           .separator(")")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .build();
        actual = literalParser.parseLiteral(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isFunctionDefinition()
                          .hasParameters(1)
                          .hasParameterTypeMatching(0, a -> a.isPrimitiveType().hasIdentifier("string"))
                          .hasParameterName(0, "MyIdentifier")
                          .hasBodyMatching(a -> a.isEqualTo(mockedStatements));
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().separator("[").build();
        assertThat(literalParser.parseLiteral(queue)).isUnsuccessful().syntaxDiagnosticContains("Expected a literal");
    }

    @Test
    void parseListLiteral() {
        ExpressionNode mockedExpression = mock(ExpressionNode.class);
        parserMockerExtension.mockExpressionParserExecution(
                programParser,
                exprParser -> exprParser.parseExpression(any()),
                mockedExpression,
                "[",
                "]",
                ","
        );

        TokenQueue queue = new TokenQueueTestBuilder().separator("[").separator("]").build();
        ParseResult<ListLiteralNode> actual = literalParser.parseListLiteral(queue);
        assertThat(actual).isSuccessful().map(NodeAssertionFactory::create).hasNoElements();
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().separator("[").operator("expression").separator("]").build();
        actual = literalParser.parseListLiteral(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasElements(1)
                          .hasElementMatching(0, a -> a.isEqualTo(mockedExpression));
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().separator("[")
                                           .operator("expression")
                                           .separator(",")
                                           .operator("expression")
                                           .separator("]")
                                           .build();
        actual = literalParser.parseListLiteral(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasElements(2)
                          .hasElementMatching(0, a -> a.isEqualTo(mockedExpression))
                          .hasElementMatching(1, a -> a.isEqualTo(mockedExpression));
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().separator("[").operator("expression").separator(";").build();
        assertThat(literalParser.parseListLiteral(queue)).isUnsuccessful().syntaxDiagnosticContains("Expected ']'");

        queue = new TokenQueueTestBuilder().separator(";").build();
        assertThat(literalParser.parseListLiteral(queue)).isUnsuccessful().syntaxDiagnosticContains("Expected '['");
    }

    @Test
    void parseStructDefinition() {
        ExpressionNode mockedExpression = mock(ExpressionNode.class);
        parserMockerExtension.mockExpressionParserExecution(
                programParser,
                exprParser -> exprParser.parseExpression(any()),
                mockedExpression,
                "[",
                "]",
                ","
        );

        TokenQueue queue = new TokenQueueTestBuilder().separator("(")
                                                      .keyword("number")
                                                      .separator("[")
                                                      .separator("]")
                                                      .separator(":")
                                                      .identifier("MyIdentifier")
                                                      .separator(")")
                                                      .build();
        ParseResult<StructDefinitionLiteralNode> actual = literalParser.parseStructDefinition(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasFields(1)
                          .hasFieldTypeMatching(0, a -> a.isListType().mapToInner().isPrimitiveType())
                          .hasFieldName(0, "MyIdentifier");
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().separator("(")
                                           .keyword("number")
                                           .separator("[")
                                           .separator("]")
                                           .separator(":")
                                           .identifier("MyIdentifier1")
                                           .separator(",")
                                           .identifier("MyType")
                                           .separator(":")
                                           .identifier("MyIdentifier2")
                                           .separator(")")
                                           .build();
        actual = literalParser.parseStructDefinition(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasFields(2)
                          .hasFieldTypeMatching(0, a -> a.isListType().mapToInner().isPrimitiveType())
                          .hasFieldName(0, "MyIdentifier1")
                          .hasFieldTypeMatching(1,
                                                a -> a.isIdentifierType()
                                                      .isNotNoneSafe()
                                                      .hasIdentifierMatching(a1 -> a1.isIdentifier().hasName("MyType"))
                          )
                          .hasFieldName(1, "MyIdentifier2");
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().separator("(").separator(")").build();
        assertThat(literalParser.parseStructDefinition(queue)).isUnsuccessful()
                                                              .syntaxDiagnosticContains("Expected a type");

        queue = new TokenQueueTestBuilder().separator("(")
                                           .keyword("string")
                                           .separator(":")
                                           .identifier("MyIdentifier")
                                           .build();
        assertThat(literalParser.parseStructDefinition(queue)).isUnsuccessful()
                                                              .syntaxDiagnosticContains("Expected ')'");

        queue = new TokenQueueTestBuilder().separator(";").build();
        assertThat(literalParser.parseStructDefinition(queue)).isUnsuccessful()
                                                              .syntaxDiagnosticContains("Expected '('");
    }

    @Test
    void parseStructNone() {
        TokenQueue queue = new TokenQueueTestBuilder().keyword("none").separator(";").build();

        assertThat(literalParser.parseStructNone(queue)).isSuccessful();
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().separator("(").separator(")").build();
        assertThat(literalParser.parseStructNone(queue)).isUnsuccessful().syntaxDiagnosticContains("Expected 'none'");
        assertThat(queue).hasNextTokenValueMatch(")");
    }

    @Test
    void parseFunctionDefinition() {
        StatementsNode mockedStatements = mock(StatementsNode.class);
        parserMockerExtension.mockStatementParserExecution(
                programParser,
                statementParser -> statementParser.parseStatements(any()),
                mockedStatements,
                "(",
                ")",
                "{",
                "}",
                ","
        );

        TokenQueue queue = new TokenQueueTestBuilder().separator("(")
                                                      .keyword("string")
                                                      .separator(":")
                                                      .identifier("MyIdentifier")
                                                      .separator(")")
                                                      .separator("{")
                                                      .operator("statements")
                                                      .separator("}")
                                                      .build();
        ParseResult<FunctionDefinitionNode> actual = literalParser.parseFunctionDefinition(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasParameters(1)
                          .hasParameterTypeMatching(0, a -> a.isPrimitiveType().hasIdentifier("string"))
                          .hasParameterName(0, "MyIdentifier")
                          .hasBodyMatching(a -> a.isEqualTo(mockedStatements));
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().separator("(")
                                           .separator(")")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .build();
        actual = literalParser.parseFunctionDefinition(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasNoParameters()
                          .hasBodyMatching(a -> a.isEqualTo(mockedStatements));
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().build();
        assertThat(literalParser.parseFunctionDefinition(queue)).isUnsuccessful()
                                                                .syntaxDiagnosticContains("Expected '('");
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().separator("(").separator(")").separator(";").build();
        assertThat(literalParser.parseFunctionDefinition(queue)).isUnsuccessful()
                                                                .syntaxDiagnosticContains("Expected '{'");

        queue = new TokenQueueTestBuilder().separator("(")
                                           .separator(")")
                                           .separator("{")
                                           .operator("statements")
                                           .separator(";")
                                           .build();
        assertThat(literalParser.parseFunctionDefinition(queue)).isUnsuccessful()
                                                                .syntaxDiagnosticContains("Expected '}'");
    }

    @Test
    void parsePrimitiveLiteral() {
        TokenQueue queue = new TokenQueueTestBuilder().number("12345").string("StringValue").bool("True").build();
        ParseResult<PrimitiveLiteralNode> actual = literalParser.parsePrimitiveLiteral(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasPrimitiveValue("12345")
                          .isNumber();
        actual = literalParser.parsePrimitiveLiteral(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasPrimitiveValue("StringValue")
                          .isString();
        actual = literalParser.parsePrimitiveLiteral(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasPrimitiveValue("True")
                          .isBoolean();

        queue = new TokenQueueTestBuilder().keyword("Keyword").build();
        assertThat(literalParser.parsePrimitiveLiteral(queue)).isUnsuccessful()
                                                              .syntaxDiagnosticContains("Expected a primitive");
    }

}