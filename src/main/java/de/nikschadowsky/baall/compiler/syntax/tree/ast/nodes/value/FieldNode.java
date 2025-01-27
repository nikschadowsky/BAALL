package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value;


import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.Node;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.TypeNode;

/**
 * @since 27.01.2025
 */
public interface FieldNode extends Node {

    TypeNode getType();

    IdentifierNode getName();

}
