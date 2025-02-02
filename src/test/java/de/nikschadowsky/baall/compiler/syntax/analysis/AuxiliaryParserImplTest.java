package de.nikschadowsky.baall.compiler.syntax.analysis;

import de.nikschadowsky.baall.compiler._utility.ParserMockerExtension;
import de.nikschadowsky.baall.compiler._utility.TokenQueueTestBuilder;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.TypeNode;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import de.nikschadowsky.baall.compiler.tokenizer.TokenType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static de.nikschadowsky.baall.compiler._utility.BaseAssertion.assertThat;
import static java.util.function.Predicate.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

/**
 * @since 13.08.2024
 */
class AuxiliaryParserImplTest {

    private final ASTNodeFactory astFactory = new ASTNodeFactory(new NodeDiagnosticCollector());

    private AuxiliaryParser auxiliaryParser;
    private ProgramParser programParser;

    @RegisterExtension
    final ParserMockerExtension parserMockerExtension = new ParserMockerExtension();

    @BeforeEach
    void setUp() {
        programParser = mock(ProgramParserImpl.class);
        auxiliaryParser = new AuxiliaryParserImpl(programParser, astFactory);
    }

    @Test
    void parseFieldDeclarations() {
        ExpressionNode mockedExpression = mock(ExpressionNode.class);
        parserMockerExtension.mockExpressionParserExecution(
                programParser,
                exprParser -> exprParser.parseExpression(any()),
                mockedExpression,
                ":",
                ","
        );

        TokenQueue queue = new TokenQueueTestBuilder().build();
        assertThat(auxiliaryParser.parseFieldDeclarations(queue)).isSuccessful()
                                                                 .resultMatches(List::isEmpty);

        queue = new TokenQueueTestBuilder().separator(",")
                                           .keyword("number")
                                           .separator(":")
                                           .identifier("MyIdentifier")
                                           .separator(",")
                                           .identifier("MyType")
                                           .separator("[")
                                           .separator("]")
                                           .separator(":")
                                           .identifier("MyIdentifier2")
                                           .separator(";")
                                           .build();
        assertThat(auxiliaryParser.parseFieldDeclarations(queue)).isSuccessful()
                                                                 .resultMatches(list -> list.size() == 2)
                                                                 .resultMatches(list -> list.get(0)
                                                                                            .getType()
                                                                                            .getType()
                                                                                            .getIdentifier()
                                                                                            .value()
                                                                                            .equals("number"))
                                                                 .resultMatches(list -> list.get(0)
                                                                                            .getType()
                                                                                            .getArrayDimensionDefinitions()
                                                                                            .isEmpty())
                                                                 .resultMatches(list -> list.get(0)
                                                                                            .getIdentifier()
                                                                                            .getIdentifier()
                                                                                            .value()
                                                                                            .equals("MyIdentifier"))
                                                                 .resultMatches(list -> list.get(1)
                                                                                            .getType()
                                                                                            .getType()
                                                                                            .getIdentifier()
                                                                                            .value()
                                                                                            .equals("MyType"))
                                                                 .resultMatches(list -> list.get(1)
                                                                                            .getType()
                                                                                            .getArrayDimensionDefinitions()
                                                                                            .size() == 1)
                                                                 .resultMatches(list -> list.get(1)
                                                                                            .getIdentifier()
                                                                                            .getIdentifier()
                                                                                            .value()
                                                                                            .equals("MyIdentifier2"));
        assertThat(queue).hasNextTokenValueMatch(";");


        queue = new TokenQueueTestBuilder().separator(",").identifier("MyType").separator(":").build();
        assertThat(auxiliaryParser.parseFieldDeclarations(queue)).isUnsuccessful()
                                                                 .syntaxDiagnosticContains(
                                                                         "Expected an identifier");
    }

