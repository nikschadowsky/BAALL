package de.nikschadowsky.baall.compiler.syntax.analysis;

import de.nikschadowsky.baall.compiler._utility.ParserMockerExtension;
import de.nikschadowsky.baall.compiler._utility.TokenQueueTestBuilder;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.*;
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
        assertThat(literalParser.parseLiteral(queue)).isSuccessful()
                                                     .resultMatches(node -> "primitive".equals(((PrimitiveLiteralNode) node).getPrimitiveValue()
                                                                                                                            .value()));
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().separator("[").separator("]").build();
        assertThat(literalParser.parseLiteral(queue)).isSuccessful()
                                                     .resultMatches(node -> ((ArrayLiteralNode) node).getElements()
                                                                                                     .isEmpty());
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                           .separator("(")
                                           .operator("expression")
                                           .separator(")")
                                           .build();
        assertThat(literalParser.parseLiteral(queue)).isSuccessful()
                                                     .resultMatches(node -> "MyIdentifier".equals(((StructInitializationLiteralNode) node).getIdentifier()
                                                                                                                                          .getIdentifier()
                                                                                                                                          .value()))
                                                     .resultMatches(node -> ((StructInitializationLiteralNode) node).getIdentifier()
                                                                                                                    .getArrayIndices()
                                                                                                                    .isEmpty())
                                                     .resultMatches(node -> ((StructInitializationLiteralNode) node).getArguments()
                                                                                                                    .size() == 1)
                                                     .resultMatches(node -> ((StructInitializationLiteralNode) node).getArguments()
                                                                                                                    .get(0) == mockedExpression);
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().separator("(")
                                           .keyword("string")
                                           .separator(":")
                                           .identifier("MyIdentifier")
                                           .separator(")")
                                           .build();
        assertThat(literalParser.parseLiteral(queue)).isSuccessful()
                                                     .resultMatches(node -> ((StructDefinitionLiteralNode) node).getFieldTypes()
                                                                                                                .size() == 1)
                                                     .resultMatches(node -> ((StructDefinitionLiteralNode) node).getFieldNames()
                                                                                                                .size() == 1)
                                                     .resultMatches(node -> "string".equals(((StructDefinitionLiteralNode) node).getFieldTypes()
                                                                                                                                .get(0)
                                                                                                                                .getType()
                                                                                                                                .value()))
                                                     .resultMatches(node -> ((StructDefinitionLiteralNode) node).getFieldTypes()
                                                                                                                .get(0)
                                                                                                                .getArrayDimensionDefinitions()
                                                                                                                .isEmpty())
                                                     .resultMatches(node -> "MyIdentifier".equals(((StructDefinitionLiteralNode) node).getFieldNames()
                                                                                                                                      .get(0)
                                                                                                                                      .value()));
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
        assertThat(literalParser.parseLiteral(queue)).isSuccessful()
                                                     .resultMatches(node -> ((FunctionDefinitionNode) node).getParameterTypes()
                                                                                                           .size() == 1)
                                                     .resultMatches(node -> ((FunctionDefinitionNode) node).getParameterNames()
                                                                                                           .size() == 1)
                                                     .resultMatches(node -> ((FunctionDefinitionNode) node).getFunctionBody() == mockedStatements);
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().separator("[").build();
        assertThat(literalParser.parseLiteral(queue)).isUnsuccessful().syntaxDiagnosticContains("Expected a literal");
    }

    @Test
    void parseArrayLiteral() {
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
        assertThat(literalParser.parseArrayLiteral(queue)).isSuccessful()
                                                          .resultMatches(node -> node.getElements().isEmpty());
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().separator("[").operator("expression").separator("]").build();
        assertThat(literalParser.parseArrayLiteral(queue)).isSuccessful()
                                                          .resultMatches(node -> node.getElements().size() == 1)
                                                          .resultMatches(node -> node.getElements()
                                                                                     .get(0) == mockedExpression);
        assertThat(queue).isAtEnd();

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
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().separator("[").operator("expression").separator(";").build();
        assertThat(literalParser.parseArrayLiteral(queue)).isUnsuccessful().syntaxDiagnosticContains("Expected ']'");

        queue = new TokenQueueTestBuilder().separator(";").build();
        assertThat(literalParser.parseArrayLiteral(queue)).isUnsuccessful().syntaxDiagnosticContains("Expected '['");
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
        assertThat(literalParser.parseStructDefinition(queue)).isSuccessful()
                                                              .resultMatches(node -> node.getFieldTypes().size() == 1)
                                                              .resultMatches(node -> node.getFieldNames().size() == 1)
                                                              .resultMatches(node -> "number".equals(node.getFieldTypes()
                                                                                                         .get(0)
                                                                                                         .getType()
                                                                                                         .value()))
                                                              .resultMatches(node -> node.getFieldTypes()
                                                                                         .get(0)
                                                                                         .getArrayDimensionDefinitions()
                                                                                         .size() == 1)
                                                              .resultMatches(node -> node.getFieldTypes()
                                                                                         .get(0)
                                                                                         .getArrayDimensionDefinitions()
                                                                                         .get(0) == null)
                                                              .resultMatches(node -> "MyIdentifier".equals(node.getFieldNames()
                                                                                                               .get(0)
                                                                                                               .value()));
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
        assertThat(literalParser.parseStructDefinition(queue)).isSuccessful()
                                                              .resultMatches(node -> node.getFieldTypes().size() == 2)
                                                              .resultMatches(node -> node.getFieldNames().size() == 2)
                                                              .resultMatches(node -> "number".equals(node.getFieldTypes()
                                                                                                         .get(0)
                                                                                                         .getType()
                                                                                                         .value()))
                                                              .resultMatches(node -> node.getFieldTypes()
                                                                                         .get(0)
                                                                                         .getArrayDimensionDefinitions()
                                                                                         .size() == 1)
                                                              .resultMatches(node -> node.getFieldTypes()
                                                                                         .get(0)
                                                                                         .getArrayDimensionDefinitions()
                                                                                         .get(0) == null)
                                                              .resultMatches(node -> "MyIdentifier1".equals(node.getFieldNames()
                                                                                                                .get(0)
                                                                                                                .value()))
                                                              .resultMatches(node -> "MyType".equals(node.getFieldTypes()
                                                                                                         .get(1)
                                                                                                         .getType()
                                                                                                         .value()))
                                                              .resultMatches(node -> node.getFieldTypes()
                                                                                         .get(1)
                                                                                         .getArrayDimensionDefinitions()
                                                                                         .isEmpty())
                                                              .resultMatches(node -> "MyIdentifier2".equals(node.getFieldNames()
                                                                                                                .get(1)
                                                                                                                .value()));
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
    void parseStructInitialization() {
        ExpressionNode mockedExpression = mock(ExpressionNode.class);
        parserMockerExtension.mockExpressionParserExecution(
                programParser,
                exprParser -> exprParser.parseExpression(any()),
                mockedExpression,
                "(",
                ")",
                ","
        );

        TokenQueue queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                                      .separator("(")
                                                      .operator("expression")
                                                      .separator(")")
                                                      .build();
        assertThat(literalParser.parseStructInitialization(queue)).isSuccessful()
                                                                  .resultMatches(node -> node.getIdentifier()
                                                                                             .getIdentifier()
                                                                                             .value()
                                                                                             .equals("MyIdentifier"))
                                                                  .resultMatches(node -> node.getIdentifier()
                                                                                             .getArrayIndices()
                                                                                             .isEmpty())
                                                                  .resultMatches(node -> node.getArguments()
                                                                                             .size() == 1)
                                                                  .resultMatches(node -> node.getArguments()
                                                                                             .get(0) == mockedExpression);
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                           .separator("[")
                                           .operator("expression")
                                           .separator("]")
                                           .separator("(")
                                           .operator("expression")
                                           .separator(",")
                                           .operator("expression")
                                           .separator(")")
                                           .build();
        assertThat(literalParser.parseStructInitialization(queue)).isSuccessful()
                                                                  .resultMatches(node -> "MyIdentifier".equals(node.getIdentifier()
                                                                                                                   .getIdentifier()
                                                                                                                   .value()))
                                                                  .resultMatches(node -> node.getIdentifier()
                                                                                             .getArrayIndices()
                                                                                             .size() == 1)
                                                                  .resultMatches(node -> node.getIdentifier()
                                                                                             .getArrayIndices()
                                                                                             .get(0) == mockedExpression)
                                                                  .resultMatches(node -> node.getArguments()
                                                                                             .size() == 2)
                                                                  .resultMatches(node -> node.getArguments()
                                                                                             .get(0) == mockedExpression)
                                                                  .resultMatches(node -> node.getArguments()
                                                                                             .get(1) == mockedExpression);
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().keyword("none").build();
        assertThat(literalParser.parseStructInitialization(queue)).isSuccessful()
                                                                  .resultMatches(node -> node.getIdentifier() == null)
                                                                  .resultMatches(node -> node.getArguments().isEmpty());
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().separator("(").separator(")").build();
        assertThat(literalParser.parseStructInitialization(queue)).isUnsuccessful()
                                                                  .syntaxDiagnosticContains("Expected an identifier");

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier").separator("(").separator(")").build();
        assertThat(literalParser.parseStructInitialization(queue)).isUnsuccessful()
                                                                  .syntaxDiagnosticContains("Expected an expression");

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                           .separator("(")
                                           .operator("expression")
                                           .separator(";")
                                           .build();
        assertThat(literalParser.parseStructInitialization(queue)).isUnsuccessful()
                                                                  .syntaxDiagnosticContains("Expected ')'");

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
        assertThat(literalParser.parseFunctionDefinition(queue)).isSuccessful()
                                                                .resultMatches(node -> node.getParameterTypes().size() == 1)
                                                                .resultMatches(node -> node.getParameterNames().size() == 1)
                                                                .resultMatches(node -> node.getParameterTypes()
                                                                                           .get(0)
                                                                                           .getArrayDimensionDefinitions()
                                                                                           .isEmpty())
                                                                .resultMatches(node -> "string".equals(node.getParameterTypes()
                                                                                                           .get(0)
                                                                                                           .getType()
                                                                                                           .value()))
                                                                .resultMatches(node -> "MyIdentifier".equals(node.getParameterNames()
                                                                                                                 .get(0)
                                                                                                                 .value()))
                                                                .resultMatches(node -> node.getFunctionBody() == mockedStatements);
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().separator("(")
                                           .separator(")")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .build();
        assertThat(literalParser.parseFunctionDefinition(queue)).isSuccessful()
                                                                .resultMatches(node -> node.getParameterTypes().isEmpty())
                                                                .resultMatches(node -> node.getParameterNames().isEmpty())
                                                                .resultMatches(node -> node.getFunctionBody() == mockedStatements);
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
        assertThat(literalParser.parsePrimitiveLiteral(queue)).isSuccessful()
                                                              .resultMatches(primitiveLiteralNode -> "12345".equals(
                                                                      primitiveLiteralNode.getPrimitiveValue().value()))
                                                              .resultMatches(node -> PrimitiveLiteralNode.PrimitiveType.NUMBER.equals(
                                                                      node.getPrimitiveType()));
        assertThat(literalParser.parsePrimitiveLiteral(queue)).isSuccessful()
                                                              .resultMatches(primitiveLiteralNode -> "StringValue".equals(
                                                                      primitiveLiteralNode.getPrimitiveValue().value()))
                                                              .resultMatches(node -> PrimitiveLiteralNode.PrimitiveType.STRING.equals(
                                                                      node.getPrimitiveType()));
        assertThat(literalParser.parsePrimitiveLiteral(queue)).isSuccessful()
                                                              .resultMatches(primitiveLiteralNode -> "True".equals(
                                                                      primitiveLiteralNode.getPrimitiveValue().value()))
                                                              .resultMatches(node -> PrimitiveLiteralNode.PrimitiveType.BOOLEAN.equals(
                                                                      node.getPrimitiveType()));

        queue = new TokenQueueTestBuilder().keyword("Keyword").build();
        assertThat(literalParser.parsePrimitiveLiteral(queue)).isUnsuccessful()
                                                              .syntaxDiagnosticContains("Expected a primitive");
    }

}