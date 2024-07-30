package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression;

import de.nikschadowsky.baall.compiler.symbol.Token;

/**
 * File created on 29.07.2024
 */
public interface PrefixOperationNode extends ExpressionNode {

    Token getOperator();

    ExpressionNode getOperand();
}
