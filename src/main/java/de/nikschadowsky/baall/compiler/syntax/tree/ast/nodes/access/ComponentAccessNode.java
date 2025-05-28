package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access;


/**
 * @since 11.03.2025
 */
public sealed interface ComponentAccessNode extends CompositeIdentifierNode permits ComponentAccessNodeImpl {

    IdentifierNode getSelected();

}
