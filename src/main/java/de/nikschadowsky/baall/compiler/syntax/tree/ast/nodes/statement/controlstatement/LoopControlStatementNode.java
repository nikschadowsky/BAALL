package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement;

import org.jetbrains.annotations.NotNull;

/**
 * Represents the control statements for loops. A loop control statement is one of the following:
 * <ul>
 *     <li>break</li>
 *     <li>continue</li>
 * </ul>
 *
 * @since 30.07.2024
 */
public sealed interface LoopControlStatementNode extends ControlStatementNode permits BreakStatementNode, ContinueStatementNode {

    enum StatementType {
        BREAK, CONTINUE
    }

    /**
     * Type of the control statement-
     *
     * @return statement type
     */
    // todo necessary?
    @NotNull
    StatementType getStatementType();

}
