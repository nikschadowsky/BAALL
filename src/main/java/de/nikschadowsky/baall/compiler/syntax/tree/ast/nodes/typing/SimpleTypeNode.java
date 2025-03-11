package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing;


import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.IdentifierNode;

/**
 * @since 09.03.2025
 */
public interface SimpleTypeNode extends TypeNode{

    IdentifierNode getType();

}
