package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitable;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

/**
 * Root interface for all AST node types.
 *
 * @since 29.07.2024
 */
public interface Node extends ASTVisitable {

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
