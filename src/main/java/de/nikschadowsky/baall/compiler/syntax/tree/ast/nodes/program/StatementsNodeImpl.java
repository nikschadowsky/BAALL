package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.Node;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.StatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @since 14.04.2024
 */
public final class StatementsNodeImpl extends AbstractNode implements StatementsNode {

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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof StatementsNodeImpl that)) return false;
        return Objects.equals(getStatements(), that.getStatements()) && getNodeType().equals(that.getNodeType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStatements(), getNodeType());
    }

    @Override
    public @NotNull String getDisplayDescriptor() {
        return getStatements().stream()
                              .map(Node::getDisplayDescriptor)
                              .map("%s;"::formatted)
                              .collect(Collectors.joining());
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitStatements(this, data);
    }
}
