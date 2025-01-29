package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.TermNode;

/**
 * Represents an expression surrounded by parentheses.
 *
 * @since 29.07.2024
 */
public interface ParenthesizedExpressionNode extends TermNode {

    /**
     * Inner evaluable expression surrounded by parentheses.
     *
     * @return inner expression
     */
    ExpressionNode getInnerExpressionNode();
}
