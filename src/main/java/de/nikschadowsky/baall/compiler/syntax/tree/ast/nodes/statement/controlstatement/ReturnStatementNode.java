package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;

/**
 * Represents a return statement.
 *
 * @since 30.07.2024
 */
public sealed interface ReturnStatementNode extends ControlStatementNode permits ReturnStatementNodeImpl {

    /**
     * Value to be returned.
     *
     * @return evaluable expression
     */
    ExpressionNode getReturnExpression();

}
