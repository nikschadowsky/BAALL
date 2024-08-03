package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.typing;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.Node;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.ExpressionNode;

import java.util.List;

/**
 * File created on 29.07.2024
 */
public interface TypeNode extends Node {

    Token getType();

    List<ExpressionNode> getArrayDimensionDefinitions();

}
