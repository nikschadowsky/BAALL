package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value;

import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.Node;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.ExpressionNode;

import java.util.List;

/**
 * @since 11.08.2024
 */
public interface ExceptionCallNode extends Node {

    IdentifierAccessNode getExceptionIdentifier();

    List<ExpressionNode> getArguments();

}
