package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes;

import de.nikschadowsky.baall.compiler.syntaxtree.ast.Node;
import de.nikschadowsky.baall.compiler.syntaxtree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

/**
 * File created on 29.07.2024
 */
public abstract class AbstractNode implements Node {

    private final NodeDiagnosticCollector diagnostics;

    protected AbstractNode(NodeDiagnosticCollector diagnostics) {
        this.diagnostics = diagnostics;
    }

    @Override
    public @NotNull NodeDiagnosticCollector getDiagnosticCollector() {
        return diagnostics;
    }
}
