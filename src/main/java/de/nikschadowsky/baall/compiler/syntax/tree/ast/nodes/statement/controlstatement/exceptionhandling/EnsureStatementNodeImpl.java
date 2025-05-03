package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @since 11.08.2024
 */
public final class EnsureStatementNodeImpl extends AbstractNode implements EnsureStatementNode {

    private StatementsNode body;

    public EnsureStatementNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public StatementsNode getBody() {
        return body;
    }

    public void setBody(StatementsNode body) {
        this.body = body;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.ENSURE;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof EnsureStatementNodeImpl that)) return false;
        return Objects.equals(getBody(), that.getBody()) && getNodeType().equals(that.getNodeType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBody(), getNodeType());
    }

    @Override
    public @NotNull String getDisplayDescriptor() {
        return "ensure {%s}".formatted(getBody().getDisplayDescriptor());
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitEnsureStatement(this, data);
    }
}
