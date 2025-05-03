package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing;


import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.ElementAccessNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @since 09.03.2025
 */
public final class IdentifierTypeNodeImpl extends AbstractNode implements IdentifierTypeNode {

    private ElementAccessNode type;
    private boolean noneSafe;

    public IdentifierTypeNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public ElementAccessNode getType() {
        return type;
    }

    public void setType(ElementAccessNode type) {
        this.type = type;
    }

    @Override
    public boolean isNoneSafe() {
        return noneSafe;
    }

    public void setNoneSafe(boolean noneSafe) {
        this.noneSafe = noneSafe;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.IDENTIFIER_TYPE;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof IdentifierTypeNodeImpl that)) return false;
        return Objects.equals(getType(), that.getType())
                       && isNoneSafe() == that.isNoneSafe()
                       && getNodeType().equals(that.getNodeType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), isNoneSafe(), getNodeType());
    }

    @Override
    public @NotNull String getDisplayDescriptor() {
        return "%s%s".formatted(getType().getDisplayDescriptor(), isNoneSafe() ? "!" : "");
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitIdentifierType(this, data);
    }
}
