package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;

/**
 * Represents a while loop.
 *
 * @since 30.07.2024
 */
public sealed interface WhileLoopNode extends ControlStructureNode permits WhileLoopNodeImpl {

    /**
     * Evaluable expression for this while loop.
     *
     * @return condition expression
     */
    ExpressionNode getCondition();

    /**
     * Body of the while loop.
     *
     * @return statements in the body
     */
    StatementsNode getBody();

}
