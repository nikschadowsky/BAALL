package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.Node;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;

/**
 * Represents the ensure statement in a try-intercept.
 *
 * @since 11.08.2024
 */
public interface EnsureStatementNode extends Node {

    /**
     * Body of the ensure statement.
     *
     * @return statements of the block
     */
    StatementsNode getBody();

}
