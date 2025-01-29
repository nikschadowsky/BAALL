package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.IdentifierAccessNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.LiteralNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

/**
 * Represents the initialization and instantiation of a struct.
 *
 * @since 29.07.2024
 */
public interface StructInitializationLiteralNode extends LiteralNode {

    /**
     * Identifier of the struct to be instantiated.
     *
     * @return struct identifier access
     */
    IdentifierAccessNode getIdentifier();

    /**
     * Arguments of the instantiation.
     *
     * @return arguments
     */
    @NotNull
    @UnmodifiableView
    List<ExpressionNode> getArguments();

}
