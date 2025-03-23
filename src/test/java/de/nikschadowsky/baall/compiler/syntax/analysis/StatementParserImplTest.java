package de.nikschadowsky.baall.compiler.syntax.analysis;

import de.nikschadowsky.baall.compiler._utility.BaseAssertion;
import de.nikschadowsky.baall.compiler._utility.TokenQueueTestBuilder;
import de.nikschadowsky.baall.compiler._utility.ast.NodeAssertionFactory;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.PartialParseResult;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.ExportsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.ImportsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.StatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.*;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.nikschadowsky.baall.compiler._utility.BaseAssertion.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StatementParserImplTest {

    private final ASTNodeFactory astFactory = new ASTNodeFactory(new NodeDiagnosticCollector());

    private StatementParser statementParser;
    private ControlStructureParser controlStructureParser;
    private ControlStatementParser controlStatementParser;
    private AuxiliaryParser auxiliaryParser;
    private LiteralParser literalParser;
    private ProgramParser programParser;
    private ExpressionParser expressionParser;

    @BeforeEach
    void setUp() {
        programParser = mock(ProgramParserImpl.class);
        statementParser = new StatementParserImpl(programParser, astFactory);
        controlStructureParser = new ControlStructureParserImpl(programParser, astFactory);
        controlStatementParser = new ControlStatementParserImpl(programParser, astFactory);
        auxiliaryParser = new AuxiliaryParserImpl(programParser, astFactory);
        literalParser = new LiteralParserImpl(programParser, astFactory);
        expressionParser = new ExpressionParserImpl(programParser, astFactory);

        when(programParser.getStatementParser()).thenReturn(statementParser);
        when(programParser.getControlStructureParser()).thenReturn(controlStructureParser);
        when(programParser.getControlStatementParser()).thenReturn(controlStatementParser);
        when(programParser.getAuxiliaryParser()).thenReturn(auxiliaryParser);
        when(programParser.getLiteralParser()).thenReturn(literalParser);
        when(programParser.getExpressionParser()).thenReturn(expressionParser);
    }

    @Test
    void parseImports() {
        TokenQueue queue =
                new TokenQueueTestBuilder().keyword("use").string("MyImport").separator(";").operator("end").build();
        PartialParseResult<ImportsNode> actual = statementParser.parseImports(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasImports(1)
                          .hasImport(0, "MyImport");
        assertThat(queue).hasNextTokenValueMatch("end");

        queue = new TokenQueueTestBuilder().keyword("use")
                                           .string("MyImport1")
                                           .separator(";")
                                           .keyword("use")
                                           .string("MyImport2")
                                           .separator(";")
                                           .build();
        actual = statementParser.parseImports(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasImports(2)
                          .hasImport(0, "MyImport1")
                          .hasImport(1, "MyImport2");
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().keyword("use").separator(";").operator("end").build();
        actual = statementParser.parseImports(queue);
        assertThat(actual).isPartiallyParsed()
                          .syntaxDiagnosticContains("Expected a string")
                          .map(NodeAssertionFactory::create)
                          .hasNoImports();
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().separator("something").separator(";").build();
        actual = statementParser.parseImports(queue);
        assertThat(actual).isSuccessful().map(NodeAssertionFactory::create).hasNoImports();
        assertThat(queue).hasNextTokenValueMatch("something");
    }

    @Test
    void parseStatements() {
        TokenQueue queue = new TokenQueueTestBuilder().build();
        PartialParseResult<StatementsNode> actual = statementParser.parseStatements(queue);
        assertThat(actual).isSuccessful().map(NodeAssertionFactory::create).hasNoStatements();

        queue = new TokenQueueTestBuilder().identifier("MyFunctionCall")
                                           .separator("(")
                                           .number("Argument")
                                           .separator(")")
                                           .separator(";")
                                           .identifier("MyCondition")
                                           .operator("?")
                                           .separator("{")
                                           .separator("}")
                                           .operator("|")
                                           .separator("{")
                                           .operator("++")
                                           .identifier("MyArray")
                                           .separator("[")
                                           .identifier("Index")
                                           .separator("]")
                                           .separator(";")
                                           .separator("}")
                                           .keyword("try")
                                           .separator("{")
                                           .separator("}")
                                           .keyword("intercept")
                                           .identifier("MyException")
                                           .separator(":")
                                           .identifier("Variable")
                                           .separator("{")
                                           .identifier("MyFunction")
                                           .separator("(")
                                           .separator(")")
                                           .separator(";")
                                           .keyword("raise")
                                           .identifier("MyExceptionType")
                                           .separator("(")
                                           .string("Message")
                                           .separator(")")
                                           .separator(";")
                                           .separator("}")
                                           .keyword("return")
                                           .number("MyNumber1")
                                           .operator("&&")
                                           .number("MyNumber2")
                                           .separator(";")
                                           .build();
        actual = statementParser.parseStatements(queue);
        assertThat(actual).isSuccessful().map(NodeAssertionFactory::create).hasStatements(4);
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().identifier("MyFunction")
                                           .separator("(")
                                           .identifier("Argument")
                                           .separator(",")
                                           .separator(")")
                                           .separator(";")
                                           .keyword("return")
                                           .number("MyNumber1")
                                           .operator("&&")
                                           .number("MyNumber2")
                                           .separator(";")
                                           .keyword("break")
                                           .separator(";")
                                           .build();
        actual = statementParser.parseStatements(queue);
        assertThat(actual).isPartiallyParsed()
                          .syntaxDiagnosticContains("Not an expression")
                          .map(NodeAssertionFactory::create).hasStatements(3);
        assertThat(queue).isAtEnd();
    }

    @Test
    void parseDelimitedStatement() {
        TokenQueue queue = new TokenQueueTestBuilder().keyword("while")
                                                      .bool("expression")
                                                      .separator("{")
                                                      .identifier("MyFunction")
                                                      .separator("(")
                                                      .separator(")")
                                                      .separator(";")
                                                      .separator("}")
                                                      .build();
        PartialParseResult<StatementNode> actual = statementParser.parseDelimitedStatement(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isWhileLoop();
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().identifier("MyFunction")
                                           .separator("(")
                                           .identifier("Parameter1")
                                           .separator(",")
                                           .bool("Parameter2")
                                           .separator(")")
                                           .separator(";")
                                           .build();
        actual = statementParser.parseDelimitedStatement(queue);
        assertThat(actual).isSuccessful().map(NodeAssertionFactory::create).isFunctionCall();
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().identifier("MyType")
                                           .separator("[")
                                           .separator("]")
                                           .separator(":")
                                           .identifier("MyFunction")
                                           .operator(":=")
                                           .separator("(")
                                           .separator(")")
                                           .separator("{")
                                           .identifier("MyFunctionCall")
                                           .separator("(")
                                           .bool("Parameter1")
                                           .operator("+")
                                           .identifier("Parameter2")
                                           .separator(")")
                                           .separator(";")
                                           .separator("}")
                                           .separator(";")
                                           .build();
        actual = statementParser.parseDelimitedStatement(queue);
        assertThat(actual).isSuccessful().map(NodeAssertionFactory::create).isConstantDeclaration();
        assertThat(queue).isAtEnd();
    }

    @Test
    void parseSimpleStatement() {
        TokenQueue queue = new TokenQueueTestBuilder().keyword("string")
                                                      .separator(":")
                                                      .identifier("MyIdentifier")
                                                      .operator("=")
                                                      .number("expression")
                                                      .separator(";")
                                                      .build();
        PartialParseResult<StatementNode> actual = statementParser.parseSimpleStatement(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isVariableDeclaration();
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("string")
                                           .separator(":")
                                           .identifier("MyIdentifier")
                                           .operator(":=")
                                           .number("expression")
                                           .separator(";")
                                           .build();
        actual = statementParser.parseSimpleStatement(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isConstantDeclaration();
        assertThat(queue).hasNextTokenValueMatch(";");


        queue = new TokenQueueTestBuilder().keyword("return").number("expression").separator(";").build();
        actual = statementParser.parseSimpleStatement(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isReturnStatement();
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().operator("--").identifier("MyIdentifier").build();
        actual = statementParser.parseSimpleStatement(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isUnaryExpression();

        queue = new TokenQueueTestBuilder().identifier("MyFunction")
                                           .separator("(")
                                           .identifier("Parameter")
                                           .separator(")")
                                           .separator(";")
                                           .build();
        actual = statementParser.parseSimpleStatement(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isFunctionCall();
        assertThat(queue).hasNextTokenValueMatch(";");

        // todo partial
        queue = new TokenQueueTestBuilder().keyword("string")
                                           .separator(":")
                                           .identifier("MyIdentifier")
                                           .operator("=")
                                           .operator("not an expression")
                                           .separator(";")
                                           .build();
        actual = statementParser.parseSimpleStatement(queue);
        assertThat(actual).isPartiallyParsed()
                          .syntaxDiagnosticContains("Expected an expression")
                          .map(NodeAssertionFactory::create)
                          .isVariableDeclaration();
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                           .operator("something")
                                           .operator("something")
                                           .operator("something")
                                           .operator("something")
                                           .separator(";")
                                           .build();
        actual = statementParser.parseSimpleStatement(queue);
        assertThat(actual).isPartiallyParsed().syntaxDiagnosticContains("Expected ':'");
        assertThat(queue).hasNextTokenValueMatch(";");


        queue = new TokenQueueTestBuilder().separator(";").build();
        actual = statementParser.parseSimpleStatement(queue);
        assertThat(actual).isUnsuccessful().syntaxDiagnosticContains("Not a statement");
        assertThat(queue).isAtEnd();
    }

    @Test
    void parseDeclaration() {
        TokenQueue queue = new TokenQueueTestBuilder().keyword("string")
                                                      .separator(":")
                                                      .identifier("MyIdentifier")
                                                      .operator("=")
                                                      .number("expression")
                                                      .separator(";")
                                                      .build();
        PartialParseResult<DeclarationNode> actual = statementParser.parseDeclaration(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isVariableDeclaration();
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("string")
                                           .separator(":")
                                           .identifier("MyIdentifier")
                                           .separator(";")
                                           .build();
        actual = statementParser.parseDeclaration(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isVariableDeclaration();
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("string")
                                           .separator(":")
                                           .identifier("MyIdentifier")
                                           .operator(":=")
                                           .number("expression")
                                           .separator(";")
                                           .build();
        actual = statementParser.parseDeclaration(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isConstantDeclaration();
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("string")
                                           .separator(":")
                                           .identifier("MyIdentifier")
                                           .operator("=")
                                           .operator("not an expression")
                                           .separator(";")
                                           .build();
        actual = statementParser.parseDeclaration(queue);
        assertThat(actual).isPartiallyParsed()
                          .syntaxDiagnosticContains("Expected an expression")
                          .map(NodeAssertionFactory::create)
                          .isVariableDeclaration();
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("string")
                                           .separator(";")
                                           .identifier("MyIdentifier")
                                           .separator(";")
                                           .build();
        actual = statementParser.parseDeclaration(queue);
        assertThat(actual).isPartiallyParsed()
                          .syntaxDiagnosticContains("Expected ':'")
                          .map(NodeAssertionFactory::create)
                          .isVariableDeclaration();
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("string")
                                           .separator(":")
                                           .identifier("MyIdentifier")
                                           .operator("=")
                                           .operator("not an expression")
                                           .separator(";")
                                           .build();
        actual = statementParser.parseDeclaration(queue);
        assertThat(actual).isPartiallyParsed()
                          .syntaxDiagnosticContains("Expected an expression")
                          .map(NodeAssertionFactory::create)
                          .isVariableDeclaration();
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().separator(";").build();
        actual = statementParser.parseDeclaration(queue);
        assertThat(actual).isUnsuccessful().syntaxDiagnosticContains("Not a statement");
        assertThat(queue).isAtEnd();
    }

    @Test
    void parseVariableDeclaration() {
        TokenQueue queue = new TokenQueueTestBuilder().keyword("string")
                                                      .separator(":")
                                                      .identifier("MyIdentifier")
                                                      .operator("=")
                                                      .number("expression")
                                                      .separator(";")
                                                      .build();
        PartialParseResult<VariableDeclarationNode> actual = statementParser.parseVariableDeclaration(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasTypeMatching(a -> a.isPrimitiveType().isString())
                          .hasIdentifierName("MyIdentifier")
                          .hasInitializationValue()
                          .hasInitializationValueMatching(a -> a.isPrimitiveLiteral().hasPrimitiveValue("expression"));
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().identifier("MyType")
                                           .separator(":")
                                           .identifier("MyIdentifier")
                                           .separator(";")
                                           .build();
        actual = statementParser.parseVariableDeclaration(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasTypeMatching(a -> a.isIdentifierType()
                                                 .hasIdentifierMatching(a1 -> a1.isIdentifier().hasName("MyType"))
                                                 .isNotNoneSafe())
                          .hasIdentifierName("MyIdentifier")
                          .hasNoInitializationValue();

        queue = new TokenQueueTestBuilder().identifier("MyType")
                                           .separator("[")
                                           .separator("]")
                                           .separator(":")
                                           .identifier("MyIdentifier")
                                           .operator("=")
                                           .operator("not an expression")
                                           .separator(";")
                                           .build();
        actual = statementParser.parseVariableDeclaration(queue);
        assertThat(actual).isPartiallyParsed()
                          .syntaxDiagnosticContains("Expected an expression")
                          .map(NodeAssertionFactory::create)
                          .hasTypeMatching(a -> a.isListType()
                                                 .mapToInner()
                                                 .isIdentifierType()
                                                 .hasIdentifierMatching(a1 -> a1.isIdentifier().hasName("MyType"))
                          )
                          .hasNoInitializationValue();
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().operator("something else").separator(";").build();
        actual = statementParser.parseVariableDeclaration(queue);
        assertThat(actual).isUnsuccessful().syntaxDiagnosticContains("Not a statement");
    }

    @Test
    void parseConstantDeclaration() {
        TokenQueue queue = new TokenQueueTestBuilder().keyword("string")
                                                      .separator(":")
                                                      .identifier("MyIdentifier")
                                                      .operator(":=")
                                                      .number("expression")
                                                      .separator(";")
                                                      .build();
        PartialParseResult<ConstantDeclarationNode> actual = statementParser.parseConstantDeclaration(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasTypeMatching(a -> a.isPrimitiveType().isString())
                          .hasIdentifierName("MyIdentifier")
                          .hasInitializationValueMatching(a -> a.isPrimitiveLiteral().hasPrimitiveValue("expression"));
        assertThat(queue).hasNextTokenValueMatch(";");
        queue = new TokenQueueTestBuilder().identifier("MyType")
                                           .separator("[")
                                           .separator("]")
                                           .separator(":")
                                           .identifier("MyIdentifier")
                                           .operator(":=")
                                           .operator("not an expression")
                                           .separator(";")
                                           .build();
        actual = statementParser.parseConstantDeclaration(queue);
        assertThat(actual).isPartiallyParsed().syntaxDiagnosticContains("Expected an expression")
                          .map(NodeAssertionFactory::create)
                          .hasTypeMatching(a -> a.isListType()
                                                 .mapToInner()
                                                 .isIdentifierType()
                                                 .hasIdentifierMatching(a1 -> a1.isIdentifier().hasName("MyType"))
                          )
                          .hasIdentifierName("MyIdentifier")
                          .hasInitializationValueMatching(BaseAssertion::isNull);
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().identifier("MyType")
                                           .separator("[")
                                           .separator("]")
                                           .separator(":")
                                           .identifier("MyIdentifier")
                                           .operator("=")
                                           .number("expression")
                                           .separator(";")
                                           .build();
        actual = statementParser.parseConstantDeclaration(queue);
        assertThat(actual).isPartiallyParsed()
                          .syntaxDiagnosticContains("Expected ':='")
                          .map(NodeAssertionFactory::create)
                          .hasTypeMatching(a -> a.isListType()
                                                 .mapToInner()
                                                 .isIdentifierType()
                                                 .hasIdentifierMatching(a1 -> a1.isIdentifier()
                                                                                .hasName("MyType"))
                          )
                          .hasIdentifierName("MyIdentifier")
                          .hasInitializationValueMatching(a -> a.isPrimitiveLiteral().hasPrimitiveValue("expression"));
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().operator("something else").separator(";").build();
        actual = statementParser.parseConstantDeclaration(queue);
        assertThat(actual).isUnsuccessful().syntaxDiagnosticContains("Not a statement");
    }

    @Test
    void parseReassignment() {
        TokenQueue queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                                      .operator("+=")
                                                      .number("expression")
                                                      .separator(";")
                                                      .build();
        PartialParseResult<ReassignmentNode> actual = statementParser.parseReassignment(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isVariableReassignment();
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier").operator("++").separator(";").build();
        actual = statementParser.parseReassignment(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .isUnaryExpression()
                          .hasOperator("++")
                          .isPostfixOperation()
                          .mapToInner()
                          .isIdentifier()
                          .hasName("MyIdentifier");

        assertThat(queue).hasNextTokenValueMatch(";");
        queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                           .operator("something")
                                           .operator("something")
                                           .operator("something")
                                           .separator(";")
                                           .build();
        actual = statementParser.parseReassignment(queue);
        assertThat(actual).isPartiallyParsed()
                          .syntaxDiagnosticContains("Expected an assignment operator")
                          .map(NodeAssertionFactory::create)
                          .isVariableReassignment();
        assertThat(queue).hasNextTokenValueMatch(";");

    }

    @Test
    void parseVariableReassignment() {
        TokenQueue queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                                      .operator("+=")
                                                      .number("expression")
                                                      .separator(";")
                                                      .build();
        PartialParseResult<VariableReassignmentNode> actual = statementParser.parseVariableReassignment(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasElementAccessMatching(a -> a.isIdentifier().hasName("MyIdentifier"))
                          .hasOperator("+=")
                          .hasValueMatching(a -> a.isPrimitiveLiteral().hasPrimitiveValue("expression"));
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                           .separator("[")
                                           .number("index")
                                           .separator("]")
                                           .operator("=")
                                           .identifier("expression")
                                           .separator(";")
                                           .build();
        actual = statementParser.parseVariableReassignment(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasElementAccessMatching(
                                  a -> a.isIndexedAccess()
                                        .hasIndexMatching(a1 -> a1.isPrimitiveLiteral()
                                                                  .isNumber()
                                                                  .hasPrimitiveValue("index")
                                        )
                                        .mapToInner()
                                        .isIdentifier()
                                        .hasName("MyIdentifier")
                          )
                          .hasOperator("=")
                          .hasValueMatching(a -> a.isElementAccess().isIdentifier().hasName("expression"));
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                           .operator(":=")
                                           .number("expression")
                                           .separator(";")
                                           .build();
        actual = statementParser.parseVariableReassignment(queue);
        assertThat(actual).isPartiallyParsed()
                          .syntaxDiagnosticContains("Expected an assignment operator")
                          .map(NodeAssertionFactory::create)
                          .hasElementAccessMatching(a -> a.isIdentifier().hasName("MyIdentifier"))
                          .hasOperatorMatching(BaseAssertion::isNull)
                          .hasValueMatching(a -> a.isPrimitiveLiteral().hasPrimitiveValue("expression").isNumber());
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                           .operator("=")
                                           .operator("not an expression")
                                           .separator(";")
                                           .build();
        actual = statementParser.parseVariableReassignment(queue);
        assertThat(actual).isPartiallyParsed()
                          .syntaxDiagnosticContains("Not an expression")
                          .map(NodeAssertionFactory::create)
                          .hasElementAccessMatching(a -> a.isIdentifier().hasName("MyIdentifier"))
                          .hasOperator("=")
                          .hasValueMatching(BaseAssertion::isNull);
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().string("string").separator(";").build();
        assertThat(statementParser.parseVariableReassignment(queue)).isUnsuccessful()
                                                                    .syntaxDiagnosticContains("Not a statement");
    }

    @Test
    void parseExports() {
        TokenQueue queue = new TokenQueueTestBuilder().keyword("export")
                                                      .separator("{")
                                                      .identifier("MyIdentifier")
                                                      .separator("[")
                                                      .number("index")
                                                      .separator("]")
                                                      .separator("}")
                                                      .keyword("as")
                                                      .identifier("MyNamespace")
                                                      .separator(";")
                                                      .operator("end")
                                                      .build();
        PartialParseResult<ExportsNode> actual = statementParser.parseExports(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasExports(1)
                          .hasExportedElementMatching(
                                  0,
                                  a -> a.isIndexedAccess()
                                        .hasIndexMatching(a1 -> a1.isPrimitiveLiteral()
                                                                  .hasPrimitiveValue("index")
                                                                  .isNumber()
                                        )
                                        .mapToInner()
                                        .isIdentifier().hasName("MyIdentifier")
                          ).hasNamespace()
                          .hasNamespaceName("MyNamespace");
        assertThat(queue).hasNextTokenValueMatch("end");

        queue = new TokenQueueTestBuilder().keyword("export")
                                           .separator("{")
                                           .identifier("MyIdentifier1")
                                           .separator(",")
                                           .identifier("MyIdentifier2")
                                           .separator("}")
                                           .keyword("as")
                                           .identifier("MyNamespace")
                                           .separator(";")
                                           .build();
        actual = statementParser.parseExports(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasExports(2)
                          .hasExportedElementMatching(0, a -> a.isIdentifier().hasName("MyIdentifier1"))
                          .hasExportedElementMatching(1, a -> a.isIdentifier().hasName("MyIdentifier2"))
                          .hasNamespace()
                          .hasNamespaceName("MyNamespace");
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().keyword("export").separator("{").separator("}").separator(";").build();
        actual = statementParser.parseExports(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasNoExports()
                          .hasNoNamespace();
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().keyword("export")
                                           .separator("{")
                                           .identifier("MyIdentifier")
                                           .separator(";")
                                           .separator("}")
                                           .separator(";")
                                           .keyword("as")
                                           .identifier("MyNamespace")
                                           .separator("end")
                                           .build();
        actual = statementParser.parseExports(queue);
        assertThat(actual).isPartiallyParsed()
                          .syntaxDiagnosticContains("Expected '}'")
                          .map(NodeAssertionFactory::create)
                          .hasExports(1)
                          .hasExportedElementMatching(0, a -> a.isIdentifier().hasName("MyIdentifier"));
        assertThat(queue).hasNextTokenValueMatch("end");

        queue = new TokenQueueTestBuilder().keyword("export")
                                           .separator("{")
                                           .identifier("MyIdentifier")
                                           .separator(",")
                                           .keyword("as")
                                           .identifier("MyNamespace")
                                           .build();
        actual = statementParser.parseExports(queue);
        assertThat(actual).isPartiallyParsed()
                          .syntaxDiagnosticContains("Expected an identifier")
                          .map(NodeAssertionFactory::create)
                          .hasExports(1)
                          .hasExportedElementMatching(0, a -> a.isIdentifier().hasName("MyIdentifier"));
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().separator(";").build();
        actual = statementParser.parseExports(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasNoExports();
        assertThat(queue).hasNextTokenValueMatch(";");
    }
}