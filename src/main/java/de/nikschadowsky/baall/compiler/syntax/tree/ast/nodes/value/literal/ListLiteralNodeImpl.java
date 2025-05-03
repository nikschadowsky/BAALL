package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.Node;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
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
 * @since 29.07.2024
 */
public final class ListLiteralNodeImpl extends AbstractNode implements ListLiteralNode {

    private List<ExpressionNode> elements = Collections.emptyList();

    public ListLiteralNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public @NotNull @UnmodifiableView List<ExpressionNode> getElements() {
        return Collections.unmodifiableList(elements);
    }

    public void setElements(@NotNull List<ExpressionNode> elements) {
        this.elements = new ArrayList<>(elements);
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.ARRAY_LITERAL;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ListLiteralNodeImpl that)) return false;
        return Objects.equals(getElements(), that.getElements()) && getNodeType().equals(that.getNodeType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getElements(), getNodeType());
    }

    @Override
    public @NotNull String getDisplayDescriptor() {
        return elements.stream().map(Node::getDisplayDescriptor).collect(Collectors.joining(",", "[", "]"));
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitListLiteral(this, data);
    }
}
