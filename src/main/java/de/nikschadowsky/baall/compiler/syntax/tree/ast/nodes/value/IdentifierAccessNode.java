package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;

import java.util.List;

/**
 * @since 29.07.2024
 */
public interface IdentifierAccessNode extends TermNode {

    Token getIdentifier();

    List<ExpressionNode> getArrayIndices();
}
