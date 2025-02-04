package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.StatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @since 14.04.2024
 */
public class StatementsNodeImpl extends AbstractNode implements StatementsNode {

    private List<StatementNode> statements = Collections.emptyList();

    public StatementsNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public @NotNull @UnmodifiableView List<StatementNode> getStatements() {
        return Collections.unmodifiableList(statements);
    }

    public void setStatements(@NotNull List<StatementNode> statements) {
        this.statements = new ArrayList<>(statements);
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.STATEMENTS;
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitStatements(this, data);
    }
}
