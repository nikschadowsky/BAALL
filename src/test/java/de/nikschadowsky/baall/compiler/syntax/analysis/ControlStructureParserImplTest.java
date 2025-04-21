package de.nikschadowsky.baall.compiler.syntax.analysis;

import de.nikschadowsky.baall.compiler._utility.BaseAssertion;
import de.nikschadowsky.baall.compiler._utility.ParserMockerExtension;
import de.nikschadowsky.baall.compiler._utility.TokenQueueTestBuilder;
import de.nikschadowsky.baall.compiler._utility.ast.NodeAssertionFactory;
import de.nikschadowsky.baall.compiler._utility.ast.nodes.ExpressionNodeAssertion;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.PartialParseResult;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures.ConditionalNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures.ForLoopNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures.WhileLoopNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.ReassignmentNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.EnsureStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.InterceptStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.TryStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static de.nikschadowsky.baall.compiler._utility.BaseAssertion.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @since 09.09.2024
 */
class ControlStructureParserImplTest {

    private final ASTNodeFactory astFactory = new ASTNodeFactory(new NodeDiagnosticCollector());

    private ControlStructureParser controlStructureParser;
    private AuxiliaryParser auxiliaryParser;
    private LiteralParser literalParser;
    private ProgramParser programParser;
    private ExpressionParser expressionParser;

    @RegisterExtension
    private ParserMockerExtension parserMockerExtension = new ParserMockerExtension();

    @BeforeEach
    void setUp() {
        programParser = mock(ProgramParserImpl.class);
        controlStructureParser = new ControlStructureParserImpl(programParser, astFactory);
        auxiliaryParser = new AuxiliaryParserImpl(programParser, astFactory);
        literalParser = new LiteralParserImpl(programParser, astFactory);
        expressionParser = new ExpressionParserImpl(programParser, astFactory);

        when(programParser.getControlStructureParser()).thenReturn(controlStructureParser);
        when(programParser.getAuxiliaryParser()).thenReturn(auxiliaryParser);
        when(programParser.getLiteralParser()).thenReturn(literalParser);
        when(programParser.getExpressionParser()).thenReturn(expressionParser);
    }

    @Test
    void parseControlStructure() {
        StatementsNode mockedStatements = parserMockerExtension.mockStatementsNode();
        parserMockerExtension.mockStatementParserExecution(
                programParser,
                stmtParser -> stmtParser.parseStatements(any()),
                mockedStatements,
                "{",
                "}", ";"
        );

        TokenQueue queue = new TokenQueueTestBuilder().keyword("for")
                                                      .identifier("i")
                                                      .operator("=")
                                                      .number("start")
                                                      .separator("..")
                                                      .number("end")
                                                      .separator("{")
                                                      .operator("statements")
                                                      .separator("}")
                                                      .separator(";")
                                                      .build();
        assertThat(controlStructureParser.parseControlStructure(queue)).isSuccessful()
                                                                       .map(NodeAssertionFactory::create)
                                                                       .isForLoop();
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("while")
                                           .bool("expression")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .separator(";")
                                           .build();
        assertThat(controlStructureParser.parseControlStructure(queue)).isSuccessful()
                                                                       .map(NodeAssertionFactory::create)
                                                                       .isWhileLoop();
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().bool("condition1")
                                           .operator("?")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .operator("|")
                                           .bool("condition2")
                                           .operator("?")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .operator("|")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .separator(";")
                                           .build();
        assertThat(controlStructureParser.parseControlStructure(queue)).isSuccessful()
                                                                       .map(NodeAssertionFactory::create)
                                                                       .isConditional();
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("try")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .keyword("intercept")
                                           .identifier("MyException")
                                           .separator(":")
                                           .identifier("MyIdentifier")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .keyword("ensure")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .separator(";").build();
        assertThat(controlStructureParser.parseControlStructure(queue)).isSuccessful()
                                                                       .map(NodeAssertionFactory::create)
                                                                       .isTryStatement();
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("return").separator(";").build();
        assertThat(controlStructureParser.parseControlStructure(queue)).isUnsuccessful()
                                                                       .syntaxDiagnosticContains("Not a statement");
    }

