package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures.ControlStructureNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;
import java.util.Optional;

/**
 * Represents the try statement of a try-intercept.
 *
 * @since 11.08.2024
 */
public interface TryStatementNode extends ControlStructureNode {

    /**
     * Body of this try.
     *
     * @return statements
     */
    StatementsNode getBody();

    /**
     * Intercept statements of this try.
     *
     * @return intercept statements
     */
    @NotNull
    @UnmodifiableView
    List<InterceptStatementNode> getInterceptBlocks();

    /**
     * Optional ensure block of the try-intercept
     *
     * @return optional ensure
     */
    Optional<EnsureStatementNode> getEnsureBlock();
}
