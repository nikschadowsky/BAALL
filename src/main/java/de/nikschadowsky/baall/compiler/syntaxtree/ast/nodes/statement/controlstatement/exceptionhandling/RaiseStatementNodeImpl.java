package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.controlstatement.exceptionhandling;

import de.nikschadowsky.baall.compiler.syntaxtree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.ExceptionCallNode;
import de.nikschadowsky.baall.compiler.syntaxtree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

/**
 * @since 11.08.2024
 */
public class RaiseStatementNodeImpl extends AbstractNode implements RaiseStatementNode {

    private ExceptionCallNode exception;

    public RaiseStatementNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public ExceptionCallNode getException() {
        return exception;
    }

    public void setException(ExceptionCallNode exception) {
        this.exception = exception;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.RAISE;
    }
}
