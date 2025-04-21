package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.ControlStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FunctionCallNode;

/**
 * Represents the raising of an exception.
 *
 * @since 11.08.2024
 */
public sealed interface RaiseStatementNode extends ControlStatementNode permits RaiseStatementNodeImpl {

    /**
     * Created exception.
     *
     * @return exception
     */
    FunctionCallNode getException();

}
