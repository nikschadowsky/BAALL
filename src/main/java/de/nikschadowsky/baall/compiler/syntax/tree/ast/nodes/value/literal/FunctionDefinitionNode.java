package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.TypeNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.LiteralNode;

import java.util.List;
import java.util.Map;

/**
 * @since 29.07.2024
 */
public interface FunctionDefinitionNode extends LiteralNode {

    List<Map.Entry<TypeNode, Token>> getParameters();

    StatementsNode getFunctionBody();

}
