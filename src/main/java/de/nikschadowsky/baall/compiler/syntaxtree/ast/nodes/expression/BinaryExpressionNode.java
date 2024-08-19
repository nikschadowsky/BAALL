package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.ValueNode;

/**
 * @since 29.07.2024
 */
public interface BinaryExpressionNode extends ExpressionNode {

    ValueNode getLeftOperand();

    Token getOperator();

    ExpressionNode getRightOperand();
}