    @Test
    void parseFieldDeclaration() {
        ExpressionNode mockedExpression = mock(ExpressionNode.class);
        parserMockerExtension.mockExpressionParserExecution(
                programParser,
                exprParser -> exprParser.parseExpression(any()),
                mockedExpression,
                ":", "[", "]"
        );

        TokenQueue queue = new TokenQueueTestBuilder().identifier("MyType")
                                                      .separator(":")
                                                      .identifier("MyIdentifier")
                                                      .separator(";")
                                                      .build();
        assertThat(auxiliaryParser.parseFieldDeclaration(queue)).isSuccessful()
                                                                .resultMatches(field -> field.getType()
                                                                                             .getType()
                                                                                             .getIdentifier()
                                                                                             .value()
                                                                                             .equals("MyType"))
                                                                .resultMatches(field -> field.getType()
                                                                                             .getArrayDimensionDefinitions()
                                                                                             .isEmpty())
                                                                .resultMatches(field -> field.getIdentifier()
                                                                                             .getIdentifier()
                                                                                             .value()
                                                                                             .equals("MyIdentifier"));
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().identifier("MyType")
                                           .separator("[")
                                           .separator("]")
                                           .separator(":")
                                           .identifier("MyIdentifier")
                                           .separator(";")
                                           .build();
        assertThat(auxiliaryParser.parseFieldDeclaration(queue)).isSuccessful()
                                                                .resultMatches(field -> field.getType()
                                                                                             .getType()
                                                                                             .getIdentifier()
                                                                                             .value()
                                                                                             .equals("MyType"))
                                                                .resultMatches(field -> field.getType()
                                                                                             .getArrayDimensionDefinitions()
                                                                                             .size() == 1)
                                                                .resultMatches(field -> field.getIdentifier()
                                                                                             .getIdentifier()
                                                                                             .value()
                                                                                             .equals("MyIdentifier"));
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().identifier("MyType")
                                           .separator("[")
                                           .separator("]")
                                           .identifier("MyIdentifier")
                                           .build();
        assertThat(auxiliaryParser.parseFieldDeclaration(queue)).isUnsuccessful()
                                                                .syntaxDiagnosticContains("Expected ':'");
    }

    @Test
    void parseArgumentList() {
        ExpressionNode mockedExpression = mock(ExpressionNode.class);
        parserMockerExtension.mockExpressionParserExecution(
                programParser,
                exprParser -> exprParser.parseExpression(any()),
                mockedExpression,
                ","
        );

        TokenQueue queue = new TokenQueueTestBuilder().build();
        assertThat(auxiliaryParser.parseArgumentList(queue)).isSuccessful().resultMatches(List::isEmpty);

        queue = new TokenQueueTestBuilder().separator(",").operator("expression").separator(";").build();
        assertThat(auxiliaryParser.parseArgumentList(queue)).isSuccessful()
                                                            .resultMatches(list -> list.size() == 1)
                                                            .resultMatches(list -> list.get(0)
                                                                                       .equals(mockedExpression));
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().separator(",").operator("expression").separator(",").operator(",").build();
        assertThat(auxiliaryParser.parseArgumentList(queue)).isUnsuccessful();
    }

    @Test
    void parseBinaryOperator() {
        TokenQueue queue = new TokenQueueTestBuilder().operator("+")
                                                      .operator(">>")
                                                      .operator(">")
                                                      .keyword("keyword")
                                                      .string("anyString")
                                                      .build();

        assertThat(auxiliaryParser.parseBinaryOperator(queue)).isSuccessful()
                                                              .resultMatches(node -> TokenType.OPERATOR.equals(
                                                                      node.getOperator().type()))
                                                              .resultMatches(token -> "+".equals(token.getOperator()
                                                                                                      .value()));
        assertThat(auxiliaryParser.parseBinaryOperator(queue)).isSuccessful()
                                                              .resultMatches(node -> TokenType.OPERATOR.equals(
                                                                      node.getOperator().type()))
                                                              .resultMatches(token -> ">>".equals(token.getOperator()
                                                                                                       .value()));
        assertThat(auxiliaryParser.parseBinaryOperator(queue)).isSuccessful()
                                                              .resultMatches(node -> TokenType.OPERATOR.equals(
                                                                      node.getOperator().type()))
                                                              .resultMatches(token -> ">".equals(token.getOperator()
                                                                                                      .value()));
        assertThat(auxiliaryParser.parseVariableAssignmentOperator(queue)).isUnsuccessful()
                                                                          .syntaxDiagnosticContains(
                                                                                  "Expected an assignment operator");
        assertThat(auxiliaryParser.parseVariableAssignmentOperator(queue)).isUnsuccessful()
                                                                          .syntaxDiagnosticContains(
                                                                                  "Expected an assignment operator");
    }

