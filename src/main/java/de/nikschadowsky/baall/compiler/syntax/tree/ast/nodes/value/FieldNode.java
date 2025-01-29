package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value;


import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.Node;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.TypeNode;

/**
 * Represents a declared field consisting of a type and a name.
 *
 * @since 27.01.2025
 */
public interface FieldNode extends Node {

    /**
     * Type of the field.
     *
     * @return type
     */
    TypeNode getType();

    /**
     * Name of the field.
     *
     * @return identifier
     */
    IdentifierNode getIdentifier();

}
