package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.LiteralNode;

/**
 * Represents a BAALL primitive.
 *
 * @since 29.07.2024
 */
public interface PrimitiveLiteralNode extends LiteralNode {

    /**
     * Token representing the value in the source code.
     *
     * @return corresponding token.
     */
    Token getPrimitiveValue();

    /**
     * Type of the primitive.
     *
     * @return primitive type
     */
    PrimitiveType getPrimitiveType();

    enum PrimitiveType {
        STRING, NUMBER, BOOLEAN, CHAR
    }
}
