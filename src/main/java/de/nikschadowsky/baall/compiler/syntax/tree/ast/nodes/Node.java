package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

/**
 * Root interface for all AST node types @since 29.07.2024
 */
public interface Node extends ASTVisitable {

    @NotNull
    NodeType getNodeType();

    @NotNull
    NodeDiagnosticCollector getDiagnosticCollector();

}
