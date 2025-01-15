package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.Node;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;

import java.util.List;

/**
 * @since 29.07.2024
 */
public interface TypeNode extends Node {

    Token getType();

    List<ExpressionNode> getArrayDimensionDefinitions();

}
