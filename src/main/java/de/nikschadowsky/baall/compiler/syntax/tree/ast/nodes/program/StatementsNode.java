package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.Node;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.StatementNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

/**
 * Represents a block of statements.
 *
 * @since 29.07.2024
 */
public sealed interface StatementsNode extends Node permits StatementsNodeImpl {

    /**
     * Statements of this block. All statements are in the same scope.
     *
     * @return statements
     */
    @NotNull
    @UnmodifiableView
    List<StatementNode> getStatements();

}
