package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;

/**
 * Represents the declaration of a constant field.
 *
 * @since 30.07.2024
 */
public sealed interface ConstantDeclarationNode extends DeclarationNode permits ConstantDeclarationNodeImpl {

    /**
     * Initialization value of the constant.
     *
     * @return initial and final value
     */
    ExpressionNode getInitializationValue();

}
