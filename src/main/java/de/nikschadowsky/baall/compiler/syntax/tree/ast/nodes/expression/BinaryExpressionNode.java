package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.TermNode;

/**
 * Representing a binary expression.
 *
 * @since 29.07.2024
 */
public interface BinaryExpressionNode extends ExpressionNode {

    /**
     * Left operand of the expression.
     *
     * @return left operand
     */
    TermNode getLeftOperand();

    /**
     * Operator of the expression.
     *
     * @return binary operator
     */
    OperatorNode getOperator();

    /**
     * Right operand of the expression.
     *
     * @return right operand
     */
    ExpressionNode getRightOperand();
}
