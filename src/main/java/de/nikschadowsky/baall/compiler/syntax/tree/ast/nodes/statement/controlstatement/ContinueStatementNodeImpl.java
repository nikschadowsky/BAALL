package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

/**
 * @since 30.07.2024
 */
public class ContinueStatementNodeImpl extends AbstractNode implements LoopControlStatementNode {

    public ContinueStatementNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public @NotNull StatementType getStatementType() {
        return StatementType.CONTINUE;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.CONTINUE;
    }
}
