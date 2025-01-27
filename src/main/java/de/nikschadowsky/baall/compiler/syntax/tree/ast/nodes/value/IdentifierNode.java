package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value;


import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.Node;

/**
 * @since 27.01.2025
 */
public interface IdentifierNode extends Node {

    Token getIdentifier();

}
