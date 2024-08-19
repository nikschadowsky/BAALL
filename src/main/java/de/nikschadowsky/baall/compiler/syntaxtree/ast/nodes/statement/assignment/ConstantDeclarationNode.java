package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.assignment;

import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.ExpressionNode;

/**
 * @since 30.07.2024
 */
public interface ConstantDeclarationNode extends DeclarationNode {

    ExpressionNode getInitializationValue();

}
