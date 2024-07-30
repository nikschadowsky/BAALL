package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression;

import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.assignment.ReassignmentNode;

/**
 * File created on 29.07.2024
 */
public interface UnaryExpressionNode extends ExpressionNode, ReassignmentNode {

    boolean isPrefix();

}
