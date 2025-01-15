package de.nikschadowsky.baall.compiler.syntax.analysis;

import de.nikschadowsky.baall.compiler._utility.TokenQueueTestBuilder;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.*;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FunctionCallNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.IdentifierAccessNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.TermNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.ArrayLiteralNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.PrimitiveLiteralNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.StructInitializationLiteralNode;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.nikschadowsky.baall.compiler._utility.BaseAssertion.assertThat;
import static java.util.function.Predicate.not;
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
        assertThat(expressionParser.parseExpression(queue)).isSuccessful().resultMatches(node -> {
            PrimitiveLiteralNode primitiveLiteralNode = (PrimitiveLiteralNode) node;
            return "value".equals(primitiveLiteralNode.getPrimitiveValue().value());
        });

        queue = new TokenQueueTestBuilder().separator("(")
                                           .identifier("MyIdentifier")
                                           .separator("(")
                                           .number("struct_initializer")
                                           .separator(")")
                                           .separator(")")
                                           .separator(";")
                                           .build();
        assertThat(expressionParser.parseExpression(queue)).isSuccessful().resultMatches(node -> {
            ParenthesizedExpressionNode parenthesizedExpressionNode = ((ParenthesizedExpressionNode) node);
            StructInitializationLiteralNode structInitializationLiteralNode =
                    ((StructInitializationLiteralNode) parenthesizedExpressionNode.getInnerExpressionNode());
            return "MyIdentifier".equals(structInitializationLiteralNode.getIdentifier().getIdentifier().value());
        }).resultMatches(node -> {
            ParenthesizedExpressionNode parenthesizedExpressionNode = ((ParenthesizedExpressionNode) node);
            StructInitializationLiteralNode structInitializationLiteralNode =
                    ((StructInitializationLiteralNode) parenthesizedExpressionNode.getInnerExpressionNode());
            PrimitiveLiteralNode primitiveLiteralNode =
                    (PrimitiveLiteralNode) structInitializationLiteralNode.getArguments().get(0);
            return "struct_initializer".equals(primitiveLiteralNode.getPrimitiveValue().value());
        });
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().separator("(")
                                           .separator("(")
                                           .number("value")
                                           .separator(")")
                                           .separator(")")
                                           .separator(";")
                                           .build();
        assertThat(expressionParser.parseExpression(queue)).isSuccessful()
                                                           .resultMatches(node -> node instanceof ParenthesizedExpressionNode)
                                                           .resultMatches(node -> {
                                                               ExpressionNode innerExpressionNode =
                                                                       ((ParenthesizedExpressionNode) node).getInnerExpressionNode();
                                                               return innerExpressionNode instanceof ParenthesizedExpressionNode;
                                                           });
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().operator("!").number("prefixed").separator(";").build();
        assertThat(expressionParser.parseExpression(queue)).isSuccessful().resultMatches(node -> {
            PrefixOperationNode prefixOperationNode = (PrefixOperationNode) node;
            return "!".equals(prefixOperationNode.getOperator().value());
        }).resultMatches(node -> {
            PrefixOperationNode prefixOperationNode = (PrefixOperationNode) node;
            PrimitiveLiteralNode primitiveLiteralNode = (PrimitiveLiteralNode) prefixOperationNode.getOperand();
            return "prefixed".equals(primitiveLiteralNode.getPrimitiveValue().value());
        });
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().number("operand1").operator("+").number("operand2").separator(";").build();
        assertThat(expressionParser.parseExpression(queue)).isSuccessful().resultMatches(node -> {
            BinaryExpressionNode binaryExpressionNode = (BinaryExpressionNode) node;
            PrimitiveLiteralNode leftOperand = (PrimitiveLiteralNode) binaryExpressionNode.getLeftOperand();
            return "operand1".equals(leftOperand.getPrimitiveValue().value());
        }).resultMatches(node -> {
            BinaryExpressionNode binaryExpressionNode = (BinaryExpressionNode) node;
            return "+".equals(binaryExpressionNode.getOperator().value());
        }).resultMatches(node -> {
            BinaryExpressionNode binaryExpressionNode = (BinaryExpressionNode) node;
            PrimitiveLiteralNode leftOperand = (PrimitiveLiteralNode) binaryExpressionNode.getRightOperand();
            return "operand2".equals(leftOperand.getPrimitiveValue().value());
        });
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().number("value").separator(";").build();
        assertThat(expressionParser.parseExpression(queue)).isSuccessful()
                                                           .resultMatches(node -> node instanceof TermNode);

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
        assertThat(expressionParser.parseExpression(queue)).isSuccessful()
                                                           .resultMatches(node -> "&".equals(((BinaryExpressionNode) node).getOperator()
                                                                                                                          .value()))
                                                           .resultMatches(node -> {
                                                               BinaryExpressionNode binaryExpressionNode =
                                                                       (BinaryExpressionNode) node;
                                                               ParenthesizedExpressionNode parenthesizedExpressionNode =
                                                                       (ParenthesizedExpressionNode) binaryExpressionNode.getLeftOperand();
                                                               BinaryExpressionNode innerExpression =
                                                                       (BinaryExpressionNode) parenthesizedExpressionNode.getInnerExpressionNode();
                                                               return "operand1".equals(((PrimitiveLiteralNode) innerExpression.getLeftOperand()).getPrimitiveValue()
                                                                                                                                                 .value());
                                                           }).resultMatches(node -> {
                                                               BinaryExpressionNode binaryExpressionNode =
                                                                       (BinaryExpressionNode) node;
                                                               ParenthesizedExpressionNode parenthesizedExpressionNode =
                                                                       (ParenthesizedExpressionNode) binaryExpressionNode.getLeftOperand();
                                                               BinaryExpressionNode innerExpression =
                                                                       (BinaryExpressionNode) parenthesizedExpressionNode.getInnerExpressionNode();
                                                               return "<<".equals(innerExpression.getOperator().value());
                                                           }).resultMatches(node -> {
                                                               BinaryExpressionNode binaryExpressionNode =
                                                                       (BinaryExpressionNode) node;
                                                               ParenthesizedExpressionNode parenthesizedExpressionNode =
                                                                       (ParenthesizedExpressionNode) binaryExpressionNode.getLeftOperand();
                                                               BinaryExpressionNode innerExpression =
                                                                       (BinaryExpressionNode) parenthesizedExpressionNode.getInnerExpressionNode();
                                                               return "operand2".equals(((PrimitiveLiteralNode) innerExpression.getRightOperand()).getPrimitiveValue()
                                                                                                                                                  .value());
                                                           }).resultMatches(node -> {
                                                               BinaryExpressionNode binaryExpressionNode = (BinaryExpressionNode) node;
                                                               BinaryExpressionNode rightSideExpressionNode =
                                                                       (BinaryExpressionNode) binaryExpressionNode.getRightOperand();
                                                               return rightSideExpressionNode.getOperator().value().equals("*");
                                                           }).resultMatches(node -> {
                                                               BinaryExpressionNode binaryExpressionNode = (BinaryExpressionNode) node;
                                                               BinaryExpressionNode rightSideExpressionNode =
                                                                       (BinaryExpressionNode) binaryExpressionNode.getRightOperand();
                                                               ParenthesizedExpressionNode leftOperand =
                                                                       (ParenthesizedExpressionNode) rightSideExpressionNode.getLeftOperand();
                                                               return ((IdentifierAccessNode) leftOperand.getInnerExpressionNode()).getIdentifier()
                                                                                                                                   .value()
                                                                                                                                   .equals("MyIdentifier");
                                                           }).resultMatches(node -> {
                                                               BinaryExpressionNode binaryExpressionNode = (BinaryExpressionNode) node;
                                                               BinaryExpressionNode rightSideExpressionNode =
                                                                       (BinaryExpressionNode) binaryExpressionNode.getRightOperand();
                                                               ParenthesizedExpressionNode leftOperand =
                                                                       (ParenthesizedExpressionNode) rightSideExpressionNode.getLeftOperand();
                                                               return ((IdentifierAccessNode) leftOperand.getInnerExpressionNode()).getArrayIndices().isEmpty();
                                                           }).resultMatches(node -> {
                                                               BinaryExpressionNode binaryExpressionNode = (BinaryExpressionNode) node;
                                                               BinaryExpressionNode rightSideExpressionNode =
                                                                       (BinaryExpressionNode) binaryExpressionNode.getRightOperand();
                                                               return ((ArrayLiteralNode) rightSideExpressionNode.getRightOperand()).getElements().isEmpty();
                                                           });
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
        assertThat(expressionParser.parseTerm(queue)).isSuccessful().resultMatches(node -> {
            IdentifierAccessNode identifierNode = (IdentifierAccessNode) node;
            return "MyIdentifier".equals(identifierNode.getIdentifier().value());
        }).resultMatches(node -> {
            IdentifierAccessNode identifierNode = (IdentifierAccessNode) node;
            return identifierNode.getArrayIndices().size() == 1;
        }).resultMatches(node -> {
            IdentifierAccessNode identifierNode = (IdentifierAccessNode) node;
            PrimitiveLiteralNode arrayIndexNode = (PrimitiveLiteralNode) identifierNode.getArrayIndices().get(0);
            return "index".equals(arrayIndexNode.getPrimitiveValue().value());
        });
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().separator("[").number("expression").separator("]").separator(";").build();
        assertThat(expressionParser.parseTerm(queue)).isSuccessful().resultMatches(node -> {
            ArrayLiteralNode arrayLiteralNode = (ArrayLiteralNode) node;
            return arrayLiteralNode.getElements().size() == 1;
        }).resultMatches(node -> {
            ArrayLiteralNode arrayLiteralNode = (ArrayLiteralNode) node;
            PrimitiveLiteralNode primitiveLiteralNode = (PrimitiveLiteralNode) arrayLiteralNode.getElements().get(0);
            return "expression".equals(primitiveLiteralNode.getPrimitiveValue().value());
        });
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                           .separator("(")
                                           .separator(")")
                                           .separator(";")
                                           .build();
        assertThat(expressionParser.parseTerm(queue)).isSuccessful().resultMatches(node -> {
            FunctionCallNode functionCallNode = ((FunctionCallNode) node);
            return "MyIdentifier".equals(functionCallNode.getFunctionIdentifier().getIdentifier().value());
        }).resultMatches(node -> {
            FunctionCallNode functionCallNode = ((FunctionCallNode) node);
            return functionCallNode.getFunctionIdentifier().getArrayIndices().isEmpty();
        }).resultMatches(node -> {
            FunctionCallNode functionCallNode = ((FunctionCallNode) node);
            return functionCallNode.getArguments().isEmpty();
        });
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().operator("++").identifier("MyIdentifier").separator(";").build();
        assertThat(expressionParser.parseTerm(queue)).isSuccessful().resultMatches(node -> {
            UnaryExpressionNode unaryExpressionNode = (UnaryExpressionNode) node;
            return unaryExpressionNode.isPrefix();
        }).resultMatches(node -> {
            UnaryExpressionNode unaryExpressionNode = (UnaryExpressionNode) node;
            return "++".equals(unaryExpressionNode.getOperator().value());
        }).resultMatches(node -> {
            UnaryExpressionNode unaryExpressionNode = (UnaryExpressionNode) node;
            return "MyIdentifier".equals(unaryExpressionNode.getIdentifierAccess().getIdentifier().value());
        }).resultMatches(node -> {
            UnaryExpressionNode unaryExpressionNode = (UnaryExpressionNode) node;
            return unaryExpressionNode.getIdentifierAccess().getArrayIndices().isEmpty();
        });
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().separator("(").number("expression").separator(")").separator(";").build();
        assertThat(expressionParser.parseTerm(queue)).isSuccessful().resultMatches(node -> {
            ParenthesizedExpressionNode primitiveLiteralNode = (ParenthesizedExpressionNode) node;
            PrimitiveLiteralNode innerExpressionNode =
                    (PrimitiveLiteralNode) primitiveLiteralNode.getInnerExpressionNode();
            return "expression".equals(innerExpressionNode.getPrimitiveValue().value());
        });
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().separator(";").build();
        assertThat(expressionParser.parseTerm(queue)).isUnsuccessful().syntaxDiagnosticContains("Not an expression");
    }

    @Test
    void parseParenthesizedExpression() {
        TokenQueue queue =
                new TokenQueueTestBuilder().separator("(").number("expression").separator(")").separator(";").build();
        assertThat(expressionParser.parseParenthesizedExpression(queue)).isSuccessful()
                                                                        .resultMatches(node -> "expression".equals(((PrimitiveLiteralNode) node.getInnerExpressionNode()).getPrimitiveValue()
                                                                                                                                                                         .value()));
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
        assertThat(expressionParser.parseFunctionCall(queue)).isSuccessful()
                                                             .resultMatches(node -> node.getArguments().isEmpty())
                                                             .resultMatches(node -> "MyIdentifier".equals(node.getFunctionIdentifier()
                                                                                                              .getIdentifier()
                                                                                                              .value()))
                                                             .resultMatches(node -> node.getFunctionIdentifier()
                                                                                        .getArrayIndices()
                                                                                        .isEmpty());
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                           .separator("[")
                                           .number("index")
                                           .separator("]")
                                           .separator("(")
                                           .number("expression")
                                           .separator(",")
                                           .identifier("IdentifierAccess")
                                           .separator(")")
                                           .separator(";")
                                           .build();
        assertThat(expressionParser.parseFunctionCall(queue)).isSuccessful()
                                                             .resultMatches(node -> node.getArguments().size() == 2)
                                                             .resultMatches(node -> "MyIdentifier".equals(node.getFunctionIdentifier()
                                                                                                              .getIdentifier()
                                                                                                              .value()))
                                                             .resultMatches(node -> node.getFunctionIdentifier()
                                                                                        .getArrayIndices()
                                                                                        .size() == 1)
                                                             .resultMatches(node -> "expression".equals(((PrimitiveLiteralNode) node.getArguments()
                                                                                                                                    .get(0)).getPrimitiveValue()
                                                                                                                                            .value()))
                                                             .resultMatches(node -> "IdentifierAccess".equals(((IdentifierAccessNode) node.getArguments()
                                                                                                                                          .get(1)).getIdentifier()
                                                                                                                                                  .value()))
                                                             .resultMatches(node -> ((IdentifierAccessNode) node.getArguments()
                                                                                                                .get(1)).getArrayIndices()
                                                                                                                        .isEmpty());
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
        assertThat(expressionParser.parseFunctionCall(queue)).isSuccessful();
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().separator(";").build();
        assertThat(expressionParser.parseFunctionCall(queue)).isUnsuccessful()
                                                             .syntaxDiagnosticContains("Not a statement");

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                           .separator("[")
                                           .number("1")
                                           .separator("]")
                                           .build();
        assertThat(expressionParser.parseFunctionCall(queue)).isPartiallyParsed()
                                                             .syntaxDiagnosticContains("Expected '('")
                                                             .resultMatches(node -> "MyIdentifier".equals(node.getFunctionIdentifier()
                                                                                                              .getIdentifier()
                                                                                                              .value()))
                                                             .resultMatches(node -> node.getArguments() == null);
        // should consume up until the next closing parenthesis
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                           .separator("[")
                                           .number("1")
                                           .separator("]")
                                           .separator("(")
                                           .separator(",")
                                           .separator(")")
                                           .build();
        assertThat(expressionParser.parseFunctionCall(queue)).isPartiallyParsed()
                                                             .syntaxDiagnosticContains("Not an expression");
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                           .separator("[")
                                           .number("1")
                                           .separator("]")
                                           .separator("(")
                                           .number("expression")
                                           .build();
        assertThat(expressionParser.parseFunctionCall(queue)).isPartiallyParsed()
                                                             .syntaxDiagnosticContains("Expected ')'");
        assertThat(queue).isAtEnd();
    }

    @Test
    void parseUnaryExpression() {
        TokenQueue queue = new TokenQueueTestBuilder().operator("++").identifier("MyIdentifier").separator(";").build();
        assertThat(expressionParser.parseUnaryExpression(queue)).isSuccessful()
                                                                .resultMatches(node -> "++".equals(node.getOperator()
                                                                                                       .value()))
                                                                .resultMatches(UnaryExpressionNode::isPrefix)
                                                                .resultMatches(node -> "MyIdentifier".equals(node.getIdentifierAccess()
                                                                                                                 .getIdentifier()
                                                                                                                 .value()))
                                                                .resultMatches(node -> node.getIdentifierAccess()
                                                                                           .getArrayIndices()
                                                                                           .isEmpty());
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier").operator("--").separator(";").build();
        assertThat(expressionParser.parseUnaryExpression(queue)).isSuccessful()
                                                                .resultMatches(node -> "MyIdentifier".equals(node.getIdentifierAccess()
                                                                                                                 .getIdentifier()
                                                                                                                 .value()))
                                                                .resultMatches(node -> node.getIdentifierAccess()
                                                                                           .getArrayIndices()
                                                                                           .isEmpty())
                                                                .resultMatches(node -> "--".equals(node.getOperator()
                                                                                                       .value()))
                                                                .resultMatches(not(UnaryExpressionNode::isPrefix));
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier").build();
        assertThat(expressionParser.parseUnaryExpression(queue)).isUnsuccessful()
                                                                .syntaxDiagnosticContains("Expected a unary operator");

        queue = new TokenQueueTestBuilder().operator("++").build();
        assertThat(expressionParser.parseUnaryExpression(queue)).isUnsuccessful()
                                                                .syntaxDiagnosticContains("Expected an identifier");

        queue = new TokenQueueTestBuilder().build();
        assertThat(expressionParser.parseUnaryExpression(queue)).isUnsuccessful()
                                                                .syntaxDiagnosticContains("Not a statement");
    }
}