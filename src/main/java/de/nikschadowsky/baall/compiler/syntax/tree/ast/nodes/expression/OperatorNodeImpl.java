package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression;


import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @since 27.01.2025
 */
public final class OperatorNodeImpl extends AbstractNode implements OperatorNode {

    private Token operator;

    public OperatorNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public Token getOperator() {
        return operator;
    }

    public void setOperator(Token operator) {
        this.operator = operator;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.OPERATOR;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof OperatorNodeImpl that)) return false;
        return Objects.equals(getOperator(), that.getOperator()) && getNodeType().equals(that.getNodeType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOperator(), getNodeType());
    }

    @Override
    public @NotNull String getDisplayDescriptor() {
        return operator.value();
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitOperator(this, data);
    }
}
