package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.ReassignmentNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.IdentifierNode;

import java.util.Optional;

/**
 * Represents a counting for loop.
 *
 * @since 30.07.2024
 */
public interface ForLoopNode extends ControlStructureNode {

    /**
     * Identifier used inside counting for loop.
     *
     * @return identifier of for loop
     */
    IdentifierNode getIdentifier();

    /**
     * Start index (inclusive) of this counting for loop.
     *
     * @return counting start index
     */
    ExpressionNode getStartIndexExpression();

    /**
     * End index (exclusive) of this counting for loop.
     *
     * @return counting end index
     */
    ExpressionNode getEndIndexExpression();

    /**
     * Optional stepper for the counting for loop
     *
     * @return optional stepper
     */
    Optional<ReassignmentNode> getOptionalStepperStatement();

    /**
     * Body of the counting for loop.
     *
     * @return statements in the body
     */
    StatementsNode getBody();
}
