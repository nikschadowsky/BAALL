package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal;


import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.LiteralNode;

/**
 * @since 28.03.2025
 */
public interface BooleanLiteralNode extends LiteralNode {

    Token getValue();

}
