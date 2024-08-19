package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression;

import de.nikschadowsky.baall.compiler.symbol.Token;

/**
 * @since 29.07.2024
 */
public interface PrefixOperationNode extends ExpressionNode {

    Token getOperator();

    ExpressionNode getOperand();
}
