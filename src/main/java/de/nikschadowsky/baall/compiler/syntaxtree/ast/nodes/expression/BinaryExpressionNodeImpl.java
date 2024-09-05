package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.TermNode;
import de.nikschadowsky.baall.compiler.syntaxtree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

/**
 * @since 29.07.2024
 */
public class BinaryExpressionNodeImpl extends AbstractNode implements BinaryExpressionNode {

    private TermNode leftOperand;

    private Token operator;

    private ExpressionNode rightOperand;

    public BinaryExpressionNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    public void setLeftOperand(TermNode leftOperand) {
        this.leftOperand = leftOperand;
    }

    @Override
    public TermNode getLeftOperand() {
        return leftOperand;
    }

    public void setOperator(Token operator) {
        this.operator = operator;
    }

    @Override
    public Token getOperator() {
        return operator;
    }

    public void setRightOperand(ExpressionNode rightOperand) {
        this.rightOperand = rightOperand;
    }

    @Override
    public ExpressionNode getRightOperand() {
        return rightOperand;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.BINARY_EXPRESSION;
    }
}
