package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.controlstatement;

import de.nikschadowsky.baall.compiler.syntaxtree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntaxtree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

/**
 * File created on 30.07.2024
 */
public class ContinueStatementNodeImpl extends AbstractNode implements LoopControlStatementNode{

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
