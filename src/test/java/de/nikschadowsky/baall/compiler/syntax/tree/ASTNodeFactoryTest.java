package de.nikschadowsky.baall.compiler.syntax.tree;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.nikschadowsky.baall.compiler._utility.BaseAssertion.assertThat;

/**
 * @since 08.09.2024
 */
class ASTNodeFactoryTest {

    private ASTNodeFactory astFactory;
    private NodeDiagnosticCollector diagnostics;

    @BeforeEach
    void setUp() {
        diagnostics = new NodeDiagnosticCollector();
        astFactory = new ASTNodeFactory(diagnostics);
    }

    @Test
    void createProgramNode() {
        assertThat(astFactory.createProgramNode()).hasNodeType(NodeType.PROGRAM);
    }

    @Test
    void createImportNode() {
        assertThat(astFactory.createImportNode()).hasNodeType(NodeType.IMPORT);
    }

    @Test
    void createExportNode() {
        assertThat(astFactory.createExportNode()).hasNodeType(NodeType.EXPORT);
    }

    @Test
    void createStatementsNode() {
        assertThat(astFactory.createStatementsNode()).hasNodeType(NodeType.STATEMENTS);
    }

    @Test
    void createVariableDeclarationNode() {
        assertThat(astFactory.createVariableDeclarationNode()).hasNodeType(NodeType.VARIABLE_DECLARATION);
    }

    @Test
    void createConstantDeclarationNode() {
        assertThat(astFactory.createConstantDeclarationNode()).hasNodeType(NodeType.CONSTANT_DECLARATION);
    }

    @Test
    void createVariableReassignmentNode() {
        assertThat(astFactory.createVariableReassignmentNode()).hasNodeType(NodeType.VARIABLE_REASSIGNMENT);
    }

    @Test
    void createWhileLoopNode() {
        assertThat(astFactory.createWhileLoopNode()).hasNodeType(NodeType.WHILE);
    }

    @Test
    void createConditionalNode() {
        assertThat(astFactory.createConditionalNode()).hasNodeType(NodeType.IF);
    }

    @Test
    void createForLoopNode() {
        assertThat(astFactory.createForLoopNode()).hasNodeType(NodeType.FOR);
    }

    @Test
    void createFunctionCallNode() {
        assertThat(astFactory.createFunctionCallNode()).hasNodeType(NodeType.FUNCTION_CALL);
    }

    @Test
    void createContinueStatementNode() {
        assertThat(astFactory.createContinueStatementNode()).hasNodeType(NodeType.CONTINUE);
    }

    @Test
    void createBreakStatementNode() {
        assertThat(astFactory.createBreakStatementNode()).hasNodeType(NodeType.BREAK);
    }

    @Test
    void createReturnStatementNode() {
        assertThat(astFactory.createReturnStatementNode()).hasNodeType(NodeType.RETURN);
    }

    @Test
    void createUnaryExpressionNode() {
        assertThat(astFactory.createUnaryExpressionNode()).hasNodeType(NodeType.UNARY_EXPRESSION);
    }

    @Test
    void createBinaryExpressionNode() {
        assertThat(astFactory.createBinaryExpressionNode()).hasNodeType(NodeType.BINARY_EXPRESSION);
    }

    @Test
    void createPrefixOperationNode() {
        assertThat(astFactory.createPrefixOperationNode()).hasNodeType(NodeType.PREFIX_OPERATION);
    }

    @Test
    void createParenthesizedExpressionNode() {
        assertThat(astFactory.createParenthesizedExpressionNode()).hasNodeType(NodeType.PAREN_EXPRESSION);
    }

    @Test
    void createRaiseStatementNode() {
        assertThat(astFactory.createRaiseStatementNode()).hasNodeType(NodeType.RAISE);
    }

    @Test
    void createTryStatementNode() {
        assertThat(astFactory.createTryStatementNode()).hasNodeType(NodeType.TRY);
    }

    @Test
    void createInterceptStatementNode() {
        assertThat(astFactory.createInterceptStatementNode()).hasNodeType(NodeType.INTERCEPT);
    }

    @Test
    void createEnsureStatementNode() {
        assertThat(astFactory.createEnsureStatementNode()).hasNodeType(NodeType.ENSURE);
    }

    @Test
    void createStructDefinitionLiteralNode() {
        assertThat(astFactory.createStructDefinitionLiteralNode()).hasNodeType(NodeType.STRUCT_DEFINITION);
    }

    @Test
    void createListLiteralNode() {
        assertThat(astFactory.createListLiteralNode()).hasNodeType(NodeType.ARRAY_LITERAL);
    }

    @Test
    void createFunctionDefinitionNode() {
        assertThat(astFactory.createFunctionDefinitionNode()).hasNodeType(NodeType.FUNCTION_DEFINITION);
    }
}