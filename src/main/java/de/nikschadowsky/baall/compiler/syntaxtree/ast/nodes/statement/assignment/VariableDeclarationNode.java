package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.assignment;

import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.ExpressionNode;

import java.util.Optional;

/**
 * File created on 30.07.2024
 */
public interface VariableDeclarationNode extends DeclarationNode {

    Optional<ExpressionNode> getInitializationValue();

}
