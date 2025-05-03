package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @since 29.07.2024
 */
public final class ParenthesizedExpressionNodeImpl extends AbstractNode implements ParenthesizedExpressionNode {

    private ExpressionNode innerExpression;

    public ParenthesizedExpressionNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public ExpressionNode getInnerExpressionNode() {
        return innerExpression;
    }

    public void setInnerExpression(ExpressionNode innerExpression) {
        this.innerExpression = innerExpression;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.PAREN_EXPRESSION;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ParenthesizedExpressionNodeImpl that)) return false;
        return Objects.equals(getInnerExpressionNode(), that.getInnerExpressionNode())
                       && getNodeType().equals(that.getNodeType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getInnerExpressionNode(), getNodeType());
    }

    @Override
    public @NotNull String getDisplayDescriptor() {
        return "(%s)".formatted(getInnerExpressionNode().getDisplayDescriptor());
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitParenthesizedExpression(this, data);
    }
}
