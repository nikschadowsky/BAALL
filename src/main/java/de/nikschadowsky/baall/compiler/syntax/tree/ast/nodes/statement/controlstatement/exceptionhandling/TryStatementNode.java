package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures.ControlStructureNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;

import java.util.List;
import java.util.Optional;

/**
 * @since 11.08.2024
 */
public interface TryStatementNode extends ControlStructureNode {

    StatementsNode getBody();

    List<InterceptStatementNode> getInterceptBlocks();

    Optional<EnsureStatementNode> getEnsureBlock();
}
