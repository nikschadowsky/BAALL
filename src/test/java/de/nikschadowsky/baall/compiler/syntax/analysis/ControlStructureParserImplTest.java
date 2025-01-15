package de.nikschadowsky.baall.compiler.syntax.analysis;

import de.nikschadowsky.baall.compiler._utility.ParserMockerExtension;
import de.nikschadowsky.baall.compiler._utility.TokenQueueTestBuilder;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures.ConditionalNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures.ForLoopNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures.WhileLoopNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.BinaryExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.ReassignmentNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.TryStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.PrimitiveLiteralNode;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

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
        StatementsNode mockedStatements = mock(StatementsNode.class);
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
                                                                       .resultMatches(node -> node instanceof ForLoopNode);
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("while")
                                           .bool("expression")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .separator(";")
                                           .build();
        assertThat(controlStructureParser.parseControlStructure(queue)).isSuccessful()
                                                                       .resultMatches(node -> node instanceof WhileLoopNode);
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
                                                                       .resultMatches(node -> node instanceof ConditionalNode);
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
                                                                       .resultMatches(node -> node instanceof TryStatementNode);
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("return").separator(";").build();
        assertThat(controlStructureParser.parseControlStructure(queue)).isUnsuccessful()
                                                                       .syntaxDiagnosticContains("Not a statement");
    }

    @Test
    void parseConditional() {
        StatementsNode mockedStatements = mock(StatementsNode.class);
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
        assertThat(controlStructureParser.parseConditional(queue)).isSuccessful()
                                                                  .resultMatches(node -> node.getCondition() instanceof BinaryExpressionNode)
                                                                  .resultMatches(node -> node.getConditionBranch()
                                                                                             .equals(
                                                                                                     ConditionalNode.ConditionBranch.IF))
                                                                  .resultMatches(node -> node.getThenBlock() == mockedStatements)
                                                                  .resultMatches(node -> node.getElseBranch()
                                                                                             .isEmpty());
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
        assertThat(controlStructureParser.parseConditional(queue)).isSuccessful()
                                                                  .resultMatches(node -> node.getCondition() instanceof BinaryExpressionNode)
                                                                  .resultMatches(node -> ConditionalNode.ConditionBranch.IF
                                                                          .equals(node.getConditionBranch()))
                                                                  .resultMatches(node -> node.getThenBlock() == mockedStatements)
                                                                  .resultMatches(node -> node.getElseBranch()
                                                                                             .orElseThrow()
                                                                                             .getCondition() instanceof BinaryExpressionNode)
                                                                  .resultMatches(node -> ConditionalNode.ConditionBranch.IF.equals(
                                                                          node.getElseBranch()
                                                                              .orElseThrow().getConditionBranch()))
                                                                  .resultMatches(node -> node.getElseBranch()
                                                                                             .orElseThrow()
                                                                                             .getThenBlock() == mockedStatements)
                                                                  .resultMatches(node -> node.getElseBranch()
                                                                                             .orElseThrow()
                                                                                             .getElseBranch()
                                                                                             .isEmpty());
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
        assertThat(controlStructureParser.parseConditional(queue)).isSuccessful()
                                                                  .resultMatches(node -> node.getCondition() instanceof BinaryExpressionNode)
                                                                  .resultMatches(node -> ConditionalNode.ConditionBranch.IF
                                                                          .equals(node.getConditionBranch()))
                                                                  .resultMatches(node -> node.getThenBlock() == mockedStatements)
                                                                  .resultMatches(node -> node.getElseBranch()
                                                                                             .orElseThrow()
                                                                                             .getCondition() == null)
                                                                  .resultMatches(node -> ConditionalNode.ConditionBranch.ELSE.equals(
                                                                          node.getElseBranch()
                                                                              .orElseThrow().getConditionBranch()))
                                                                  .resultMatches(node -> node.getElseBranch()
                                                                                             .orElseThrow()
                                                                                             .getThenBlock() == mockedStatements)
                                                                  .resultMatches(node -> node.getElseBranch()
                                                                                             .orElseThrow()
                                                                                             .getElseBranch()
                                                                                             .isEmpty());
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
        assertThat(controlStructureParser.parseConditional(queue)).isPartiallyParsed()
                                                                  .resultMatches(node -> node.getCondition() instanceof PrimitiveLiteralNode)
                                                                  .resultMatches(node -> node.getThenBlock() == mockedStatements)
                                                                  .resultMatches(node -> node.getElseBranch()
                                                                                             .orElseThrow()
                                                                                             .getCondition() == null)
                                                                  .syntaxDiagnosticContains(
                                                                          "Expected '{' or an expression");
        assertThat(queue).hasNextTokenValueMatch(".");
    }

    @Test
    void parseElseBlock() {
        StatementsNode mockedStatements = mock(StatementsNode.class);
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
        assertThat(controlStructureParser.parseElseBlock(queue)).isSuccessful()
                                                                .resultMatches(node -> node.getCondition() == null)
                                                                .resultMatches(node -> ConditionalNode.ConditionBranch.ELSE.equals(
                                                                        node.getConditionBranch()))
                                                                .resultMatches(node -> node.getThenBlock() == mockedStatements)
                                                                .resultMatches(node -> node.getElseBranch().isEmpty());
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().operator("|")
                                           .number("condition")
                                           .operator("?")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .separator(";")
                                           .build();
        assertThat(controlStructureParser.parseElseBlock(queue)).isSuccessful()
                                                                .resultMatches(node -> {
                                                                    PrimitiveLiteralNode condition =
                                                                            (PrimitiveLiteralNode) node.getCondition();
                                                                    return "condition".equals(condition.getPrimitiveValue()
                                                                                                       .value());
                                                                })
                                                                .resultMatches(node -> ConditionalNode.ConditionBranch.IF.equals(
                                                                        node.getConditionBranch()))
                                                                .resultMatches(node -> node.getThenBlock() == mockedStatements)
                                                                .resultMatches(node -> node.getElseBranch().isEmpty());
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
        assertThat(controlStructureParser.parseElseBlock(queue)).isSuccessful()
                                                                .resultMatches(node -> {
                                                                    PrimitiveLiteralNode condition =
                                                                            (PrimitiveLiteralNode) node.getCondition();
                                                                    return "condition1".equals(condition.getPrimitiveValue()
                                                                                                        .value());
                                                                })
                                                                .resultMatches(node -> ConditionalNode.ConditionBranch.IF.equals(
                                                                        node.getConditionBranch()))
                                                                .resultMatches(node -> node.getThenBlock() == mockedStatements)
                                                                .resultMatches(node -> {
                                                                    ConditionalNode elseIfNode =
                                                                            node.getElseBranch().orElseThrow();
                                                                    PrimitiveLiteralNode condition =
                                                                            (PrimitiveLiteralNode) elseIfNode.getCondition();
                                                                    return "condition2".equals(condition.getPrimitiveValue()
                                                                                                        .value());
                                                                }).resultMatches(node -> {
                                                                    ConditionalNode elseIfNode =
                                                                            node.getElseBranch().orElseThrow();
                                                                    return ConditionalNode.ConditionBranch.IF.equals(
                                                                            elseIfNode.getConditionBranch());
                                                                }).resultMatches(node -> {
                                                                    ConditionalNode elseIfNode =
                                                                            node.getElseBranch().orElseThrow();
                                                                    return elseIfNode.getThenBlock() == mockedStatements;
                                                                }).resultMatches(node -> {
                                                                    ConditionalNode elseIfNode =
                                                                            node.getElseBranch().orElseThrow();
                                                                    ConditionalNode elseNode = elseIfNode.getElseBranch().orElseThrow();
                                                                    return elseNode.getCondition() == null;
                                                                }).resultMatches(node -> {
                                                                    ConditionalNode elseIfNode =
                                                                            node.getElseBranch().orElseThrow();
                                                                    ConditionalNode elseNode = elseIfNode.getElseBranch().orElseThrow();
                                                                    return elseNode.getConditionBranch().equals(
                                                                            ConditionalNode.ConditionBranch.ELSE);
                                                                }).resultMatches(node -> {
                                                                    ConditionalNode elseIfNode =
                                                                            node.getElseBranch().orElseThrow();
                                                                    ConditionalNode elseNode = elseIfNode.getElseBranch().orElseThrow();
                                                                    return elseNode.getThenBlock() == mockedStatements;
                                                                }).resultMatches(node -> {
                                                                    ConditionalNode elseIfNode =
                                                                            node.getElseBranch().orElseThrow();
                                                                    ConditionalNode elseNode = elseIfNode.getElseBranch().orElseThrow();
                                                                    return elseNode.getElseBranch().isEmpty();
                                                                });
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
        assertThat(controlStructureParser.parseElseBlock(queue)).isSuccessful()
                                                                .resultMatches(node -> node.getCondition() == null)
                                                                .resultMatches(node -> ConditionalNode.ConditionBranch.ELSE.equals(
                                                                        node.getConditionBranch()))
                                                                .resultMatches(node -> node.getThenBlock() == mockedStatements)
                                                                .resultMatches(node -> node.getElseBranch().isEmpty());
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().operator("|").separator("{").operator("statements").separator(";").build();
        assertThat(controlStructureParser.parseElseBlock(queue)).isPartiallyParsed()
                                                                .resultMatches(node -> node.getThenBlock() == mockedStatements)
                                                                .resultMatches(node -> node.getElseBranch().isEmpty())
                                                                .resultMatches(node -> ConditionalNode.ConditionBranch.ELSE.equals(
                                                                        node.getConditionBranch()))
                                                                .syntaxDiagnosticContains("Expected '}'");
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().operator("|").separator(";").build();
        assertThat(controlStructureParser.parseElseBlock(queue)).isPartiallyParsed()
                                                                .resultMatches(node -> node.getCondition() == null)
                                                                .resultMatches(node -> node.getThenBlock() == null)
                                                                .syntaxDiagnosticContains(
                                                                        "Expected '{' or an expression");
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().separator(";").build();
        assertThat(controlStructureParser.parseElseBlock(queue)).isUnsuccessful()
                                                                .syntaxDiagnosticContains("Expected '|'");
    }

    @Test
    void parseForLoop() {
        StatementsNode mockedStatements = mock(StatementsNode.class);
        parserMockerExtension.mockStatementParserExecution(
                programParser,
                stmtParser -> stmtParser.parseStatements(any()),
                mockedStatements,
                "{",
                "}", ";"
        );

        assertThat(programParser.getStatementParser().parseStatements(new TokenQueueTestBuilder().build())).isNotNull();

        ReassignmentNode mockedReassignment = mock(ReassignmentNode.class);
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
                                                      .number("expression1")
                                                      .separator("..")
                                                      .number("expression2")
                                                      .separator("::")
                                                      .operator("reassignment")
                                                      .separator("{")
                                                      .operator("statements")
                                                      .separator("}")
                                                      .separator(";")
                                                      .build();
        assertThat(controlStructureParser.parseForLoop(queue)).isSuccessful()
                                                              .resultMatches(node -> "i".equals(node.getIdentifier()
                                                                                                    .value()))
                                                              .resultMatches(node -> {
                                                                  PrimitiveLiteralNode expressionNode =
                                                                          (PrimitiveLiteralNode) (node.getStartIndexExpression());
                                                                  return "expression1".equals(expressionNode.getPrimitiveValue()
                                                                                                            .value());
                                                              })
                                                              .resultMatches(node -> {
                                                                  PrimitiveLiteralNode expressionNode =
                                                                          (PrimitiveLiteralNode) (node.getEndIndexExpression());
                                                                  return "expression2".equals(expressionNode.getPrimitiveValue()
                                                                                                            .value());
                                                              })
                                                              .resultMatches(node -> node.getOptionalStepperStatement()
                                                                                         .orElseThrow() == mockedReassignment)
                                                              .resultMatches(node -> node.getBody() == mockedStatements);
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("for")
                                           .identifier("i")
                                           .operator("=")
                                           .number("expression1")
                                           .separator("..")
                                           .number("expression2")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .separator(";")
                                           .build();
        assertThat(controlStructureParser.parseForLoop(queue)).isSuccessful()
                                                              .resultMatches(node -> "i".equals(node.getIdentifier()
                                                                                                    .value()))
                                                              .resultMatches(node -> {
                                                                  PrimitiveLiteralNode expressionNode =
                                                                          (PrimitiveLiteralNode) (node.getStartIndexExpression());
                                                                  return "expression1".equals(expressionNode.getPrimitiveValue()
                                                                                                            .value());
                                                              })
                                                              .resultMatches(node -> {
                                                                  PrimitiveLiteralNode expressionNode =
                                                                          (PrimitiveLiteralNode) (node.getEndIndexExpression());
                                                                  return "expression2".equals(expressionNode.getPrimitiveValue()
                                                                                                            .value());
                                                              })
                                                              .resultMatches(node -> node.getOptionalStepperStatement()
                                                                                         .isEmpty())
                                                              .resultMatches(node -> node.getBody() == mockedStatements);
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
        assertThat(controlStructureParser.parseForLoop(queue)).isPartiallyParsed()
                                                              .resultMatches(node -> node.getIdentifier() == null)
                                                              .resultMatches(node -> node.getBody() == mockedStatements)
                                                              .syntaxDiagnosticContains("Expected an identifier");
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().separator(";").build();
        assertThat(controlStructureParser.parseForLoop(queue)).isUnsuccessful()
                                                              .syntaxDiagnosticContains("Expected 'for'");
    }

    @Test
    void parseWhileLoop() {
        StatementsNode mockedStatements = mock(StatementsNode.class);
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
        assertThat(controlStructureParser.parseWhileLoop(queue)).isSuccessful()
                                                                .resultMatches(node -> "true".equals(((PrimitiveLiteralNode) node.getCondition()).getPrimitiveValue()
                                                                                                                                                 .value()))
                                                                .resultMatches(node -> node.getBody() == mockedStatements);
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("while")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .separator(";")
                                           .build();
        assertThat(controlStructureParser.parseWhileLoop(queue)).isPartiallyParsed()
                                                                .resultMatches(node -> node.getCondition() == null)
                                                                .resultMatches(node -> node.getBody() == mockedStatements)
                                                                .syntaxDiagnosticContains("Not an expression");
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("while")
                                           .bool("true")
                                           .separator("{")
                                           .operator("statements")
                                           .separator(";")
                                           .build();
        assertThat(controlStructureParser.parseWhileLoop(queue)).isPartiallyParsed()
                                                                .resultMatches(node -> ((PrimitiveLiteralNode) node.getCondition()).getPrimitiveValue()
                                                                                                                                   .value()
                                                                                                                                   .equals("true"))
                                                                .resultMatches(node -> node.getBody() == mockedStatements)
                                                                .syntaxDiagnosticContains("Expected '}'");
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().keyword("while")
                                           .bool("true")
                                           .operator("statements")
                                           .separator("}")
                                           .separator(";")
                                           .build();
        assertThat(controlStructureParser.parseWhileLoop(queue)).isPartiallyParsed()
                                                                .resultMatches(node -> ((PrimitiveLiteralNode) node.getCondition()).getPrimitiveValue()
                                                                                                                                   .value()
                                                                                                                                   .equals("true"))
                                                                .syntaxDiagnosticContains("Expected '{'");
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().separator(";").build();
        assertThat(controlStructureParser.parseWhileLoop(queue)).isUnsuccessful()
                                                                .syntaxDiagnosticContains("Expected 'while'");
    }

    @Test
    void parseTryStatement() {
        StatementsNode mockedStatements = mock(StatementsNode.class);
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

        assertThat(controlStructureParser.parseTryStatement(queue)).isSuccessful()
                                                                   .resultMatches(node -> node.getBody() == mockedStatements)
                                                                   .resultMatches(node -> node.getInterceptBlocks()
                                                                                              .size() == 2)
                                                                   .resultMatches(node -> node.getInterceptBlocks()
                                                                                              .get(0)
                                                                                              .getInterceptedExceptions()
                                                                                              .size() == 1)
                                                                   .resultMatches(node -> "MyException1"
                                                                           .equals(node.getInterceptBlocks()
                                                                                       .get(0)
                                                                                       .getInterceptedExceptions()
                                                                                       .get(0)
                                                                                       .getIdentifier()
                                                                                       .value()))
                                                                   .resultMatches(node -> node.getInterceptBlocks()
                                                                                              .get(0)
                                                                                              .getInterceptedExceptions()
                                                                                              .get(0)
                                                                                              .getArrayIndices()
                                                                                              .isEmpty())
                                                                   .resultMatches(node -> "MyIdentifier1"
                                                                           .equals(node.getInterceptBlocks()
                                                                                       .get(0)
                                                                                       .getRaisedExceptionIdentifier()
                                                                                       .value()))
                                                                   .resultMatches(node -> node.getInterceptBlocks()
                                                                                              .get(1)
                                                                                              .getBody() == mockedStatements)
                                                                   .resultMatches(node -> "MyException2"
                                                                           .equals(node.getInterceptBlocks()
                                                                                       .get(1)
                                                                                       .getInterceptedExceptions()
                                                                                       .get(0)
                                                                                       .getIdentifier()
                                                                                       .value()))
                                                                   .resultMatches(node -> node.getInterceptBlocks()
                                                                                              .get(1)
                                                                                              .getInterceptedExceptions()
                                                                                              .get(0)
                                                                                              .getArrayIndices()
                                                                                              .isEmpty())
                                                                   .resultMatches(node -> "MyIdentifier2"
                                                                           .equals(node.getInterceptBlocks()
                                                                                       .get(1)
                                                                                       .getRaisedExceptionIdentifier()
                                                                                       .value()))
                                                                   .resultMatches(node -> node.getInterceptBlocks()
                                                                                              .get(0)
                                                                                              .getBody() == mockedStatements)
                                                                   .resultMatches(node -> node.getEnsureBlock()
                                                                                              .orElseThrow()
                                                                                              .getBody() == mockedStatements);
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
        assertThat(controlStructureParser.parseTryStatement(queue)).isSuccessful()
                                                                   .resultMatches(node -> node.getBody() == mockedStatements)
                                                                   .resultMatches(node -> node.getInterceptBlocks()
                                                                                              .size() == 1)
                                                                   .resultMatches(node -> node.getInterceptBlocks()
                                                                                              .get(0)
                                                                                              .getInterceptedExceptions()
                                                                                              .size() == 1)
                                                                   .resultMatches(node -> "MyException"
                                                                           .equals(node.getInterceptBlocks()
                                                                                       .get(0)
                                                                                       .getInterceptedExceptions()
                                                                                       .get(0)
                                                                                       .getIdentifier()
                                                                                       .value()))
                                                                   .resultMatches(node -> node.getInterceptBlocks()
                                                                                              .get(0)
                                                                                              .getInterceptedExceptions()
                                                                                              .get(0)
                                                                                              .getArrayIndices()
                                                                                              .isEmpty())
                                                                   .resultMatches(node -> "MyIdentifier"
                                                                           .equals(node.getInterceptBlocks()
                                                                                       .get(0)
                                                                                       .getRaisedExceptionIdentifier()
                                                                                       .value()))
                                                                   .resultMatches(node -> node.getInterceptBlocks()
                                                                                              .get(0)
                                                                                              .getBody() == mockedStatements)
                                                                   .resultMatches(node -> node.getEnsureBlock()
                                                                                              .isEmpty());
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("try")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .separator(";")
                                           .build();
        assertThat(controlStructureParser.parseTryStatement(queue)).isPartiallyParsed()
                                                                   .resultMatches(node -> node.getBody() == mockedStatements)
                                                                   .resultMatches(node -> node.getInterceptBlocks()
                                                                                              .isEmpty())
                                                                   .resultMatches(node -> node.getEnsureBlock()
                                                                                              .isEmpty())
                                                                   .syntaxDiagnosticContains("Expected 'intercept'");
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("try").separator(";").build();
        assertThat(controlStructureParser.parseTryStatement(queue)).isPartiallyParsed()
                                                                   .resultMatches(node -> node.getBody() == null)
                                                                   .syntaxDiagnosticContains("Expected '{'");
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().keyword("try").separator("}")
                                           .keyword("intercept")
                                           .identifier("MyException")
                                           .separator(":")
                                           .identifier("MyIdentifier")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}").build();
        assertThat(controlStructureParser.parseTryStatement(queue)).isPartiallyParsed()
                                                                   .resultMatches(node -> node.getBody() == null)
                                                                   .resultMatches(node -> node.getInterceptBlocks()
                                                                                              .size() == 1)
                                                                   .syntaxDiagnosticContains("Expected '{'");
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().keyword("something").separator(";").build();
        assertThat(controlStructureParser.parseTryStatement(queue)).isUnsuccessful()
                                                                   .syntaxDiagnosticContains("Not a statement");
    }

    @Test
    void parseInterceptStatements() {
        StatementsNode mockedStatements = mock(StatementsNode.class);
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

        assertThat(controlStructureParser.parseInterceptStatements(queue)).isSuccessful()
                                                                          .resultMatches(nodes -> nodes.size() == 2)
                                                                          .resultMatches(nodes -> nodes.get(0)
                                                                                                       .getInterceptedExceptions()
                                                                                                       .size() == 1)
                                                                          .resultMatches(nodes -> "MyException1".equals(
                                                                                  nodes.get(0)
                                                                                       .getInterceptedExceptions()
                                                                                       .get(0)
                                                                                       .getIdentifier()
                                                                                       .value()))
                                                                          .resultMatches(nodes -> nodes.get(0)
                                                                                                       .getRaisedExceptionIdentifier()
                                                                                                       .value()
                                                                                                       .equals("MyIdentifier1"))
                                                                          .resultMatches(nodes -> nodes.get(0)
                                                                                                       .getBody() == mockedStatements)
                                                                          .resultMatches(nodes -> nodes.get(1)
                                                                                                       .getInterceptedExceptions()
                                                                                                       .size() == 1)
                                                                          .resultMatches(nodes -> "MyException2".equals(
                                                                                  nodes.get(1)
                                                                                       .getInterceptedExceptions()
                                                                                       .get(0)
                                                                                       .getIdentifier()
                                                                                       .value()))
                                                                          .resultMatches(nodes -> nodes.get(1)
                                                                                                       .getRaisedExceptionIdentifier()
                                                                                                       .value()
                                                                                                       .equals("MyIdentifier2"))
                                                                          .resultMatches(nodes -> nodes.get(1)
                                                                                                       .getBody() == mockedStatements);
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

        assertThat(controlStructureParser.parseInterceptStatements(queue)).isPartiallyParsed()
                                                                          .resultMatches(nodes -> nodes.size() == 2)
                                                                          .syntaxDiagnosticContains("Expected '}'");
        assertThat(queue).isAtEnd();
    }

    @Test
    void parseInterceptStatement() {
        StatementsNode mockedStatements = mock(StatementsNode.class);
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

        assertThat(controlStructureParser.parseInterceptStatement(queue)).isSuccessful()
                                                                         .resultMatches(node -> node.getInterceptedExceptions()
                                                                                                    .size() == 1)
                                                                         .resultMatches(node -> "MyException".equals(
                                                                                 node.getInterceptedExceptions()
                                                                                     .get(0)
                                                                                     .getIdentifier()
                                                                                     .value()))
                                                                         .resultMatches(node -> "MyIdentifier".equals(
                                                                                 node.getRaisedExceptionIdentifier()
                                                                                     .value()))
                                                                         .resultMatches(node -> node.getBody() == mockedStatements);
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("intercept")
                                           .identifier("MyException1")
                                           .separator(",")
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
        assertThat(controlStructureParser.parseInterceptStatement(queue)).isSuccessful()
                                                                         .resultMatches(node -> node.getInterceptedExceptions()
                                                                                                    .size() == 2)
                                                                         .resultMatches(node -> "MyException1".equals(
                                                                                 node.getInterceptedExceptions()
                                                                                     .get(0)
                                                                                     .getIdentifier()
                                                                                     .value()))
                                                                         .resultMatches(node -> node.getInterceptedExceptions()
                                                                                                    .get(0)
                                                                                                    .getArrayIndices()
                                                                                                    .isEmpty())
                                                                         .resultMatches(node -> "MyException2".equals(
                                                                                 node.getInterceptedExceptions()
                                                                                     .get(1)
                                                                                     .getIdentifier()
                                                                                     .value()))
                                                                         .resultMatches(node -> node.getInterceptedExceptions()
                                                                                                    .get(1)
                                                                                                    .getArrayIndices()
                                                                                                    .size() == 1)
                                                                         .resultMatches(node -> "MyIdentifier".equals(
                                                                                 node.getRaisedExceptionIdentifier()
                                                                                     .value()))
                                                                         .resultMatches(node -> node.getBody() == mockedStatements);
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("intercept")
                                           .identifier("myException")
                                           .separator("{")
                                           .operator("statements")
                                           .separator("}")
                                           .separator(";")
                                           .build();

        assertThat(controlStructureParser.parseInterceptStatement(queue)).isPartiallyParsed()
                                                                         .resultMatches(node -> node.getInterceptedExceptions()
                                                                                                    .size() == 1)
                                                                         .resultMatches(node -> node.getInterceptedExceptions()
                                                                                                    .get(0)
                                                                                                    .getIdentifier()
                                                                                                    .value()
                                                                                                    .equals("myException")).
                                                                         resultMatches(node -> node.getInterceptedExceptions()
                                                                                                   .size() == 1)
                                                                         .resultMatches(node -> node.getRaisedExceptionIdentifier() == null)
                                                                         .resultMatches(node -> node.getBody() == mockedStatements)
                                                                         .syntaxDiagnosticContains("Expected ':'");
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().separator(";")
                                           .build();
        assertThat(controlStructureParser.parseInterceptStatement(queue)).isUnsuccessful()
                                                                         .syntaxDiagnosticContains(
                                                                                 "Expected 'intercept'");
    }

    @Test
    void parseEnsureStatement() {
        StatementsNode mockedStatements = mock(StatementsNode.class);
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

        assertThat(controlStructureParser.parseEnsureStatement(queue)).isSuccessful()
                                                                      .resultMatches(node -> node.getBody() == mockedStatements);
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("ensure")
                                           .operator("statements")
                                           .separator("}")
                                           .separator(";")
                                           .build();
        assertThat(controlStructureParser.parseEnsureStatement(queue)).isPartiallyParsed()
                                                                      .resultMatches(node -> node.getBody() == null)
                                                                      .syntaxDiagnosticContains("Expected '{'");
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("ensure")
                                           .separator("{")
                                           .operator("statements")
                                           .separator(";")
                                           .build();
        assertThat(controlStructureParser.parseEnsureStatement(queue)).isPartiallyParsed()
                                                                      .resultMatches(node -> node.getBody() == mockedStatements)
                                                                      .syntaxDiagnosticContains("Expected '}'");
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().separator(";").build();
        assertThat(controlStructureParser.parseEnsureStatement(queue)).isUnsuccessful()
                                                                      .syntaxDiagnosticContains("Expected 'ensure'");
    }
}