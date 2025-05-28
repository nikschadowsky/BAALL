package de.nikschadowsky.baall.compiler.syntax.analysis;

import de.nikschadowsky.baall.compiler._utility.ParserMockerExtension;
import de.nikschadowsky.baall.compiler._utility.TokenQueueTestBuilder;
import de.nikschadowsky.baall.compiler._utility.ast.NodeAssertionFactory;
import de.nikschadowsky.baall.compiler._utility.ast.nodes.ExpressionNodeAssertion;
import de.nikschadowsky.baall.compiler._utility.ast.nodes.TypeNodeAssertion;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.ElementAccessNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.OperatorNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.TypeNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FieldNode;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import de.nikschadowsky.baall.compiler.tokenizer.TokenType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static de.nikschadowsky.baall.compiler._utility.BaseAssertion.assertThat;
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
        ExpressionNode mockedExpression = parserMockerExtension.mockExpressionNode();
        parserMockerExtension.mockExpressionParserExecution(
                programParser,
                exprParser -> exprParser.parseExpression(any()),
                mockedExpression,
                ":",
                ","
        );

        TokenQueue queue = new TokenQueueTestBuilder().build();
        ParseResult<List<FieldNode>> actual = auxiliaryParser.parseFieldDeclarations(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isEmpty();

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
                                           .separator(",")
                                           .keyword("struct")
                                           .operator("<")
                                           .operator(">")
                                           .separator(":")
                                           .identifier("MyIdentifier3")
                                           .separator(";")
                                           .build();
        actual = auxiliaryParser.parseFieldDeclarations(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasSize(3)
                          .hasElementMatching(
                                  0,
                                  NodeAssertionFactory::create,
                                  a -> a.hasTypeMatching(a1 -> a1.isPrimitiveType().isNumber())
                                        .hasName("MyIdentifier")
                          )
                          .hasElementMatching(
                                  1,
                                  NodeAssertionFactory::create,
                                  a -> a.hasTypeMatching(a1 -> a1.isListType()
                                                                 .mapToInner()
                                                                 .isIdentifierType()
                                                                 .hasIdentifierMatching(a2 -> a2.isIdentifier()
                                                                                                .hasName("MyType")
                                                                 )
                                        )
                                        .hasName("MyIdentifier2")
                          )
                          .hasElementMatching(
                                  2,
                                  NodeAssertionFactory::create,
                                  a -> a.hasName("MyIdentifier3")
                                        .hasTypeMatching(a1 -> a1.isFunctionType()
                                                                 .hasNoParameterTypes()
                                                                 .mapToInner()
                                                                 .isPrimitiveType()
                                                                 .isStruct()
                                        )
                          );
        assertThat(queue).hasNextTokenValueMatch(";");


        queue = new TokenQueueTestBuilder().separator(",").identifier("MyType").separator(":").build();
        actual = auxiliaryParser.parseFieldDeclarations(queue);
        assertThat(actual).isUnsuccessful().syntaxDiagnosticContains("Expected an identifier");
    }

    @Test
    void parseFieldDeclaration() {
        ExpressionNode mockedExpression = parserMockerExtension.mockExpressionNode();
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
        ParseResult<FieldNode> actual = auxiliaryParser.parseFieldDeclaration(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasTypeMatching(TypeNodeAssertion::isIdentifierType)
                          .hasTypeMatching(a -> a.isIdentifierType().isNotNoneSafe())
                          .hasName("MyIdentifier");
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().identifier("MyType")
                                           .separator("[")
                                           .separator("]")
                                           .separator(":")
                                           .identifier("MyIdentifier")
                                           .separator(";")
                                           .build();
        actual = auxiliaryParser.parseFieldDeclaration(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasTypeMatching(a -> a.isListType()
                                                 .mapToInner()
                                                 .isIdentifierType()
                                                 .hasIdentifierMatching(a1 -> a1.isIdentifier().hasName("MyType")))
                          .hasName("MyIdentifier");
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
        ExpressionNode mockedExpression = parserMockerExtension.mockExpressionNode();
        parserMockerExtension.mockExpressionParserExecution(
                programParser,
                exprParser -> exprParser.parseExpression(any()),
                mockedExpression,
                ","
        );

        TokenQueue queue = new TokenQueueTestBuilder().build();
        assertThat(auxiliaryParser.parseArgumentList(queue)).isSuccessful().map(NodeAssertionFactory::create).isEmpty();

        queue = new TokenQueueTestBuilder().separator(",").operator("expression").separator(";").build();
        ParseResult<List<ExpressionNode>> actual = auxiliaryParser.parseArgumentList(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasSize(1)
                          .hasElementMatching(0, NodeAssertionFactory::create, a -> a.isEqualTo(mockedExpression));
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
                                                              .map(NodeAssertionFactory::create)
                                                              .hasOperator("+");
        assertThat(auxiliaryParser.parseBinaryOperator(queue)).isSuccessful()
                                                              .map(NodeAssertionFactory::create)
                                                              .hasOperator(">>");
        assertThat(auxiliaryParser.parseBinaryOperator(queue)).isSuccessful()
                                                              .map(NodeAssertionFactory::create)
                                                              .hasOperator(">");
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
                                                             .map(NodeAssertionFactory::create)
                                                             .hasOperator("++");
        assertThat(auxiliaryParser.parseUnaryOperator(queue)).isSuccessful()
                                                             .map(NodeAssertionFactory::create)
                                                             .hasOperator("--");

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

        ParseResult<OperatorNode> actual = auxiliaryParser.parseVariableAssignmentOperator(queue);
        assertThat(actual).isSuccessful().map(NodeAssertionFactory::create).hasOperator("=");
        actual = auxiliaryParser.parseVariableAssignmentOperator(queue);
        assertThat(actual).isSuccessful().map(NodeAssertionFactory::create).hasOperator("+=");
        actual = auxiliaryParser.parseVariableAssignmentOperator(queue);
        assertThat(actual).isSuccessful().map(NodeAssertionFactory::create).hasOperator("|=");
        actual = auxiliaryParser.parseVariableAssignmentOperator(queue);
        assertThat(actual).isUnsuccessful().syntaxDiagnosticContains("Expected an assignment operator");
        actual = auxiliaryParser.parseVariableAssignmentOperator(queue);
        assertThat(actual).isUnsuccessful().syntaxDiagnosticContains("Expected an assignment operator");
        actual = auxiliaryParser.parseVariableAssignmentOperator(queue);
        assertThat(actual).isUnsuccessful().syntaxDiagnosticContains("Expected an assignment operator");
        actual = auxiliaryParser.parseVariableAssignmentOperator(queue);
        assertThat(actual).isUnsuccessful().syntaxDiagnosticContains("Expected an assignment operator");
    }

    @Test
    void parsePrefixOperator() {
        TokenQueue queue = new TokenQueueTestBuilder().operator("+").operator("-").operator("!").build();

        ParseResult<OperatorNode> actual = auxiliaryParser.parsePrefixOperator(queue);
        assertThat(actual).isSuccessful().map(NodeAssertionFactory::create).hasOperator("+");
        actual = auxiliaryParser.parsePrefixOperator(queue);
        assertThat(actual).isSuccessful().map(NodeAssertionFactory::create).hasOperator("-");
        actual = auxiliaryParser.parsePrefixOperator(queue);
        assertThat(actual).isSuccessful().map(NodeAssertionFactory::create).hasOperator("!");

        queue = new TokenQueueTestBuilder().operator("++").build();
        actual = auxiliaryParser.parsePrefixOperator(queue);
        assertThat(actual).isUnsuccessful().syntaxDiagnosticContains("Expected a prefix operator");
    }

    @Test
    void parseIdentifier() {
        TokenQueue queue = new TokenQueueTestBuilder().identifier("MyIdentifier").separator(";").build();

        // parsing identifier
        assertThat(auxiliaryParser.parseIdentifier(queue)).isSuccessful()
                                                          .map(NodeAssertionFactory::create)
                                                          .hasName("MyIdentifier")
                                                          .hasTokenType(TokenType.IDENTIFIER);

        // parsing number
        assertThat(auxiliaryParser.parseIdentifier(queue)).isUnsuccessful()
                                                          .syntaxDiagnosticContains("Expected an identifier");
    }

    @Test
    void parseType() {
        ExpressionNode mockedExpression = parserMockerExtension.mockExpressionNode();
        parserMockerExtension.mockExpressionParserExecution(
                programParser,
                exprParser -> exprParser.parseExpression(any()),
                mockedExpression,
                "[",
                "]"
        );

        TokenQueue queue = new TokenQueueTestBuilder().keyword("string")
                                                      .separator("[")
                                                      .separator("]")
                                                      .separator("[")
                                                      .separator("]")
                                                      .separator("[")
                                                      .separator("]")
                                                      .separator("[")
                                                      .separator("]")
                                                      .separator(";")
                                                      .build();

        ParseResult<TypeNode> actual = auxiliaryParser.parseType(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isListType()
                          .mapToInner()
                          .isListType()
                          .mapToInner()
                          .isListType()
                          .mapToInner()
                          .isListType()
                          .mapToInner()
                          .isPrimitiveType()
                          .isString();

        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier").keyword("another statement").build();

        actual = auxiliaryParser.parseType(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isIdentifierType()
                          .hasIdentifierMatching(a -> a.isIdentifier().hasName("MyIdentifier"))
                          .isNotNoneSafe();

        assertThat(queue).hasNextTokenValueMatch("another statement");

        queue = new TokenQueueTestBuilder().identifier("MyType")
                                           .operator("!")
                                           .separator("[")
                                           .separator("]")
                                           .separator(";")
                                           .build();

        actual = auxiliaryParser.parseType(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isListType()
                          .mapToInner()
                          .isIdentifierType()
                          .hasIdentifierMatching(a -> a.isIdentifier().hasName("MyType"))
                          .isNoneSafe();

        queue = new TokenQueueTestBuilder().operator("+")
                                           .separator("[")
                                           .separator("]")
                                           .build();
        actual = auxiliaryParser.parseType(queue);
        assertThat(actual).isUnsuccessful().syntaxDiagnosticContains("Expected a type");

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                           .separator("[")
                                           .separator(";")
                                           .build();
        actual = auxiliaryParser.parseType(queue);
        assertThat(actual).isUnsuccessful().syntaxDiagnosticContains("Expected ']'");

        queue = new TokenQueueTestBuilder().keyword("number").operator("<").keyword("string").operator(">").build();
        actual = auxiliaryParser.parseType(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isFunctionType()
                          .hasParameterTypes(1)
                          .hasParameterTypeMatching(0, a -> a.isPrimitiveType().isString())
                          .mapToInner()
                          .isPrimitiveType()
                          .isNumber();

        // myType![]<>[]<string<boolean>,number>
        queue = new TokenQueueTestBuilder().identifier("myType")
                                           .operator("!")
                                           .separator("[")
                                           .separator("]")
                                           .operator("<")
                                           .operator(">")
                                           .separator("[")
                                           .separator("]")
                                           .operator("<")
                                           .keyword("string")
                                           .operator("<")
                                           .keyword("boolean")
                                           .operator(">")
                                           .separator(",")
                                           .keyword("number")
                                           .operator(">")
                                           .build();
        actual = auxiliaryParser.parseType(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isFunctionType()
                          .hasParameterTypes(2)
                          .hasParameterTypeMatching(0, TypeNodeAssertion::isFunctionType)
                          .hasParameterTypeMatching(1, TypeNodeAssertion::isPrimitiveType)
                          .mapToInner()
                          .isListType()
                          .mapToInner()
                          .isFunctionType()
                          .hasNoParameterTypes()
                          .mapToInner()
                          .isListType()
                          .mapToInner()
                          .isIdentifierType()
                          .isNoneSafe()
                          .hasIdentifierMatching(a -> a.isIdentifier().hasName("myType"));
    }

    @Test
    void parseElementAccess() {
        ExpressionNode mockedExpression = parserMockerExtension.mockExpressionNode();
        parserMockerExtension.mockExpressionParserExecution(
                programParser,
                expParser -> expParser.parseExpression(any()),
                mockedExpression,
                "[",
                "]"
        );

        TokenQueue queue = new TokenQueueTestBuilder().identifier("myIdentifier").separator(";").build();
        ParseResult<ElementAccessNode> actual = auxiliaryParser.parseElementAccess(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isIdentifier()
                          .hasName("myIdentifier");
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().operator("$").identifier("myIdentifier").separator(";").build();
        actual = auxiliaryParser.parseElementAccess(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isScopeElevation()
                          .mapToInner()
                          .isIdentifier()
                          .hasName("myIdentifier");
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().identifier("myIdentifier")
                                           .separator("[")
                                           .number("expr")
                                           .separator("]")
                                           .separator("[")
                                           .number("expr")
                                           .separator("]")
                                           .separator(";")
                                           .build();
        actual = auxiliaryParser.parseElementAccess(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isIndexedAccess()
                          .hasIndexMatching(a -> a.isEqualTo(mockedExpression))
                          .mapToInner()
                          .isIndexedAccess()
                          .hasIndexMatching(a -> a.isEqualTo(mockedExpression))
                          .mapToInner()
                          .isIdentifier()
                          .hasName("myIdentifier");
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().identifier("myIdentifier1")
                                           .separator(".")
                                           .identifier("myIdentifier2")
                                           .separator(";")
                                           .build();
        actual = auxiliaryParser.parseElementAccess(queue);
        assertThat(actual).isSuccessful().map(NodeAssertionFactory::create)
                          .isMemberReference()
                          .hasSelectedMatching(a -> a.hasName("myIdentifier2"))
                          .mapToInner()
                          .isIdentifier()
                          .hasName("myIdentifier1");
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().operator("$")
                                           .identifier("myIdentifier1")
                                           .separator("[")
                                           .number("expr0")
                                           .separator("]")
                                           .separator(".")
                                           .identifier("myIdentifier2")
                                           .separator("[")
                                           .number("expr1")
                                           .separator("]")
                                           .separator("[")
                                           .number("expr2")
                                           .separator("]")
                                           .separator(";")
                                           .build();
        actual = auxiliaryParser.parseElementAccess(queue);
        assertThat(actual).isSuccessful().map(NodeAssertionFactory::create)
                          .isScopeElevation()
                          .mapToInner()
                          .isIndexedAccess()
                          .hasIndexMatching(b -> b.isEqualTo(mockedExpression))
                          .mapToInner()
                          .isIndexedAccess()
                          .hasIndexMatching(b -> b.isEqualTo(mockedExpression))
                          .mapToInner()
                          .isMemberReference()
                          .hasSelectedMatching(b -> b.hasName("myIdentifier2"))
                          .mapToInner()
                          .isIndexedAccess()
                          .hasIndexMatching(b -> b.isEqualTo(mockedExpression))
                          .mapToInner()
                          .isIdentifier()
                          .hasName("myIdentifier1");
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().operator("$").separator(";").build();
        actual = auxiliaryParser.parseElementAccess(queue);
        assertThat(actual).isUnsuccessful().syntaxDiagnosticContains("Expected an identifier");

        queue = new TokenQueueTestBuilder().identifier("myIdentifier").separator("[").number("expr").build();
        actual = auxiliaryParser.parseElementAccess(queue);
        // incomplete index information -> will not be consumed
        assertThat(actual).isSuccessful().map(NodeAssertionFactory::create).isIdentifier();

        queue = new TokenQueueTestBuilder().identifier("myIdentifier").separator(".").build();
        actual = auxiliaryParser.parseElementAccess(queue);
        assertThat(actual).isUnsuccessful().syntaxDiagnosticContains("Expected an identifier");
    }

    @Test
    void parseListIndexInformation() {
        ExpressionNode mockedExpression = parserMockerExtension.mockExpressionNode();
        parserMockerExtension.mockExpressionParserExecution(
                programParser,
                expParser -> expParser.parseExpression(any()),
                mockedExpression,
                "[",
                "]"
        );

        TokenQueue queue = new TokenQueueTestBuilder().build();
        ParseResult<List<ExpressionNode>> actual = auxiliaryParser.parseListIndexInformation(queue);
        assertThat(actual).isSuccessful().map(NodeAssertionFactory::create).isEmpty();

        queue = new TokenQueueTestBuilder().separator("[").operator("expression").separator("]").separator(";").build();

        actual = auxiliaryParser.parseListIndexInformation(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasSize(1)
                          .hasElementMatching(0, NodeAssertionFactory::create, a -> a.isEqualTo(mockedExpression));
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().separator("[")
                                           .operator("expression")
                                           .separator("]")
                                           .separator("[")
                                           .operator("expression")
                                           .separator("]")
                                           .separator(";")
                                           .build();

        actual = auxiliaryParser.parseListIndexInformation(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasSize(2)
                          .hasElementMatching(0, NodeAssertionFactory::create, a -> a.isEqualTo(mockedExpression))
                          .hasElementMatching(1, NodeAssertionFactory::create, a -> a.isEqualTo(mockedExpression));
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().separator("[").separator(";").build();
        actual = auxiliaryParser.parseListIndexInformation(queue);
        assertThat(actual).isSuccessful().map(NodeAssertionFactory::create).hasSize(0);
    }

    @Test
    void parseCodeBlock() {
        StatementsNode mockedStatements = parserMockerExtension.mockStatementsNode();
        parserMockerExtension.mockStatementParserExecution(
                programParser,
                parser -> parser.parseStatements(any()),
                mockedStatements,
                "{",
                "}"
        );

        TokenQueue queue = new TokenQueueTestBuilder().separator("{")
                                                      .keyword("statements")
                                                      .separator("}")
                                                      .separator(";")
                                                      .build();

        assertThat(auxiliaryParser.parseCodeBlock(queue)).isSuccessful()
                                                         .map(NodeAssertionFactory::create)
                                                         .isEqualTo(mockedStatements);
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().separator("{").keyword("statements").separator(";").separator(";").build();
        assertThat(auxiliaryParser.parseCodeBlock(queue)).isPartiallyParsed()
                                                         .syntaxDiagnosticContains("Expected '}'")
                                                         .map(NodeAssertionFactory::create)
                                                         .isEqualTo(mockedStatements);
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().separator(";").keyword("statements").separator("}").build();
        assertThat(auxiliaryParser.parseCodeBlock(queue)).isUnsuccessful()
                                                         .syntaxDiagnosticContains("Expected '{'");
    }
}