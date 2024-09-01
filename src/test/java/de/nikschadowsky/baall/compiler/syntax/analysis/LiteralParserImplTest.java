package de.nikschadowsky.baall.compiler.syntax.analysis;

import de.nikschadowsky.baall.compiler._utility.TokenQueueTestBuilder;
import de.nikschadowsky.baall.compiler.lexer.tokenizer.TokenType;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntaxtree.util.NodeDiagnosticCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.nikschadowsky.baall.compiler._utility.BaseAssertion.assertThat;
import static de.nikschadowsky.baall.compiler._utility.ParserMocker.mockExpressionParserExecution;
import static de.nikschadowsky.baall.compiler._utility.ParserMocker.mockStatementParserExecution;
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
        mockStatementParserExecution(
                programParser,
                statementParser -> statementParser.parseStatements(any()),
                mockedStatements,
                "(", ")", "{", "}", ","
        );
        ExpressionNode mockedExpression = mock(ExpressionNode.class);
        mockExpressionParserExecution(
                programParser,
                exprParser -> exprParser.parseExpression(any()),
                mockedExpression,
                "[",
                "]", ","
        );

        TokenQueue queue = new TokenQueueTestBuilder().string("primitive").build();
        assertThat(literalParser.parseLiteral(queue)).isSuccessful()
                                                     .resultMatches(node -> NodeType.PRIMITIVE.equals(node.getNodeType()));

        queue = new TokenQueueTestBuilder().separator("[").separator("]").build();
        assertThat(literalParser.parseLiteral(queue)).isSuccessful()
                                                     .resultMatches(node -> NodeType.ARRAY_LITERAL.equals(node.getNodeType()));

        queue = new TokenQueueTestBuilder().separator("(").operator("expression").separator(")").build();
        assertThat(literalParser.parseLiteral(queue)).isSuccessful()
                                                     .resultMatches(node -> NodeType.STRUCT_INITIALIZATION.equals(node.getNodeType()));

        queue = new TokenQueueTestBuilder().separator("(")
                                           .keyword("string")
                                           .separator(":")
                                           .identifier("MyIdentifier")
                                           .separator(")")
                                           .build();
        assertThat(literalParser.parseLiteral(queue)).isSuccessful()
                                                     .resultMatches(node -> NodeType.STRUCT_DEFINITION.equals(node.getNodeType()));

        queue = new TokenQueueTestBuilder().separator("(")
                                           .keyword("string")
                                           .separator(":")
                                           .identifier("MyIdentifier")
                                           .separator(")")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .build();
        assertThat(literalParser.parseLiteral(queue)).isSuccessful()
                                                     .resultMatches(node -> NodeType.FUNCTION_DEFINITION.equals(node.getNodeType()));

        queue = new TokenQueueTestBuilder().separator("[").build();
        assertThat(literalParser.parseLiteral(queue)).isUnsuccessful().syntaxDiagnosticContains("Expected a literal");
    }

    @Test
    void parseArrayLiteral() {
        ExpressionNode mockedExpression = mock(ExpressionNode.class);
        mockExpressionParserExecution(
                programParser,
                exprParser -> exprParser.parseExpression(any()),
                mockedExpression,
                "[",
                "]", ","
        );

        TokenQueue queue = new TokenQueueTestBuilder().separator("[").separator("]").build();
        assertThat(literalParser.parseArrayLiteral(queue)).isSuccessful()
                                                          .resultMatches(node -> node.getElements().isEmpty());

        queue = new TokenQueueTestBuilder().separator("[").operator("expression").separator("]").build();
        assertThat(literalParser.parseArrayLiteral(queue)).isSuccessful()
                                                          .resultMatches(node -> node.getElements().size() == 1)
                                                          .resultMatches(node -> node.getElements()
                                                                                     .get(0) == mockedExpression);

        queue = new TokenQueueTestBuilder().separator("[")
                                           .operator("expression")
                                           .separator(",")
                                           .operator("expression")
                                           .separator("]")
                                           .build();
        assertThat(literalParser.parseArrayLiteral(queue)).isSuccessful()
                                                          .resultMatches(node -> node.getElements().size() == 2)
                                                          .resultMatches(node -> node.getElements()
                                                                                     .get(0) == mockedExpression)
                                                          .resultMatches(node -> node.getElements()
                                                                                     .get(1) == mockedExpression);

        queue = new TokenQueueTestBuilder().separator("[").operator("expression").separator(";").build();
        assertThat(literalParser.parseArrayLiteral(queue)).isUnsuccessful().syntaxDiagnosticContains("Expected ']'");

        queue = new TokenQueueTestBuilder().separator(";").build();
        assertThat(literalParser.parseArrayLiteral(queue)).isUnsuccessful().syntaxDiagnosticContains("Expected '['");
    }

    @Test
    void parseStructDefinition() {
        ExpressionNode mockedExpression = mock(ExpressionNode.class);
        mockExpressionParserExecution(
                programParser,
                exprParser -> exprParser.parseExpression(any()),
                mockedExpression,
                "[",
                "]", ","
        );

        TokenQueue queue = new TokenQueueTestBuilder().separator("(")
                                                      .keyword("number")
                                                      .separator("[")
                                                      .separator("]").separator(":")
                                                      .identifier("MyIdentifier")
                                                      .separator(")")
                                                      .build();
        assertThat(literalParser.parseStructDefinition(queue)).isSuccessful()
                                                              .resultMatches(node -> node.getFields().size() == 1)
                                                              .resultMatches(node -> "number".equals(node.getFields()
                                                                                                         .get(0)
                                                                                                         .getKey()
                                                                                                         .getType()
                                                                                                         .value()))
                                                              .resultMatches(node -> node.getFields()
                                                                                         .get(0)
                                                                                         .getKey()
                                                                                         .getArrayDimensionDefinitions()
                                                                                         .size() == 1)
                                                              .resultMatches(node -> node.getFields()
                                                                                         .get(0)
                                                                                         .getKey()
                                                                                         .getArrayDimensionDefinitions()
                                                                                         .get(0) == null)
                                                              .resultMatches(node -> "MyIdentifier".equals(node.getFields()
                                                                                                               .get(0)
                                                                                                               .getValue()
                                                                                                               .value()));

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
        assertThat(literalParser.parseStructDefinition(queue)).isSuccessful()
                                                              .resultMatches(node -> node.getFields().size() == 2)
                                                              .resultMatches(node -> "number".equals(node.getFields()
                                                                                                         .get(0)
                                                                                                         .getKey()
                                                                                                         .getType()
                                                                                                         .value()))
                                                              .resultMatches(node -> node.getFields()
                                                                                         .get(0)
                                                                                         .getKey()
                                                                                         .getArrayDimensionDefinitions()
                                                                                         .size() == 1)
                                                              .resultMatches(node -> node.getFields()
                                                                                         .get(0)
                                                                                         .getKey()
                                                                                         .getArrayDimensionDefinitions()
                                                                                         .get(0) == null)
                                                              .resultMatches(node -> "MyIdentifier1".equals(node.getFields()
                                                                                                                .get(0)
                                                                                                                .getValue()
                                                                                                                .value()))
                                                              .resultMatches(node -> "MyType".equals(node.getFields()
                                                                                                         .get(1)
                                                                                                         .getKey()
                                                                                                         .getType()
                                                                                                         .value()))
                                                              .resultMatches(node -> node.getFields()
                                                                                         .get(1)
                                                                                         .getKey()
                                                                                         .getArrayDimensionDefinitions()
                                                                                         .isEmpty())
                                                              .resultMatches(node -> "MyIdentifier2".equals(node.getFields()
                                                                                                                .get(1)
                                                                                                                .getValue()
                                                                                                                .value()));

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
    void parseStructInitialization() {
        ExpressionNode mockedExpression = mock(ExpressionNode.class);
        mockExpressionParserExecution(
                programParser,
                exprParser -> exprParser.parseExpression(any()),
                mockedExpression,
                "(",
                ")", ","
        );

        TokenQueue queue = new TokenQueueTestBuilder().separator("(").operator("expression").separator(")").build();
        assertThat(literalParser.parseStructInitialization(queue)).isSuccessful()
                                                                  .resultMatches(node -> node.getArguments()
                                                                                             .size() == 1)
                                                                  .resultMatches(node -> node.getArguments()
                                                                                             .get(0) == mockedExpression);

        queue = new TokenQueueTestBuilder().separator("(")
                                           .operator("expression")
                                           .separator(",")
                                           .operator("expression")
                                           .separator(")")
                                           .build();
        assertThat(literalParser.parseStructInitialization(queue)).isSuccessful()
                                                                  .resultMatches(node -> node.getArguments()
                                                                                             .size() == 2)
                                                                  .resultMatches(node -> node.getArguments()
                                                                                             .get(0) == mockedExpression)
                                                                  .resultMatches(node -> node.getArguments()
                                                                                             .get(1) == mockedExpression);

        queue = new TokenQueueTestBuilder().keyword("none").build();
        assertThat(literalParser.parseStructInitialization(queue)).isSuccessful().resultMatches(node -> node.getArguments().isEmpty());

        queue = new TokenQueueTestBuilder().separator("(").separator(")").build();
        assertThat(literalParser.parseStructInitialization(queue)).isUnsuccessful()
                                                                  .syntaxDiagnosticContains("Expected an expression");

        queue = new TokenQueueTestBuilder().separator("(").operator("expression").separator(";").build();
        assertThat(literalParser.parseStructInitialization(queue)).isUnsuccessful()
                                                                  .syntaxDiagnosticContains("Expected ')'");

        queue = new TokenQueueTestBuilder().separator(";").build();
        assertThat(literalParser.parseStructInitialization(queue)).isUnsuccessful()
                                                                  .syntaxDiagnosticContains("Expected '('");
    }

    @Test
    void parseFunctionDefinition() {
        StatementsNode mockedStatements = mock(StatementsNode.class);
        mockStatementParserExecution(
                programParser,
                statementParser -> statementParser.parseStatements(any()),
                mockedStatements,
                "(", ")", "{", "}", ","
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
        assertThat(literalParser.parseFunctionDefinition(queue)).isSuccessful()
                                                                .resultMatches(node -> node.getParameters().size() == 1)
                                                                .resultMatches(node -> node.getParameters()
                                                                                           .get(0)
                                                                                           .getKey()
                                                                                           .getArrayDimensionDefinitions()
                                                                                           .isEmpty())
                                                                .resultMatches(node -> "string".equals(node.getParameters()
                                                                                                           .get(0)
                                                                                                           .getKey()
                                                                                                           .getType()
                                                                                                           .value()))
                                                                .resultMatches(node -> "MyIdentifier".equals(node.getParameters()
                                                                                                                 .get(0)
                                                                                                                 .getValue()
                                                                                                                 .value()))
                                                                .resultMatches(node -> node.getFunctionBody() == mockedStatements);

        queue = new TokenQueueTestBuilder().separator("(")
                                           .separator(")")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .build();
        assertThat(literalParser.parseFunctionDefinition(queue)).isSuccessful()
                                                                .resultMatches(node -> node.getParameters().isEmpty())
                                                                .resultMatches(node -> node.getFunctionBody() == mockedStatements);

        queue = new TokenQueueTestBuilder().build();
        assertThat(literalParser.parseFunctionDefinition(queue)).isUnsuccessful()
                                                                .syntaxDiagnosticContains("Expected '('");

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
        assertThat(literalParser.parsePrimitiveLiteral(queue)).isSuccessful()
                                                              .resultMatches(primitiveLiteralNode -> "12345".equals(
                                                                      primitiveLiteralNode.getPrimitiveValue()
                                                                                          .value()))
                                                              .resultMatches(primitiveLiteralNode -> TokenType.NUMBER.equals(
                                                                      primitiveLiteralNode.getPrimitiveValue()
                                                                                          .type()));
        assertThat(literalParser.parsePrimitiveLiteral(queue)).isSuccessful()
                                                              .resultMatches(primitiveLiteralNode -> "StringValue".equals(
                                                                      primitiveLiteralNode.getPrimitiveValue()
                                                                                          .value()))
                                                              .resultMatches(primitiveLiteralNode -> TokenType.STRING.equals(
                                                                      primitiveLiteralNode.getPrimitiveValue()
                                                                                          .type()));
        assertThat(literalParser.parsePrimitiveLiteral(queue)).isSuccessful()
                                                              .resultMatches(primitiveLiteralNode -> "True".equals(
                                                                      primitiveLiteralNode.getPrimitiveValue()
                                                                                          .value()))
                                                              .resultMatches(primitiveLiteralNode -> TokenType.BOOLEAN.equals(
                                                                      primitiveLiteralNode.getPrimitiveValue()
                                                                                          .type()));

        queue = new TokenQueueTestBuilder().keyword("Keyword").build();
        assertThat(literalParser.parsePrimitiveLiteral(queue)).isUnsuccessful()
                                                              .syntaxDiagnosticContains(
                                                                      "Expected a primitive");
    }

    @Test
    void parseExceptionCreation() {
        ExpressionNode mockedExpression = mock(ExpressionNode.class);
        mockExpressionParserExecution(
                programParser,
                exprParser -> exprParser.parseExpression(any()),
                mockedExpression,
                "(",
                ")", ","
        );

        TokenQueue queue = new TokenQueueTestBuilder().identifier("MyException")
                                                      .separator("(")
                                                      .separator(")")
                                                      .build();
        assertThat(literalParser.parseExceptionCreation(queue)).isSuccessful()
                                                               .resultMatches(node -> "MyException".equals(node.getExceptionIdentifier()
                                                                                                               .getIdentifier()
                                                                                                               .value()))
                                                               .resultMatches(node -> node.getExceptionIdentifier()
                                                                                          .getArrayIndices()
                                                                                          .isEmpty())
                                                               .resultMatches(node -> node.getArguments()
                                                                                          .isEmpty());


        queue = new TokenQueueTestBuilder().identifier("MyException")
                                           .separator("(")
                                           .operator("expression1")
                                           .separator(",")
                                           .operator("expression2")
                                           .separator(")")
                                           .build();
        assertThat(literalParser.parseExceptionCreation(queue)).isSuccessful()
                                                               .resultMatches(node -> "MyException".equals(node.getExceptionIdentifier()
                                                                                                               .getIdentifier()
                                                                                                               .value()))
                                                               .resultMatches(node -> node.getExceptionIdentifier()
                                                                                          .getArrayIndices()
                                                                                          .isEmpty())
                                                               .resultMatches(node -> node.getArguments().size() == 2)
                                                               .resultMatches(node -> node.getArguments()
                                                                                          .get(0) == mockedExpression)
                                                               .resultMatches(node -> node.getArguments()
                                                                                          .get(1) == mockedExpression);

        queue = new TokenQueueTestBuilder().keyword("raise").build();
        assertThat(literalParser.parseExceptionCreation(queue)).isUnsuccessful()
                                                               .syntaxDiagnosticContains("Not a statement");

        queue = new TokenQueueTestBuilder().identifier("MyException").separator(";").build();
        assertThat(literalParser.parseExceptionCreation(queue)).isUnsuccessful()
                                                               .syntaxDiagnosticContains("Expected '('");

        queue = new TokenQueueTestBuilder().identifier("MyException")
                                           .separator("(")
                                           .operator("expression")
                                           .separator(";")
                                           .build();
        assertThat(literalParser.parseExceptionCreation(queue)).isUnsuccessful()
                                                               .syntaxDiagnosticContains("Expected ')'");
    }
}