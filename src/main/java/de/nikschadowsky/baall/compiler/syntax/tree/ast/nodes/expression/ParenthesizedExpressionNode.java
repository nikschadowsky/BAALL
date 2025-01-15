package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.TermNode;

/**
 * @since 29.07.2024
 */
public interface ParenthesizedExpressionNode extends TermNode {

    ExpressionNode getInnerExpressionNode();
}