    @Test
    void parseUnaryOperator() {
        TokenQueue queue = new TokenQueueTestBuilder().operator("++")
                                                      .operator("--")
                                                      .keyword("keyword")
                                                      .string("anyString")
                                                      .build();

        assertThat(auxiliaryParser.parseUnaryOperator(queue)).isSuccessful()
                                                             .resultMatches(node -> TokenType.OPERATOR.equals(
                                                                     node.getOperator().type()))
                                                             .resultMatches(node -> ("++").equals(node.getOperator()
                                                                                                      .value()));
        assertThat(auxiliaryParser.parseUnaryOperator(queue)).isSuccessful()
                                                             .resultMatches(node -> TokenType.OPERATOR.equals(
                                                                     node.getOperator().type()))
                                                             .resultMatches(node -> "--".equals(node.getOperator()
                                                                                                    .value()));

        assertThat(auxiliaryParser.parseUnaryOperator(queue)).isUnsuccessful()
                                                             .syntaxDiagnosticContains(
                                                                     "Expected a unary operator");
        assertThat(auxiliaryParser.parseUnaryOperator(queue)).isUnsuccessful()
                                                             .syntaxDiagnosticContains(
                                                                     "Expected a unary operator");
    }

    @Test
    void parseVariableAssignmentOperator() {
        TokenQueue queue = new TokenQueueTestBuilder().operator("=")
                                                      .operator("+=")
                                                      .operator("|=")
                                                      .operator("++")
                                                      // not considered a variable assignment operator
                                                      .operator(":=")
                                                      .keyword("keyword")
                                                      .string("anyString")
                                                      .build();

        assertThat(auxiliaryParser.parseVariableAssignmentOperator(queue)).isSuccessful()
                                                                          .resultMatches(node -> TokenType.OPERATOR.equals(
                                                                                  node.getOperator().type()))
                                                                          .resultMatches(node -> "=".equals(node.getOperator()
                                                                                                                .value()));
        assertThat(auxiliaryParser.parseVariableAssignmentOperator(queue)).isSuccessful()
                                                                          .resultMatches(node -> TokenType.OPERATOR.equals(
                                                                                  node.getOperator().type()))
                                                                          .resultMatches(node -> "+=".equals(node.getOperator()
                                                                                                                 .value()));
        assertThat(auxiliaryParser.parseVariableAssignmentOperator(queue)).isSuccessful()
                                                                          .resultMatches(node -> TokenType.OPERATOR.equals(
                                                                                  node.getOperator().type()))
                                                                          .resultMatches(node -> "|=".equals(node.getOperator()
                                                                                                                 .value()));

        assertThat(auxiliaryParser.parseVariableAssignmentOperator(queue)).isUnsuccessful()
                                                                          .syntaxDiagnosticContains(
                                                                                  "Expected an assignment operator");
        assertThat(auxiliaryParser.parseVariableAssignmentOperator(queue)).isUnsuccessful()
                                                                          .syntaxDiagnosticContains(
                                                                                  "Expected an assignment operator");
        assertThat(auxiliaryParser.parseVariableAssignmentOperator(queue)).isUnsuccessful()
                                                                          .syntaxDiagnosticContains(
                                                                                  "Expected an assignment operator");
        assertThat(auxiliaryParser.parseVariableAssignmentOperator(queue)).isUnsuccessful()
                                                                          .syntaxDiagnosticContains(
                                                                                  "Expected an assignment operator");
    }

