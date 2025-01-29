package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.StatementNode;

/**
 * Represents a control structure. A control structure is one of the following:
 * <ul>
 *     <li>Conditional</li>
 *     <li>Counting for loop</li>
 *     <li>While loop</li>
 * </ul>
 *
 * @since 30.07.2024
 */
public interface ControlStructureNode extends StatementNode {
}
