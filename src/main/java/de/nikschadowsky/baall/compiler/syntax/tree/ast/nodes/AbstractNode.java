package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes;

import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract superclass of every AST node.
 *
 * @since 29.07.2024
 */
public abstract non-sealed class AbstractNode implements Node {

    private final NodeDiagnosticCollector diagnostics;

    protected AbstractNode(NodeDiagnosticCollector diagnostics) {
        this.diagnostics = diagnostics;
    }

    @Override
    public @NotNull NodeDiagnosticCollector getDiagnosticCollector() {
        return diagnostics;
    }
}
