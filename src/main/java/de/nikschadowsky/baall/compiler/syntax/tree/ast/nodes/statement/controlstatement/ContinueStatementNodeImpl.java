package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @since 30.07.2024
 */
public final class ContinueStatementNodeImpl extends AbstractNode implements ContinueStatementNode {

    public ContinueStatementNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public @NotNull StatementType getStatementType() {
        return StatementType.CONTINUE;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.CONTINUE;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ContinueStatementNodeImpl that)) return false;
        return getNodeType().equals(that.getNodeType()) && getStatementType().equals(that.getStatementType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNodeType());
    }

    @Override
    public @NotNull String getDisplayDescriptor() {
        return "continue";
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitContinueStatement(this, data);
    }
}
