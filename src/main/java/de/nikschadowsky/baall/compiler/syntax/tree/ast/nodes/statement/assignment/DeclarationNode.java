package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.IdentifierNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.StatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.TypeNode;

/**
 * Represents a declaration of a field, struct, exception or function.
 *
 * @since 29.07.2024
 */
public sealed interface DeclarationNode extends StatementNode permits ConstantDeclarationNode, VariableDeclarationNode {

    /**
     * Type of the declared element.
     *
     * @return declared static type
     */
    TypeNode getType();

    /**
     * Identifier of the declared element.
     *
     * @return identifier
     */
    IdentifierNode getIdentifier();

}
