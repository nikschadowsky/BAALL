package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression;

/**
 * Represents a prefix operation on an expression.
 *
 * @since 29.07.2024
 */
public interface PrefixOperationNode extends ExpressionNode {

    /**
     * Operator of the prefix operation.
     *
     * @return prefix operator
     */
    OperatorNode getOperator();

    /**
     * Operand of the prefix expression
     *
     * @return inner expression
     */
    ExpressionNode getOperand();
}