    @Test
    void parsePrefixOperator() {
        TokenQueue queue = new TokenQueueTestBuilder().operator("+").operator("-").operator("!").build();

        assertThat(auxiliaryParser.parsePrefixOperator(queue)).isSuccessful()
                                                              .resultMatches(node -> "+".equals(node.getOperator()
                                                                                                    .value()));
        assertThat(auxiliaryParser.parsePrefixOperator(queue)).isSuccessful()
                                                              .resultMatches(node -> "-".equals(node.getOperator()
                                                                                                    .value()));
        assertThat(auxiliaryParser.parsePrefixOperator(queue)).isSuccessful()
                                                              .resultMatches(node -> "!".equals(node.getOperator()
                                                                                                    .value()));

        queue = new TokenQueueTestBuilder().operator("++").build();
        assertThat(auxiliaryParser.parsePrefixOperator(queue)).isUnsuccessful()
                                                              .syntaxDiagnosticContains("Expected a prefix operator");
    }

    @Test
    void parseIdentifier() {
        TokenQueue queue = new TokenQueueTestBuilder().identifier("MyIdentifier").separator(";").build();

        // parsing identifier
        assertThat(auxiliaryParser.parseIdentifier(queue)).isSuccessful()
                                                          .resultMatches(node -> TokenType.IDENTIFIER.equals(
                                                                  node.getIdentifier().type()))
                                                          .resultMatches(node -> "MyIdentifier".equals(
                                                                  node.getIdentifier().value()));
        // parsing number
        assertThat(auxiliaryParser.parseIdentifier(queue)).isUnsuccessful()
                                                          .syntaxDiagnosticContains("Expected an identifier");
    }

