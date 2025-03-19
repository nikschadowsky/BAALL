package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access;


import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

/**
 * @since 11.03.2025
 */
public class MemberReferenceNodeImpl extends AbstractNode implements MemberReferenceNode {

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
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitMemberReference(this, data);
    }
}
