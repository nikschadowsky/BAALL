package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.controlstatement;

import org.jetbrains.annotations.NotNull;

/**
 * @since 30.07.2024
 */
public interface LoopControlStatementNode extends ControlStatementNode {

    enum StatementType {
        BREAK, CONTINUE
    }

    // todo necessary?
    @NotNull
    StatementType getStatementType();

}
