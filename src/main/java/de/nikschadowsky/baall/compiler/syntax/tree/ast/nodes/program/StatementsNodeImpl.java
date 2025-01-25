package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program;

import de.nikschadowsky.baall.compiler.semantic.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.StatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeUtility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.LinkedList;
import java.util.List;

/**
 * @since 14.04.2024
 */
public class StatementsNodeImpl extends AbstractNode implements StatementsNode {

    private List<StatementNode> statements;

    public StatementsNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public @UnmodifiableView List<StatementNode> getStatements() {
        return NodeUtility.toUnmodifiableList(statements);
    }

    public void setStatements(List<StatementNode> statements) {
        this.statements = new LinkedList<>(statements);
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.STATEMENTS;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitStatements(this);
    }
}
