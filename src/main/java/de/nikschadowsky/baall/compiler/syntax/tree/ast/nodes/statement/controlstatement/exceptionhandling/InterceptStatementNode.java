package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.Node;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.VariableDeclarationNode;

/**
 * Represents an intercept statement of a try-intercept.
 *
 * @since 11.08.2024
 */
public sealed interface InterceptStatementNode extends Node permits InterceptStatementNodeImpl {

    /**
     * Binding variable of the exceptions.
     *
     * @return binding variable identifier
     */
    VariableDeclarationNode getCaughtException();

    /**
     * Block of the intercept statement
     *
     * @return statements.
     */
    StatementsNode getBody();

}
