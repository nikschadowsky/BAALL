package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.literal;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.LiteralNode;

/**
 * @since 29.07.2024
 */
public interface PrimitiveLiteralNode extends LiteralNode {

    Token getPrimitiveValue();

}
