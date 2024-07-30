package de.nikschadowsky.baall.compiler.syntaxtree.ast;

import de.nikschadowsky.baall.compiler.syntaxtree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

/**
 * Root interface for all AST node types File created on 29.07.2024
 */
public interface Node {

    @NotNull
    NodeType getNodeType();

    @NotNull
    NodeDiagnosticCollector getDiagnosticCollector();

}
