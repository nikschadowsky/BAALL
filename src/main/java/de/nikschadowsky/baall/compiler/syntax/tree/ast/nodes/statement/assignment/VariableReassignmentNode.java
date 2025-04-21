package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;

/**
 * Represents the reassignment of a variable.
 *
 * @since 30.07.2024
 */
public sealed interface VariableReassignmentNode extends ReassignmentNode permits VariableReassignmentNodeImpl {

    /**
     * Updated value of the variable.
     *
     * @return evaluable expression
     */
    ExpressionNode getValueExpression();
}
