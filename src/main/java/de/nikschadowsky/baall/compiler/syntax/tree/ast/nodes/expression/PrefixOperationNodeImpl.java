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
public final class PrefixOperationNodeImpl extends AbstractNode implements PrefixOperationNode {

    private OperatorNode operator;
    private ExpressionNode operand;

    public PrefixOperationNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public OperatorNode getOperator() {
        return operator;
    }

    public void setOperator(OperatorNode operator) {
        this.operator = operator;
    }

    @Override
    public ExpressionNode getOperand() {
        return operand;
    }

    public void setOperand(ExpressionNode expressionNode) {
        this.operand = expressionNode;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.PREFIX_OPERATION;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PrefixOperationNodeImpl that)) return false;
        return Objects.equals(getOperand(), that.getOperand())
                       && Objects.equals(getOperator(), that.getOperator())
                       && getNodeType().equals(that.getNodeType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOperand(), getOperator(), getNodeType());
    }

    @Override
    public @NotNull String getDisplayDescriptor() {
        return "%s%s".formatted(getOperator().getDisplayDescriptor(), getOperand().getDisplayDescriptor());
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitPrefixOperation(this, data);
    }
}
