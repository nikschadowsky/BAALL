package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.controlstatement;

import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.ExpressionNode;

/**
 * File created on 30.07.2024
 */
public interface ReturnStatementNode extends ControlStatementNode{

    ExpressionNode getReturnExpression();

}
