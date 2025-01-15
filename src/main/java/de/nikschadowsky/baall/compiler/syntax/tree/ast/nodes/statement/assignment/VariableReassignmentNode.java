package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;

/**
 * @since 30.07.2024
 */
public interface VariableReassignmentNode extends ReassignmentNode {

    ExpressionNode getValueExpression();
}
