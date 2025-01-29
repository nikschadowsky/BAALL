package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value;


import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.Node;

/**
 * Represents a generic identifier. This can also represent a BAALL base type!
 *
 * @since 27.01.2025
 */
public interface IdentifierNode extends Node {

    /**
     * Token representing the identifier in the source code.
     *
     * @return corresponding token
     */
    Token getIdentifier();

}
