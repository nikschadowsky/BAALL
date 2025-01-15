package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

/**
 * @since 29.07.2024
 */
public class PrefixOperationNodeImpl extends AbstractNode implements PrefixOperationNode {

    private Token operator;

    private ExpressionNode operand;

    public PrefixOperationNodeImpl(NodeDiagnosticCollector diagnostics) {
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
}
