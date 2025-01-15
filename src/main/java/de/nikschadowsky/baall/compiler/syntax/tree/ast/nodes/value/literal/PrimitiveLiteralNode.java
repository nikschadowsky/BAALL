package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.LiteralNode;

/**
 * @since 29.07.2024
 */
public interface PrimitiveLiteralNode extends LiteralNode {

    Token getPrimitiveValue();

    PrimitiveType getPrimitiveType();

    enum PrimitiveType {
        STRING, NUMBER, BOOLEAN, CHAR
    }
}
