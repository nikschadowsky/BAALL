package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing;


import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.ElementAccessNode;

/**
 * @since 08.03.2025
 */
public sealed interface IdentifierTypeNode extends SimpleTypeNode permits IdentifierTypeNodeImpl {

    ElementAccessNode getType();
}
