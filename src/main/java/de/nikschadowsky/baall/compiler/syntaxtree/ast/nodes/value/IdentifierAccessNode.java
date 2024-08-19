package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.ExpressionNode;

import java.util.List;

/**
 * @since 29.07.2024
 */
public interface IdentifierAccessNode extends ValueNode {

    Token getIdentifier();

    List<ExpressionNode> getArrayIndices();
}
