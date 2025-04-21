package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access;


import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;

/**
 * @since 11.03.2025
 */
public sealed interface IndexedAccessNode extends CompositeIdentifierNode permits IndexedAccessNodeImpl {

    ExpressionNode getIndex();

}
