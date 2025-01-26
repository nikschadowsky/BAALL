package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @since 29.07.2024
 */
public class ParenthesizedExpressionNodeImpl extends AbstractNode implements ParenthesizedExpressionNode {

    private ExpressionNode innerExpression;

    public ParenthesizedExpressionNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    public void setInnerExpression(ExpressionNode innerExpression) {
        this.innerExpression = innerExpression;
    }

    @Override
    public ExpressionNode getInnerExpressionNode() {
        return innerExpression;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.PAREN_EXPRESSION;
    }

    @Override
    public <D, R> Optional<R> accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitParenthesizedExpression(this, data);
    }
}
