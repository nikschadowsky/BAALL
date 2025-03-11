package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing;


import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;

import java.util.Optional;

/**
 * @since 09.03.2025
 */
public interface NestedTypeNode extends CompositionTypeNode {

    Optional<ExpressionNode> getArrayDimension();
}
