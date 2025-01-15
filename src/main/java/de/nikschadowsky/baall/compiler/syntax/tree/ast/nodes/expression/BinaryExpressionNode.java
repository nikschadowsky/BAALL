package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.TermNode;

/**
 * @since 29.07.2024
 */
public interface BinaryExpressionNode extends ExpressionNode {

    TermNode getLeftOperand();

    Token getOperator();

    ExpressionNode getRightOperand();
}
