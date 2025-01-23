package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes;

import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

/**
 * @since 29.07.2024
 */
public abstract class AbstractNode implements Node, ASTVisitable {

    private final NodeDiagnosticCollector diagnostics;

    protected AbstractNode(NodeDiagnosticCollector diagnostics) {
        this.diagnostics = diagnostics;
    }

    @Override
    public @NotNull NodeDiagnosticCollector getDiagnosticCollector() {
        return diagnostics;
    }
}
