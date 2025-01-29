package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FieldNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.LiteralNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

/**
 * Represents a function definition.
 *
 * @since 29.07.2024
 */
public interface FunctionDefinitionNode extends LiteralNode {

    /**
     * Declared parameters of the function.
     *
     * @return function parameters
     */
    @NotNull
    @UnmodifiableView
    List<FieldNode> getParameters();

    /**
     * Body of the function.
     *
     * @return statements
     */
    StatementsNode getFunctionBody();

}
