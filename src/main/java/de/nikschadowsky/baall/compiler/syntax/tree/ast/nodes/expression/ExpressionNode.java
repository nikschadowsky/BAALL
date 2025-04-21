package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.Node;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.TermNode;

/**
 * Represents an evaluable expression.
 *
 * @since 29.07.2024
 */
public sealed interface ExpressionNode extends Node permits BinaryExpressionNode, PrefixOperationNode, TermNode {
}
