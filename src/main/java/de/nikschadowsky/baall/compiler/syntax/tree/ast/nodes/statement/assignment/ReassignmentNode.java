package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.ElementAccessNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.OperatorNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.UnaryExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.StatementNode;

/**
 * Represents a reassigned field.
 *
 * @since 29.07.2024
 */
public sealed interface ReassignmentNode extends StatementNode permits UnaryExpressionNode, VariableReassignmentNode {

    /**
     * Access of the element to be updated.
     *
     * @return element to be updated
     */
    ElementAccessNode getElementAccess();

    /**
     * Operator of the reassignment.
     *
     * @return reassignment operator
     */
    OperatorNode getOperator();

}
