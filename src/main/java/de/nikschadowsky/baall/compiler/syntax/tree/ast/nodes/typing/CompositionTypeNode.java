package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing;


/**
 * @since 09.03.2025
 */
public sealed interface CompositionTypeNode extends TypeNode permits FunctionTypeNode, ListTypeNode {

    TypeNode getInnerType();

}
