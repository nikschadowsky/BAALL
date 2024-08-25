package de.nikschadowsky.baall.compiler.syntax.analysis;

import de.nikschadowsky.baall.compiler._utility.TokenQueueTestBuilder;
import de.nikschadowsky.baall.compiler.lexer.tokenizer.TokenType;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntaxtree.util.NodeDiagnosticCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.nikschadowsky.baall.compiler._utility.BaseAssertion.assertThat;
import static de.nikschadowsky.baall.compiler._utility.ParserMocker.mockExpressionParserExecution;
import static de.nikschadowsky.baall.compiler._utility.ParserMocker.mockStatementParserExecution;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

/**
 * @since 13.08.2024
 */
class AuxiliaryParserTest {

    private final ASTNodeFactory astFactory = new ASTNodeFactory(new NodeDiagnosticCollector());

    private AuxiliaryParser auxiliaryParser;
    private ProgramParser programParser;

    @BeforeEach
    void setUp() {
        programParser = mock(ProgramParserImpl.class);
        auxiliaryParser = new AuxiliaryParserImpl(programParser);
    }

    @Test
    void parseFieldDeclarations() {
        fail("Not yet implemented. Requires #parseFieldDeclaration()");
    }

    @Test
    void parseFieldDeclaration() {
        ExpressionNode mockedExpression = mock(ExpressionNode.class);
        mockExpressionParserExecution(programParser, exprParser -> exprParser.parseExpression(any(), any()), mockedExpression, false, ":");

        TokenQueue queue = new TokenQueueTestBuilder().identifier("MyIdentifier").separator(":").identifier("MyIdentifier").build();

        fail("Not yet implemented. Requires #parseType()");
    }

    @Test
    void parseArgumentList() {
        fail("Not yet implemented. Requires #parseExpression()");
    }

    @Test
    void parseBinaryOperator() {
        TokenQueue queue = new TokenQueueTestBuilder().operator("+")
                                                      .operator(">>")
                                                      .operator(">")
                                                      .keyword("keyword")
                                                      .string("anyString")
                                                      .build();

        assertThat(auxiliaryParser.parseBinaryOperator(queue, astFactory)).isSuccessful()
                                                                          .resultMatches(token -> TokenType.OPERATOR.equals(
                                                                                  token.type()))
                                                                          .resultMatches(token -> "+".equals(token.value()));
        assertThat(auxiliaryParser.parseBinaryOperator(queue, astFactory)).isSuccessful()
                                                                          .resultMatches(token -> TokenType.OPERATOR.equals(
                                                                                  token.type()))
                                                                          .resultMatches(token -> ">>".equals(token.value()));
        assertThat(auxiliaryParser.parseBinaryOperator(queue, astFactory)).isSuccessful()
                                                                          .resultMatches(token -> TokenType.OPERATOR.equals(
                                                                                  token.type()))
                                                                          .resultMatches(token -> ">".equals(token.value()));
        assertThat(auxiliaryParser.parseShorthandOperator(queue, astFactory)).isUnsuccessful()
                                                                             .syntaxDiagnosticContains(
                                                                                     "Expected a assignment operator");
        assertThat(auxiliaryParser.parseShorthandOperator(queue, astFactory)).isUnsuccessful()
                                                                             .syntaxDiagnosticContains(
                                                                                     "Expected a assignment operator");
    }

    @Test
    void parseUnaryOperator() {
        TokenQueue queue = new TokenQueueTestBuilder().operator("++")
                                                      .operator("--")
                                                      .keyword("keyword")
                                                      .string("anyString")
                                                      .build();

        assertThat(auxiliaryParser.parseUnaryOperator(queue, astFactory)).isSuccessful()
                                                                         .resultMatches(token -> TokenType.OPERATOR.equals(
                                                                                 token.type()))
                                                                         .resultMatches(token -> ("++").equals(token.value()));
        assertThat(auxiliaryParser.parseUnaryOperator(queue, astFactory)).isSuccessful()
                                                                         .resultMatches(token -> TokenType.OPERATOR.equals(
                                                                                 token.type()))
                                                                         .resultMatches(token -> "--".equals(token.value()));

        assertThat(auxiliaryParser.parseUnaryOperator(queue, astFactory)).isUnsuccessful()
                                                                         .syntaxDiagnosticContains(
                                                                                 "Expected a unary operator");
        assertThat(auxiliaryParser.parseUnaryOperator(queue, astFactory)).isUnsuccessful()
                                                                         .syntaxDiagnosticContains(
                                                                                 "Expected a unary operator");

    }

