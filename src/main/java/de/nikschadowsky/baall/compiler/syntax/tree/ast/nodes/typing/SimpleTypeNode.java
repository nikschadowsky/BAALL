package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing;


import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.ElementAccessNode;

/**
 * @since 09.03.2025
 */
public interface SimpleTypeNode extends TypeNode {

    ElementAccessNode getType();

}
