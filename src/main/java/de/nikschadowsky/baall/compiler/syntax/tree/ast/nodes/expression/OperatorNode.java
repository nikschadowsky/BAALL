package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression;


import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.Node;

/**
 * @since 27.01.2025
 */
public interface OperatorNode extends Node {

    Token getOperator();

}