    @Test
    void parseShorthandOperator() {
        TokenQueue queue = new TokenQueueTestBuilder().operator(":=")
                                                      .operator("=")
                                                      .operator("+=")
                                                      .operator("|=")
                                                      .operator("++")
                                                      .keyword("keyword")
                                                      .string("anyString")
                                                      .build();

        assertThat(auxiliaryParser.parseShorthandOperator(queue, astFactory)).isSuccessful()
                                                                             .resultMatches(token -> TokenType.OPERATOR.equals(
                                                                                     token.type()))
                                                                             .resultMatches(token -> ":=".equals(token.value()));
        assertThat(auxiliaryParser.parseShorthandOperator(queue, astFactory)).isSuccessful()
                                                                             .resultMatches(token -> TokenType.OPERATOR.equals(
                                                                                     token.type()))
                                                                             .resultMatches(token -> "=".equals(token.value()));
        assertThat(auxiliaryParser.parseShorthandOperator(queue, astFactory)).isSuccessful()
                                                                             .resultMatches(token -> TokenType.OPERATOR.equals(
                                                                                     token.type()))
                                                                             .resultMatches(token -> "+=".equals(token.value()));
        assertThat(auxiliaryParser.parseShorthandOperator(queue, astFactory)).isSuccessful()
                                                                             .resultMatches(token -> TokenType.OPERATOR.equals(
                                                                                     token.type()))
                                                                             .resultMatches(token -> "|=".equals(token.value()));

        assertThat(auxiliaryParser.parseShorthandOperator(queue, astFactory)).isUnsuccessful()
                                                                             .syntaxDiagnosticContains(
                                                                                     "Expected a assignment operator");
        assertThat(auxiliaryParser.parseShorthandOperator(queue, astFactory)).isUnsuccessful()
                                                                             .syntaxDiagnosticContains(
                                                                                     "Expected a assignment operator");
        assertThat(auxiliaryParser.parseShorthandOperator(queue, astFactory)).isUnsuccessful()
                                                                             .syntaxDiagnosticContains(
                                                                                     "Expected a assignment operator");
    }

    @Test
    void parseIdentifier() {
        TokenQueue queue = new TokenQueueTestBuilder().identifier("MyIdentifier").separator(";").build();

        // parsing identifier
        assertThat(auxiliaryParser.parseIdentifier(queue, astFactory)).isSuccessful()
                                                                      .resultMatches(token -> TokenType.IDENTIFIER.equals(
                                                                              token.type()))
                                                                      .resultMatches(token -> "MyIdentifier".equals(
                                                                              token.value()));
        // parsing number
        assertThat(auxiliaryParser.parseIdentifier(queue, astFactory)).isUnsuccessful()
                                                                      .syntaxDiagnosticContains("Expected an identifier");
    }

