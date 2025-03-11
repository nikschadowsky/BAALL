package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.Node;

/**
 * Represents a static type.
 *
 * @since 29.07.2024
 */
public interface TypeNode extends Node {

    /**
     * Is the type none-safe? BAALL base types are always none-safe.
     *
     * @return if the type is none-safe
     */
    boolean isNoneSafe();

}
