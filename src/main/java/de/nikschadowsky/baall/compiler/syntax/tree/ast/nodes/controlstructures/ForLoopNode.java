package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.ReassignmentNode;

import java.util.Optional;

/**
 * @since 30.07.2024
 */
public interface ForLoopNode extends ControlStructureNode{

    Token getIdentifier();

    ExpressionNode getStartIndexExpression();

    ExpressionNode getEndIndexExpression();

    Optional<ReassignmentNode> getOptionalStepperStatement();

    boolean hasOptionalStepperStatement();

    StatementsNode getBody();
}
