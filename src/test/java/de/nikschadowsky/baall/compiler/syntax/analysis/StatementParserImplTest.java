package de.nikschadowsky.baall.compiler.syntax.analysis;

import de.nikschadowsky.baall.compiler._utility.TokenQueueTestBuilder;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures.WhileLoopNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.UnaryExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.ConstantDeclarationNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.VariableDeclarationNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.VariableReassignmentNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.ReturnStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FunctionCallNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.IdentifierAccessNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.FunctionDefinitionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.PrimitiveLiteralNode;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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
        assertThat(statementParser.parseImports(queue)).isSuccessful()
                                                       .resultMatches(imports -> imports.size() == 1)
                                                       .resultMatches(imports -> "MyImport".equals(imports.get(0)
                                                                                                          .value()));
        assertThat(queue).hasNextTokenValueMatch("end");

        queue = new TokenQueueTestBuilder().keyword("use")
                                           .string("MyImport1")
                                           .separator(";")
                                           .keyword("use")
                                           .string("MyImport2")
                                           .separator(";")
                                           .build();
        assertThat(statementParser.parseImports(queue)).isSuccessful()
                                                       .resultMatches(imports -> imports.size() == 2)
                                                       .resultMatches(imports -> "MyImport1".equals(imports.get(0)
                                                                                                           .value()))
                                                       .resultMatches(imports -> "MyImport2".equals(imports.get(1)
                                                                                                           .value()));
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().keyword("use").separator(";").operator("end").build();
        assertThat(statementParser.parseImports(queue)).isPartiallyParsed()
                                                       .resultMatches(List::isEmpty)
                                                       .syntaxDiagnosticContains("Expected a string");
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().separator("something").separator(";").build();
        assertThat(statementParser.parseImports(queue)).isSuccessful().resultMatches(List::isEmpty);
        assertThat(queue).hasNextTokenValueMatch("something");
    }

    @Test
    void parseStatements() {
        TokenQueue queue = new TokenQueueTestBuilder().build();
        assertThat(statementParser.parseStatements(queue)).isSuccessful()
                                                          .resultMatches(node -> node.getStatements().isEmpty());

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
        assertThat(statementParser.parseStatements(queue)).isSuccessful()
                                                          .resultMatches(node -> node.getStatements().size() == 4);
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
        assertThat(statementParser.parseStatements(queue)).isPartiallyParsed()
                                                          .resultMatches(node -> node.getStatements().size() == 3)
                                                          .syntaxDiagnosticContains("Not an expression");
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
        assertThat(statementParser.parseDelimitedStatement(queue)).isSuccessful().resultMatches(node -> {
            WhileLoopNode whileLoopNode = (WhileLoopNode) node;
            PrimitiveLiteralNode condition = (PrimitiveLiteralNode) whileLoopNode.getCondition();
            return PrimitiveLiteralNode.PrimitiveType.BOOLEAN.equals(condition.getPrimitiveType());
        }).resultMatches(node -> {
            WhileLoopNode whileLoopNode = (WhileLoopNode) node;
            return whileLoopNode.getBody().getStatements().size() == 1;
        }).resultMatches(node -> {
            WhileLoopNode whileLoopNode = (WhileLoopNode) node;
            return whileLoopNode.getBody().getStatements().get(0) instanceof FunctionCallNode;
        });
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().identifier("MyFunction")
                                           .separator("(")
                                           .identifier("Parameter1")
                                           .separator(",")
                                           .bool("Parameter2")
                                           .separator(")")
                                           .separator(";")
                                           .build();
        assertThat(statementParser.parseDelimitedStatement(queue)).isSuccessful()
                                                                  .resultMatches(node -> node instanceof FunctionCallNode);
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().identifier("MyType")
                                           .separator("[")
                                           .identifier("Array-Size")
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
        assertThat(statementParser.parseDelimitedStatement(queue)).isSuccessful()
                                                                  .resultMatches(node -> node instanceof ConstantDeclarationNode)
                                                                  .resultMatches(node -> ((ConstantDeclarationNode) node).getInitializationValue() instanceof FunctionDefinitionNode);
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
        assertThat(statementParser.parseSimpleStatement(queue)).isSuccessful()
                                                               .resultMatches(node -> node instanceof VariableDeclarationNode);
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("string")
                                           .separator(":")
                                           .identifier("MyIdentifier")
                                           .operator(":=")
                                           .number("expression")
                                           .separator(";")
                                           .build();
        assertThat(statementParser.parseSimpleStatement(queue)).isSuccessful()
                                                               .resultMatches(node -> node instanceof ConstantDeclarationNode);
        assertThat(queue).hasNextTokenValueMatch(";");


        queue = new TokenQueueTestBuilder().keyword("return").number("expression").separator(";").build();
        assertThat(statementParser.parseSimpleStatement(queue)).isSuccessful()
                                                               .resultMatches(node -> node instanceof ReturnStatementNode);
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().operator("--").identifier("MyIdentifier").build();
        assertThat(statementParser.parseSimpleStatement(queue)).isSuccessful()
                                                               .resultMatches(node -> node instanceof UnaryExpressionNode);

        queue = new TokenQueueTestBuilder().identifier("MyFunction")
                                           .separator("(")
                                           .identifier("Parameter")
                                           .separator(")")
                                           .separator(";")
                                           .build();
        assertThat(statementParser.parseSimpleStatement(queue)).isSuccessful()
                                                               .resultMatches(node -> node instanceof FunctionCallNode);
        assertThat(queue).hasNextTokenValueMatch(";");

        // todo partial
        queue = new TokenQueueTestBuilder().keyword("string")
                                           .separator(":")
                                           .identifier("MyIdentifier")
                                           .operator("=")
                                           .operator("not an expression")
                                           .separator(";")
                                           .build();
        assertThat(statementParser.parseSimpleStatement(queue)).isPartiallyParsed()
                                                               .resultMatches(node -> node instanceof VariableDeclarationNode)
                                                               .syntaxDiagnosticContains("Expected an expression");
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                           .operator("something")
                                           .operator("something")
                                           .operator("something")
                                           .operator("something")
                                           .separator(";")
                                           .build();
        assertThat(statementParser.parseSimpleStatement(queue)).isPartiallyParsed()
                                                               .syntaxDiagnosticContains("Expected ':'");
        assertThat(queue).hasNextTokenValueMatch(";");


        queue = new TokenQueueTestBuilder().separator(";").build();
        assertThat(statementParser.parseSimpleStatement(queue)).isUnsuccessful()
                                                               .syntaxDiagnosticContains("Not a statement");
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
        assertThat(statementParser.parseDeclaration(queue)).isSuccessful()
                                                           .resultMatches(node -> node instanceof VariableDeclarationNode);
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("string")
                                           .separator(":")
                                           .identifier("MyIdentifier")
                                           .separator(";")
                                           .build();
        assertThat(statementParser.parseDeclaration(queue)).isSuccessful()
                                                           .resultMatches(node -> node instanceof VariableDeclarationNode);
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("string")
                                           .separator(":")
                                           .identifier("MyIdentifier")
                                           .operator(":=")
                                           .number("expression")
                                           .separator(";")
                                           .build();
        assertThat(statementParser.parseDeclaration(queue)).isSuccessful()
                                                           .resultMatches(node -> node instanceof ConstantDeclarationNode);
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("string")
                                           .separator(":")
                                           .identifier("MyIdentifier")
                                           .operator("=")
                                           .operator("not an expression")
                                           .separator(";")
                                           .build();
        assertThat(statementParser.parseDeclaration(queue)).isPartiallyParsed()
                                                           .resultMatches(node -> node instanceof VariableDeclarationNode)
                                                           .syntaxDiagnosticContains("Expected an expression");
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("string")
                                           .separator(";")
                                           .identifier("MyIdentifier")
                                           .separator(";")
                                           .build();
        assertThat(statementParser.parseDeclaration(queue)).isPartiallyParsed()
                                                           .resultMatches(node -> node instanceof VariableDeclarationNode)
                                                           .syntaxDiagnosticContains("Expected ':'");
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().keyword("string")
                                           .separator(":")
                                           .identifier("MyIdentifier")
                                           .operator("=")
                                           .operator("not an expression")
                                           .separator(";")
                                           .build();
        assertThat(statementParser.parseDeclaration(queue)).isPartiallyParsed()
                                                           .resultMatches(node -> node instanceof VariableDeclarationNode)
                                                           .syntaxDiagnosticContains("Expected an expression");
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().separator(";").build();
        assertThat(statementParser.parseDeclaration(queue)).isUnsuccessful()
                                                           .syntaxDiagnosticContains("Not a statement");
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
        assertThat(statementParser.parseVariableDeclaration(queue)).isSuccessful()
                                                                   .resultMatches(node -> "string".equals(node.getType()
                                                                                                              .getType()
                                                                                                              .value()))
                                                                   .resultMatches(node -> node.getType()
                                                                                              .getArrayDimensionDefinitions()
                                                                                              .isEmpty())
                                                                   .resultMatches(node -> "MyIdentifier".equals(node.getIdentifier()
                                                                                                                    .value()))
                                                                   .resultMatches(node -> PrimitiveLiteralNode.PrimitiveType.NUMBER.equals(
                                                                           ((PrimitiveLiteralNode) node.getInitializationValue()
                                                                                                       .orElseThrow()).getPrimitiveType()));
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().identifier("MyType")
                                           .separator(":")
                                           .identifier("MyIdentifier")
                                           .separator(";")
                                           .build();
        assertThat(statementParser.parseVariableDeclaration(queue)).isSuccessful()
                                                                   .resultMatches(node -> node.getType()
                                                                                              .getType()
                                                                                              .value()
                                                                                              .equals("MyType"))
                                                                   .resultMatches(node -> node.getIdentifier()
                                                                                              .value()
                                                                                              .equals("MyIdentifier"))
                                                                   .resultMatches(node -> node.getInitializationValue()
                                                                                              .isEmpty());


        queue = new TokenQueueTestBuilder().identifier("MyType")
                                           .separator("[")
                                           .separator("]")
                                           .separator(":")
                                           .identifier("MyIdentifier")
                                           .operator("=")
                                           .operator("not an expression")
                                           .separator(";")
                                           .build();
        assertThat(statementParser.parseVariableDeclaration(queue)).isPartiallyParsed()
                                                                   .resultMatches(node -> "MyType".equals(node.getType()
                                                                                                              .getType()
                                                                                                              .value()))
                                                                   .resultMatches(node -> node.getType()
                                                                                              .getArrayDimensionDefinitions()
                                                                                              .size() == 1)
                                                                   .resultMatches(node -> "MyIdentifier".equals(node.getIdentifier()
                                                                                                                    .value()))
                                                                   .resultMatches(node -> node.getInitializationValue()
                                                                                              .isEmpty())
                                                                   .syntaxDiagnosticContains("Expected an expression");
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().operator("something else").separator(";").build();
        assertThat(statementParser.parseVariableDeclaration(queue)).isUnsuccessful()
                                                                   .syntaxDiagnosticContains("Not a statement");
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
        assertThat(statementParser.parseConstantDeclaration(queue)).isSuccessful()
                                                                   .resultMatches(node -> "string".equals(node.getType()
                                                                                                              .getType()
                                                                                                              .value()))
                                                                   .resultMatches(node -> node.getType()
                                                                                              .getArrayDimensionDefinitions()
                                                                                              .isEmpty())
                                                                   .resultMatches(node -> "MyIdentifier".equals(node.getIdentifier()
                                                                                                                    .value()))
                                                                   .resultMatches(node -> PrimitiveLiteralNode.PrimitiveType.NUMBER.equals(
                                                                           ((PrimitiveLiteralNode) node.getInitializationValue()).getPrimitiveType()));
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
        assertThat(statementParser.parseConstantDeclaration(queue)).isPartiallyParsed()
                                                                   .resultMatches(node -> "MyType".equals(node.getType()
                                                                                                              .getType()
                                                                                                              .value()))
                                                                   .resultMatches(node -> node.getType()
                                                                                              .getArrayDimensionDefinitions()
                                                                                              .size() == 1)
                                                                   .resultMatches(node -> "MyIdentifier".equals(node.getIdentifier()
                                                                                                                    .value()))
                                                                   .resultMatches(node -> node.getInitializationValue() == null)
                                                                   .syntaxDiagnosticContains("Expected an expression");
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
        assertThat(statementParser.parseConstantDeclaration(queue)).isPartiallyParsed()
                                                                   .resultMatches(node -> "MyType".equals(node.getType()
                                                                                                              .getType()
                                                                                                              .value()))
                                                                   .resultMatches(node -> node.getType()
                                                                                              .getArrayDimensionDefinitions()
                                                                                              .size() == 1)
                                                                   .resultMatches(node -> "MyIdentifier".equals(node.getIdentifier()
                                                                                                                    .value()))
                                                                   .resultMatches(node -> PrimitiveLiteralNode.PrimitiveType.NUMBER.equals(
                                                                           ((PrimitiveLiteralNode) node.getInitializationValue()).getPrimitiveType()))
                                                                   .syntaxDiagnosticContains("Expected ':='");
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().operator("something else").separator(";").build();
        assertThat(statementParser.parseConstantDeclaration(queue)).isUnsuccessful()
                                                                   .syntaxDiagnosticContains("Not a statement");
    }

    @Test
    void parseReassignment() {
        TokenQueue queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                                      .operator("+=")
                                                      .number("expression")
                                                      .separator(";")
                                                      .build();
        assertThat(statementParser.parseReassignment(queue)).isSuccessful()
                                                            .resultMatches(node -> node instanceof VariableReassignmentNode);
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier").operator("++").separator(";").build();
        assertThat(statementParser.parseReassignment(queue)).isSuccessful()
                                                            .resultMatches(node -> node.getIdentifierAccess()
                                                                                       .getIdentifier()
                                                                                       .value()
                                                                                       .equals("MyIdentifier"))
                                                            .resultMatches(node -> node.getIdentifierAccess()
                                                                                       .getArrayIndices()
                                                                                       .isEmpty())
                                                            .resultMatches(node -> node.getOperator()
                                                                                       .value()
                                                                                       .equals("++"))
                                                            .resultMatches(node -> !((UnaryExpressionNode) node).isPrefix());
        assertThat(queue).hasNextTokenValueMatch(";");
        queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                           .operator("something")
                                           .operator("something")
                                           .operator("something")
                                           .separator(";")
                                           .build();
        assertThat(statementParser.parseReassignment(queue)).isPartiallyParsed()
                                                            .resultMatches(node -> node instanceof VariableReassignmentNode)
                                                            .syntaxDiagnosticContains("Expected an assignment operator");
        assertThat(queue).hasNextTokenValueMatch(";");

    }

    @Test
    void parseVariableReassignment() {
        TokenQueue queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                                      .operator("+=")
                                                      .number("expression")
                                                      .separator(";")
                                                      .build();
        assertThat(statementParser.parseVariableReassignment(queue)).isSuccessful()
                                                                    .resultMatches(node -> {
                                                                        PrimitiveLiteralNode value =
                                                                                (PrimitiveLiteralNode) node.getValueExpression();
                                                                        return "expression".equals(value.getPrimitiveValue()
                                                                                                        .value());
                                                                    })
                                                                    .resultMatches(node -> "MyIdentifier".equals(node.getIdentifierAccess()
                                                                                                                     .getIdentifier()
                                                                                                                     .value()))
                                                                    .resultMatches(node -> node.getOperator()
                                                                                               .value()
                                                                                               .equals("+="));
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                           .separator("[")
                                           .number("index")
                                           .separator("]")
                                           .operator("=")
                                           .identifier("expression")
                                           .separator(";")
                                           .build();
        assertThat(statementParser.parseVariableReassignment(queue)).isSuccessful()
                                                                    .resultMatches(node -> {
                                                                        IdentifierAccessNode value =
                                                                                (IdentifierAccessNode) node.getValueExpression();
                                                                        return "expression".equals(value.getIdentifier()
                                                                                                        .value());
                                                                    })
                                                                    .resultMatches(node -> node.getIdentifierAccess()
                                                                                               .getIdentifier()
                                                                                               .value()
                                                                                               .equals("MyIdentifier"))
                                                                    .resultMatches(node -> node.getIdentifierAccess()
                                                                                               .getArrayIndices()
                                                                                               .size() == 1)
                                                                    .resultMatches(node -> {
                                                                        PrimitiveLiteralNode index =
                                                                                (PrimitiveLiteralNode) node.getIdentifierAccess()
                                                                                                           .getArrayIndices()
                                                                                                           .get(0);
                                                                        return "index".equals(index.getPrimitiveValue()
                                                                                                   .value());
                                                                    })
                                                                    .resultMatches(node -> "=".equals(node.getOperator()
                                                                                                          .value()));
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                           .operator(":=")
                                           .number("expression")
                                           .separator(";")
                                           .build();
        assertThat(statementParser.parseVariableReassignment(queue)).isPartiallyParsed()
                                                                    .resultMatches(node -> node.getValueExpression() instanceof PrimitiveLiteralNode)
                                                                    .resultMatches(node -> "MyIdentifier".equals(node.getIdentifierAccess()
                                                                                                                     .getIdentifier()
                                                                                                                     .value()))
                                                                    .syntaxDiagnosticContains(
                                                                            "Expected an assignment operator");
        assertThat(queue).hasNextTokenValueMatch(";");

        queue = new TokenQueueTestBuilder().identifier("MyIdentifier")
                                           .operator("=")
                                           .operator("not an expression")
                                           .separator(";")
                                           .build();
        assertThat(statementParser.parseVariableReassignment(queue)).isPartiallyParsed()
                                                                    .resultMatches(node -> node.getValueExpression() == null)
                                                                    .resultMatches(node -> "=".equals(node.getOperator()
                                                                                                          .value()))
                                                                    .resultMatches(node -> "MyIdentifier".equals(node.getIdentifierAccess()
                                                                                                                     .getIdentifier()
                                                                                                                     .value()))
                                                                    .syntaxDiagnosticContains("Not an expression");
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
        assertThat(statementParser.parseExports(queue)).isSuccessful()
                                                       .resultMatches(node -> node.getExportedElements().size() == 1)
                                                       .resultMatches(node -> node.getExportedElements()
                                                                                  .get(0)
                                                                                  .getArrayIndices()
                                                                                  .size() == 1)
                                                       .resultMatches(node -> "MyIdentifier".equals(node.getExportedElements()
                                                                                                        .get(0)
                                                                                                        .getIdentifier()
                                                                                                        .value()))
                                                       .resultMatches(node -> "MyNamespace".equals(node.getNamespace()
                                                                                                       .orElseThrow()
                                                                                                       .value()));;
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
        assertThat(statementParser.parseExports(queue)).isSuccessful()
                                                       .resultMatches(node -> node.getExportedElements().size() == 2)
                                                       .resultMatches(node -> "MyIdentifier1".equals(node.getExportedElements()
                                                                                                         .get(0)
                                                                                                         .getIdentifier()
                                                                                                         .value()))
                                                       .resultMatches(node -> "MyIdentifier2".equals(node.getExportedElements()
                                                                                                         .get(1)
                                                                                                         .getIdentifier()
                                                                                                         .value()))
                                                       .resultMatches(node -> "MyNamespace".equals(node.getNamespace()
                                                                                                       .orElseThrow()
                                                                                                       .value()));
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().keyword("export").separator("{").separator("}").separator(";").build();
        assertThat(statementParser.parseExports(queue)).isSuccessful()
                                                       .resultMatches(node -> node.getExportedElements().isEmpty());
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
        assertThat(statementParser.parseExports(queue)).isPartiallyParsed()
                                                       .resultMatches(node -> node.getExportedElements().size() == 1)
                                                       .resultMatches(node -> "MyIdentifier".equals(node.getExportedElements()
                                                                                                        .get(0)
                                                                                                        .getIdentifier()
                                                                                                        .value()))
                                                       .syntaxDiagnosticContains("Expected '}'");
        assertThat(queue).hasNextTokenValueMatch("end");

        queue = new TokenQueueTestBuilder().keyword("export")
                                           .separator("{")
                                           .identifier("MyIdentifier")
                                           .separator(",")
                                           .keyword("as")
                                           .identifier("MyNamespace")
                                           .build();
        assertThat(statementParser.parseExports(queue)).isPartiallyParsed()
                                                       .resultMatches(node -> node.getExportedElements().size() == 1)
                                                       .resultMatches(node -> "MyIdentifier".equals(node.getExportedElements()
                                                                                                        .get(0)
                                                                                                        .getIdentifier()
                                                                                                        .value()))
                                                       .syntaxDiagnosticContains("Expected an identifier");
        assertThat(queue).isAtEnd();

        queue = new TokenQueueTestBuilder().separator(";").build();
        assertThat(statementParser.parseExports(queue)).isSuccessful()
                                                       .resultMatches(node -> node.getExportedElements().isEmpty());
        assertThat(queue).hasNextTokenValueMatch(";");
    }
}