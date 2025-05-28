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
public final class ComponentAccessNodeImpl extends AbstractNode implements ComponentAccessNode {

    private ElementAccessNode inner;
    private IdentifierNode selected;

    public ComponentAccessNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public ElementAccessNode getInner() {
        return inner;
    }

    public void setInner(ElementAccessNode inner) {
        this.inner = inner;
    }

    @Override
    public IdentifierNode getSelected() {
        return selected;
    }

    public void setSelected(IdentifierNode selected) {
        this.selected = selected;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.MEMBER_REFERENCE;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ComponentAccessNodeImpl that)) return false;
        return Objects.equals(getSelected(), that.getSelected())
                       && Objects.equals(getInner(), that.getInner())
                       && getNodeType().equals(that.getNodeType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSelected(), getInner(), getNodeType());
    }

    @Override
    public @NotNull String getDisplayDescriptor() {
        return "%s.%s".formatted(getInner().getDisplayDescriptor(), getSelected().getDisplayDescriptor());
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitMemberReference(this, data);
    }
}
