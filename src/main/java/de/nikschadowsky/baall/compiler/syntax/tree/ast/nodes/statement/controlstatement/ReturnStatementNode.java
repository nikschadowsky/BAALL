package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;

/**
 * @since 30.07.2024
 */
public interface ReturnStatementNode extends ControlStatementNode{

    ExpressionNode getReturnExpression();

}
