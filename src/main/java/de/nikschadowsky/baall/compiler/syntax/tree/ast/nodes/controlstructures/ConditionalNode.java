package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;

import java.util.Optional;

/**
 * Represents a conditional expression in code.
 *
 * @since 30.07.2024
 */
public interface ConditionalNode extends ControlStructureNode {

    /**
     * Evaluable condition for this conditional.
     *
     * @return condition expression
     */
    ExpressionNode getCondition();

    /**
     * Then block of this conditional.
     *
     * @return statements in the body
     */
    StatementsNode getThenBlock();

    /**
     * Optional else (if) branch.
     *
     * @return optional of else branch
     */
    Optional<ConditionalNode> getElseBranch();

    /**
     * Type of this conditional.
     *
     * @return branch type
     */
    ConditionBranch getConditionBranch();

    enum ConditionBranch {
        IF, ELSE
    }
}
