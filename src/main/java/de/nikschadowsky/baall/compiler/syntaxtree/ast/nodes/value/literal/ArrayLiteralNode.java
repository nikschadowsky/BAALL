package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.literal;

import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.LiteralNode;

import java.util.List;

/**
 * File created on 29.07.2024
 */
public interface ArrayLiteralNode extends LiteralNode {

    List<ExpressionNode> getElements();

}
