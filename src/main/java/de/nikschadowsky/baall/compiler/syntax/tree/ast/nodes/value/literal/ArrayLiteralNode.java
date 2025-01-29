package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.LiteralNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

/**
 * Represents an array literal.
 *
 * @since 29.07.2024
 */
public interface ArrayLiteralNode extends LiteralNode {

    /**
     * Defined array elements.
     *
     * @return array elements
     */
    @NotNull
    @UnmodifiableView
    List<ExpressionNode> getElements();

}
