package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.ElementAccessNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.StatementNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

/**
 * Represents the call of a function.
 *
 * @since 29.07.2024
 */
public interface FunctionCallNode extends TermNode, StatementNode {

    /**
     * Identifier of the called function.
     *
     * @return function identifier access
     */
    ElementAccessNode getFunctionIdentifier();

    /**
     * Arguments to call the function with.
     *
     * @return arguments
     */
    @NotNull
    @UnmodifiableView
    List<ExpressionNode> getArguments();

}
