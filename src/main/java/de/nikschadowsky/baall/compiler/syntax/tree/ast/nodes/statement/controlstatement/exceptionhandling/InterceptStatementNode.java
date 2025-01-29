package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.Node;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.IdentifierAccessNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.IdentifierNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

/**
 * Represents an intercept statement of a try-intercept.
 *
 * @since 11.08.2024
 */
public interface InterceptStatementNode extends Node {

    /**
     * Exceptions to be caught.
     *
     * @return exceptions
     */
    @NotNull
    @UnmodifiableView
    List<IdentifierAccessNode> getInterceptedExceptions();

    /**
     * Binding variable of the exceptions.
     *
     * @return binding variable identifier
     */
    IdentifierNode getRaisedExceptionIdentifier();

    /**
     * Block of the intercept statement
     *
     * @return statements.
     */
    StatementsNode getBody();

}
