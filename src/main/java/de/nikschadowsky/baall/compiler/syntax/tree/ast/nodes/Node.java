package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.OperatorNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.ExportsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.ImportsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.ProgramNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.StatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.EnsureStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.InterceptStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.TypeNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FieldNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitable;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

/**
 * Root interface for all AST node types.
 *
 * @since 29.07.2024
 */
public sealed interface Node extends ASTVisitable
        permits AbstractNode, ExpressionNode, OperatorNode, ExportsNode, ImportsNode, ProgramNode, StatementsNode,
        StatementNode, EnsureStatementNode, InterceptStatementNode, TypeNode, FieldNode {

    /**
     * Type of the node. Can be used for type checking without instanceof.
     *
     * @return node type
     */
    @NotNull
    NodeType getNodeType();

    /**
     * Diagnostic collector of the node.
     *
     * @return diagnostic collector
     * @deprecated Still needs to be evaluated if necessary.
     */
    @Deprecated
    @NotNull
    NodeDiagnosticCollector getDiagnosticCollector();

}
