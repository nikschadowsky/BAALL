package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.controlstatement;

import de.nikschadowsky.baall.compiler.syntaxtree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntaxtree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

/**
 * @since 30.07.2024
 */
public class BreakStatementNodeImpl extends AbstractNode implements LoopControlStatementNode {

    public BreakStatementNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public @NotNull StatementType getStatementType() {
        return StatementType.BREAK;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.BREAK;
    }
}
