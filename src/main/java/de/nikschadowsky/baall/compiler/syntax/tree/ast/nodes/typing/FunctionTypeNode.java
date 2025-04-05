package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

/**
 * @since 04.03.2025
 */
public sealed interface FunctionTypeNode extends CompositionTypeNode permits FunctionTypeNodeImpl {

    @NotNull
    @UnmodifiableView
    List<TypeNode> getParameterTypes();

}
