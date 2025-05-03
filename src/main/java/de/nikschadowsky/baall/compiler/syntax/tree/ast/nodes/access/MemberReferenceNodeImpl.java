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
public final class MemberReferenceNodeImpl extends AbstractNode implements MemberReferenceNode {

    private ElementAccessNode inner;
    private ElementAccessNode self;

    public MemberReferenceNodeImpl(NodeDiagnosticCollector diagnostics) {
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
    public ElementAccessNode getSelf() {
        return self;
    }

    public void setSelf(ElementAccessNode self) {
        this.self = self;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.MEMBER_REFERENCE;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MemberReferenceNodeImpl that)) return false;
        return Objects.equals(getSelf(), that.getSelf())
                       && Objects.equals(getInnerIdentifier(), that.getInnerIdentifier())
                       && getNodeType().equals(that.getNodeType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSelf(), getInnerIdentifier(), getNodeType());
    }

    @Override
    public @NotNull String getDisplayDescriptor() {
        return "%s.%s".formatted(getSelf().getDisplayDescriptor(), getInnerIdentifier().getDisplayDescriptor());
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitMemberReference(this, data);
    }
}
