package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression;

/**
 * @since 29.07.2024
 */
public interface ParenthesizedExpressionNode extends ExpressionNode {

    ExpressionNode getInnerExpressionNode();
}
