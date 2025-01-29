package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

/**
 * Represents the access of a field or element.
 *
 * @since 29.07.2024
 */
public interface IdentifierAccessNode extends TermNode {

    /**
     * Identifier of the field or element to be accessed.
     *
     * @return identifier
     */
    IdentifierNode getIdentifier();

    /**
     * Optional array indices of the field or element to be accessed.
     *
     * @return array indices
     */
    @NotNull
    @UnmodifiableView
    List<ExpressionNode> getArrayIndices();
}
