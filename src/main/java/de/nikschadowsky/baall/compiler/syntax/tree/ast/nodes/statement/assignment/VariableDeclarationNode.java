package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;

import java.util.Optional;

/**
 * Represents the declaration of a variable
 *
 * @since 30.07.2024
 */
public sealed interface VariableDeclarationNode extends DeclarationNode permits VariableDeclarationNodeImpl {

    /**
     * Optional initialization value.
     *
     * @return optional initial value
     * @apiNote Optional is empty when there was no initial value supplied.
     */
    Optional<ExpressionNode> getInitializationValue();

}
