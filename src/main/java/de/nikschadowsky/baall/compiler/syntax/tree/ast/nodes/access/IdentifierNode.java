package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access;


import de.nikschadowsky.baall.compiler.symbol.Token;

/**
 * Represents a generic identifier. This can also represent a BAALL base type!
 *
 * @since 27.01.2025
 */
public sealed interface IdentifierNode extends ElementAccessNode permits IdentifierNodeImpl{

    /**
     * Token representing the identifier in the source code.
     *
     * @return corresponding token
     */
    Token getIdentifier();

}
