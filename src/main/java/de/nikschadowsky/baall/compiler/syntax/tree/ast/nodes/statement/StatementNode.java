package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.Node;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures.ControlStructureNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.DeclarationNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.ReassignmentNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.ControlStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FunctionCallNode;

/**
 * Represents a single statement.
 *
 * @since 29.07.2024
 */
public sealed interface StatementNode extends Node permits ControlStructureNode, DeclarationNode, ReassignmentNode, ControlStatementNode, FunctionCallNode {
}
