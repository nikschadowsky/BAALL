package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.*;

/**
 * Representing a literal.
 *
 * @since 29.07.2024
 */
public sealed interface LiteralNode extends TermNode permits BooleanLiteralNode, FunctionDefinitionNode, ListLiteralNode, NumberLiteralNode, StringLiteralNode, StructDefinitionLiteralNode, StructNoneLiteralNode {
}
