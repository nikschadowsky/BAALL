package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.VariableDeclarationNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @since 11.08.2024
 */
public final class InterceptStatementNodeImpl extends AbstractNode implements InterceptStatementNode {

    private VariableDeclarationNode caughtException;
    private StatementsNode body;

    public InterceptStatementNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public VariableDeclarationNode getCaughtException() {
        return caughtException;
    }

    public void setCaughtException(VariableDeclarationNode raisedExceptionIdentifier) {
        this.caughtException = raisedExceptionIdentifier;
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
        return NodeType.INTERCEPT;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof InterceptStatementNodeImpl that)) return false;
        return Objects.equals(getCaughtException(), that.getCaughtException())
                       && Objects.equals(getBody(), that.getBody())
                       && getNodeType().equals(that.getNodeType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCaughtException(), getBody(), getNodeType());
    }

    @Override
    public @NotNull String getDisplayDescriptor() {
        return "intercept %s: %s {%s}".formatted(
                getCaughtException().getType().getDisplayDescriptor(),
                getCaughtException().getIdentifier().getDisplayDescriptor(),
                getBody().getDisplayDescriptor()
        );
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitInterceptStatement(this, data);
    }
}
