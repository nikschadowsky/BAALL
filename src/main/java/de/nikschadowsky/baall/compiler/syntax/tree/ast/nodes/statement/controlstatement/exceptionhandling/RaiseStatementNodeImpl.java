package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FunctionCallNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @since 11.08.2024
 */
public final class RaiseStatementNodeImpl extends AbstractNode implements RaiseStatementNode {

    private FunctionCallNode exception;

    public RaiseStatementNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public FunctionCallNode getException() {
        return exception;
    }

    public void setException(FunctionCallNode exception) {
        this.exception = exception;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.RAISE;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof RaiseStatementNodeImpl that)) return false;
        return Objects.equals(getException(), that.getException()) && getNodeType().equals(that.getNodeType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getException(), getNodeType());
    }

    @Override
    public @NotNull String getDisplayDescriptor() {
        return "raise %s".formatted(getException().getDisplayDescriptor());
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitRaiseStatement(this, data);
    }
}