    @Test
    void parseType() {
        ExpressionNode mockedExpression = mock(ExpressionNode.class);
        parserMockerExtension.mockExpressionParserExecution(
                programParser,
                exprParser -> exprParser.parseExpression(any()),
                mockedExpression,
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

        assertThat(auxiliaryParser.parseType(queue)).isSuccessful()
                                                    .resultMatches(node -> "string".equals(node.getType()
                                                                                               .getIdentifier()
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
                                                                               .get(0) == mockedExpression)
                                                    .resultMatches(TypeNode::isNoneSafe);
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier").keyword("another statement").build();
        assertThat(auxiliaryParser.parseType(queue)).isSuccessful()
                                                    .resultMatches(node -> "MyIdentifier".equals(node.getType()
                                                                                                     .getIdentifier()
                                                                                                     .value()))
                                                    .resultMatches(node -> node.getArrayDimensionDefinitions()
                                                                               .isEmpty())
                                                    .resultMatches(not(TypeNode::isNoneSafe));
        assertThat(queue).hasNextTokenValueMatch("another statement");

        queue = new TokenQueueTestBuilder().identifier("MyType")
                                           .operator("!")
                                           .separator("[")
                                           .number("4")
                                           .separator("]")
                                           .separator(";")
                                           .build();
        assertThat(auxiliaryParser.parseType(queue)).isSuccessful()
                                                    .resultMatches(node -> "MyType".equals(node.getType()
                                                                                               .getIdentifier()
                                                                                               .value()))
                                                    .resultMatches(node -> node.getArrayDimensionDefinitions()
                                                                               .get(0) == mockedExpression)
                                                    .resultMatches(TypeNode::isNoneSafe);

        queue = new TokenQueueTestBuilder().operator("+")
                                           .separator("[")
                                           .keyword("another statement")
                                           .separator("]")
                                           .build();
        assertThat(auxiliaryParser.parseType(queue)).isUnsuccessful()
                                                    .syntaxDiagnosticContains("Expected a type");

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                           .separator("[")
                                           .separator(";")
                                           .build();
        assertThat(auxiliaryParser.parseType(queue)).isUnsuccessful()
                                                    .syntaxDiagnosticContains("Expected ']'");
    }

    @Test
    void parseArrayTypeDefinition() {
        ExpressionNode mockedExpression = mock(ExpressionNode.class);
        parserMockerExtension.mockExpressionParserExecution(
                programParser,
                expParser -> expParser.parseExpression(any()),
                mockedExpression,
                "[",
                "]"
        );

        TokenQueue queue = new TokenQueueTestBuilder().build();
        assertThat(auxiliaryParser.parseArrayTypeDefinition(queue)).isSuccessful()
                                                                   .resultMatches(List::isEmpty);

        queue = new TokenQueueTestBuilder().separator("[")
                                           .separator("]")
                                           .separator("[")
                                           .operator("expression")
                                           .separator("]")
                                           .separator(";")
                                           .build();
        assertThat(auxiliaryParser.parseArrayTypeDefinition(queue)).isSuccessful()
                                                                   .resultMatches(list -> list.size() == 2)
                                                                   .resultMatches(list -> list.get(0) == null)
                                                                   .resultMatches(list -> list.get(1) == mockedExpression);
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().separator("[").separator(";").separator(".").build();
        assertThat(auxiliaryParser.parseArrayTypeDefinition(queue)).isUnsuccessful()
                                                                   .syntaxDiagnosticContains("Expected ']'");
    }

    @Test
    void parseIdentifierAccess() {
        ExpressionNode mockedExpression = mock(ExpressionNode.class);
        parserMockerExtension.mockExpressionParserExecution(
                programParser,
                expParser -> expParser.parseExpression(any()),
                mockedExpression,
                "[",
                "]"
        );

        TokenQueue queue = new TokenQueueTestBuilder().identifier("MyIdentifier").separator(";").build();
        assertThat(auxiliaryParser.parseIdentifierAccess(queue)).isSuccessful()
                                                                .resultMatches(ian -> "MyIdentifier".equals(
                                                                        ian.getIdentifier().getIdentifier().value()))
                                                                .resultMatches(ian -> ian.getArrayIndices()
                                                                                         .isEmpty());
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                           .separator("[")
                                           .operator("expression")
                                           .separator("]")
                                           .separator(";")
                                           .build();
        assertThat(auxiliaryParser.parseIdentifierAccess(queue)).isSuccessful()
                                                                .resultMatches(ian -> "MyIdentifier".equals(
                                                                        ian.getIdentifier().getIdentifier().value()))
                                                                .resultMatches(ian -> ian.getArrayIndices()
                                                                                         .size() == 1);
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                           .separator("[")
                                           .operator("expression")
                                           .separator(";")
                                           .build();

        assertThat(auxiliaryParser.parseIdentifierAccess(queue)).isUnsuccessful()
                                                                .syntaxDiagnosticContains("Expected ']'");

        queue = new TokenQueueTestBuilder().separator(";").build();
        assertThat(auxiliaryParser.parseIdentifierAccess(queue)).isUnsuccessful()
                                                                .syntaxDiagnosticContains(
                                                                        "Expected an identifier");
    }

    @Test
    void parseArrayIndexInformation() {
        ExpressionNode mockedExpression = mock(ExpressionNode.class);
        parserMockerExtension.mockExpressionParserExecution(
                programParser,
                expParser -> expParser.parseExpression(any()),
                mockedExpression,
                "[",
                "]"
        );

        TokenQueue queue = new TokenQueueTestBuilder().build();
        assertThat(auxiliaryParser.parseArrayIndexInformation(queue)).isSuccessful()
                                                                     .resultMatches(List::isEmpty);

        queue = new TokenQueueTestBuilder().separator("[").operator("expression").separator("]").separator(";").build();
        assertThat(auxiliaryParser.parseArrayIndexInformation(queue)).isSuccessful()
                                                                     .resultMatches(list -> list.size() == 1)
                                                                     .resultMatches(lst -> lst.get(0) == mockedExpression);
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().separator("[")
                                           .operator("expression")
                                           .separator("]")
                                           .separator("[")
                                           .operator("expression")
                                           .separator("]")
                                           .separator(";")
                                           .build();
        assertThat(auxiliaryParser.parseArrayIndexInformation(queue)).isSuccessful()
                                                                     .resultMatches(list -> list.size() == 2)
                                                                     .resultMatches(list -> list.get(0) == mockedExpression)
                                                                     .resultMatches(list -> list.get(1) == mockedExpression);
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().separator("[").separator(";").build();
        assertThat(auxiliaryParser.parseArrayIndexInformation(queue)).isUnsuccessful()
                                                                     .syntaxDiagnosticContains(
                                                                             "Expected ']'");
    }

    @Test
    void parseAdditionalIdentifierAccesses() {
        ExpressionNode mockedExpression = mock(ExpressionNode.class);
        parserMockerExtension.mockExpressionParserExecution(
                programParser,
                expParser -> expParser.parseExpression(any()),
                mockedExpression,
                "[",
                "]"
        );

        TokenQueue queue = new TokenQueueTestBuilder().build();
        assertThat(auxiliaryParser.parseAdditionalIdentifierAccesses(queue)).isSuccessful()
                                                                            .resultMatches(List::isEmpty);

        queue = new TokenQueueTestBuilder().separator(",")
                                           .identifier("MyIdentifier")
                                           .separator(",")
                                           .identifier("MyIdentifier")
                                           .separator("[")
                                           .operator("expression")
                                           .separator("]")
                                           .separator("[")
                                           .operator("expression")
                                           .separator("]")
                                           .separator(";")
                                           .build();
        assertThat(auxiliaryParser.parseAdditionalIdentifierAccesses(queue)).isSuccessful()
                                                                            .resultMatches(list -> list.size() == 2)
                                                                            .resultMatches(list -> list.stream()
                                                                                                       .allMatch(
                                                                                                               ian -> "MyIdentifier".equals(
                                                                                                                       ian.getIdentifier()
                                                                                                                          .getIdentifier()
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
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().separator(",").keyword("keyword").build();
        assertThat(auxiliaryParser.parseAdditionalIdentifierAccesses(queue)).isPartiallyParsed()
                                                                            .resultMatches(List::isEmpty)
                                                                            .syntaxDiagnosticContains(
                                                                                    "Expected an identifier");

        queue = new TokenQueueTestBuilder().separator(",")
                                           .identifier("MyIdentifier")
                                           .separator(",")
                                           .keyword("keyword")
                                           .build();
        assertThat(auxiliaryParser.parseAdditionalIdentifierAccesses(queue)).isPartiallyParsed()
                                                                            .resultMatches(list -> list.size() == 1)
                                                                            .syntaxDiagnosticContains(
                                                                                    "Expected an identifier");
        // parsing gets rolled back when the parsing of 'keyword' fails
        assertThat(queue).hasNextTokenValueMatch(",");
    }

    @Test
    void parseCodeBlock() {
        StatementsNode mockedStatements = mock(StatementsNode.class);
        parserMockerExtension.mockStatementParserExecution(
                programParser,
                parser -> parser.parseStatements(any()),
                mockedStatements,
                "{",
                "}"
        );

        TokenQueue queue =
                new TokenQueueTestBuilder().separator("{").keyword("statements").separator("}").separator(";").build();
        assertThat(auxiliaryParser.parseCodeBlock(queue)).isSuccessful()
                                                         .resultMatches(node -> node == mockedStatements);
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().separator("{").keyword("statements").separator(";").separator(";").build();
        assertThat(auxiliaryParser.parseCodeBlock(queue)).isPartiallyParsed()
                                                         .syntaxDiagnosticContains("Expected '}'")
                                                         .resultMatches(statements -> statements == mockedStatements);
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().separator(";").keyword("statements").separator("}").build();
        assertThat(auxiliaryParser.parseCodeBlock(queue)).isUnsuccessful()
                                                         .syntaxDiagnosticContains("Expected '{'");
    }
}