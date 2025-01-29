package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.TermNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @since 29.07.2024
 */
public class BinaryExpressionNodeImpl extends AbstractNode implements BinaryExpressionNode {

    private TermNode leftOperand;
    private OperatorNode operator;
    private ExpressionNode rightOperand;

    public BinaryExpressionNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public TermNode getLeftOperand() {
        return leftOperand;
    }

    public void setLeftOperand(TermNode leftOperand) {
        this.leftOperand = leftOperand;
    }

    @Override
    public OperatorNode getOperator() {
        return operator;
    }

    public void setOperator(OperatorNode operator) {
        this.operator = operator;
    }

    @Override
    public ExpressionNode getRightOperand() {
        return rightOperand;
    }

    public void setRightOperand(ExpressionNode rightOperand) {
        this.rightOperand = rightOperand;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.BINARY_EXPRESSION;
    }

    @Override
    public <D, R> Optional<R> accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitBinaryExpression(this, data);
    }
}
