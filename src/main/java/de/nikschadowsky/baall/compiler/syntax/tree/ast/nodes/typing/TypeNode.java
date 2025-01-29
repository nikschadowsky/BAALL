package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.Node;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.IdentifierNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

/**
 * Represents a static type.
 *
 * @since 29.07.2024
 */
public interface TypeNode extends Node {

    /**
     * Identifier of the type. Can be a BAALL base type.
     *
     * @return type identifier
     */
    IdentifierNode getType();

    /**
     * Array dimensions of the type.
     *
     * @return array dimensions
     */
    @NotNull
    @UnmodifiableView
    List<ExpressionNode> getArrayDimensionDefinitions();

    /**
     * Is the type none-safe? BAALL base types are always none-safe.
     *
     * @return if the type is none-safe
     */
    boolean isNoneSafe();
}