    @Test
    void parseConditional() {
        StatementsNode mockedStatements = parserMockerExtension.mockStatementsNode();
        parserMockerExtension.mockStatementParserExecution(
                programParser,
                stmtParser -> stmtParser.parseStatements(any()),
                mockedStatements,
                "{",
                "}", ";"
        );

        TokenQueue queue = new TokenQueueTestBuilder().number("expression1")
                                                      .operator("|")
                                                      .number("expression2")
                                                      .operator("?")
                                                      .separator("{")
                                                      .operator("statements")
                                                      .separator("}")
                                                      .operator("statements")
                                                      .separator(";")
                                                      .build();
        PartialParseResult<ConditionalNode> actual = controlStructureParser.parseConditional(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasConditionMatching(ExpressionNodeAssertion::isBinaryExpression)
                          .isIfBranch()
                          .hasBodyMatching(a -> a.isEqualTo(mockedStatements))
                          .hasNoElseBranch();
        assertThat(queue).hasNextTokenValueMatch("statements");

        queue = new TokenQueueTestBuilder().number("expression1")
                                           .operator("|")
                                           .number("expression2")
                                           .operator("?")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .operator("|")
                                           .number("expression1")
                                           .operator("|")
                                           .number("expression2")
                                           .operator("?")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .separator(";")
                                           .build();
        actual = controlStructureParser.parseConditional(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasConditionMatching(ExpressionNodeAssertion::isBinaryExpression)
                          .isIfBranch()
                          .hasBodyMatching(a -> a.isEqualTo(mockedStatements))
                          .hasElseBranch()
                          .hasConditionMatching(ExpressionNodeAssertion::isBinaryExpression)
                          .isIfBranch()
                          .hasBodyMatching(a -> a.isEqualTo(mockedStatements))
                          .hasNoElseBranch();
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().number("expression1")
                                           .operator("|")
                                           .number("expression2")
                                           .operator("?")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .operator("|")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .separator(";")
                                           .build();
        actual = controlStructureParser.parseConditional(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasConditionMatching(ExpressionNodeAssertion::isBinaryExpression)
                          .isIfBranch()
                          .hasBodyMatching(a -> a.isEqualTo(mockedStatements))
                          .hasElseBranch()
                          .hasConditionMatching(BaseAssertion::isNull)
                          .isElseBranch()
                          .hasBodyMatching(a -> a.isEqualTo(mockedStatements))
                          .hasNoElseBranch();
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().number("expression")
                                           .operator("?")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .operator("|")
                                           .separator(";")
                                           .separator("}")
                                           .separator(".")
                                           .build();
        actual = controlStructureParser.parseConditional(queue);
        assertThat(actual).isPartiallyParsed()
                          .syntaxDiagnosticContains("Expected '{' or an expression")
                          .map(NodeAssertionFactory::create)
                          .hasConditionMatching(a -> a.isNumberLiteral().hasValue("expression"))
                          .hasBodyMatching(a -> a.isEqualTo(mockedStatements))
                          .hasElseBranch()
                          .hasConditionMatching(BaseAssertion::isNull);
        assertThat(queue).hasNextTokenValueMatch(".");
    }

    @Test
    void parseElseBlock() {
        StatementsNode mockedStatements = parserMockerExtension.mockStatementsNode();
        parserMockerExtension.mockStatementParserExecution(
                programParser,
                stmtParser -> stmtParser.parseStatements(any()),
                mockedStatements,
                "{",
                "}", ";"
        );

        TokenQueue queue = new TokenQueueTestBuilder().operator("|")
                                                      .separator("{")
                                                      .operator("statements")
                                                      .separator("}")
                                                      .separator(";")
                                                      .build();
        PartialParseResult<ConditionalNode> actual = controlStructureParser.parseElseBlock(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasConditionMatching(BaseAssertion::isNull)
                          .isElseBranch()
                          .hasBodyMatching(a -> a.isEqualTo(mockedStatements))
                          .hasNoElseBranch();
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().operator("|")
                                           .number("condition")
                                           .operator("?")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .separator(";")
                                           .build();
        actual = controlStructureParser.parseElseBlock(queue);

        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasConditionMatching(a -> a.isNumberLiteral().hasValue("condition"))
                          .isIfBranch()
                          .hasBodyMatching(a -> a.isEqualTo(mockedStatements))
                          .hasNoElseBranch();
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().operator("|")
                                           .number("condition1")
                                           .operator("?")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .operator("|")
                                           .number("condition2")
                                           .operator("?")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .operator("|")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .separator(";")
                                           .build();
        actual = controlStructureParser.parseElseBlock(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasConditionMatching(a -> a.isNumberLiteral().hasValue("condition1"))
                          .isIfBranch()
                          .hasBodyMatching(a -> a.isEqualTo(mockedStatements))
                          .hasElseBranch()
                          .hasConditionMatching(a -> a.isNumberLiteral().hasValue("condition2"))
                          .isIfBranch()
                          .hasBodyMatching(a -> a.isEqualTo(mockedStatements))
                          .hasElseBranch()
                          .hasConditionMatching(BaseAssertion::isNull)
                          .isElseBranch()
                          .hasBodyMatching(a -> a.isEqualTo(mockedStatements))
                          .hasNoElseBranch();
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().operator("|")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .separator(";")
                                           .operator("|")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .build();
        actual = controlStructureParser.parseElseBlock(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasConditionMatching(BaseAssertion::isNull)
                          .isElseBranch()
                          .hasBodyMatching(a -> a.isEqualTo(mockedStatements))
                          .hasNoElseBranch();
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().operator("|").separator("{").operator("statements").separator(";").build();
        actual = controlStructureParser.parseElseBlock(queue);
        assertThat(actual).isPartiallyParsed()
                          .syntaxDiagnosticContains("Expected '}'")
                          .map(NodeAssertionFactory::create)
                          .hasConditionMatching(BaseAssertion::isNull)
                          .isElseBranch()
                          .hasBodyMatching(a -> a.isEqualTo(mockedStatements))
                          .hasNoElseBranch();
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().operator("|").separator(";").build();
        actual = controlStructureParser.parseElseBlock(queue);
        assertThat(actual).isPartiallyParsed()
                          .syntaxDiagnosticContains("Expected '{' or an expression")
                          .map(NodeAssertionFactory::create)
                          .hasConditionMatching(BaseAssertion::isNull)
                          .hasBodyMatching(BaseAssertion::isNull);
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().separator(";").build();
        assertThat(controlStructureParser.parseElseBlock(queue)).isUnsuccessful()
                                                                .syntaxDiagnosticContains("Expected '|'");
    }

    @Test
    void parseForLoop() {
        StatementsNode mockedStatements = parserMockerExtension.mockStatementsNode();
        parserMockerExtension.mockStatementParserExecution(
                programParser,
                stmtParser -> stmtParser.parseStatements(any()),
                mockedStatements,
                "{",
                "}", ";"
        );

        assertThat(programParser.getStatementParser().parseStatements(new TokenQueueTestBuilder().build())).isNotNull();

        ReassignmentNode mockedReassignment = parserMockerExtension.mockReassignmentNode();
        parserMockerExtension.mockStatementParserExecution(
                programParser,
                stmtParser -> stmtParser.parseReassignment(any()),
                mockedReassignment,
                "{", "}", ";", "::"
        );

        assertThat(programParser.getStatementParser().parseStatements(new TokenQueueTestBuilder().build())).isNotNull();

        TokenQueue queue = new TokenQueueTestBuilder().keyword("for")
                                                      .identifier("i")
                                                      .operator("=")
                                                      .number("expr1")
                                                      .separator("..")
                                                      .number("expr2")
                                                      .separator("::")
                                                      .operator("reassignment")
                                                      .separator("{")
                                                      .operator("statements")
                                                      .separator("}")
                                                      .separator(";")
                                                      .build();
        PartialParseResult<ForLoopNode> actual = controlStructureParser.parseForLoop(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasIdentifier("i")
                          .hasStartIndexMatching(a -> a.isNumberLiteral().hasValue("expr1"))
                          .hasEndIndexMatching(a -> a.isNumberLiteral().hasValue("expr2"))
                          .hasOptionalStepper()
                          .hasOptionalStepperMatching(a -> a.isEqualTo(mockedReassignment))
                          .hasBodyMatching(a -> a.isEqualTo(mockedStatements));

        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("for")
                                           .identifier("i")
                                           .operator("=")
                                           .number("expr1")
                                           .separator("..")
                                           .number("expr2")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .separator(";")
                                           .build();
        actual = controlStructureParser.parseForLoop(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasIdentifier("i")
                          .hasStartIndexMatching(a -> a.isNumberLiteral().hasValue("expr1"))
                          .hasEndIndexMatching(a -> a.isNumberLiteral().hasValue("expr2"))
                          .doesNotHaveOptionalStepper()
                          .hasBodyMatching(a -> a.isEqualTo(mockedStatements));
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("for")
                                           .operator("=")
                                           .number("expression1")
                                           .separator("..")
                                           .number("expression2")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .separator(";")
                                           .build();
        actual = controlStructureParser.parseForLoop(queue);
        assertThat(actual).isPartiallyParsed()
                          .syntaxDiagnosticContains("Expected an identifier")
                          .map(NodeAssertionFactory::create)
                          .hasIdentifierMatching(BaseAssertion::isNull)
                          .hasBodyMatching(a -> a.isEqualTo(mockedStatements));
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().separator(";").build();
        assertThat(controlStructureParser.parseForLoop(queue)).isUnsuccessful()
                                                              .syntaxDiagnosticContains("Expected 'for'");
    }

    @Test
    void parseWhileLoop() {
        StatementsNode mockedStatements = parserMockerExtension.mockStatementsNode();
        parserMockerExtension.mockStatementParserExecution(
                programParser,
                stmtParser -> stmtParser.parseStatements(any()),
                mockedStatements,
                "{",
                "}", ";"
        );

        TokenQueue queue = new TokenQueueTestBuilder().keyword("while")
                                                      .bool("true")
                                                      .separator("{")
                                                      .operator("statements")
                                                      .separator("}")
                                                      .separator(";")
                                                      .build();
        PartialParseResult<WhileLoopNode> actual = controlStructureParser.parseWhileLoop(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasConditionMatching(a -> a.isBooleanLiteral().hasValue("true"))
                          .hasBodyMatching(a -> a.isEqualTo(mockedStatements));
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("while")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .separator(";")
                                           .build();
        actual = controlStructureParser.parseWhileLoop(queue);
        assertThat(actual).isPartiallyParsed()
                          .syntaxDiagnosticContains("Not an expression")
                          .map(NodeAssertionFactory::create)
                          .hasConditionMatching(BaseAssertion::isNull)
                          .hasBodyMatching(a -> a.isEqualTo(mockedStatements));

        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("while")
                                           .bool("true")
                                           .separator("{")
                                           .operator("statements")
                                           .separator(";")
                                           .build();
        actual = controlStructureParser.parseWhileLoop(queue);
        assertThat(actual).isPartiallyParsed()
                          .syntaxDiagnosticContains("Expected '}'")
                          .map(NodeAssertionFactory::create)
                          .hasConditionMatching(a -> a.isBooleanLiteral().hasValue("true"))
                          .hasBodyMatching(a -> a.isEqualTo(mockedStatements));
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().keyword("while")
                                           .bool("true")
                                           .operator("statements")
                                           .separator("}")
                                           .separator(";")
                                           .build();
        actual = controlStructureParser.parseWhileLoop(queue);
        assertThat(actual).isPartiallyParsed()
                          .syntaxDiagnosticContains("Expected '{'")
                          .map(NodeAssertionFactory::create)
                          .hasConditionMatching(a -> a.isBooleanLiteral().hasValue("true"));
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().separator(";").build();
        assertThat(controlStructureParser.parseWhileLoop(queue)).isUnsuccessful()
                                                                .syntaxDiagnosticContains("Expected 'while'");
    }

    @Test
    void parseTryStatement() {
        StatementsNode mockedStatements = parserMockerExtension.mockStatementsNode();
        parserMockerExtension.mockStatementParserExecution(
                programParser,
                stmtParser -> stmtParser.parseStatements(any()),
                mockedStatements,
                "{",
                "}",
                ";"
        );

        TokenQueue queue = new TokenQueueTestBuilder().keyword("try")
                                                      .separator("{")
                                                      .operator("statements")
                                                      .separator("}")
                                                      .keyword("intercept")
                                                      .identifier("MyException1")
                                                      .separator(":")
                                                      .identifier("MyIdentifier1")
                                                      .separator("{")
                                                      .operator("statements")
                                                      .separator("}")
                                                      .keyword("intercept")
                                                      .identifier("MyException2")
                                                      .separator(":")
                                                      .identifier("MyIdentifier2")
                                                      .separator("{")
                                                      .operator("statements")
                                                      .separator("}")
                                                      .keyword("ensure")
                                                      .separator("{")
                                                      .operator("statements")
                                                      .separator("}")
                                                      .separator(";")
                                                      .build();

        PartialParseResult<TryStatementNode> actual = controlStructureParser.parseTryStatement(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasBodyMatching(a -> a.isEqualTo(mockedStatements))
                          .hasInterceptBlocks(2)
                          .hasInterceptBlockMatching(
                                  0,
                                  a -> a.hasExceptionMatching(
                                                a1 -> a1.isIdentifierType()
                                                        .hasIdentifierMatching(a2 -> a2.isIdentifier()
                                                                                       .hasName("MyException1"))
                                        )
                                        .hasIdentifier("MyIdentifier1")
                                        .hasBodyMatching(a1 -> a1.isEqualTo(mockedStatements))
                          )
                          .hasInterceptBlockMatching(
                                  1,
                                  a -> a.hasExceptionMatching(
                                                a1 -> a1.isIdentifierType()
                                                        .hasIdentifierMatching(a2 -> a2.isIdentifier()
                                                                                       .hasName(
                                                                                               "MyException2"))
                                        )
                                        .hasIdentifier("MyIdentifier2")
                                        .hasBodyMatching(a1 -> a1.isEqualTo(mockedStatements))
                          )
                          .hasEnsureBlock()
                          .hasBodyMatching(a -> a.isEqualTo(mockedStatements));
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("try")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .keyword("intercept")
                                           .identifier("MyException")
                                           .separator(":")
                                           .identifier("MyIdentifier")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .separator(";")
                                           .build();
        actual = controlStructureParser.parseTryStatement(queue);
        assertThat(actual).isSuccessful().map(NodeAssertionFactory::create)
                          .hasBodyMatching(a -> a.isEqualTo(mockedStatements))
                          .hasInterceptBlocks(1)
                          .hasInterceptBlockMatching(
                                  0,
                                  a -> a.hasExceptionMatching(
                                                a1 -> a1.isIdentifierType()
                                                        .hasIdentifierMatching(a2 -> a2.isIdentifier()
                                                                                       .hasName("MyException"))
                                        )
                                        .hasIdentifier("MyIdentifier")
                                        .hasBodyMatching(a1 -> a1.isEqualTo(mockedStatements))
                          )
                          .doesNotHaveEnsureBlock();
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("try")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .separator(";")
                                           .build();
        actual = controlStructureParser.parseTryStatement(queue);
        assertThat(actual).isPartiallyParsed()
                          .syntaxDiagnosticContains("Expected 'intercept'")
                          .map(NodeAssertionFactory::create)
                          .hasBodyMatching(a -> a.isEqualTo(mockedStatements))
                          .hasInterceptBlocks(0)
                          .doesNotHaveEnsureBlock();
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("try").separator(";").build();
        actual = controlStructureParser.parseTryStatement(queue);
        assertThat(actual).isPartiallyParsed()
                          .syntaxDiagnosticContains("Expected '{'")
                          .map(NodeAssertionFactory::create)
                          .hasBodyMatching(BaseAssertion::isNull);
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().keyword("try").separator("}")
                                           .keyword("intercept")
                                           .identifier("MyException")
                                           .separator(":")
                                           .identifier("MyIdentifier")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}").build();
        actual = controlStructureParser.parseTryStatement(queue);
        assertThat(actual).isPartiallyParsed()
                          .syntaxDiagnosticContains("Expected '{'")
                          .map(NodeAssertionFactory::create)
                          .hasBodyMatching(BaseAssertion::isNull)
                          .hasInterceptBlocks(1);
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().keyword("something").separator(";").build();
        assertThat(controlStructureParser.parseTryStatement(queue)).isUnsuccessful()
                                                                   .syntaxDiagnosticContains("Not a statement");
    }

    @Test
    void parseInterceptStatements() {
        StatementsNode mockedStatements = parserMockerExtension.mockStatementsNode();
        parserMockerExtension.mockStatementParserExecution(
                programParser,
                stmtParser -> stmtParser.parseStatements(any()),
                mockedStatements,
                "{",
                "}",
                ";"
        );

        TokenQueue queue = new TokenQueueTestBuilder().keyword("intercept")
                                                      .identifier("MyException1")
                                                      .separator(":")
                                                      .identifier("MyIdentifier1")
                                                      .separator("{")
                                                      .operator("statements")
                                                      .separator("}")
                                                      .keyword("intercept")
                                                      .identifier("MyException2")
                                                      .separator(":")
                                                      .identifier("MyIdentifier2")
                                                      .separator("{")
                                                      .operator("statements")
                                                      .separator("}")
                                                      .separator(";")
                                                      .build();

        PartialParseResult<List<InterceptStatementNode>> actual =
                controlStructureParser.parseInterceptStatements(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasSize(2)
                          .hasElementMatching(
                                  0,
                                  NodeAssertionFactory::create,
                                  a -> a.hasIdentifier("MyIdentifier1")
                                        .hasBodyMatching(a1 -> a1.isEqualTo(mockedStatements))
                          )
                          .hasElementMatching(
                                  1,
                                  NodeAssertionFactory::create,
                                  a -> a.hasIdentifier("MyIdentifier2")
                                        .hasBodyMatching(a1 -> a1.isEqualTo(mockedStatements))
                          );

        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("intercept")
                                           .identifier("MyException1")
                                           .separator(":")
                                           .identifier("MyIdentifier1")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .keyword("intercept")
                                           .identifier("MyException2")
                                           .separator(":")
                                           .identifier("MyIdentifier2")
                                           .separator("{")
                                           .operator("statements")
                                           .separator(";")
                                           .build();
        actual = controlStructureParser.parseInterceptStatements(queue);
        assertThat(actual).isPartiallyParsed()
                          .syntaxDiagnosticContains("Expected '}'")
                          .map(NodeAssertionFactory::create).hasSize(2);
        assertThat(queue).isAtEnd();
    }

    @Test
    void parseInterceptStatement() {
        StatementsNode mockedStatements = parserMockerExtension.mockStatementsNode();
        parserMockerExtension.mockStatementParserExecution(
                programParser,
                stmtParser -> stmtParser.parseStatements(any()),
                mockedStatements,
                "{",
                "}",
                ";"
        );

        TokenQueue queue = new TokenQueueTestBuilder().keyword("intercept")
                                                      .identifier("MyException")
                                                      .separator(":")
                                                      .identifier("MyIdentifier")
                                                      .separator("{")
                                                      .operator("statements")
                                                      .separator("}")
                                                      .separator(";")
                                                      .build();

        PartialParseResult<InterceptStatementNode> actual = controlStructureParser.parseInterceptStatement(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasExceptionMatching(
                                  a -> a.isIdentifierType()
                                        .hasIdentifierMatching(a1 -> a1.isIdentifier().hasName("MyException"))
                          )
                          .hasIdentifier("MyIdentifier")
                          .hasBodyMatching(a -> a.isEqualTo(mockedStatements));
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("intercept")
                                           .identifier("MyException2")
                                           .separator("[")
                                           .number("index")
                                           .separator("]")
                                           .separator(":")
                                           .identifier("MyIdentifier")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .separator(";")
                                           .build();
        actual = controlStructureParser.parseInterceptStatement(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasExceptionMatching(
                                  a -> a.isIdentifierType()
                                        .hasIdentifierMatching(a1 -> a1.isIndexedAccess()
                                                                       .hasIndexMatching(ExpressionNodeAssertion::isNumberLiteral)
                                                                       .mapToInner()
                                                                       .isIdentifier()
                                                                       .hasName("MyException2")
                                        )
                          )
                          .hasIdentifier("MyIdentifier")
                          .hasBodyMatching(a -> a.isEqualTo(mockedStatements));
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("intercept")
                                           .identifier("MyException")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .separator(";")
                                           .build();
        actual = controlStructureParser.parseInterceptStatement(queue);
        assertThat(actual).isPartiallyParsed()
                          .syntaxDiagnosticContains("Expected ':'")
                          .map(NodeAssertionFactory::create)
                          .hasIdentifierMatching(BaseAssertion::isNull)
                          .hasBodyMatching(a -> a.isEqualTo(mockedStatements));
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().separator(";").build();
        actual = controlStructureParser.parseInterceptStatement(queue);
        assertThat(actual).isUnsuccessful().syntaxDiagnosticContains("Expected 'intercept'");
    }

    @Test
    void parseEnsureStatement() {
        StatementsNode mockedStatements = parserMockerExtension.mockStatementsNode();
        parserMockerExtension.mockStatementParserExecution(
                programParser,
                stmtParser -> stmtParser.parseStatements(any()),
                mockedStatements,
                "{",
                "}",
                ";"
        );

        TokenQueue queue = new TokenQueueTestBuilder().keyword("ensure")
                                                      .separator("{")
                                                      .operator("statements")
                                                      .separator("}")
                                                      .separator(";")
                                                      .build();

        PartialParseResult<EnsureStatementNode> actual = controlStructureParser.parseEnsureStatement(queue);
        assertThat(actual).isSuccessful()
                          .map(NodeAssertionFactory::create)
                          .hasBodyMatching(a -> a.isEqualTo(mockedStatements));
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("ensure")
                                           .operator("statements")
                                           .separator("}")
                                           .separator(";")
                                           .build();
        actual = controlStructureParser.parseEnsureStatement(queue);
        assertThat(actual).isPartiallyParsed()
                          .syntaxDiagnosticContains("Expected '{'")
                          .map(NodeAssertionFactory::create)
                          .hasBodyMatching(BaseAssertion::isNull);

        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("ensure")
                                           .separator("{")
                                           .operator("statements")
                                           .separator(";")
                                           .build();
        actual = controlStructureParser.parseEnsureStatement(queue);
        assertThat(actual).isPartiallyParsed()
                          .syntaxDiagnosticContains("Expected '}'")
                          .map(NodeAssertionFactory::create)
                          .hasBodyMatching(a -> a.isEqualTo(mockedStatements));
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().separator(";").build();
        actual = controlStructureParser.parseEnsureStatement(queue);
        assertThat(actual).isUnsuccessful()
                          .syntaxDiagnosticContains("Expected 'ensure'");
    }

}