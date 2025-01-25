package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.TypeNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.LiteralNode;

import java.util.List;

/**
 * @since 29.07.2024
 */
public interface StructDefinitionLiteralNode extends LiteralNode {

    List<TypeNode> getFieldTypes();

    List<Token> getFieldNames();

}
