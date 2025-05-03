package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access;


import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @since 11.03.2025
 */
public final class ScopeElevationNodeImpl extends AbstractNode implements ScopeElevationNode {

    private ElementAccessNode inner;

    public ScopeElevationNodeImpl(NodeDiagnosticCollector diagnostics) {
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
    public @NotNull NodeType getNodeType() {
        return NodeType.SCOPE_ELEVATION;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ScopeElevationNodeImpl that)) return false;
        return Objects.equals(getInnerIdentifier(), that.getInnerIdentifier())
                       && getNodeType().equals(that.getNodeType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getInnerIdentifier(), getNodeType());
    }

    @Override
    public @NotNull String getDisplayDescriptor() {
        return "$%s".formatted(getInnerIdentifier().getDisplayDescriptor());
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitScopeElevation(this, data);
    }
}
