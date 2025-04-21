package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.ElementAccessNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ParenthesizedExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.UnaryExpressionNode;

/**
 * Representing a term in an expression.
 *
 * @since 29.07.2024
 */
public sealed interface TermNode extends ExpressionNode permits ElementAccessNode, ParenthesizedExpressionNode, UnaryExpressionNode, FunctionCallNode, LiteralNode {
}