    @Test
    void parseType() {
        ExpressionNode mockedExpression = mock(ExpressionNode.class);
        mockExpressionParserExecution(
                programParser,
                exprParser -> exprParser.parseExpression(any(), any()),
                mockedExpression,
                false,
                "[",
                "]"
        );

        TokenQueue queue = new TokenQueueTestBuilder().keyword("string")
                                                      .separator("[")
                                                      .operator("expression")
                                                      .separator("]")
                                                      .separator("[")
                                                      .separator("]")
                                                      .separator("[")
                                                      .separator("]")
                                                      .separator("[")
                                                      .operator("expression")
                                                      .separator("]")
                                                      .separator(";")
                                                      .build();

        assertThat(auxiliaryParser.parseType(queue, astFactory)).isSuccessful()
                                                                .resultMatches(node -> "string".equals(node.getType()
                                                                                                           .value()))
                                                                .resultMatches(node -> node.getArrayDimensionDefinitions()
                                                                                           .size() == 4)
                                                                .resultMatches(node -> node.getArrayDimensionDefinitions()
                                                                                           .get(0) == mockedExpression)
                                                                .resultMatches(node -> node.getArrayDimensionDefinitions()
                                                                                           .get(1) == null)
                                                                .resultMatches(node -> node.getArrayDimensionDefinitions()
                                                                                           .get(1) == null)
                                                                .resultMatches(node -> node.getArrayDimensionDefinitions()
                                                                                           .get(0) == mockedExpression);

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier").keyword("another statement").build();
        assertThat(auxiliaryParser.parseType(queue, astFactory)).isSuccessful()
                                                                .resultMatches(node -> "MyIdentifier".equals(node.getType()
                                                                                                                 .value()))
                                                                .resultMatches(node -> node.getArrayDimensionDefinitions()
                                                                                           .isEmpty());

        queue = new TokenQueueTestBuilder().operator("+")
                                           .separator("[")
                                           .keyword("another statement")
                                           .separator("]")
                                           .build();
        assertThat(auxiliaryParser.parseType(queue, astFactory)).isUnsuccessful()
                                                                .syntaxDiagnosticContains("Expected a type");

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier").separator("[").separator(";").build();
        assertThat(auxiliaryParser.parseType(queue, astFactory)).isUnsuccessful()
                                                                .syntaxDiagnosticContains("Expected ']'");

    }

    @Test
    void parseSimpleType() {
        TokenQueue queue = new TokenQueueTestBuilder().keyword("number")
                                                      .keyword("string")
                                                      .identifier("MyIdentifier")
                                                      .bool("true")
                                                      .build();

        assertThat(auxiliaryParser.parseSimpleType(queue, astFactory)).isSuccessful()
                                                                      .resultMatches(token -> TokenType.KEYWORD.equals(
                                                                              token.type()))
                                                                      .resultMatches(token -> "number".equals(token.value()));

        assertThat(auxiliaryParser.parseSimpleType(queue, astFactory)).isSuccessful()
                                                                      .resultMatches(token -> TokenType.KEYWORD.equals(
                                                                              token.type()))
                                                                      .resultMatches(token -> "string".equals(token.value()));

        assertThat(auxiliaryParser.parseSimpleType(queue, astFactory)).isSuccessful()
                                                                      .resultMatches(token -> TokenType.IDENTIFIER.equals(
                                                                              token.type()))
                                                                      .resultMatches(token -> "MyIdentifier".equals(
                                                                              token.value()));

        assertThat(auxiliaryParser.parseSimpleType(queue, astFactory)).isUnsuccessful()
                                                                      .syntaxDiagnosticContains("Expected a type");
    }


    @Test
    void parseArrayTypeDefinition() {
        ExpressionNode mockedExpression = mock(ExpressionNode.class);
        mockExpressionParserExecution(
                programParser,
                expParser -> expParser.parseExpression(any(), any()),
                mockedExpression,
                false,
                "[",
                "]"
        );


        TokenQueue queue = new TokenQueueTestBuilder().separator("[")
                                                      .separator("]")
                                                      .separator("[")
                                                      .operator("expression")
                                                      .separator("]")
                                                      .build();

        assertThat(auxiliaryParser.parseArrayTypeDefinition(queue, astFactory)).isSuccessful()
                                                                               .resultMatches(list -> list.size() == 2)
                                                                               .resultMatches(list -> list.get(0) == null)
                                                                               .resultMatches(list -> list.get(1) == mockedExpression);

        queue = new TokenQueueTestBuilder().build();
        assertThat(auxiliaryParser.parseArrayTypeDefinition(queue, astFactory)).isSuccessful()
                                                                               .resultMatches(List::isEmpty);

        queue = new TokenQueueTestBuilder().separator("[").separator(";").build();
        assertThat(auxiliaryParser.parseArrayTypeDefinition(queue, astFactory)).isUnsuccessful()
                                                                               .syntaxDiagnosticContains("Expected ']'");
    }

    @Test
    void parseIdentifierAccess() {
        ExpressionNode mockedExpression = mock(ExpressionNode.class);
        mockExpressionParserExecution(
                programParser,
                expParser -> expParser.parseExpression(any(), any()),
                mockedExpression,
                false,
                "[",
                "]"
        );


        TokenQueue queue = new TokenQueueTestBuilder().identifier("MyIdentifier").separator(";").build();
        assertThat(auxiliaryParser.parseIdentifierAccess(queue, astFactory)).isSuccessful()
                                                                            .resultMatches(ian -> "MyIdentifier".equals(
                                                                                    ian.getIdentifier().value()))
                                                                            .resultMatches(ian -> ian.getArrayIndices()
                                                                                                     .isEmpty());

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                           .separator("[")
                                           .operator("expression")
                                           .separator("]")
                                           .separator(";")
                                           .build();

        assertThat(auxiliaryParser.parseIdentifierAccess(queue, astFactory)).isSuccessful()
                                                                            .resultMatches(ian -> "MyIdentifier".equals(
                                                                                    ian.getIdentifier().value()))
                                                                            .resultMatches(ian -> ian.getArrayIndices()
                                                                                                     .size() == 1);

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                           .separator("[")
                                           .operator("expression")
                                           .separator(";")
                                           .build();

        assertThat(auxiliaryParser.parseIdentifierAccess(queue, astFactory)).isUnsuccessful()
                                                                            .syntaxDiagnosticContains("Expected ']'");

        queue = new TokenQueueTestBuilder().separator(";").build();
        assertThat(auxiliaryParser.parseIdentifierAccess(queue, astFactory)).isUnsuccessful()
                                                                            .syntaxDiagnosticContains(
                                                                                    "Expected an identifier");
    }

    @Test
    void parseArrayIndexInformation() {
        ExpressionNode mockedExpression = mock(ExpressionNode.class);
        mockExpressionParserExecution(
                programParser,
                expParser -> expParser.parseExpression(any(), any()),
                mockedExpression,
                false,
                "[",
                "]"
        );

        TokenQueue queue =
                new TokenQueueTestBuilder().separator("[").operator("expression").separator("]").separator(";").build();

        assertThat(auxiliaryParser.parseArrayIndexInformation(queue, astFactory)).isSuccessful()
                                                                                 .resultMatches(lst -> lst.size() == 1)
                                                                                 .resultMatches(lst -> lst.get(0) == mockedExpression);

        queue = new TokenQueueTestBuilder().separator("[")
                                           .operator("expression")
                                           .separator("]")
                                           .separator("[")
                                           .operator("expression")
                                           .separator("]")
                                           .separator(";")
                                           .build();

        assertThat(auxiliaryParser.parseArrayIndexInformation(queue, astFactory)).isSuccessful()
                                                                                 .resultMatches(lst -> lst.size() == 2)
                                                                                 .resultMatches(lst -> lst.get(0) == mockedExpression)
                                                                                 .resultMatches(lst -> lst.get(1) == mockedExpression);


        queue = new TokenQueueTestBuilder().separator("[").separator(";").build();
        assertThat(auxiliaryParser.parseArrayIndexInformation(queue, astFactory)).isUnsuccessful()
                                                                                 .syntaxDiagnosticContains(
                                                                                         "Expected ']'");


    }

    @Test
    void parseAdditionalIdentifierAccesses() {
        ExpressionNode mockedExpression = mock(ExpressionNode.class);
        mockExpressionParserExecution(
                programParser,
                expParser -> expParser.parseExpression(any(), any()),
                mockedExpression,
                false,
                "[",
                "]"
        );

        TokenQueue queue = new TokenQueueTestBuilder().separator(",")
                                                      .identifier("MyIdentifier")
                                                      .separator(",")
                                                      .identifier("MyIdentifier")
                                                      .separator("[")
                                                      .operator("expression")
                                                      .separator("]")
                                                      .separator("[")
                                                      .operator("expression")
                                                      .separator("]")
                                                      .build();

        assertThat(auxiliaryParser.parseAdditionalIdentifierAccesses(queue, astFactory)).isSuccessful()
                                                                                        .resultMatches(list -> list.size() == 2)
                                                                                        .resultMatches(list -> list.stream()
                                                                                                                   .allMatch(
                                                                                                                           ian -> "MyIdentifier".equals(
                                                                                                                                   ian.getIdentifier()
                                                                                                                                      .value())))
                                                                                        .resultMatches(list -> list.get(
                                                                                                                           0)
                                                                                                                   .getArrayIndices()
                                                                                                                   .isEmpty())
                                                                                        .resultMatches(list -> list.get(
                                                                                                                           1)
                                                                                                                   .getArrayIndices()
                                                                                                                   .size() == 2)
                                                                                        .resultMatches(list -> list.get(
                                                                                                                           1)
                                                                                                                   .getArrayIndices()
                                                                                                                   .get(0) == mockedExpression)
                                                                                        .resultMatches(list -> list.get(
                                                                                                                           1)
                                                                                                                   .getArrayIndices()
                                                                                                                   .get(1) == mockedExpression);


        queue = new TokenQueueTestBuilder().separator(";").build();
        assertThat(auxiliaryParser.parseAdditionalIdentifierAccesses(queue, astFactory)).isSuccessful()
                                                                                        .resultMatches(List::isEmpty);

        queue = new TokenQueueTestBuilder().separator(",").keyword("keyword").build();
        assertThat(auxiliaryParser.parseAdditionalIdentifierAccesses(queue, astFactory)).isUnsuccessful()
                                                                                        .syntaxDiagnosticContains(
                                                                                                "Expected an identifier");
    }

    @Test
    void parseCodeBlock() {
        StatementsNode mockedStatements = mock(StatementsNode.class);
        mockStatementParserExecution(
                programParser,
                parser -> parser.parseStatements(any(), any()),
                mockedStatements,
                "{",
                "}"
        );

        TokenQueue queue =
                new TokenQueueTestBuilder().separator("{").keyword("statements").separator("}").separator(";").build();
        assertThat(auxiliaryParser.parseCodeBlock(queue, astFactory)).isSuccessful()
                                                                     .resultMatches(node -> node == mockedStatements);


        queue = new TokenQueueTestBuilder().separator("{").keyword("statements").separator(";").build();
        assertThat(auxiliaryParser.parseCodeBlock(queue, astFactory)).isPartiallyParsed()
                                                                     .syntaxDiagnosticContains("Expected '}'")
                                                                     .resultMatches(statements -> statements == mockedStatements);

        queue = new TokenQueueTestBuilder().separator(";").keyword("statements").separator("}").build();
        assertThat(auxiliaryParser.parseCodeBlock(queue, astFactory)).isUnsuccessful()
                                                                     .syntaxDiagnosticContains("Expected '{'");
    }
}