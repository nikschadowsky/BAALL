package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.StatementNode;

/**
 * Represents a control statement. A control statement is one of the following:
 * <ul>
 *     <li>break</li>
 *     <li>continue</li>
 *     <li>return x</li>
 * </ul>
 *
 * @since 29.07.2024
 */
public interface ControlStatementNode extends StatementNode {
}
