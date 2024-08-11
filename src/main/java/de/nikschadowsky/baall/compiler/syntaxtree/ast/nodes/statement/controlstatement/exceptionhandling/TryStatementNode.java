package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.controlstatement.exceptionhandling;

import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.StatementNode;

import java.util.List;
import java.util.Optional;

/**
 * @since 11.08.2024
 */
public interface TryStatementNode extends StatementNode {

    StatementsNode getBody();

    List<InterceptStatementNode> getInterceptBlocks();

    Optional<EnsureStatementNode> getEnsureBlock();
}
