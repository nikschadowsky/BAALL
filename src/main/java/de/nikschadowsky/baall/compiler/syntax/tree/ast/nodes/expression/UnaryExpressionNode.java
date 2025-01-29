package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.ReassignmentNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.TermNode;

/**
 * Represents a unary operation. This operation may be a prefix or postfix operation.
 *
 * @since 29.07.2024
 */
public interface UnaryExpressionNode extends TermNode, ReassignmentNode {

    /**
     * Is the operator pre- or postfix?
     *
     * @return if operator is a prefix operator
     */
    boolean isPrefix();

    /**
     * Operator if this unary operation.
     *
     * @return unary operator
     */
    @Override
    OperatorNode getOperator();
}
