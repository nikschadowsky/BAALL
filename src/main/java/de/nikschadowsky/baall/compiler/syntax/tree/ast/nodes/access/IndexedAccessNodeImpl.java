package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access;


import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @since 11.03.2025
 */
public final class IndexedAccessNodeImpl extends AbstractNode implements IndexedAccessNode {

    private ElementAccessNode inner;
    private ExpressionNode index;

    public IndexedAccessNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public ElementAccessNode getInnerIdentifier() {
        return inner;
    }

    public void setInner(ElementAccessNode inner) {
        this.inner = inner;
    }

    @Override
    public ExpressionNode getIndex() {
        return index;
    }

    public void setIndex(ExpressionNode index) {
        this.index = index;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.INDEXED_ACCESS;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof IndexedAccessNodeImpl that)) return false;
        return Objects.equals(getInnerIdentifier(), that.getInnerIdentifier())
                       && Objects.equals(getIndex(), that.getIndex())
                       && getNodeType().equals(that.getNodeType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getInnerIdentifier(), getIndex(), getNodeType());
    }

    @Override
    public @NotNull String getDisplayDescriptor() {
        return "%s[%s]".formatted(getInnerIdentifier().getDisplayDescriptor(), getIndex().getDisplayDescriptor());
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitIndexedAccess(this, data);
    }
}
