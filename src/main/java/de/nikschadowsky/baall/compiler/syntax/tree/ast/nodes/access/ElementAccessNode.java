package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access;


import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.TermNode;

/**
 * @since 11.03.2025
 */
public sealed interface ElementAccessNode extends TermNode permits CompositeIdentifierNode, IdentifierNode {
}
