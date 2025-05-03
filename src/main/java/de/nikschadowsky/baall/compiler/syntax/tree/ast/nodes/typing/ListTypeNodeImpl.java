package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing;


import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @since 09.03.2025
 */
public final class ListTypeNodeImpl extends AbstractNode implements ListTypeNode {

    private TypeNode inner;

    public ListTypeNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public TypeNode getInnerType() {
        return inner;
    }

    public void setInner(TypeNode inner) {
        this.inner = inner;
    }

    @Override
    public boolean isNoneSafe() {
        return true;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.NESTED_TYPE;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ListTypeNodeImpl that)) return false;
        return Objects.equals(getInnerType(), that.getInnerType())
                       && getNodeType().equals(that.getNodeType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getInnerType(), getNodeType());
    }

    @Override
    public @NotNull String getDisplayDescriptor() {
        return "%s[]".formatted(getInnerType().getDisplayDescriptor());
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitListType(this, data);
    }
}
