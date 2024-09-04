package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.controlstatement.exceptionhandling;

import de.nikschadowsky.baall.compiler.syntaxtree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.literal.StructInitializationLiteralNode;
import de.nikschadowsky.baall.compiler.syntaxtree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

/**
 * @since 11.08.2024
 */
public class RaiseStatementNodeImpl extends AbstractNode implements RaiseStatementNode {

    private StructInitializationLiteralNode exception;

    public RaiseStatementNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public StructInitializationLiteralNode getException() {
        return exception;
    }

    public void setException(StructInitializationLiteralNode exception) {
        this.exception = exception;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.RAISE;
    }
}
