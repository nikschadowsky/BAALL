package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.assignment;

import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.ExpressionNode;

/**
 * File created on 30.07.2024
 */
public interface VariableReassignmentNode extends ReassignmentNode {

    ExpressionNode getValueExpression();
}
