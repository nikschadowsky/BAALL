package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.ControlStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.StructInitializationLiteralNode;

/**
 * @since 11.08.2024
 */
public interface RaiseStatementNode extends ControlStatementNode {

    StructInitializationLiteralNode getException();

}
