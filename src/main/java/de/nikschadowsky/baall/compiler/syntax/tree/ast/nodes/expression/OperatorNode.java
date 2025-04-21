package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression;


import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.Node;

/**
 * Represents an operator.
 *
 * @since 27.01.2025
 */
public sealed interface OperatorNode extends Node permits OperatorNodeImpl {

    /**
     * Token representing the operator in the source code.
     *
     * @return corresponding token
     */
    Token getOperator();

}
