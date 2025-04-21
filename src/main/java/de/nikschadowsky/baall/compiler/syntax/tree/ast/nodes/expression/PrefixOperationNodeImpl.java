package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

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
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitPrefixOperation(this, data);
    }
}
