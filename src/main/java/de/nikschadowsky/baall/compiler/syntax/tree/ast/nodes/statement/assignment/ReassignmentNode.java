package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.OperatorNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.StatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.IdentifierAccessNode;

/**
 * Represents a reassigned field.
 *
 * @since 29.07.2024
 */
public interface ReassignmentNode extends StatementNode {

    /**
     * Access of the element to be updated.
     *
     * @return element to be updated
     */
    IdentifierAccessNode getIdentifierAccess();

    /**
     * Operator of the reassignment.
     *
     * @return reassignment operator
     */
    OperatorNode getOperator();

}
