package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value;

import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.StatementNode;

import java.util.List;

/**
 * File created on 29.07.2024
 */
public interface FunctionCallNode extends ValueNode, StatementNode {

    IdentifierAccessNode getFunctionIdentifier();

    List<ExpressionNode> getArguments();

}
